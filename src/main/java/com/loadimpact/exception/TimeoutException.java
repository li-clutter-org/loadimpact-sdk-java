package com.loadimpact.exception;

/**
 * Too long time.
 *
 * @author jens
 */
public class TimeoutException extends ApiException {
    public TimeoutException(Throwable cause) {
        super(cause);
    }
}
