package org.mpizlibs.weblite.test;

import io.undertow.server.HttpServerExchange;
import org.apache.http.entity.ContentType;
import org.mpizlibs.weblite.endpoint.ActionableEndpoint;
import org.mpizlibs.weblite.endpoint.ParentEndpoint;

import java.util.Deque;
import java.util.Map;

public class SumEndpoint extends ActionableEndpoint {

    public SumEndpoint(ParentEndpoint parent) {
        super("/sum", ContentType.TEXT_PLAIN, parent);
    }

    @Override
    public void onRequest(HttpServerExchange exchange, ActionableEndpoint actionable) {
        Map<String, Deque<String>> pathParameters = exchange.getPathParameters();
        if (pathParameters.isEmpty())
            onError(exchange, actionable);
        else {
            if (pathParameters.size() == 2) {
            }
            else {
                onError(exchange, actionable);
            }
        }
    }

    @Override
    public void onSucess(HttpServerExchange exchange, ActionableEndpoint actionable) {

    }

    @Override
    public void onError(HttpServerExchange exchange, ActionableEndpoint actionable) {

    }

    @Override
    public ContentType getRequestContentType() {
        return null;
    }

    @Override
    public ContentType getResponseContentType() {
        return null;
    }
}
