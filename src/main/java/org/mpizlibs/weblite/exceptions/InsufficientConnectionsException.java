package org.mpizlibs.weblite.exceptions;

public class InsufficientConnectionsException extends Exception {
    public InsufficientConnectionsException() {
        super("Insufficient connections to initialize: minimun 1");
    }
}
