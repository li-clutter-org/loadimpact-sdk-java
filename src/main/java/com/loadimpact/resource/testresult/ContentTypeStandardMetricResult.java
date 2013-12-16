package com.loadimpact.resource.testresult;

import javax.json.JsonObject;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * Special value class class for content-type results.
 *
 * @author jens
 */
public class ContentTypeStandardMetricResult extends StandardMetricResult {
    public final Map<String, Integer> types;

    public ContentTypeStandardMetricResult(Metrics m, JsonObject json) {
        super(m, json);

        Map<String, Integer> map = new TreeMap<String, Integer>();
        JsonObject content_type = json.getJsonObject("content_type");
        for (String type : content_type.keySet()) {
            map.put(type, content_type.getInt(type, 0));
        }
        types = Collections.unmodifiableMap(map);
    }

    @Override
    protected StringBuilder toString(StringBuilder buf) {
        return super.toString(buf).append(", ")
                    .append("types=").append(types)
                ;
    }

}
