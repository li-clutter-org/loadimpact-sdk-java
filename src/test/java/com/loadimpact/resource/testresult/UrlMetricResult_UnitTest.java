package com.loadimpact.resource.testresult;

import com.loadimpact.resource.HttpMethods;
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
public class UrlMetricResult_UnitTest {

    @Test
    public void create_from_sample_JSON_should_pass() throws Exception {
        JsonObject json = JsonUtils.loadJSON(this, "url-metrics-result.json");

        UrlMetricResult target = new UrlMetricResult(json);
        assertThat(target, notNullValue());
        assertThat(target.status_code, is(200));
        assertThat(target.method, is(HttpMethods.GET));
        assertThat(target.url.toString(), is("http://www.ribomation.se/"));
        assertThat(target.count, is(2));
        assertThat(target.average, is(520.1));
        assertThat(target.average_content_length, is(78591));
        
        assertThat(target.types.size(), is(2));
        assertThat(target.types.get("text/html"), notNullValue());
        assertThat(target.types.get("text/html"), is(2));
        assertThat(target.types.get("image/png"), is(5));

//        System.out.println("target = " + target);
    }

}
