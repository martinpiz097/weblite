package org.mpizlibs.weblite.exceptions;

public class ServerNotInitializedException extends RuntimeException {
    public ServerNotInitializedException() {
        super("Server is not initialized: WebConfig unknown");
    }
}
