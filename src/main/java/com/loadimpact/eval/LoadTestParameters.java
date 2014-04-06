package com.loadimpact.eval;

/**
 * The set of parameters needed for running a load test.
 *
 * @author jens
 */
public interface LoadTestParameters {

    /**
     * Returns the API Token used to connect to the LoadImpact REST web services.
     * @return its API Token
     */
    String getApiToken();

    /**
     * Returns the test-configuration ID of the load test to run.
     * @return its test-configuration id
     */
    int getTestConfigurationId();

    /**
     * Returns the set of configured thresholds or a zero-sized array.
     * @return its set of thresholds
     */
    Threshold[] getThresholds();

    /**
     * Returns the unit for the initial delay before evaluating all thresholds.
     * Used in combination with {@link #getDelayValue()}.
     * @return its delay-unit
     */
    DelayUnit getDelayUnit();

    /**
     * Returns the value for the initial delay before evaluating all thresholds.
     * Used in combination with {@link #getDelayUnit()}.
     * @return its delay value
     */
    int getDelayValue();

    /**
     * Returns the number of metric values to aggregate for the threshold evaluations.
     * @return its queue size
     */
    int getDelaySize();

    /**
     * Returns true if the load test should be aborted when a threshold triggers failure.
     * @return true if failure should abort the load test
     */
    boolean isAbortAtFailure();

    /**
     * Returns the number of seconds between each poll to the LoadImpact web service.
     * @return its poll interval
     */
    int getPollInterval();

    /**
     * Returns true if the HTTP invocations should be printed to the standard log stream.
     * @return true if show HTTP traffic
     */
    boolean isLogHttp();

    /**
     * Returns true if the reply object should be printed to teh standard log stream.
     * @return true if show replies
     */
    boolean isLogReplies();

    /**
     * Returns true if the plugin should print out debug messages.
     * @return true if debug mode
     */
    boolean isLogDebug();
    
}
