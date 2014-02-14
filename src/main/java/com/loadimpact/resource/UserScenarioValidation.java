package com.loadimpact.resource;

import com.loadimpact.util.DateUtils;

import javax.json.JsonObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Container for a scenario-validation.
 *
 * @author jens
 */
public class UserScenarioValidation implements Serializable {
    public enum Status {
        QUEUED, INITIALIZING, RUNNING, FINISHED, FAILED
    }

    public static class Result implements Serializable {
        public Date   timestamp;
        public int    type;
        public int    offset;
        public String message;

        public Result(Date timestamp, int type, int offset, String message) {
            this.timestamp = timestamp;
            this.type = type;
            this.offset = offset;
            this.message = message;
        }

        public Result(JsonObject json) {
            this.timestamp = DateUtils.toDateFromIso8601(json.getString("timestamp", null));
            this.type = json.getInt("type", 0);
            this.offset = json.getInt("offset", 0);
            this.message = json.getString("message", null);
        }

        @Override
        public String toString() {
            return "Result{" +
                    "timestamp=" + timestamp +
                    ", type=" + type +
                    ", offset=" + offset +
                    ", message='" + message + '\'' +
                    '}';
        }
    }

    public int          id;
    public int          scenarioId;
    public Status       status;
    public Date         created;
    public Date         started;
    public Date         ended;
    public List<Result> results;

    public UserScenarioValidation() { }

    public UserScenarioValidation(int id, int scenarioId, Status status, Date created, Date started, Date ended) {
        this.id = id;
        this.scenarioId = scenarioId;
        this.status = status;
        this.created = created;
        this.started = started;
        this.ended = ended;
        this.results = new ArrayList<Result>();
    }

    public UserScenarioValidation(JsonObject json) {
        this.id = json.getInt("id", 0);
        this.scenarioId = json.getInt("user_scenario_id", 0);
        this.status = Status.values()[json.getInt("status", 0)];
        this.created = DateUtils.toDateFromIso8601(json.getString("created", null));
        this.started = DateUtils.toDateFromIso8601(json.getString("started", null));
        this.ended = DateUtils.toDateFromIso8601(json.getString("ended", null));
        this.results = new ArrayList<Result>();
    }

    @Override
    public String toString() {
        return "ScenarioValidation{" +
                "id=" + id +
                ", scenarioId=" + scenarioId +
                ", status=" + status +
                ", created=" + created +
                ", started=" + started +
                ", ended=" + ended +
                ", results=" + results +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserScenarioValidation that = (UserScenarioValidation) o;

        if (id != that.id) return false;
        if (scenarioId != that.scenarioId) return false;
        if (created != null ? !created.equals(that.created) : that.created != null) return false;
        if (ended != null ? !ended.equals(that.ended) : that.ended != null) return false;
        if (started != null ? !started.equals(that.started) : that.started != null) return false;
        if (status != that.status) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + scenarioId;
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (created != null ? created.hashCode() : 0);
        result = 31 * result + (started != null ? started.hashCode() : 0);
        result = 31 * result + (ended != null ? ended.hashCode() : 0);
        return result;
    }
}
