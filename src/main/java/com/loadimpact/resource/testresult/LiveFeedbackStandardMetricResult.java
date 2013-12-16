package com.loadimpact.resource.testresult;

import com.loadimpact.resource.LoadZone;

import javax.json.JsonObject;

/**
 * Special value class class for live-feedback results.
 *
 * @author jens
 */
public class LiveFeedbackStandardMetricResult extends StandardMetricResult {
    public static class Location {
        public final double latitude;
        public final double longitude;

        public Location(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        public String toString() {
            return "{" +
                    "lat=" + latitude +
                    ", lng=" + longitude +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Location location = (Location) o;

            if (Double.compare(location.latitude, latitude) != 0) return false;
            if (Double.compare(location.longitude, longitude) != 0) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            temp = Double.doubleToLongBits(latitude);
            result = (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(longitude);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            return result;
        }
    }

    public final LoadZone zone;
    public final Location  location;
    public final Double    percent;
    public final String    type;
    public final String    message;


    public LiveFeedbackStandardMetricResult(Metrics m, JsonObject json) {
        super(m, json);

        zone = LoadZone.valueOf(json.getInt("load_zone_id", 1));
        location = new Location(getDouble(json, "lat", 0), getDouble(json, "lng", 0));
        percent = getDouble(json, "percent", 0);
        type = json.getString("type", null);
        message = json.getString("msg", null);
    }

    @Override
    protected StringBuilder toString(StringBuilder buf) {
        return super.toString(buf).append(", ")
                    .append("zone=").append(zone).append(", ")
                    .append("location=").append(location).append(", ")
                    .append("percent=").append(percent).append(", ")
                    .append("type=").append(type).append(", ")
                    .append("message=").append(message)
                ;
    }

}
