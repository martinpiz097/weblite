package org.mpizlibs.weblite.net;

import io.undertow.io.Sender;
import io.undertow.server.HttpServerExchange;
import org.apache.http.HttpStatus;
import org.mpizlibs.weblite.endpoint.ParentEndpoint;

import java.util.ArrayList;

public class WebConfiguration implements Executable {
    private final String remoteHost;
    private final int port;
    private final ArrayList<ParentEndpoint> listParents;

    public WebConfiguration(String remoteHost, int port, ParentEndpoint parentEndpoint) {
        this.remoteHost = remoteHost;
        this.port = port;
        listParents = new ArrayList<>();
        listParents.add(parentEndpoint);
    }

    public WebConfiguration(int port, ParentEndpoint parentEndpoint) {
        this("0.0.0.0", port, parentEndpoint);
    }

    public WebConfiguration(String remoteHost, int port,
                            ArrayList<ParentEndpoint> listParents) {
        this.remoteHost = remoteHost;
        this.port = port;
        this.listParents = listParents;
    }

    public WebConfiguration(String remoteHost, int port) {
        this.remoteHost = remoteHost;
        this.port = port;
        listParents = new ArrayList<>();
    }
    
    public synchronized String getRemoteHost() {
        return remoteHost;
    }

    public synchronized int getPort() {
        return port;
    }

    public synchronized ParentEndpoint findByPath(String path) {
        return listParents
                .parallelStream()
                .filter(parent->parent.getPath().startsWith(path))
                .findFirst()
                .orElse(null);
    }

    public synchronized void addParent(ParentEndpoint parentEndpoint) {
        if (!listParents
                .parallelStream()
                .anyMatch(p->p.getPath().equals(parentEndpoint.getPath()))) {
            listParents.add(parentEndpoint);
        }
    }

    public synchronized ArrayList<ParentEndpoint> getListParents() {
        return listParents;
    }

    @Override
    public synchronized int execute(HttpServerExchange exchange) {
        String requestPath = exchange.getRequestPath();
        String requestMethod = exchange.getRequestMethod().toString();

        int notFounds = 0;
        int notAlloweds = 0;

        ParentEndpoint parent = null;
        for (ParentEndpoint parentEndpoint : listParents) {
            if (parentEndpoint.execute(exchange) == 0) {
                parent = parentEndpoint;
                break;
            }
            else if (parentEndpoint.execute(exchange) == HttpStatus.SC_NOT_FOUND) {
                notFounds++;
            }
            else if (parentEndpoint.execute(exchange) == HttpStatus.SC_METHOD_NOT_ALLOWED){
                notAlloweds++;
            }
        }

        if (parent == null) {
            Sender sender = exchange.getResponseSender();
            if (notFounds > 0) {
                // este mensaje despues puede y debe cambiar en la
                // forma que se genera
                sender.send("Resource in path "+requestPath+" not found!");
            }
            else if (notAlloweds > 0) {
                sender.send("Method "+requestMethod+" not allowed in " +
                        "path "+requestPath);
            }
        }
        else {
            parent.getFinded().execute(exchange);
        }
        return 0;
    }

}
