package com.loadimpact.exception;

import javax.ws.rs.WebApplicationException;

/**
 * Not allowed to access requested resource (HTTP Response: 403).
 *
 * @author jens
 */
public class UnauthorizedException extends ClientException {
    public UnauthorizedException(String operation, String id, String action) {
        super(operation, id, action);
    }
}
