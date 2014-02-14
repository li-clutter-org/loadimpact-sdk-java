package com.loadimpact.resource;

import com.loadimpact.util.StringUtils;

import javax.json.JsonObject;

/**
 * Enumeration of all known load zones.
 *
 * @author jens
 */
public enum LoadZone {
    /* Aggregate load zones */
    AGGREGATE_WORLD(1, Countries.World, Providers.Aggregated),

    /* Amazon load zones */
    AMAZON_US_ASHBURN(11, Countries.US, Providers.Amazon),
    AMAZON_US_PALOALTO(12, Countries.US, Providers.Amazon),
    AMAZON_IE_DUBLIN(13, Countries.IE, Providers.Amazon),
    AMAZON_SG_SINGAPORE(14, Countries.SG, Providers.Amazon),
    AMAZON_JP_TOKYO(15, Countries.JP, Providers.Amazon),
    AMAZON_US_PORTLAND(22, Countries.US, Providers.Amazon),
    AMAZON_BR_SAOPAULO(23, Countries.BR, Providers.Amazon),
    AMAZON_AU_SYDNEY(25, Countries.AU, Providers.Amazon),

    /* Rackspace load zones */
    RACKSPACE_US_CHICAGO(26, Countries.US, Providers.Rackspace),
    RACKSPACE_US_DALLAS(27, Countries.US, Providers.Rackspace),
    RACKSPACE_UK_LONDON(28, Countries.UK, Providers.Rackspace),
    RACKSPACE_AU_SYDNEY(29, Countries.AU, Providers.Rackspace);

    public enum Countries {World, US, IE, UK, BR, SG, AU, JP}

    public enum Providers {Aggregated, Amazon, Rackspace}

    public final int       id;
    public final String    uid;
    public final String    city;
    public final Countries country;
    public final Providers provider;

    LoadZone(int id, Countries country, Providers provider) {
        this.id = id;
        this.uid = String.format("%s:%s:%s", provider.name().toLowerCase(), country.name().toLowerCase(), this.name().toLowerCase());
        this.city = toCityName(name());
        this.country = country;
        this.provider = provider;
    }

    @Override
    public String toString() {
        return uid;
    }

    public static LoadZone valueOf(JsonObject json) {
        String jsonId = json.getString("id", null);
        if (jsonId == null) return AGGREGATE_WORLD;

        for (LoadZone z : values()) {
            if (z.uid.equals(jsonId)) return z;
        }

        return AGGREGATE_WORLD;
    }

    public static LoadZone valueOf(int zoneId) {
        for (LoadZone z : values()) {
            if (z.id == zoneId) return z;
        }
        return AGGREGATE_WORLD;
    }

    private String toCityName(String s) {
        s = StringUtils.toInitialCase(s);
        int p = s.indexOf('_');
        if (p < 0) return s;

        StringBuilder buf = new StringBuilder(s);
        buf.setCharAt(p, ' ');
        p++;
        buf.setCharAt(p, Character.toUpperCase(buf.charAt(p)));
        return buf.toString();
    }

}
