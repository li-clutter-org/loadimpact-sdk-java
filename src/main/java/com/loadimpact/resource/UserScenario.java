package com.loadimpact.resource;

import com.loadimpact.util.DateUtils;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.Serializable;
import java.util.Date;

/**
 * Container for a scenario.
 *
 * @author jens
 */
public class UserScenario implements Serializable {
    public int    id;
    public String name;
    public Date   created;
    public Date   updated;
    public String type;
    public String loadScript;
    public List<Integer> dataStores = new ArrayList<Integer>();

    public UserScenario() {  }

    public UserScenario(int id, String name, Date created, Date updated, String type, String loadScript, List<Integer> dataStores) {
        this.id = id;
        this.name = name;
        this.created = created;
        this.updated = updated;
        this.type = type;
        this.loadScript = loadScript;
        this.dataStores = dataStores;
    }

    public UserScenario(JsonObject json) {
        this.id = json.getInt("id", 0);
        this.name = json.getString("name", null);
        this.created = DateUtils.toDateFromIso8601(json.getString("created"));
        this.updated = DateUtils.toDateFromIso8601(json.getString("updated"));
        this.type = json.getString("script_type", null);
        this.loadScript = json.getString("load_script", null);

        JsonArray dataStoresJson = json.getJsonArray("data_stores");
        if (dataStoresJson != null) {
            for (int i = 0; i < dataStoresJson.size(); ++i) {
                this.dataStores.add(dataStoresJson.getInt(i));
            }
        }
    }

    public JsonObject toJSON() {
        JsonBuilderFactory f = Json.createBuilderFactory(null);
        JsonObjectBuilder json = f.createObjectBuilder();
        if (name != null) json.add("name", name);
        if (type != null) json.add("script_type", type);
        if (loadScript != null) json.add("load_script", loadScript);
        if (created != null) json.add("created", DateUtils.toIso8601(created));
        if (updated != null) json.add("updated", DateUtils.toIso8601(updated));
        if (dataStores != null && !dataStores.isEmpty()) {
            JsonArrayBuilder dataStoresJson = f.createArrayBuilder();
            for (int d : dataStores) {
                dataStoresJson.add(d);
            }
            json.add("data_stores", dataStoresJson);
        }
        return json.build();
    }

    @Override
    public String toString() {
        return "Scenario{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", created=" + created +
                ", updated=" + updated +
                ", type='" + type + '\'' +
                ", loadScript='" + (loadScript != null ? loadScript.substring(0, 100) : "") + '\'' +
                ", dataStores=" + dataStores +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserScenario scenario = (UserScenario) o;

        if (id != scenario.id) return false;
        if (created != null ? !created.equals(scenario.created) : scenario.created != null) return false;
        if (name != null ? !name.equals(scenario.name) : scenario.name != null) return false;
        if (type != null ? !type.equals(scenario.type) : scenario.type != null) return false;
        if (updated != null ? !updated.equals(scenario.updated) : scenario.updated != null) return false;
        if (dataStores != null ? !dataStores.equals(scenario.dataStores) : scenario.dataStores != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (created != null ? created.hashCode() : 0);
        result = 31 * result + (updated != null ? updated.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (dataStores != null ? dataStores.hashCode() : 0);
        return result;
    }
}
