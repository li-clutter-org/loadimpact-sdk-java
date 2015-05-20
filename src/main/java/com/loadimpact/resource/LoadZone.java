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
    AGGREGATE_WORLD(1, "aggregated:world:world", "World"),

    /* Amazon load zones */
    AMAZON_US_ASHBURN(11, "amazon:us:ashburn", "Ashburn"),
    AMAZON_US_PALOALTO(12, "amazon:us:palo alto", "Palo Alto"),
    AMAZON_IE_DUBLIN(13, "amazon:ie:dublin", "Dublin"),
    AMAZON_SG_SINGAPORE(14, "amazon:sg:singapore", "Singapore"),
    AMAZON_JP_TOKYO(15, "amazon:jp:tokyo", "Tokyo"),
    AMAZON_US_PORTLAND(22, "amazon:us:portland", "Portland"),
    AMAZON_BR_SAOPAULO(23, "amazon:br:são paulo", "São Paulo"),
    AMAZON_AU_SYDNEY(25, "amazon:au:sydney", "Sydney"),

    /* Rackspace load zones */
    RACKSPACE_US_CHICAGO(26, "rackspace:us:chicago", "Chicago"),
    RACKSPACE_US_DALLAS(27, "rackspace:us:dallas", "Dallas"),
    RACKSPACE_UK_LONDON(28, "rackspace:uk:london", "London"),
    RACKSPACE_AU_SYDNEY(29, "rackspace:au:sydney", "Sydney");

    public enum Countries {World, US, IE, UK, BR, SG, AU, JP}

    public enum Providers {Aggregated, Amazon, Rackspace}

    public final   int                   id;
    public final   String                uid;
    public final   String                city;
    public final   Countries             country;
    public final   Providers             provider;
    private static Map<String, LoadZone> zones;


    LoadZone(int id, String uid, String city) {
        this.id = id;
        this.city = city;
        this.uid = uid;
        this.country = toCountry(uid);
        this.provider = toProvider(uid);
        add(this);
    }

    @Override
    public String toString() {
        return uid;
    }

    private Countries toCountry(String uid) {
        String   countryCode = uid.split(":")[1];
        for (Countries c : Countries.values()) {
            if (countryCode.equals(c.name().toLowerCase())) return c;
        }
        throw new IllegalArgumentException("Country code not found: " + uid);
    }

    private Providers toProvider(String uid) {
        String   provider = uid.split(":")[0];
        for (Providers p : Providers.values()) {
            if (provider.equals(p.name().toLowerCase())) return p;
        }
        throw new IllegalArgumentException("Provider code not found: " + uid);
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

        LoadZone zone = zones.get(zoneId);
        if (zone == null) return AGGREGATE_WORLD;
        return zone;
    }

    public static LoadZone valueOf(int zoneId) {
        for (LoadZone z : values()) {
            if (z.id == zoneId) return z;
        }
        return AGGREGATE_WORLD;
    }

}
