package org.mpizlibs.weblite.net;

import io.undertow.Undertow;
import org.mpizlibs.weblite.exceptions.ServerNotInitializedException;
import org.mpizlibs.weblite.exceptions.WebLiteException;

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

    public void initialize(WebConfiguration configuration) throws WebLiteException {
        if (configuration == null) {
            throw new NullPointerException("Web configuration is null");
        }
        if (connected) {
            throw new WebLiteException();
        }
        server = Undertow.builder()
                .addHttpListener(configuration.getPort(),
                        configuration.getRemoteHost(),
                        configuration::execute)
                .setBufferSize(10240)
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
