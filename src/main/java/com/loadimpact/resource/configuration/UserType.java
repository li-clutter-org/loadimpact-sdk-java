package com.loadimpact.resource.configuration;

/**
 * Enumerates all (simulated) user types.
 *
 * @author jens
 */
public enum UserType {
    SBU("Simulated Browser User"), 
    VU("Virtual User");

    public final String label;

    private UserType(String label) {
        this.label = label;
    }
}
