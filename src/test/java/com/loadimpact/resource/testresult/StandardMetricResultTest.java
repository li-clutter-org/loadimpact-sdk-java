package com.loadimpact.resource.testresult;

import com.loadimpact.resource.LoadZone;
import com.loadimpact.util.JsonUtils;
import org.junit.Test;

import javax.json.JsonObject;

import static com.loadimpact.resource.testresult.StandardMetricResult.Metrics.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * DESCRIPTION
 *
 * @author jens
 */
public class StandardMetricResultTest {

    @Test
    public void test_accumulated_load_time() throws Exception {
        JsonObject json = JsonUtils.loadJSON(this, "standard-metrics-result-value.json");

        StandardMetricResult target = new StandardMetricResult(ACCUMULATED_LOAD_TIME, json);
        assertThat(target, notNullValue());

        assertThat(target.offset, is(3));
        assertThat(target.value.doubleValue(), is(42D));
        assertThat(target.count, nullValue());

//        System.out.println("target = " + target);
    }

    @Test
    public void test_clients_active() throws Exception {
        JsonObject json = JsonUtils.loadJSON(this, "standard-metrics-result-value.json");

        StandardMetricResult target = new StandardMetricResult(CLIENTS_ACTIVE, json);
        assertThat(target, notNullValue());

        assertThat(target.offset, is(3));
        assertThat(target.value.intValue(), is(42));
        assertThat(target.count, nullValue());

//        System.out.println("target = " + target);
    }

    @Test
    public void test_bandwidth() throws Exception {
        JsonObject json = JsonUtils.loadJSON(this, "standard-metrics-result-avg.json");

        StandardMetricResult target = new StandardMetricResult(BANDWIDTH, json);
        assertThat(target, notNullValue());

        assertThat(target.offset, is(5));
        assertThat(target.value.doubleValue(), is(10.5));
        assertThat(target.count, nullValue());

//        System.out.println("target = " + target);
    }

    @Test
    public void test_reps_succeeded_percent() throws Exception {
        JsonObject json = JsonUtils.loadJSON(this, "standard-metrics-result-percent.json");

        StandardMetricResult target = new StandardMetricResult(REPS_SUCCEEDED_PERCENT, json);
        assertThat(target, notNullValue());

        assertThat(target.offset, is(17));
        assertThat(target.value.doubleValue(), is(0.75));
        assertThat(target.count.intValue(), is(35));

//        System.out.println("target = " + target);
    }

    @Test
    public void test_content_type_load_time() throws Exception {
        JsonObject json = JsonUtils.loadJSON(this, "standard-metrics-result-content-type-load-time.json");

        ContentTypeLoadTimeStandardMetricResult target = new ContentTypeLoadTimeStandardMetricResult(CONTENT_TYPE_LOAD_TIME, json);
        assertThat(target, notNullValue());

        assertThat(target.offset, is(5));
        assertThat(target.value.doubleValue(), is(1.6));
        assertThat(target.count.intValue(), is(11));
        assertThat(target.minimum, is(1.1));
        assertThat(target.maximum, is(2.5));
        assertThat(target.type, is("text/html"));

//        System.out.println("target = " + target);
    }

    @Test
    public void test_live_feedback() throws Exception {
        JsonObject json = JsonUtils.loadJSON(this, "standard-metrics-result-live-feedback.json");

        LiveFeedbackStandardMetricResult target = new LiveFeedbackStandardMetricResult(LIVE_FEEDBACK, json);
        assertThat(target, notNullValue());

        assertThat(target.offset, is(15));
        assertThat(target.zone, is(LoadZone.AMAZON_US_ASHBURN));
        assertThat(target.location.toString(), is("{lat=39.04, lng=-77.48}"));

        assertThat(target.percent, is(80.5));
        assertThat(target.type, is("wait_for_loadgen"));
        assertThat(target.message, is("Waiting for load generators..."));

        assertThat(target.value, nullValue());
        assertThat(target.count, nullValue());

//        System.out.println("target = " + target);
    }

    @Test
    public void test_log() throws Exception {
        JsonObject json = JsonUtils.loadJSON(this, "standard-metrics-result-log.json");

        LogStandardMetricResult target = new LogStandardMetricResult(LOG, json);
        assertThat(target, notNullValue());

        assertThat(target.offset, is(19));
        assertThat(target.scenarioId, is(12345));
        assertThat(target.zone, is(LoadZone.AMAZON_US_PALOALTO));

        assertThat(target.level, is("info"));
        assertThat(target.message, is("I'm a log message!"));

        assertThat(target.value, nullValue());
        assertThat(target.count, nullValue());

//        System.out.println("target = " + target);
    }

    @Test
    public void test_content_type() throws Exception {
        JsonObject json = JsonUtils.loadJSON(this, "standard-metrics-result-content-type.json");

        ContentTypeStandardMetricResult target = new ContentTypeStandardMetricResult(CONTENT_TYPE, json);
        assertThat(target, notNullValue());

        assertThat(target.offset, is(25));
        assertThat(target.types.size(), is(3));
        assertThat(target.types.get("image/png"), is(5));

        assertThat(target.value, nullValue());
        assertThat(target.count, nullValue());

//        System.out.println("target = " + target);
    }
}
