package org.mpizlibs.weblite.net;

import io.undertow.server.HttpServerExchange;

@FunctionalInterface
public interface Executable {
    int execute(HttpServerExchange exchange);
}
