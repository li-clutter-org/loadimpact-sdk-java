package com.loadimpact.util;

import com.loadimpact.eval.LoadTestResult;
import com.loadimpact.resource.testresult.StandardMetricResult;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * DESCRIPTION
 *
 * @author jens
 */
public class ParametersTest {
    private Parameters target;

    @Before
    public void setUp() throws Exception {
        target = new Parameters();

        target.add("threshold.1.metric", "user_load_time");
        target.add("threshold.1.operator", "greaterThan");
        target.add("threshold.1.value", "1000");
        target.add("threshold.1.result", "unstable");

        target.add("threshold.2.metric", "failure_rate");
        target.add("threshold.2.value", "5");
        target.add("threshold.2.operator", "greaterThan");
        target.add("threshold.2.result", "unstable");
        
        target.add("delay.unit", "seconds");
        target.add("delay.value", "10");
        target.add("delay.size", "8");
        target.add("poll.interval", "5");
        target.add("log.http", "true");
    }

    @Test
    public void testAdd() throws Exception {
        final int N = target.size();
        target.add("message", "foobar");
        assertThat(target.size(), is(N+1));
    }

    @Test
    public void testSize() throws Exception {
        assertThat(target.size(), is(2 * 4 + 5));
    }
    
    @Test
    public void testKeys() throws Exception {
        assertThat(target.keys().size(), is(2 * 4 + 5));
    }

    @Test
    public void testKeysWithPattern() throws Exception {
        Set<String> keys = target.keys("threshold\\.\\d+\\.value");
        assertThat(keys.size(), is(2));
    }

    @Test
    public void testHas() throws Exception {
        assertThat(target.has("threshold.2.result"), is(true));
        assertThat(target.has("foobar"), is(false));
    }

    @Test
    public void testGet() throws Exception {
        assertThat(target.get("delay.value", -1), is(10));
        assertThat(target.get("log.http", false), is(true));
        assertThat(target.get("threshold.2.metric", StandardMetricResult.Metrics.USER_LOAD_TIME), is(StandardMetricResult.Metrics.FAILURE_RATE));
        assertThat(target.get("threshold.2.value", -1), is(5));
        assertThat(target.get("threshold.2.result", LoadTestResult.aborted), is(LoadTestResult.unstable));
    }
    
}
