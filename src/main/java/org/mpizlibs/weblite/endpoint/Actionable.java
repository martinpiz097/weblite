package org.mpizlibs.weblite.endpoint;

import io.undertow.io.Sender;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.apache.http.entity.ContentType;

public interface Actionable {

    public void onRequest(HttpServerExchange exchange, ActionableEndpoint actionable);
    public void onSucess(HttpServerExchange exchange, ActionableEndpoint actionable);
    public void onError(HttpServerExchange exchange, ActionableEndpoint actionable);

    public ContentType getRequestContentType();
    public ContentType getResponseContentType();

    public default void sendResponse(HttpServerExchange exchange, Object response) {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE,
                getResponseContentType().getMimeType());
        Sender responseSender = exchange.getResponseSender();
        responseSender.send(response.toString());
        exchange.endExchange();
    }

    public default void sendResponse(HttpServerExchange exchange,
                                     int statusCode, Object response) {
        exchange.setStatusCode(statusCode);
        sendResponse(exchange, response);
    }
}