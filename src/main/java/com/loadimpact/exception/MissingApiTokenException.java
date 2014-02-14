package com.loadimpact.exception;

/**
 * Missing or invalid API Token.
 *
 * @author jens
 */
public class MissingApiTokenException extends ApiException {
    public MissingApiTokenException() {
        super();
    }
    public MissingApiTokenException(String message) {
        super(message);
    }
}
