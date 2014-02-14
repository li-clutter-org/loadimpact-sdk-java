package com.loadimpact.resource.testresult;

import com.loadimpact.util.JsonUtils;
import org.junit.Test;

import javax.json.JsonObject;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;

/**
 * DESCRIPTION
 *
 * @author jens
 */
public class ServerMetricResultTest {
    
    @Test
    public void create_from_sample_JSON_should_pass() throws Exception {
        JsonObject json = JsonUtils.loadJSON(this, "server-metrics-result.json");
        
        ServerMetricResult target = new ServerMetricResult(json);
        assertThat(target, notNullValue());
        assertThat(target.name, is("stockholm2"));
        assertThat(target.count, is(4));
        assertThat(target.unit, is("%"));
        assertThat(target.label, is("CPU"));
        assertThat(target.average, lessThan(0.06));
        assertThat(target.median, is(0D));

//        System.out.println("target = " + target);
    }
    
}
