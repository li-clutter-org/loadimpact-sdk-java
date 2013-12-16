package com.loadimpact.exception;

import javax.ws.rs.WebApplicationException;

/**
 * Request was badly formatted (HTTP Response: 400).
 *
 * @author jens
 */
public class BadRequestException extends ClientException {
    public BadRequestException(String operation, String id, String action, Exception x) {
        super(operation, id, action, x);
    }
}
