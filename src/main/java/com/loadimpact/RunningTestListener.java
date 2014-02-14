package com.loadimpact;

import com.loadimpact.exception.ApiException;
import com.loadimpact.resource.Test;

/**
 * Call-back interface that can be used during a running load-test using
 * {@link ApiTokenClient#monitorTest(int, int, RunningTestListener)}
 *
 * @author jens
 */
public interface RunningTestListener {

    /**
     * Invoked at every poll, during the test.
     * If the test should be aborted, for any reason, just throw {@link com.loadimpact.exception.AbortTest}
     * @param test      contain test status
     * @param client    active client to use, for fetching more data
     * @throws  com.loadimpact.exception.AbortTest   if the test should be aborted
     */
    void onProgress(Test test, ApiTokenClient client);

    /**
     * Invoked after a successful test.
     * @param test  last test status
     */
    void onSuccess(Test test);

    /**
     * Invoked after a failed test.
     * @param test  last test status
     */
    void onFailure(Test test);

    /**
     * Invoked after an error.
     * @param error     exception
     */
    void onError(ApiException error);

    /**
     * Invoked after a running test has been aborted.
     * Typically, because {@link #onProgress(com.loadimpact.resource.Test, ApiTokenClient)}
     * throw an {@link com.loadimpact.exception.AbortTest} exception.
     * Can be used for some cleanup.
     */
    void onAborted();
    
}
