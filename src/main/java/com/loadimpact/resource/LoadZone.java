package com.loadimpact.resource;

import com.loadimpact.util.StringUtils;

import javax.json.JsonObject;
import java.util.HashMap;
import java.util.Map;

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
    private static Map<String, LoadZone> zones;


    LoadZone(int id, Countries country, Providers provider) {
        this.id = id;
        this.city = toCityName(name());
        this.country = country;
        this.provider = provider;
        this.uid = String.format("%s:%s:%s", this.provider, this.country, this.city).toLowerCase();
        add(this);
    }

    @Override
    public String toString() {
        return uid;
    }

    private static void add(LoadZone z) {
        if (zones == null) {
            zones = new HashMap<String, LoadZone>();
        }
        zones.put(z.uid, z);
    }
    
    public static LoadZone valueOf(JsonObject json) {
        String zoneId = json.getString("id", null);
        if (zoneId == null) return AGGREGATE_WORLD;

        zoneId = zoneId.replaceAll("\\s", "");
        zoneId = zoneId.replaceAll("\u00e3", "a");
        LoadZone zone = zones.get(zoneId);
        return (zone != null) ? zone : AGGREGATE_WORLD;
    }

    public static LoadZone valueOf(int zoneId) {
        for (LoadZone z : values()) {
            if (z.id == zoneId) return z;
        }
        return AGGREGATE_WORLD;
    }

    private String toCityName(String s) {
        String[] parts = s.split("_");
        if (parts.length != 3) return s;

        return StringUtils.toInitialCase(parts[2]);
    }

    
}
