package com.loadimpact.resource;

import com.loadimpact.resource.configuration.LoadClip;
import com.loadimpact.resource.configuration.LoadScheduleStep;
import com.loadimpact.resource.configuration.LoadTrack;
import com.loadimpact.resource.configuration.UserType;
import com.loadimpact.util.DateUtils;
import com.loadimpact.util.StringUtils;

import javax.json.*;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Container for a test configuration.
 *
 * @author jens
 */
public class TestConfiguration implements Serializable {
    public int      id;
    public String   name;
    public URL      url;
    public Date     created;
    public Date     updated;
    public UserType userType;
    public List<LoadScheduleStep> loadSchedule = new ArrayList<LoadScheduleStep>();
    public List<LoadTrack>        tracks       = new ArrayList<LoadTrack>();

    public TestConfiguration() {
    }

    public TestConfiguration(int id, String name, URL url, Date created, Date updated, UserType userType, List<LoadScheduleStep> loadSchedule, List<LoadTrack> tracks) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.created = created;
        this.updated = updated;
        this.userType = userType;
        this.loadSchedule = loadSchedule;
        this.tracks = tracks;
    }

    public TestConfiguration(JsonObject json) {
//        System.err.println("TestConfiguration: " + json);

        this.id = json.getInt("id", 0);
        this.name = json.getString("name", null);
        this.created = DateUtils.toDateFromIso8601(json.getString("created", null));
        this.updated = DateUtils.toDateFromIso8601(json.getString("updated", null));

        try {
            String u = json.getString("url", null);
            this.url = (!StringUtils.isBlank(u)) ? new URL(u) : null;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        JsonObject configJson = json.getJsonObject("config");
        if (configJson != null) {
            this.userType = configJson.getString("user_type") != null ? UserType.valueOf(configJson.getString("user_type").toUpperCase()) : null;

            JsonArray loadScheduleJson = configJson.getJsonArray("load_schedule");
            if (loadScheduleJson != null) {
                ArrayList<LoadScheduleStep> loadScheduleSteps = new ArrayList<LoadScheduleStep>(loadScheduleJson.size());
                for (int k = 0; k < loadScheduleJson.size(); ++k) {
                    loadScheduleSteps.add(new LoadScheduleStep(loadScheduleJson.getJsonObject(k)));
                }
                this.loadSchedule = Collections.unmodifiableList(loadScheduleSteps);
            }

            JsonArray tracksJson = configJson.getJsonArray("tracks");
            if (tracksJson != null) {
                ArrayList<LoadTrack> tracksList = new ArrayList<LoadTrack>(tracksJson.size());
                for (int k = 0; k < tracksJson.size(); ++k) {
                    tracksList.add(new LoadTrack(tracksJson.getJsonObject(k)));
                }
                this.tracks = Collections.unmodifiableList(tracksList);
            }
        }
    }

    public JsonObject toJSON() {
        JsonBuilderFactory f    = Json.createBuilderFactory(null);
        JsonObjectBuilder  json = f.createObjectBuilder();
        if (name != null) json.add("name", name);
        if (url != null) json.add("url", url.toString());
        if (created != null) json.add("created", DateUtils.toIso8601(created));
        if (updated != null) json.add("updated", DateUtils.toIso8601(updated));

        JsonObjectBuilder configJson = f.createObjectBuilder();
        boolean           hasConfig  = false;
        if (userType != null) {
            hasConfig = true;
            configJson.add("user_type", userType.name().toLowerCase());
        }

        if (loadSchedule != null && !loadSchedule.isEmpty()) {
            hasConfig = true;
            JsonArrayBuilder loadScheduleJson = f.createArrayBuilder();
            for (LoadScheduleStep s : loadSchedule) {
                loadScheduleJson.add(f.createObjectBuilder()
                                .add("duration", s.duration)
                                .add("users", s.users)
                );
            }
            configJson.add("load_schedule", loadScheduleJson);
        }

        if (tracks != null && !tracks.isEmpty()) {
            hasConfig = true;
            JsonArrayBuilder tracksJson = f.createArrayBuilder();
            for (LoadTrack t : tracks) {
                JsonObjectBuilder trackJson = f.createObjectBuilder();
                trackJson.add("loadzone", t.zone);
                if (t.clips != null && !t.clips.isEmpty()) {
                    JsonArrayBuilder clipsJson = f.createArrayBuilder();
                    for (LoadClip c : t.clips) {
                        JsonObjectBuilder clipJson = f.createObjectBuilder()
                                .add("percent", c.percent)
                                .add("user_scenario_id", c.scenarioId);
                        clipsJson.add(clipJson);
                    }
                    trackJson.add("clips", clipsJson);
                }
                tracksJson.add(trackJson);
            }
            configJson.add("tracks", tracksJson);
        }

        if (hasConfig) {
            json.add("config", configJson);
        }

        return json.build();
    }

    @Override
    public String toString() {
        return "TestConfiguration{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", created=" + created +
                ", updated=" + updated +
                ", userType=" + userType +
                ", loadSchedule=" + loadSchedule +
                ", tracks=" + tracks +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestConfiguration that = (TestConfiguration) o;

        if (id != that.id) return false;
        if (created != null ? !created.equals(that.created) : that.created != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (loadSchedule != null ? !loadSchedule.equals(that.loadSchedule) : that.loadSchedule != null) return false;
        if (tracks != null ? !tracks.equals(that.tracks) : that.tracks != null) return false;
        if (updated != null ? !updated.equals(that.updated) : that.updated != null) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        if (userType != that.userType) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (created != null ? created.hashCode() : 0);
        result = 31 * result + (updated != null ? updated.hashCode() : 0);
        result = 31 * result + (userType != null ? userType.hashCode() : 0);
        result = 31 * result + (loadSchedule != null ? loadSchedule.hashCode() : 0);
        result = 31 * result + (tracks != null ? tracks.hashCode() : 0);
        return result;
    }
}
