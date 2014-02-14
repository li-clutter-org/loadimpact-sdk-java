package com.loadimpact.exception;

/**
 * Request was rate limited (HTTPS Response: 427).
 *
 * @author jens
 * @see <a href="http://developers.loadimpact.com/api/#api-ref-rate-limiting">Rate limiting</a>
 */
public class RateLimitedException extends ClientException {
    public RateLimitedException(String operation, String id, String action) {
        super(operation, id, action);
    }
}
