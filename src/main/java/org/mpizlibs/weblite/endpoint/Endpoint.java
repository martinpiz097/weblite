package org.mpizlibs.weblite.endpoint;

public abstract class Endpoint {
    protected String path;

    public Endpoint(String path) {
        this.path = path;
        normalizePath();
    }

    protected void normalizePath() {
        if (path.length() > 1 && path.endsWith("/"))
            path = path.substring(0, path.length()-1);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
