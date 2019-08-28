package org.mpizlibs.weblite.http;

import io.undertow.server.HttpServerExchange;
import org.apache.http.HttpStatus;

public abstract class ResponserManagerImpl implements ResponserManager {
    @Override
    public void sendNotFoundError(HttpServerExchange exchange) {
        exchange.setStatusCode(HttpStatus.SC_NOT_FOUND);
        exchange.getResponseSender().send("{}");
    }
}
