package com.loadimpact.resource;

import java.util.Arrays;
import java.util.List;

/**
 * Enumerates all possible status types during a load test.
 *
 * @author jens
 */
public enum Status {
    CREATED(-1), QUEUED(0), INITIALIZING(1), RUNNING(2), FINISHED(3), TIMED_OUT(4),
    ABORTING_BY_USER(5), ABORTED_BY_USER(6), ABORTING_BY_SYSTEM(7), ABORTED_BY_SYSTEM(8), FAILED(99);

    private static List<Status> WARMING_UP    = Arrays.asList(CREATED, QUEUED, INITIALIZING);
    private static List<Status> NOT_COMPLETED = Arrays.asList(CREATED, QUEUED, INITIALIZING, RUNNING);
    private static List<Status> ABORTED       = Arrays.asList(ABORTING_BY_USER, ABORTED_BY_USER, ABORTING_BY_SYSTEM, ABORTED_BY_SYSTEM);

    final int code;

    Status(int code) {
        this.code = code;
    }

    public boolean isWarmingUp() {
        return WARMING_UP.contains(this);
    }

    public boolean isInProgress() {
        return NOT_COMPLETED.contains(this);
    }
    
    public boolean isCompleted() {
        return !isInProgress();
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
//        return values()[status + 1];
        for (Status s : values()) {
            if (s.code == status) return s;
        }
        throw new IllegalArgumentException("unknown status code " + status);
    }
}
