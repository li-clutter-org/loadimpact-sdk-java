package com.loadimpact.exception;

/**
 * Internal server error occurred while serving request (HTTP Response: 500).
 *
 * @author jens
 */
public class ServerException extends HttpException {
    public ServerException(Throwable cause) {
        super(cause);
    }

    public ServerException(String message) {
        super(message);
    }
}
