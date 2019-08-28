package org.mpizlibs.weblite.net;

import io.undertow.Undertow;
import org.mpizlibs.weblite.exceptions.ServerNotInitializedException;
import org.mpizlibs.weblite.exceptions.WebLiteException;
import org.mpizlibs.weblite.sys.WebConfiguration;

public class WebService extends Thread {
    private Undertow server;
    private WebConfiguration connection;
    private boolean connected;

    public WebService() {
        connected = false;
    }

    public WebService(WebConfiguration configuration) {
        this();
        try {
            initialize(configuration);
        } catch (WebLiteException e) {
        }
    }

    public void initialize(WebConfiguration connection) throws WebLiteException {
        if (connection == null) {
            throw new NullPointerException("Web connection is null");
        }
        if (connected) {
            throw new WebLiteException();
        }
        server = Undertow.builder()
                .addHttpListener(connection.getPort(),
                        connection.getRemoteHost(),
                        connection::receivRequest)
                .build();
    }

    public void shutdown() {
        server.stop();
        server = null;
        connection = null;
        connected = false;
    }

    public WebConfiguration getConnection() {
        return connection;
    }

    public boolean isConnected() {
        return connected;
    }

    @Override
    public void run() {
        if (server == null)
            throw new ServerNotInitializedException();
        server.start();
        connected = true;

        while (connected) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
