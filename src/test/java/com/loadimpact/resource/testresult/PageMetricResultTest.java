package com.loadimpact.resource.testresult;

import com.loadimpact.util.JsonUtils;
import org.junit.Test;

import javax.json.JsonObject;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * DESCRIPTION
 *
 * @author jens
 */
public class PageMetricResultTest {
    
    @Test
    public void create_from_sample_JSON_should_pass() throws Exception {
        JsonObject json = JsonUtils.loadJSON(this, "page-metrics-result.json");
        
        PageMetricResult target = new PageMetricResult(json);
        assertThat(target, notNullValue());
        assertThat(target.name, is("Foobar"));
        assertThat(target.type, is("seconds"));
        assertThat(target.count, is(42));

//        System.out.println("target = " + target);
    }
    
}
