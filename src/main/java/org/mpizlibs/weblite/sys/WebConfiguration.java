package org.mpizlibs.weblite.sys;

import org.mpizlibs.weblite.core.Endpoint;
import org.mpizlibs.weblite.core.WebConfigurationCallback;

public abstract class WebConfiguration implements WebConfigurationCallback {
    private final String remoteHost;
    private final int port;
    private final Endpoint endpoint;

    public WebConfiguration(String remoteHost, int port, Endpoint endpoint) {
        this.remoteHost = remoteHost;
        this.port = port;
        this.endpoint = endpoint;
    }

    public WebConfiguration(int port, Endpoint endpoint) {
        this("0.0.0.0", port, endpoint);
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public int getPort() {
        return port;
    }

    public Endpoint findByPath(String path) {
        return endpoint.findByPathRecursive(path);
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

}
