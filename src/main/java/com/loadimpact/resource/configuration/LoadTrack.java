package com.loadimpact.resource.configuration;

import com.loadimpact.resource.LoadZone;
import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Container for JSON load_schedule.tracks[]
 *
 * @author jens
 */
public class LoadTrack implements Serializable {
    public String zone;
    public List<LoadClip> clips = new ArrayList<LoadClip>();

    public LoadTrack() { }

    public LoadTrack(LoadZone loadZone) {
        this.zone = loadZone.uid;
    }

    public LoadTrack(LoadZone loadZone, List<LoadClip> clips) {
        this.zone = loadZone.uid;
        this.clips = clips;
    }

    public LoadTrack(JsonObject json) {
        this.zone = json.getString("loadzone", null);
        JsonArray clipsJson = json.getJsonArray("clips");
        if (clipsJson != null) {
            ArrayList<LoadClip> list = new ArrayList<LoadClip>(clipsJson.size());
            for (int k = 0; k < clipsJson.size(); ++k) {
                list.add(new LoadClip(clipsJson.getJsonObject(k)));
            }
            this.clips = Collections.unmodifiableList(list);
        }
    }

    public LoadTrack clip(LoadClip clip) {
        clips.add(clip);
        return this;
    }

    public LoadTrack clip(int percent, int scenarioId) {
        return clip(new LoadClip(percent, scenarioId));
    }

    @Override
    public String toString() {
        return "LoadTrack{" +
                "zone='" + zone + '\'' +
                ", clips=" + clips +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LoadTrack loadTrack = (LoadTrack) o;

        if (!clips.equals(loadTrack.clips)) return false;
        if (!zone.equals(loadTrack.zone)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = zone.hashCode();
        result = 31 * result + clips.hashCode();
        return result;
    }
    
}
