package com.loadimpact.exception;

/**
 * Base class for failures that involve HTTP communications.
 *
 * @author jens
 */
public class HttpException extends ApiException {
    public HttpException() { }

    public HttpException(Throwable cause) {
        super(cause);
    }

    public HttpException(String message) {
        super(message);
    }
}
