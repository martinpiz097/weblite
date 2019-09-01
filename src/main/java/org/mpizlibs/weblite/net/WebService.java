package org.mpizlibs.weblite.net;

import io.undertow.Undertow;
import io.undertow.server.RoutingHandler;
import org.mpizlibs.weblite.exceptions.ServerNotInitializedException;
import org.mpizlibs.weblite.exceptions.WebLiteException;

import java.util.concurrent.Exchanger;

public class WebService extends Thread {
    private Undertow server;
    private WebConfiguration configuration;
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

    public WebService(String remoteHost, int port) {
        this();
        configuration = new WebConfiguration(remoteHost, port);
        try {
            initialize(configuration);
        } catch (WebLiteException e) {
            e.printStackTrace();
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
        configuration = null;
        connected = false;
    }

    public WebConfiguration getConfiguration() {
        return configuration;
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
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
