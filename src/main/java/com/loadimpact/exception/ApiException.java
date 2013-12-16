package com.loadimpact.exception;

/**
 * Base class of all (run-time) exceptions thrown by {@link com.loadimpact.ApiTokenClient}.
 *
 * @author jens
 */
public class ApiException extends RuntimeException {
    public ApiException() { }

    public ApiException(Throwable cause) {
        super(cause);
    }

    public ApiException(String message) {
        super(message);
    }
}
