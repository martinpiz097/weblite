package org.mpizlibs.weblite.test;

import io.undertow.io.Sender;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.mpizlibs.weblite.core.Endpoint;
import org.mpizlibs.weblite.exceptions.WebLiteException;
import org.mpizlibs.weblite.net.WebService;
import org.mpizlibs.weblite.sys.WebConnection;

import java.util.logging.Logger;

public class TestServer {

    private static Logger logger = Logger.getLogger("TestServer");

    public static void main(String[] args) {
        WebService service = new WebService();
        Endpoint parent = new Endpoint("GET", "/", ContentType.APPLICATION_JSON);

        Endpoint child = new Endpoint("GET", "/a", ContentType.APPLICATION_JSON);
        Endpoint subChild = new Endpoint("GET", "/b", ContentType.APPLICATION_JSON);
        child.addChildEndpoint(subChild);
        parent.addChildEndpoint(child);

        logger.info("SubChild path: "+subChild.getFullPath());

        System.out.println("Endpoints: "+parent.getChildCount());
        WebConnection connection = new WebConnection("0.0.0.0", 8080, parent) {
            @Override
            public void receivRequest(HttpServerExchange exchange) {
                String method = exchange.getRequestMethod().toString().toUpperCase().trim();
                String path = exchange.getRequestPath();
                logger.info("RequestMethod: "+method);
                logger.info("RequestPath: "+path);

                Endpoint byPath = this.getEndpoint().findByPath(path);
                HeaderMap header = exchange.getResponseHeaders();
                Sender sender = exchange.getResponseSender();

                if (byPath == null) {
                    header.put(Headers.CONTENT_TYPE, ContentType.TEXT_PLAIN.getMimeType());
                    exchange.setStatusCode(HttpStatus.SC_NOT_FOUND);
                    sender.send("Error 404");
                }
                else {
                    boolean validMethod = byPath.isValidMethod(method);
                    if (validMethod) {
                        header.put(Headers.CONTENT_TYPE, parent.getContentType());
                        exchange.setStatusCode(HttpStatus.SC_OK);
                        sender.send("{\n\t\"message\": \"Hello Weblite!\"\n}");
                    }
                    else {
                        header.put(Headers.CONTENT_TYPE, ContentType.TEXT_PLAIN.getMimeType());
                        exchange.setStatusCode(HttpStatus.SC_METHOD_NOT_ALLOWED);
                        sender.send("Error 405");
                    }
                }
            }
        };
        try {
            service.initialize(connection);
            service.start();
        } catch (WebLiteException e) {
            e.printStackTrace();
        }
    }
}
