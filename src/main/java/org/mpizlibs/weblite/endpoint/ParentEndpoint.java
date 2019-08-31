package org.mpizlibs.weblite.endpoint;

import io.undertow.server.HttpServerExchange;
import org.apache.http.HttpStatus;
import org.mpizlibs.weblite.net.Executable;

import java.util.ArrayList;

public class ParentEndpoint implements Executable {
    protected String path;
    protected final ArrayList<ActionableEndpoint> listChilds;

    protected ActionableEndpoint finded;

    public ParentEndpoint(String path) {
        this.path = path;
        this.listChilds = new ArrayList<>();

        normalizePath();
    }

    public ActionableEndpoint getFinded() {
        return finded;
    }

    public void saveFinded(ActionableEndpoint finded) {
        this.finded = finded;
    }

    protected void normalizePath() {
        if (path.length() > 1 && path.endsWith("/"))
            path = path.substring(0, path.length()-1);
    }

    public int getChildCount() {
        return listChilds.size();
    }

    public boolean hasChilds() {
        return getChildCount() > 0;
    }

    public boolean addChild(ActionableEndpoint actionableEndpoint) {
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

    public boolean removeEndpoint(String path) {
        for (int i = 0; i < listChilds.size(); i++) {
            if (listChilds.get(i).getPath().equals(path)) {
                listChilds.remove(i);
                return true;
            }
        }
        return false;
    }

    public String getPath() {
        return path;
    }

    public String getFullPath() {
        return path;
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

    public ActionableEndpoint findByPath(String path) {
        ActionableEndpoint actionableEndpoint = listChilds
                .parallelStream()
                .filter(actionable -> actionable.isEqualsPath(path))
                .findFirst().orElse(null);
        finded = actionableEndpoint;
        return actionableEndpoint;
    }

    public ArrayList<ActionableEndpoint> getListChilds() {
        return listChilds;
    }

    public int execute(HttpServerExchange exchange) {
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

}
