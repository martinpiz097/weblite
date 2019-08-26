package org.mpizlibs.weblite.core;

import io.undertow.server.HttpServerExchange;

@FunctionalInterface
public interface Connectable {
    void receivRequest(HttpServerExchange exchange);
}
