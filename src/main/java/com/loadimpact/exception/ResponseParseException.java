package com.loadimpact.exception;

/**
 * Something went wrong (HTTP Response: 429).
 *
 * @author jens
 */
public class ResponseParseException extends ApiException {
    public ResponseParseException(String operation, int id, String action, Exception x) {
        this(operation, Integer.toString(id), action, x);
    }
    
    public ResponseParseException(String operation, String id, String action, Exception x) {
        this(String.format("op=%s, id=%s, action=%s: %s", operation, id, action, x.toString()));
    }

    public ResponseParseException(String message) {
        super(message);
    }
}
