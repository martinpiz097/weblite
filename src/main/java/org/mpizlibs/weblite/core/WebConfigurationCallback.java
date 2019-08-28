package org.mpizlibs.weblite.core;

import io.undertow.server.HttpServerExchange;

@FunctionalInterface
public interface WebConfigurationCallback {
    void receivRequest(HttpServerExchange exchange);
}
