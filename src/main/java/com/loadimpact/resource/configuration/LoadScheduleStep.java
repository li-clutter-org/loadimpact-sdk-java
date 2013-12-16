package com.loadimpact.resource.configuration;

import javax.json.JsonObject;
import java.io.Serializable;

/**
 * Container for JSON load_schedule steps.
 *
 * @author jens
 */
public class LoadScheduleStep implements Serializable {
    public  int duration;
    public  int users;

    public LoadScheduleStep() { }

    public LoadScheduleStep(int duration, int users) {
        this.duration = duration;
        this.users = users;
    }

    public LoadScheduleStep(JsonObject json) {
        this(json.getInt("duration",0), json.getInt("users",0));
    }

    @Override
    public String toString() {
        return "LoadScheduleStep{" +
                "duration=" + duration +
                ", users=" + users +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LoadScheduleStep that = (LoadScheduleStep) o;

        if (duration != that.duration) return false;
        if (users != that.users) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = duration;
        result = 31 * result + users;
        return result;
    }
}
