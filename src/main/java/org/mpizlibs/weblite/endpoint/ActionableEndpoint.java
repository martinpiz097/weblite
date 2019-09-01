package org.mpizlibs.weblite.endpoint;

import io.undertow.server.HttpServerExchange;
import org.apache.http.entity.ContentType;
import org.mpizlibs.weblite.http.HttpMethod;

public abstract class ActionableEndpoint extends Endpoint implements Actionable {
    protected final String method;
    protected final String contentType;

    protected ParentEndpoint parent;

    public ActionableEndpoint(String method, String path,
                              String contentType, ParentEndpoint parent) {
        super(path);
        this.method = method;
        this.contentType = contentType;
        this.parent = parent;
    }

    public ActionableEndpoint(String path, String contentType, ParentEndpoint parent) {
        this(HttpMethod.GET, path, contentType, parent);
    }

    public ActionableEndpoint(String method, String path,
                              ContentType contentType, ParentEndpoint parent) {
        this(method, path, contentType.toString(), parent);
    }

    public ActionableEndpoint(String path, ContentType contentType, ParentEndpoint parent) {
        this(HttpMethod.GET, path, contentType, parent);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getFullPath() {
        if (parent == null) {
            return path;
        }
        else {
            String pathClone = path;
            String fullPath = parent.getFullPath();

            // parent termina en /
            if (fullPath.charAt(fullPath.length()-1) == '/') {
                if (pathClone.startsWith("/"))
                    if (pathClone.length() > 1)
                        pathClone = pathClone.substring(1);
                    else
                        pathClone = "";
            }
            else if (!pathClone.startsWith("/")){
                pathClone = "/".concat(pathClone);
            }

            return fullPath.concat(pathClone);
        }
    }

    public String getContentType() {
        return contentType;
    }

    public ParentEndpoint getParent() {
        return parent;
    }

    public void setParent(ParentEndpoint parent) {
        this.parent = parent;
    }

    public boolean isValidMethod(String method) {
        return this.method.equals(method);
    }

    public boolean isEqualsPath(String path) {
        String fullPath = getFullPath();
        if (path.endsWith("/") && fullPath.length() > 1) {
            fullPath = fullPath.concat("/");
        }

        else if (!path.endsWith("/") && fullPath.endsWith("/")) {
            path = path.concat("/");
        }

        return fullPath.equals(path);
    }

    public void execute(HttpServerExchange exchange) {
        onRequest(exchange, this);
    }

    @Override
    protected ActionableEndpoint clone() {
        return new ActionableEndpoint(
                method, path, contentType, parent) {
            @Override
            public void onRequest(HttpServerExchange exchange, ActionableEndpoint actionable) {
                onRequest(exchange, actionable);
            }

            @Override
            public void onSucess(HttpServerExchange exchange, ActionableEndpoint actionable) {
                onSucess(exchange, actionable);
            }

            @Override
            public void onError(HttpServerExchange exchange, ActionableEndpoint actionable) {
                onError(exchange, actionable);
            }

            @Override
            public ContentType getRequestContentType() {
                return getRequestContentType();
            }

            @Override
            public ContentType getResponseContentType() {
                return getResponseContentType();
            }
        };
    }

}
