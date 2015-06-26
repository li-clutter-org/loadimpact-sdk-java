package com.loadimpact.util;

import com.loadimpact.eval.DelayUnit;
import com.loadimpact.eval.LoadTestResult;
import com.loadimpact.eval.Operator;
import com.loadimpact.resource.testresult.StandardMetricResult;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Container for a set of parameters, plus some convenience methods.
 *
 * @author jens
 */
public class Parameters {
    private Map<String, String> parameters;
    private static final String NULL_String = null;

    public Parameters() {
        this.parameters = new TreeMap<String, String>();
    }

    public Parameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public int size() {
        return parameters.size();
    }

    /**
     * Adds a parameter
     *
     * @param key   its key
     * @param value its value
     */
    public void add(String key, String value) {
        parameters.put(key, value);
    }

    /**
     * Returns all keys.
     *
     * @return set of keys
     */
    public Set<String> keys() {
        return parameters.keySet();
    }

    /**
     * Returns all keys matching the given pattern.
     *
     * @param pattern key pattern
     * @return set of keys
     */
    public Set<String> keys(String pattern) {
        Set<String> result = new TreeSet<String>();
        for (String key : keys()) {
            if (key.matches(pattern)) result.add(key);
        }
        return result;
    }

    /**
     * Returns true if key is a member.
     *
     * @param key key to check
     * @return true if member
     */
    public boolean has(String key) {
        return parameters.containsKey(key);
    }

    /**
     * Returns the value associated with the key, or the given default value.
     *
     * @param key          key to find
     * @param defaultValue if not found
     * @return value
     */
    public String get(String key, String defaultValue) {
        String value = parameters.get(key);
        return StringUtils.isBlank(value) ? defaultValue : value;
    }

    public int get(String key, int defaultValue) {
        try {
            return Integer.parseInt(get(key, NULL_String));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public boolean get(String key, boolean defaultValue) {
        try {
            return Boolean.parseBoolean(get(key, NULL_String));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public float get(String key, float defaultValue) {
        try {
            return Float.parseFloat(get(key, NULL_String));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public StandardMetricResult.Metrics get(String key, StandardMetricResult.Metrics defaultValue) {
        try {
            return StandardMetricResult.Metrics.valueOf(get(key, defaultValue.name()).toUpperCase());
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public DelayUnit get(String key, DelayUnit defaultValue) {
        return DelayUnit.valueOf(get(key, defaultValue.name()));
    }

    public Operator get(String key, Operator defaultValue) {
        return Operator.valueOf(get(key, defaultValue.name()));
    }

    public LoadTestResult get(String key, LoadTestResult defaultValue) {
        return LoadTestResult.valueOf(get(key, defaultValue.name()));
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(10000);
        buf.append(String.format("--- Load Test Parameters ---%n"));
        for (Map.Entry<String, String> e : parameters.entrySet()) {
            buf.append(String.format("  %s=%s%n", e.getKey(), e.getValue()));
        }
        buf.append(String.format("--- END ---%n"));
        return buf.toString();
    }
}
