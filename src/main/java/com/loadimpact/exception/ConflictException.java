package com.loadimpact.exception;

import javax.ws.rs.WebApplicationException;

/**
 * Request could not be processed because of a conflict, see API documentation for specific resources below for
 * details (HTTP Response: 409).
 *
 * @author jens
 * @see <a href="http://developers.loadimpact.com/api/#api-ref-error-handling">API documentation</a>
 */
public class ConflictException extends ClientException {
    public ConflictException(String operation, String id, String action, Exception x) {
        super(operation, id, action, x);
    }
}
