package com.loadimpact.resource.testresult;

import javax.json.JsonObject;

/**
 * Server metric results.
 *
 * @author jens
 */
public class ServerMetricResult extends AggregatedNumericResult {
    public static final String METRIC_ID_PREFIX = "__server_metric_";
    public final String unit;
    public final double median;
    public final double standard_deviation;
    public final String label;

    public ServerMetricResult(JsonObject json) {
        super(json);
        unit = json.getString("unit", null);
        median = getDouble(json, "median", 0);
        standard_deviation = getDouble(json, "stddev", 0);
        label = json.getString("label", null);
    }

}
