package org.mpizlibs.weblite.test;
import io.undertow.io.Receiver;
import io.undertow.server.HttpServerExchange;
import org.apache.http.entity.ContentType;
import org.mpizlibs.weblite.exceptions.WebLiteException;
import org.mpizlibs.weblite.endpoint.ActionableEndpoint;
import org.mpizlibs.weblite.endpoint.ParentEndpoint;
import org.mpizlibs.weblite.net.WebService;
import org.mpizlibs.weblite.net.WebConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

public class TestServer {

    private static Logger logger = Logger.getLogger("TestServer");

    public static void main(String[] args) throws IOException {
        startServer();
        //testParallelConsuming();
    }

    private static String getConsumeContent(URL url) throws IOException {
        URLConnection urlConnection = url.openConnection();
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);

        InputStream inputStream = urlConnection.getInputStream();

        StringBuilder sbInput = new StringBuilder();

        int read = 0;
        while ((read = inputStream.read()) != -1) {
            sbInput.append((char)read);
        }
        return sbInput.toString();
    }

    private static void testParallelConsuming() throws IOException {
        for (int i = 0; i < 1000000; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    URL url;
                    String requestPath;
                    try {
                        requestPath = "/";
                        url = new URL("http://localhost:8080"+requestPath);
                        System.out.println("From "+requestPath + " " + getConsumeContent(url));

                        requestPath = "/add";
                        url = new URL("http://localhost:8080"+requestPath);
                        System.out.println("From "+requestPath + " " + getConsumeContent(url));

                        requestPath = "/del";
                        url = new URL("http://localhost:8080"+requestPath);
                        System.out.println("From "+requestPath + " " + getConsumeContent(url));

                        System.out.println("------------------------------------");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private static void startServer() {
        WebService service = new WebService();
        ParentEndpoint parentEndpoint = new ParentEndpoint("/");
        ActionableEndpoint endpoint = new ActionableEndpoint(
                "/test",
                ContentType.TEXT_PLAIN,
                parentEndpoint
        ) {
            @Override
            public void onRequest(HttpServerExchange exchange, ActionableEndpoint actionable) {
                Receiver receiver = exchange.getRequestReceiver();
                receiver.receiveFullString((httpServerExchange, message) -> {
                    sendResponse(httpServerExchange, "Data received: "+message);
                });
            }

            @Override
            public void onSucess(HttpServerExchange exchange, ActionableEndpoint actionable) {

            }

            @Override
            public void onError(HttpServerExchange exchange, ActionableEndpoint actionable) {

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

        parentEndpoint.addChild(endpoint);
        WebConfiguration configuration = new WebConfiguration("0.0.0.0", 8080, parentEndpoint);

        try {
            service.initialize(configuration);
            service.start();
        } catch (WebLiteException e) {
            e.printStackTrace();
        }
    }


}
