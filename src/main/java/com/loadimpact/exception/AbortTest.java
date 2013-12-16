package com.loadimpact.exception;

/**
 * When thrown from 
 * {@link com.loadimpact.RunningTestListener#onProgress(com.loadimpact.resource.Test, com.loadimpact.ApiTokenClient)}
 * of a running test monitored by
 * {@link com.loadimpact.ApiTokenClient#monitorTest(int, int, com.loadimpact.RunningTestListener)}
 * the test will be aborted.
 *
 * @author jens
 */
public class AbortTest extends ApiException {
    public AbortTest() {
        super("Aborted by program");
    }
}
