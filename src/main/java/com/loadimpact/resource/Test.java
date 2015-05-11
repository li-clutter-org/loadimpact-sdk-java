package com.loadimpact.resource;

import com.loadimpact.resource.Status;
import com.loadimpact.util.DateUtils;
import com.loadimpact.util.StringUtils;

import javax.json.JsonObject;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

/**
 * Container for as single (running or completed) test instance.
 *
 * @author jens
 */
public class Test implements Serializable {
    public int    id;
    public String title;
    public Status status;
    public URL    url;
    public URL    publicUrl;
    public Date   started;
    public Date   ended;

    public Test() { }

    public Test(int id, String title, Status status, URL url, URL publicUrl, Date started, Date ended) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.url = url;
        this.publicUrl = publicUrl;
        this.started = started;
        this.ended = ended;
    }

    public Test(JsonObject json) {
        this.id = json.getInt("id", 0);
        this.title = json.getString("title", null);
        this.status = Status.valueOf(json.getInt("status", 0));
        this.started = DateUtils.toDateFromIso8601(json.getString("started", null));
        this.ended = DateUtils.toDateFromIso8601(json.getString("ended", null));

        try {
            String u = json.getString("url", null);
            this.url = !StringUtils.isBlank(u) ? new URL(u) : null;
        } catch (MalformedURLException e) { throw new RuntimeException(e); }

        try {
            String u = json.getString("public_url", null);
            this.publicUrl = !StringUtils.isBlank(u) ? new URL(u) : null;
        } catch (MalformedURLException e) { throw new RuntimeException(e); }
    }

    @Override
    public String toString() {
        return "Test{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", url=" + url +
                ", publicUrl=" + publicUrl +
                ", started=" + started +
                ", ended=" + ended +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Test that = (Test) o;

        if (id != that.id) return false;
        if (ended != null ? !ended.equals(that.ended) : that.ended != null) return false;
        if (publicUrl != null ? !publicUrl.equals(that.publicUrl) : that.publicUrl != null) return false;
        if (started != null ? !started.equals(that.started) : that.started != null) return false;
        if (status != that.status) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (publicUrl != null ? publicUrl.hashCode() : 0);
        result = 31 * result + (started != null ? started.hashCode() : 0);
        result = 31 * result + (ended != null ? ended.hashCode() : 0);
        return result;
    }
}
