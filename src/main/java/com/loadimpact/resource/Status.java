package com.loadimpact.resource;

import java.util.Arrays;
import java.util.List;

/**
 * Enumerates all possible status types during a load test.
 *
 * @author jens
 */
public enum Status {
    CREATED, QUEUED, INITIALIZING, RUNNING, FINISHED, TIMED_OUT,
    ABORTING_BY_USER, ABORTED_BY_USER, ABORTING_BY_SYSTEM, ABORTED_BY_SYSTEM, FAILED;

    private static List<Status> NOT_COMPLETED = Arrays.asList(CREATED, QUEUED, INITIALIZING, RUNNING);
    private static List<Status> ABORTED       = Arrays.asList(ABORTING_BY_USER, ABORTED_BY_USER, ABORTING_BY_SYSTEM, ABORTED_BY_SYSTEM);

    public boolean isInProgress() {
        return NOT_COMPLETED.contains(this);
    }

    public boolean isAborted() {
        return ABORTED.contains(this);
    }

    public boolean isRunning() {
        return this == RUNNING;
    }

    public boolean isSuccessful() {
        return this == FINISHED;
    }

    public static Status valueOf(int status) {
        return values()[status + 1];
    }
}
