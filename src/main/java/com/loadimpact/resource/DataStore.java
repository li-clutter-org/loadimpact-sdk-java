package com.loadimpact.resource;

import com.loadimpact.util.DateUtils;

import javax.json.JsonObject;
import java.io.Serializable;
import java.util.Date;

/**
 * Container for a data store.
 *
 * @author jens
 */
public class DataStore implements Serializable {
    public enum Status {QUEUED, CONVERTING, READY, FAILED}

    public enum Separator {
        COMMA, SEMICOLON, SPACE, TAB;

        public String param() {
            return name();
        }
    }

    public enum StringDelimiter {
        DOUBLEQUOTE, SINGLEQUOTE, NONE;

        public String param() {
            if (this == DOUBLEQUOTE) return "double";
            if (this == SINGLEQUOTE) return "single";
            return "none";
        }

        public static StringDelimiter from(String s) {
            if ("double".equals(s)) return DOUBLEQUOTE;
            if ("single".equals(s)) return SINGLEQUOTE;
            return NONE;
        }
    }

    public int    id;
    public String name;
    public Status status;
    public int    rows;
    public Date   created;
    public Date   updated;

    public DataStore() { }

    public DataStore(int id, String name, Status status, int rows, Date created, Date updated) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.rows = rows;
        this.created = created;
        this.updated = updated;
    }

    public DataStore(JsonObject json) {
        this.id = json.getInt("id", 0);
        this.name = json.getString("name", null);
        this.status = Status.values()[json.getInt("status", 0)];
        this.rows = json.getInt("rows", 0);
        this.created = DateUtils.toDateFromIso8601(json.getString("created"));
        this.updated = DateUtils.toDateFromIso8601(json.getString("updated"));
    }

    @Override
    public String toString() {
        return "DataStore{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", rows=" + rows +
                ", created=" + created +
                ", updated=" + updated +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataStore dataStore = (DataStore) o;

        if (id != dataStore.id) return false;
        if (rows != dataStore.rows) return false;
        if (created != null ? !created.equals(dataStore.created) : dataStore.created != null) return false;
        if (name != null ? !name.equals(dataStore.name) : dataStore.name != null) return false;
        if (status != dataStore.status) return false;
        if (updated != null ? !updated.equals(dataStore.updated) : dataStore.updated != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + rows;
        result = 31 * result + (created != null ? created.hashCode() : 0);
        result = 31 * result + (updated != null ? updated.hashCode() : 0);
        return result;
    }
}
