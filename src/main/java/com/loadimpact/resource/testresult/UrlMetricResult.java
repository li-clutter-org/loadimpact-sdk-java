package com.loadimpact.resource.testresult;

import com.loadimpact.resource.HttpMethods;

import javax.json.JsonObject;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * URL metric results.
 *
 * @author jens
 */
public class UrlMetricResult extends AggregatedNumericResult {
    public static final String METRIC_ID_PREFIX = "__li_url_";
    public final URL                  url;
    public final HttpMethods          method;
    public final int                  status_code;
    public final int                  average_content_length;
    public final int                  compressed_responses_count;
    public final int                  compressed_responses_average_content_length;
    public final Map<String, Integer> types;

    public UrlMetricResult(JsonObject json) {
        super(json);
        url = getUrl(json, "url");
        method = HttpMethods.valueOf(json.getString("method", "GET").toUpperCase());
        status_code = json.getInt("status_code", 200);
        average_content_length = json.getInt("avg_cntlen", 0);
        compressed_responses_count = json.getInt("comp_count", 0);
        compressed_responses_average_content_length = json.getInt("avg_comp_cntlen", 0);

        Map<String, Integer> map = new TreeMap<String, Integer>();
        JsonObject contentTypesJson = json.getJsonObject("content_types");
        if (contentTypesJson != null) {
            for (String type : contentTypesJson.keySet()) {
                map.put(type, contentTypesJson.getInt(type, 0));
            }
        }
        types = Collections.unmodifiableMap(map);
    }

    protected StringBuilder toString(StringBuilder buf) {
        return super.toString(buf).append(", ")
                  .append("url=").append(url).append(", ")
                  .append("method=").append(method).append(", ")
                  .append("status_code=").append(status_code).append(", ")
                  .append("average_content_length=").append(average_content_length).append(", ")
                  .append("compressed_responses_count=").append(compressed_responses_count).append(", ")
                  .append("compressed_responses_average_content_length=").append(compressed_responses_average_content_length).append(", ")
                  .append("types=").append(types)
                ;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UrlMetricResult)) return false;
        if (!super.equals(o)) return false;

        UrlMetricResult that = (UrlMetricResult) o;

        if (average_content_length != that.average_content_length) return false;
        if (compressed_responses_average_content_length != that.compressed_responses_average_content_length)
            return false;
        if (compressed_responses_count != that.compressed_responses_count) return false;
        if (status_code != that.status_code) return false;
        if (types != null ? !types.equals(that.types) : that.types != null)
            return false;
        if (method != that.method) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (method != null ? method.hashCode() : 0);
        result = 31 * result + status_code;
        result = 31 * result + average_content_length;
        result = 31 * result + compressed_responses_count;
        result = 31 * result + compressed_responses_average_content_length;
        result = 31 * result + (types != null ? types.hashCode() : 0);
        return result;
    }
}
