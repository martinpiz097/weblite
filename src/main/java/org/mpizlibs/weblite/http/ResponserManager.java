package org.mpizlibs.weblite.http;

import io.undertow.io.Sender;
import io.undertow.server.HttpServerExchange;

public interface ResponserManager {

    public void sendNotFoundError(HttpServerExchange exchange);
    public void sendMethodNotAllowedError(HttpServerExchange exchange);
    public void sendBadGatewayError(HttpServerExchange exchange);

    public void doPost(HttpServerExchange exchange);
    public void doGet(HttpServerExchange exchange);
    public void doPut(HttpServerExchange exchange);
    public void doPatch(HttpServerExchange exchange);
    public void doDelete(HttpServerExchange exchange);
    public void doOptions(HttpServerExchange exchange);
    public void doConnect(HttpServerExchange exchange);
    public void doTrace(HttpServerExchange exchange);
    public void doHead(HttpServerExchange exchange);
}
