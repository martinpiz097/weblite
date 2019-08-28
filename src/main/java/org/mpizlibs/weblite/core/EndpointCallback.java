package org.mpizlibs.weblite.core;

import io.undertow.io.Sender;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;

public interface EndpointCallback {

    public boolean isValidRequest(HttpServerExchange exchange, Endpoint endpoint);
    public default Endpoint findCalledEndpoint(HttpServerExchange exchange, Endpoint endpoint) {
        String path = exchange.getRequestPath();

        Endpoint finded = endpoint.findByPathRecursive(path);
        endpoint.saveFinded(finded);
        return endpoint.getFinded();
    }

    public default void onRequest(HttpServerExchange exchange, Endpoint endpoint) {
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

    public void onSucess(HttpServerExchange exchange, Endpoint endpoint);
    public void onError(HttpServerExchange exchange, Endpoint endpoint);
    //public void onNotFound(HttpServerExchange exchange, int statusCode, Endpoint endpoint);
    //public void onMethodNotAllowed(HttpServerExchange exchange, int statusCode, Endpoint endpoint);

    public ContentType getRequestContentType();
    public ContentType getResponseContentType();


    public default void sendResponse(HttpServerExchange exchange, String msg) {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE,
                getResponseContentType().getMimeType());
        Sender responseSender = exchange.getResponseSender();
        responseSender.send(msg);
        exchange.endExchange();
    }
}