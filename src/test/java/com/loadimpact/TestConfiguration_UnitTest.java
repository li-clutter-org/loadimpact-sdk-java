package com.loadimpact;

import com.loadimpact.resource.TestConfiguration;
import com.loadimpact.resource.configuration.UserType;
import org.junit.Before;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.StringContains.containsString;

/**
 * Unit test of {@link com.loadimpact.resource.TestConfiguration}.
 *
 * @author jens
 */
public class TestConfiguration_UnitTest {
    private JsonObject json, json2;

    @Before
    public void init() {
        json = loadJSON("/test-configuration.json");
        json2 = loadJSON("/test-configuration-partial.json");
    }

    @Test
    public void testToString() throws Exception {
        TestConfiguration target = new TestConfiguration(json);
        assertThat(target.toString(), containsString("amazon:us:palo-alto"));
    }

    @Test
    public void testEquals() throws Exception {
        TestConfiguration t1 = new TestConfiguration(json);
        TestConfiguration t2 = new TestConfiguration(json);
        assertThat(t1.equals(t2), is(true));
    }

    @Test
    public void parsingTestConfigurationJsonShouldPass() {
        TestConfiguration target = new TestConfiguration(json);
        assertThat(target.id, is(83959));
        assertThat(target.name, is("Name of resource"));
        assertThat(target.updated, is(date(2013, 10, 1, 8, 8, 58)));
        assertThat(target.userType, is(UserType.SBU));
        
        assertThat(target.loadSchedule.size(), is(1));
        assertThat(target.loadSchedule.get(0).duration, is(10));
        assertThat(target.loadSchedule.get(0).users, is(50));

        assertThat(target.tracks.size(), is(2));
        assertThat(target.tracks.get(0).zone, is("amazon:us:ashburn"));
        assertThat(target.tracks.get(0).clips.size(), is(1));
        assertThat(target.tracks.get(0).clips.get(0).percent, is(50));
        assertThat(target.tracks.get(0).clips.get(0).scenarioId, is(65706));

        assertThat(target.tracks.get(1).zone, is("amazon:us:palo-alto"));
        assertThat(target.tracks.get(1).clips.size(), is(2));
        assertThat(target.tracks.get(1).clips.get(0).percent, is(25));
        assertThat(target.tracks.get(1).clips.get(1).percent, is(25));
    }

    @Test
    public void testToJSON() throws Exception {
        Date date = new GregorianCalendar(2013, 10 - 1, 30, 9, 30, 0).getTime();
        TestConfiguration tc = new TestConfiguration(0, "aaa", new URL("http://foo.com"), date, date, UserType.SBU, null, null);
        JsonObject jsonObject = tc.toJSON();
        assertThat(jsonObject, notNullValue());
//        System.out.println("jsonObject = " + jsonObject);
        
        assertThat(jsonObject.toString(), is("{\"name\":\"aaa\",\"url\":\"http://foo.com\",\"created\":\"2013-10-30T09:30:00+01:00\",\"updated\":\"2013-10-30T09:30:00+01:00\",\"config\":{\"user_type\":\"sbu\"}}"));
    }

    @Test
    public void testToJSON2() throws Exception {
        TestConfiguration tc = new TestConfiguration(json2);
        JsonObject jsonObject = tc.toJSON();
//        System.out.println("jsonObject = " + jsonObject);

        assertThat(jsonObject.toString(), containsString("\"name\":\"Foobar\""));
        assertThat(jsonObject.toString(), containsString("\"loadzone\":\"amazon:us:ashburn\""));
    }

    

    private JsonObject loadJSON(String name) {
        InputStream is = getClass().getResourceAsStream(name);
        assertThat("Resource not found: " + name, is, notNullValue());
        JsonObject json = Json.createReader(is).readObject();
        assertThat("Failed to read: " + name, json, notNullValue());
        return json;
    }

    private Date date(int year, int month, int day, int hour, int minute, int second) {
        GregorianCalendar c = new GregorianCalendar(year, month - 1, day, hour, minute, second);
        c.setTimeZone(TimeZone.getTimeZone("UTC"));
        c.clear(Calendar.MILLISECOND);
        return c.getTime();
    }
    
}
