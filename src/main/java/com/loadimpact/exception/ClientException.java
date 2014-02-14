package com.loadimpact.exception;

/**
 * Base class for failures that has its origin in faulty client usage. 
 *
 * @author jens
 */
public class ClientException extends HttpException {
    public final String operation;
    public final String id;
    public final String action;

    public ClientException(Throwable cause) {
        super(cause);
        this.operation = null;
        this.id = null;
        this.action = null;
    }

    public ClientException(String message) {
        super(message);
        this.operation = null;
        this.id = null;
        this.action = null;
    }

    public ClientException(String operation, String id, String action, Exception x) {
        super(x);
        this.operation = operation;
        this.id = id;
        this.action = action;
    }
    
    public ClientException(String operation, String id, String action) {
        this.operation = operation;
        this.id = id;
        this.action = action;
    }
}
