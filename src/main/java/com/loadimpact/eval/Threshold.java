package com.loadimpact.eval;

import com.loadimpact.resource.testresult.StandardMetricResult;
import com.loadimpact.util.ListUtils;

import java.util.List;

/**
 * Represents a single threshold and handles the metric value aggregations and threshold trigger evaluations.
 *
 * @author jens
 */
public class Threshold {
    /**
     * Threshold number (1,2,3,...)
     */
    private final int id;

    /**
     * The metric to check.
     */
    private final StandardMetricResult.Metrics metric;

    /**
     * The threshold to compare with.
     */
    private final int thresholdValue;

    /**
     * The result if exceeded
     */
    private final LoadTestResult result;

    /**
     * The comparison operator
     */
    private final Operator operator;

    /**
     * Contains the last N metric values
     */
    private final BoundedDroppingQueue<Integer> values;

    /**
     * Offset of the last bunch of metric values
     */
    private int lastOffset;

    /**
     * Last computed aggregated value
     */
    private int lastAggregatedValue;

    /**
     * True if the last evaluation exceeded the threshold
     */
    private boolean lastExceededValue;

    public Threshold(int id, StandardMetricResult.Metrics metric, Operator operator, int thresholdValue, LoadTestResult result) {
        this.id = id;
        this.metric = metric;
        this.operator = operator;
        this.thresholdValue = thresholdValue;
        this.result = result;
        this.values = new BoundedDroppingQueue<Integer>();
        this.lastOffset = -1;
    }

    public int getId() { return id; }

    public StandardMetricResult.Metrics getMetric() { return metric; }

    public LoadTestResult getResult() { return result; }

    /**
     * Returns the computed aggregated value (median) of the latest N metric values.
     *
     * @return the median of the N last values
     */
    public int getAggregatedValue() { return ListUtils.median(values.toList()); }

    /**
     * Adds a metric value to the metric value queue.
     *
     * @param metricValues
     *         bunch of values
     */
    public void accumulate(List<? extends StandardMetricResult> metricValues) {
        if (metricValues == null || metricValues.isEmpty()) return;
        for (StandardMetricResult v : metricValues) {
            if (lastOffset < v.offset) values.put(v.value.intValue());
        }
        lastOffset = ListUtils.last(metricValues).offset;
    }

    /**
     * Returns true of this threshold has been exceeded.
     *
     * @return true if exceeded
     */
    public boolean isExceeded() {
        lastAggregatedValue = getAggregatedValue();
        lastExceededValue = false;
        switch (operator) {
            case lessThan:
                lastExceededValue = (lastAggregatedValue < thresholdValue);
                break;
            case greaterThan: lastExceededValue = (lastAggregatedValue > thresholdValue);
                break;
        }
        return lastExceededValue;
    }

    @Override
    public String toString() {
        return "Threshold[id:" + id
                + ", metric:" + metric.name()
                + ", aggregatedValue:" + lastAggregatedValue
                + ", thresholdValue:" + thresholdValue
                + ", operator:" + operator.symbol
                + ", result=" + result
                + ", lastExceededValue:" + lastExceededValue
                + ", lastOffset:" + lastOffset
                + "]";
    }

    /**
     * Returns summary intended for logging.
     *
     * @return a summary
     */
    public String getReason() {
        if (lastExceededValue) {
            return String.format("Metric '%s' has aggregated-value=%d %s %d as threshold", metric.name(), lastAggregatedValue, operator.symbol, thresholdValue);
        } else {
            return String.format("Metric %s: aggregated-value=%d", metric.name(), lastAggregatedValue);
        }
    }
}
