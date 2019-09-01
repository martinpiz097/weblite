package org.mpizlibs.weblite.test;

import io.undertow.server.HttpServerExchange;
import org.apache.http.HttpStatus;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class TestServer {

    private static Logger logger = Logger.getLogger("TestServer");
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) throws Exception {
        if (args.length > 0) {
            long start = System.currentTimeMillis();
            testParallelConsuming();
            long end = System.currentTimeMillis();
            System.out.println("Time in millis: "+(end-start));
        }
        else {
            startServer();
        }
    }

    private static void testEmptyServer() {
        WebService webService = new WebService("0.0.0.0", SERVER_PORT);
        webService.start();
    }

    private static String getConsumeContent(URL url) {
        try {
            URLConnection urlConnection = url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            InputStream inputStream = urlConnection.getInputStream();

            StringBuilder sbInput = new StringBuilder();

            int read;
            while ((read = inputStream.read()) != -1) {
                sbInput.append((char)read);
            }
            return sbInput.toString();
        } catch (IOException e) {
            return e.toString();
        }
    }

    private static void testParallelConsuming() {
        final String[] response = new String[1];

        for (int i = 0; i < 100000; i++) {
            new Thread(() -> {
                URL url;
                String requestPath;
                try {
                    requestPath = "/";
                    url = new URL("http://localhost:"+SERVER_PORT+requestPath);
                    response[0] = getConsumeContent(url);
                    //System.out.println("From "+requestPath + " " + response[0]);
                    if (response[0].contains("refused"))
                        System.out.println("Connection refused!");
                        System.exit(1);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private static void startServer() {
        WebService service = new WebService();
        ParentEndpoint parentEndpoint = new ParentEndpoint("/");
        ActionableEndpoint endpoint = new ActionableEndpoint(
                "",
                ContentType.TEXT_PLAIN,
                parentEndpoint
        ) {
            @Override
            public void onRequest(HttpServerExchange exchange, ActionableEndpoint actionable) {
                onSucess(exchange, actionable);
            }

            @Override
            public void onSucess(HttpServerExchange exchange, ActionableEndpoint actionable) {
                exchange.setStatusCode(HttpStatus.SC_OK);
                sendResponse(exchange, "Hello World");
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
        WebConfiguration configuration = new WebConfiguration(
                "0.0.0.0", SERVER_PORT, parentEndpoint);

        try {
            service.initialize(configuration);
            service.start();
        } catch (WebLiteException e) {
            e.printStackTrace();
        }
    }


}
