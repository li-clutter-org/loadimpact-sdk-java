package com.loadimpact.resource.testresult;

import javax.json.JsonObject;

/**
 * Custom metric results.
 *
 * @author jens
 */
public class CustomMetricResult extends AggregatedNumericResult {
    public static final String METRIC_ID_PREFIX = "__custom_";
    public final String type;

    public CustomMetricResult(JsonObject json) {
        super(json);
        type = json.getString("type", null);
    }
}
