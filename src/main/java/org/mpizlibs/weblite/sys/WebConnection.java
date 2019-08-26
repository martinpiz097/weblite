package org.mpizlibs.weblite.sys;

import org.mpizlibs.weblite.core.Connectable;
import org.mpizlibs.weblite.core.Endpoint;

public abstract class WebConnection implements Connectable {
    private final String remoteHost;
    private final int port;
    private final Endpoint endpoint;

    public WebConnection(String remoteHost, int port, Endpoint endpoint) {
        this.remoteHost = remoteHost;
        this.port = port;
        this.endpoint = endpoint;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public int getPort() {
        return port;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }
}
