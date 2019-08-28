package org.mpizlibs.weblite.test;
import io.undertow.server.HttpServerExchange;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.mpizlibs.weblite.core.Endpoint;
import org.mpizlibs.weblite.core.EndpointCallback;
import org.mpizlibs.weblite.exceptions.WebLiteException;
import org.mpizlibs.weblite.http.HttpMethod;
import org.mpizlibs.weblite.net.WebService;
import org.mpizlibs.weblite.sys.WebConfiguration;

import java.util.logging.Logger;

public class TestServer {

    private static Logger logger = Logger.getLogger("TestServer");

    public static void main(String[] args) {
        WebService service = new WebService();

        EndpointCallback callback = new EndpointCallback() {
            @Override
            public boolean isValidRequest(HttpServerExchange exchange, Endpoint endpoint) {
                String path = exchange.getRequestPath();
                String method = exchange.getRequestMethod().toString();

                Endpoint finded = endpoint.findByPathRecursive(path);
                endpoint.saveFinded(finded);

                // falta verificar el content type

                if (finded == null) {
                    return false;
                }
                else {
                    return finded.getMethod().equals(method);
                }
            }

            @Override
            public void onRequest(HttpServerExchange exchange, Endpoint endpoint) {
                if (isValidRequest(exchange, endpoint)) {
                    onSucess(exchange, endpoint.getFinded());
                }
                else {
                    Endpoint finded = endpoint.getFinded();
                    if (finded == null) {
                        exchange.setStatusCode(HttpStatus.SC_NOT_FOUND);
                    }
                    else {
                        exchange.setStatusCode(HttpStatus.SC_METHOD_NOT_ALLOWED);
                    }
                    onError(exchange, finded);
                }
            }

            @Override
            public void onSucess(HttpServerExchange exchange, Endpoint endpoint) {
                sendResponse(exchange, "Sucess in path "+endpoint.getFullPath()+"!");
            }

            @Override
            public void onError(HttpServerExchange exchange, Endpoint endpoint) {
                sendResponse(exchange, "Error "+exchange.getStatusCode()+
                        " in path "+endpoint.getFullPath()+"!");
            }

            @Override
            public ContentType getRequestContentType() {
                return ContentType.TEXT_PLAIN;
            }

            @Override
            public ContentType getResponseContentType() {
                return ContentType.TEXT_PLAIN;
            }
        };
        Endpoint endpoint = new Endpoint(HttpMethod.GET,
                "/", ContentType.TEXT_PLAIN, callback);

        WebConfiguration configuration = new WebConfiguration("0.0.0.0", 8080, endpoint) {
            @Override
            public void receivRequest(HttpServerExchange exchange) {
                getEndpoint().execute(exchange);
            }
        };

        try {
            service.initialize(configuration);
            service.start();
        } catch (WebLiteException e) {
            e.printStackTrace();
        }
    }
}
