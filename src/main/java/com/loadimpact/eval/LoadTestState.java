package com.loadimpact.eval;

import com.loadimpact.resource.Status;

import java.util.Arrays;

/**
 * Tracks the state transitions for progress monitoring.
 *
 * @author jens
 */
@SuppressWarnings("UnusedDeclaration")
public enum LoadTestState {
    notStarted, initializing, warmingUp, checkingThresholds, finishing, terminated;

    public boolean isBeforeCheckingThresholds() {
        return this == warmingUp;
    }

    public boolean isCheckingThresholds() {
        return this == checkingThresholds;
    }

    public boolean isActive() {
        return Arrays.asList(warmingUp, checkingThresholds).contains(this);
    }

    public LoadTestState moveToNext(Status status) {
        return moveToNext(status, false);
    }

    public LoadTestState moveToNext(Status status, boolean shouldTransition) {
        if (status == Status.CREATED || status == Status.QUEUED) return notStarted;
        if (status == Status.INITIALIZING) return initializing;
        if (status == Status.RUNNING && (this == notStarted || this == initializing)) return warmingUp;

        if (status == Status.RUNNING && this == warmingUp && shouldTransition) return checkingThresholds;
        if (status == Status.RUNNING && this == checkingThresholds && shouldTransition) return finishing;
        if (status.isCompleted()) return terminated;

        return this;
    }

}
