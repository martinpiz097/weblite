package org.mpizlibs.weblite.exceptions;

public class InvalidEndpointException extends RuntimeException {
    public InvalidEndpointException(String path) {
        super("EndpointCallback in path "+path+" is null");
    }
}
