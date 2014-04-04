package com.loadimpact.resource.testresult;

import javax.json.JsonNumber;
import javax.json.JsonObject;

/**
 * Base class for all Standard metric results.
 *
 * @author jens
 */
public class StandardMetricResult extends Result {
    private static final String VALUE   = "value";
    private static final String AVG     = "avg";
    private static final String PERCENT = "percent";

    public enum Metrics {
        ACCUMULATED_LOAD_TIME(VALUE),
        BANDWIDTH(AVG, false),
        CLIENTS_ACTIVE(VALUE, true),
        CONNECTIONS_ACTIVE(VALUE, true),
        CONTENT_TYPE(ContentTypeStandardMetricResult.class),
        CONTENT_TYPE_LOAD_TIME(AVG, false, ContentTypeLoadTimeStandardMetricResult.class),
        FAILURE_RATE(AVG),
        LIVE_FEEDBACK(LiveFeedbackStandardMetricResult.class),
        LOADGEN_CPU_UTILIZATION(VALUE),
        LOADGEN_MEMORY_UTILIZATION(VALUE),
        LOG(LogStandardMetricResult.class),
        PROGRESS_PERCENT_TOTAL(VALUE),
        REPS_FAILED_PERCENT(PERCENT),
        REPS_SUCCEEDED_PERCENT(PERCENT),
        REQUESTS_PER_SECOND(AVG),
        TOTAL_RX_BYTES(VALUE, true),
        TOTAL_REQUESTS(VALUE, true),
        USER_LOAD_TIME(VALUE);

        /**
         * REST query parameter name.
         */
        public final String id;

        /**
         * Name of value-field(in json response), such as 'value' or 'avg'
         */
        public final String valueName;

        /**
         * If true for int, else float.
         */
        public final Boolean integral;

        public final Class<? extends StandardMetricResult> resultType;

        Metrics(Class<? extends StandardMetricResult> resultType) {
            this(null, null, resultType);
        }
        
        Metrics(String valueName) {
            this(valueName, false, StandardMetricResult.class);
        }
        
        Metrics(String valueName, Boolean integral) {
            this(valueName, integral, StandardMetricResult.class);
        }
        
        Metrics(String valueName, Boolean integral, Class<? extends StandardMetricResult> resultType) {
            this.valueName = valueName;
            this.integral = integral;
            this.resultType = resultType;
            this.id = PREFIX + name().toLowerCase();
        }


        @Override
        public String toString() {
            return String.format("%s:%s:%s", name(), valueName != null ? valueName : "*", integral != null ? integral : "*");
        }
    }

    public final Metrics metric;
    public final Number  value;
    public final Number  count;

    public StandardMetricResult(Metrics m, JsonObject json) {
        super(json);
        metric = m;

        JsonNumber jsonNumber = json.getJsonNumber(this.metric.valueName);
        if (jsonNumber != null) {
            value = metric.integral ? jsonNumber.longValue() : jsonNumber.doubleValue();
        } else {
            value = null;
        }

        if (metric.valueName != null && metric.valueName.equals(PERCENT)) {
            count = json.getInt("value", 0);
        } else if (metric.equals(Metrics.CONTENT_TYPE_LOAD_TIME)) {
            count = json.getInt("count", 0);
        } else {
            count = null;
        } 
    }

    @Override
    protected StringBuilder toString(StringBuilder buf) {
        return super.toString(buf).append(", ")
                .append("metric=").append(metric).append(", ")
                .append("value=").append(value).append(", ")
                .append("count=").append(count)
                ;
    }
}
