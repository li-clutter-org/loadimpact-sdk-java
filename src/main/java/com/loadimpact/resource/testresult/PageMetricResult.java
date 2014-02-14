package com.loadimpact.resource.testresult;

import javax.json.JsonObject;

/**
 * Page metric results.
 *
 * @author jens
 */
public class PageMetricResult extends AggregatedNumericResult {
    public static final String METRIC_ID_PREFIX = "__li_page_";
    public final String type;

    public PageMetricResult(JsonObject json) {
        super(json);
        type = json.getString("type", null);
    }

    @Override
    protected StringBuilder toString(StringBuilder buf) {
        return super.toString(buf).append(", ")
                    .append("type=").append(type)
                ;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PageMetricResult)) return false;
        if (!super.equals(o)) return false;

        PageMetricResult that = (PageMetricResult) o;

        if (type != null ? !type.equals(that.type) : that.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
