package com.loadimpact.resource.configuration;

import javax.json.JsonObject;
import java.io.Serializable;

/**
* Container for JSON load_schedule.track[].clips[]
*
* @author jens
*/
public class LoadClip implements Serializable {
    public  int percent;
    public  int scenarioId;

    public LoadClip() { }

    public LoadClip(int percent, int scenarioId) {
        this.percent = percent;
        this.scenarioId = scenarioId;
    }

    public LoadClip(JsonObject json) {
        this(json.getInt("percent",0), json.getInt("user_scenario_id",0));
    }

    @Override
    public String toString() {
        return "Clip{" +
                "percent=" + percent +
                ", scenarioId=" + scenarioId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LoadClip loadClip = (LoadClip) o;

        if (percent != loadClip.percent) return false;
        if (scenarioId != loadClip.scenarioId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = percent;
        result = 31 * result + scenarioId;
        return result;
    }
}
