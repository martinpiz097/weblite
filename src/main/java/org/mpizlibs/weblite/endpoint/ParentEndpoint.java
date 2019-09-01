package org.mpizlibs.weblite.endpoint;

import io.undertow.server.HttpServerExchange;
import org.apache.http.HttpStatus;
import org.mpizlibs.weblite.net.Executable;

import java.util.ArrayList;

public class ParentEndpoint extends Endpoint implements Executable {
    protected final ArrayList<ActionableEndpoint> listChilds;

    protected ActionableEndpoint finded;

    public ParentEndpoint(String path) {
        super(path);
        this.listChilds = new ArrayList<>();
    }

    public synchronized ActionableEndpoint getFinded() {
        return finded;
    }

    public synchronized void saveFinded(ActionableEndpoint finded) {
        this.finded = finded;
    }

    public synchronized int getChildCount() {
        return listChilds.size();
    }

    public synchronized boolean hasChilds() {
        return getChildCount() > 0;
    }

    public synchronized boolean addChild(ActionableEndpoint actionableEndpoint) {
        if (actionableEndpoint == null)
            throw new NullPointerException("Endpoint param is null");
        for (int i = 0; i < listChilds.size(); i++) {
            if (listChilds.get(i).getPath().equals(actionableEndpoint.getPath())) {
                return false;
            }
        }
        actionableEndpoint.setParent(this);
        listChilds.add(actionableEndpoint);
        return true;
    }

    public synchronized boolean removeEndpoint(String path) {
        for (int i = 0; i < listChilds.size(); i++) {
            if (listChilds.get(i).getPath().equals(path)) {
                listChilds.remove(i);
                return true;
            }
        }
        return false;
    }

    public synchronized String getPath() {
        return path;
    }

    public synchronized String getFullPath() {
        return path;
    }

    public synchronized boolean isEqualsPath(String path) {
        String fullPath = getFullPath();
        if (path.endsWith("/") && fullPath.length() > 1) {
            fullPath = fullPath.concat("/");
        }

        else if (!path.endsWith("/") && fullPath.endsWith("/")) {
            path = path.concat("/");
        }

        return fullPath.equals(path);
    }

    public synchronized ActionableEndpoint findByPath(String path) {
        ActionableEndpoint actionableEndpoint = listChilds
                .parallelStream()
                .filter(actionable -> actionable.isEqualsPath(path))
                .findFirst().orElse(null);
        finded = actionableEndpoint;
        return actionableEndpoint;
    }

    public synchronized ArrayList<ActionableEndpoint> getListChilds() {
        return listChilds;
    }

    public synchronized int execute(HttpServerExchange exchange) {
        final String requestPath = exchange.getRequestPath();
        final String requestMethod = exchange.getRequestMethod().toString();

        ActionableEndpoint resource = findByPath(requestPath);

        if (resource == null) {
            exchange.setStatusCode(HttpStatus.SC_NOT_FOUND);
            return HttpStatus.SC_NOT_FOUND;
        }
        else if (resource.getMethod().equals(requestMethod)){
            return 0;
        }
        else {
            exchange.setStatusCode(HttpStatus.SC_METHOD_NOT_ALLOWED);
            return HttpStatus.SC_METHOD_NOT_ALLOWED;
        }
    }

    @Override
    protected ParentEndpoint clone() {
        ParentEndpoint parentEndpoint = new ParentEndpoint(path);
        ArrayList<ActionableEndpoint> listChilds = getListChilds();

        for (ActionableEndpoint actionableEndpoint : listChilds) {
            parentEndpoint.addChild(actionableEndpoint.clone());
        }
        return parentEndpoint;
    }
}
