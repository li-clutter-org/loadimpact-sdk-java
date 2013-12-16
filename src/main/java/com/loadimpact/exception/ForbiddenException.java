package com.loadimpact.exception;

import javax.ws.rs.WebApplicationException;

/**
 * Not allowed to access requested resource (HTTP Response: 403).
 *
 * @author jens
 */
public class ForbiddenException extends ClientException {
    public ForbiddenException(String operation, String id, String action, Exception x) {
        super(operation, id, action, x);
    }
}
