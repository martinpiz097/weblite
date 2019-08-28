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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
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

        EndpointCallback callback = new EndpointCallback() {
            @Override
            public boolean isValidRequest(HttpServerExchange exchange, Endpoint endpoint) {
                Endpoint finded = findCalledEndpoint(exchange, endpoint);

                // falta verificar el content type

                if (finded == null) {
                    return false;
                }
                else {
                    return finded.getMethod().equalsIgnoreCase(
                            exchange.getRequestMethod().toString());
                }
            }

            @Override
            public void onSucess(HttpServerExchange exchange, Endpoint endpoint) {
                sendResponse(exchange, "Sucess in path "+endpoint.getFullPath()+"!");
            }

            @Override
            public void onError(HttpServerExchange exchange, Endpoint endpoint) {
                sendResponse(exchange, "Error "+exchange.getStatusCode()+
                        " in path "+exchange.getRequestPath()+"!");
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

        endpoint.addChildEndpoint(new Endpoint(
                HttpMethod.GET, "/add", ContentType.TEXT_PLAIN, callback
        ));
        endpoint.addChildEndpoint(new Endpoint("/del", ContentType.TEXT_PLAIN, callback));

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
