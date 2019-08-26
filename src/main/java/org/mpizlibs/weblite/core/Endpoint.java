package org.mpizlibs.weblite.core;

import org.apache.http.entity.ContentType;

import java.util.ArrayList;

public class Endpoint {
    private final String method;
    private String path;
    private final String contentType;

    private Endpoint parent;
    private final ArrayList<Endpoint> listEndpoints;

    public Endpoint(String method, String path, String contentType) {
        this(method, path, contentType, null);
    }

    public Endpoint(String method, String path, ContentType contentType) {
        this(method, path, contentType, null);
    }

    public Endpoint(String method, String path, String contentType, Endpoint parent) {
        this.method = method.toUpperCase().trim();
        this.path = path;
        this.parent = parent;
        this.contentType = contentType;
        listEndpoints = new ArrayList<>();

        normalizePath();
    }

    public Endpoint(String method, String path, ContentType contentType, Endpoint parent) {
        this.method = method.toUpperCase().trim();
        this.path = path;
        this.parent = parent;
        this.contentType = contentType.getMimeType();
        listEndpoints = new ArrayList<>();

        normalizePath();
    }

    private void normalizePath() {
        if (path.length() > 1 && path.endsWith("/"))
            path = path.substring(0, path.length()-1);
    }

    public int getChildCount() {
        return listEndpoints.size();
    }

    public boolean hasChilds() {
        return getChildCount() > 0;
    }

    public boolean addChildEndpoint(Endpoint endpoint) {
        if (endpoint == null)
            throw new NullPointerException("Endpoint param is null");
        for (int i = 0; i < listEndpoints.size(); i++) {
            if (listEndpoints.get(i).getPath().equals(endpoint.getPath())) {
                return false;
            }
        }
        endpoint.setParent(this);
        listEndpoints.add(endpoint);
        return true;
    }

    public boolean removeEndpoint(String path) {
        for (int i = 0; i < listEndpoints.size(); i++) {
            if (listEndpoints.get(i).getPath().equals(path)) {
                listEndpoints.remove(i);
                return true;
            }
        }
        return false;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getFullPath() {
        if (parent == null) {
            return path;
        }
        else {
            String pathClone = path;
            String fullPath = parent.getFullPath();

            // parent termina en /
            if (fullPath.charAt(fullPath.length()-1) == '/') {
                if (pathClone.startsWith("/"))
                    if (pathClone.length() > 1)
                        pathClone = pathClone.substring(1);
                    else
                        pathClone = "";
            }
            else if (!pathClone.startsWith("/")){
                pathClone = "/".concat(pathClone);
            }

            return fullPath.concat(pathClone);
        }
    }

    public String getContentType() {
        return contentType;
    }

    public Endpoint getParent() {
        return parent;
    }

    public void setParent(Endpoint parent) {
        this.parent = parent;
    }

    public boolean isValidMethod(String method) {
        return this.method.equals(method);
    }

    public Endpoint findByPath(String path) {
        String fullPath = getFullPath();

        // si el parametro termina en / y el path actual
        // no corresponde a raiz entonces se agraga un / al final
        // de esta path
        if (path.endsWith("/") && fullPath.length() > 1) {
            fullPath = fullPath.concat("/");
        }

        // la segunda condicion se deja en caso de que se agreguen
        // dos // al final cuando se crea el endpoint
        else if (!path.endsWith("/") && fullPath.endsWith("/")) {
            path = path.concat("/");
        }

        if (fullPath.equals(path))
            return this;
        else if (hasChilds()) {
            Endpoint finded = null;
            for (Endpoint child : listEndpoints) {
                finded = child.findByPath(path);
                if (finded != null) {
                    break;
                }
            }
            return finded;
        }
        else
            return null;
    }

    public ArrayList<Endpoint> getListEndpoints() {
        return listEndpoints;
    }

}
