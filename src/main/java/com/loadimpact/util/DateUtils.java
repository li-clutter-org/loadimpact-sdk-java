package com.loadimpact.util;

import org.joda.time.format.ISODateTimeFormat;

import java.util.Date;

/**
 * Utility methods for dates.
 *
 * @author jens
 */
public class DateUtils {

    /**
     * Parses a text string with a date in ISO 8601 format into a {@link java.util.Date} object.
     * @param s     text with ISO 8601 formatted date
     * @return Date
     */
    public static Date toDateFromIso8601(String s) {
        if (s == null) return null;
        return ISODateTimeFormat.dateTimeNoMillis().parseDateTime(s).toDate();
    }

    /**
     * Returns a ISO8601 string of the given date.
     * @param date  the date
     * @return ISO8601 formatted date (UTC)
     */
    public static String toIso8601(Date date) {
        if (date == null) return null;
        return ISODateTimeFormat.dateTimeNoMillis()./*withZoneUTC().*/print(date.getTime());
    }

    /**
     * Adjusts a LoadImpact/Python timestamp [us] value into an epoch based ditto [ms] and returns it as a {@link java.util.Date} object.
     * @param ts    timestamp value in micro-seconds
     * @return Date
     */
    public static Date toDateFromTimestamp(long ts) {
        return new Date(ts / 1000);
    }
    
}
