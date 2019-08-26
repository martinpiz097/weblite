package org.mpizlibs.weblite.net;

import io.undertow.Undertow;
import org.mpizlibs.weblite.exceptions.InsufficientConnectionsException;
import org.mpizlibs.weblite.exceptions.ServerNotInitializedException;
import org.mpizlibs.weblite.exceptions.WebLiteException;
import org.mpizlibs.weblite.sys.WebConnection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WebService extends Thread {
    private Undertow server;
    private ArrayList<WebConnection> listConnections;
    private boolean connected;

    public WebService() {
        listConnections = new ArrayList<>();
        connected = false;
    }

    public WebService(WebConnection connection) {
        this();
        try {
            initialize(connection);
        } catch (WebLiteException e) {
        }
    }

    public void initialize(WebConnection connection) throws WebLiteException {
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

    public void initialize(WebConnection... connections) throws InsufficientConnectionsException {
        initialize(Arrays.asList(connections));
    }

    public void initialize(List<WebConnection> listConnections) throws InsufficientConnectionsException {
        if (listConnections.isEmpty()) {
            throw new InsufficientConnectionsException();
        }
        Undertow.Builder builder = Undertow.builder();

        for (WebConnection connection : listConnections) {
            builder.addHttpListener(connection.getPort(),
                    connection.getRemoteHost(),
                    connection::receivRequest);
        }
        this.listConnections.addAll(listConnections);
        server = builder.build();
    }

    public void shutdown() {
        server.stop();
        server = null;
        listConnections.clear();
        connected = false;
    }

    public ArrayList<WebConnection> getListConnections() {
        return listConnections;
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
