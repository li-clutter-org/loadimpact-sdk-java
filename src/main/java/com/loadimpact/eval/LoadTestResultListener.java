package com.loadimpact.eval;

/**
 * Listener for the results-processing of a load test.
 *
 * @author jens
 */
public interface LoadTestResultListener {

    void markAs(LoadTestResult result, String reason);

    LoadTestResult getResult();

    String getReason();

    void stopBuild();

    boolean isFailure();

    boolean isNonSuccessful();
}
