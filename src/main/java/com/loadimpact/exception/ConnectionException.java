package com.loadimpact.exception;

/**
 * Communication error.
 *
 * @author jens
 */
public class ConnectionException extends ApiException {
    public ConnectionException(Throwable cause) {
        super(cause);
    }
}
