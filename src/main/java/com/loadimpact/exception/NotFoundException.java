package com.loadimpact.exception;

/**
 * Requested resource does not exist (HTTP Response: 404)
 *
 * @author jens
 */
public class NotFoundException extends ClientException {
    public NotFoundException(String operation, String id) {
        super(operation, id, null);
    }
}
