package org.mpizlibs.weblite.exceptions;

public class MethodNotAllowedException extends Exception {
    public MethodNotAllowedException(String source) {
        super("The request method "+source.toUpperCase()+" is not allowed in endpoint");
    }
}
