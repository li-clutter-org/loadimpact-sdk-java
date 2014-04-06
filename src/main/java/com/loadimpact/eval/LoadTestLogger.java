package com.loadimpact.eval;

/**
 * Wrapper around the platform logger.
 *
 * @author jens
 */
public interface LoadTestLogger {

    void started(String msg);
    
    void finished(String msg);

    void message(String msg);
    
    void message(String fmt, Object... args);

    void failure(String reason);
}
