package org.mpizlibs.weblite.exceptions;

public class WebLiteException extends Exception {
    public WebLiteException() {
        super("Server is already started");
    }
}
