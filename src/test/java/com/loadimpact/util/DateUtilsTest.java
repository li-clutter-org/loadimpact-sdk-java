package com.loadimpact.util;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * DESCRIPTION
 *
 * @author jens
 */
public class DateUtilsTest {

    @Test
    public void testToDateFromIso8601() {
        String input = "2013-09-09T02:34:51+00:00";
        GregorianCalendar expected = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        expected.set(2013, 9 - 1, 9, 2, 34, 51);
        assertThat(clearMilliSecs(DateUtils.toDateFromIso8601(input)), is(clearMilliSecs(expected.getTime())));
    }

    @Test
    public void testToIso8601() throws Exception {
        GregorianCalendar date = new GregorianCalendar(2013, 9 - 1, 9, 2, 34, 51);
        date.setTimeZone(TimeZone.getTimeZone("UTC"));
        assertThat(DateUtils.toIso8601(date.getTime()), is("2013-09-09T02:34:51Z"));
    }

    @Test
    public void testToDateFromTimestamp() {
        GregorianCalendar expected = new GregorianCalendar(2013, 9 - 1, 10, 15, 38, 36);
        expected.setTimeZone(TimeZone.getTimeZone("UTC"));
        assertThat(clearMilliSecs(DateUtils.toDateFromTimestamp(1378827516043595L)), is(clearMilliSecs(expected.getTime())));
    }

    private Date clearMilliSecs(Date date) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        c.clear(Calendar.MILLISECOND);
        return c.getTime();
    }
}
