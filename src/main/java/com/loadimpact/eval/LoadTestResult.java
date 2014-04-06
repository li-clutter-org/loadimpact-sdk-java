package com.loadimpact.eval;

import com.loadimpact.util.StringUtils;

/**
 * Models the outcome of a load test.
 *
 * @author jens
 */
public enum LoadTestResult {
    aborted, unstable, failed, error;
    
    private final String id;
    private final String displayName;

    {
        this.id = name();
        this.displayName = StringUtils.toInitialCase(name());
    }
    
    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }
    
}
