package com.loadimpact.resource.testresult;

import javax.json.JsonObject;

/**
 * Special value class class for content-type load-time results.
 *
 * @author jens
 */
public class ContentTypeLoadTimeStandardMetricResult extends StandardMetricResult {
    public final String type;
    public final Double minimum;
    public final Double maximum;

    public ContentTypeLoadTimeStandardMetricResult(Metrics m, JsonObject json) {
        super(m, json);
        
        type = json.getString("content_type", null);
        minimum = getDouble(json, "min", 0);
        maximum = getDouble(json, "max", 0);
    }

    @Override
    protected StringBuilder toString(StringBuilder buf) {
        return super.toString(buf).append(", ")
                    .append("type=").append(type).append(", ")
                    .append("minimum=").append(minimum).append(", ")
                    .append("maximum=").append(maximum)
                ;
    }
    
}
