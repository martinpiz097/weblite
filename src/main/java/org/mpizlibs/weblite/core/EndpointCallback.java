package org.mpizlibs.weblite.core;

import io.undertow.io.Sender;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import org.apache.http.entity.ContentType;

public interface EndpointCallback {

    public boolean isValidRequest(HttpServerExchange exchange, Endpoint endpoint);
    public void onRequest(HttpServerExchange exchange, Endpoint endpoint);

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