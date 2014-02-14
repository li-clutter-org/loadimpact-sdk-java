package com.loadimpact.resource.testresult;

import javax.json.JsonObject;

/**
 * Base class for results with an average, min/max and count.
 *
 * @author jens
 */
public abstract class AggregatedNumericResult extends Result {
    public final String name;
    public final int    count;
    public final double average;
    public final double minimum;
    public final double maximum;

    protected AggregatedNumericResult(JsonObject json) {
        super(json);

        name = json.getString("name", null);
        count = json.getInt("count", 0);
        average = getDouble(json, "avg", 0D);
        minimum = getDouble(json, "min", 0D);
        maximum = getDouble(json, "max", 0D);
    }

    protected StringBuilder toString(StringBuilder buf) {
        return super.toString(buf).append(", ")
                  .append("name=").append(name).append(", ")
                  .append("count=").append(count).append(", ")
                  .append("average=").append(average).append(", ")
                  .append("minimum=").append(minimum).append(", ")
                  .append("maximum=").append(maximum)
                ;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AggregatedNumericResult)) return false;
        if (!super.equals(o)) return false;

        AggregatedNumericResult that = (AggregatedNumericResult) o;

        if (Double.compare(that.average, average) != 0) return false;
        if (count != that.count) return false;
        if (Double.compare(that.maximum, maximum) != 0) return false;
        if (Double.compare(that.minimum, minimum) != 0) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        long temp;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + count;
        temp = Double.doubleToLongBits(average);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(minimum);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(maximum);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
