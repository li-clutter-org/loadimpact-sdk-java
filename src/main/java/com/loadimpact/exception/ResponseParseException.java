package com.loadimpact.exception;

import javax.ws.rs.WebApplicationException;

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
        
    }

    public ResponseParseException(String message) {
        
    }
}
