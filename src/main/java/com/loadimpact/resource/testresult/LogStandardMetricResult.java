package com.loadimpact.resource.testresult;

import com.loadimpact.resource.LoadZone;

import javax.json.JsonObject;

/**
 * Special value class class for log results.
 *
 * @author jens
 */
public class LogStandardMetricResult extends StandardMetricResult {
    public final LoadZone zone;
    public final Integer   scenarioId;
    public final String    level;
    public final String    message;


    public LogStandardMetricResult(Metrics m, JsonObject json) {
        super(m, json);

        zone = LoadZone.valueOf(json.getInt("load_zone_id", 1));
        scenarioId = json.getInt("user_scenario_id", 0);
        level = json.getString("level", null);
        message = json.getString("message", null);
    }

    @Override
    protected StringBuilder toString(StringBuilder buf) {
        return super.toString(buf).append(", ")
                    .append("zone=").append(zone).append(", ")
                    .append("scenario=").append(scenarioId).append(", ")
                    .append("level=").append(level).append(", ")
                    .append("message=").append(message)
                ;
    }
    
}
