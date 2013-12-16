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
public class CustomMetricResultTest {
    
    @Test
    public void create_from_sample_JSON_should_pass() throws Exception {
        JsonObject json = JsonUtils.loadJSON(this, "custom-metrics-result.json");
        
        CustomMetricResult target = new CustomMetricResult(json);
        assertThat(target, notNullValue());
        assertThat(target.name, is("DNS lookup time"));
        assertThat(target.type, is("number"));
        assertThat(target.count, is(17));
        assertThat(target.average, lessThan(0.01));

//        System.out.println("target = " + target);
    }
    
}
