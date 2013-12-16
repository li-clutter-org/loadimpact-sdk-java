package com.loadimpact.exception;

import javax.ws.rs.WebApplicationException;

/**
 * Request was well formatted but contained semantic errors, see documentation for specific resources below for details (HTTP Response: 422).
 *
 * @author jens
 * @see <a href="http://developers.loadimpact.com/api/#api-ref-error-handling">API documentation</a>
 */
public class CoercionException extends ApiException {
    public CoercionException(String operation, String id, String action, Exception x) {
        
    }
}
