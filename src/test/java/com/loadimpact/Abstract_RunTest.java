package com.loadimpact;

import org.junit.Before;

import java.io.InputStream;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * DESCRIPTION
 *
 * @author jens
 */
public abstract class Abstract_RunTest {
    protected String         apiToken;
    protected ApiTokenClient client;

    @Before
    public void init() throws Exception {
        InputStream is = getClass().getResourceAsStream("/api-token.properties");
        assertThat("Cannot find a resource file containing the API Token", is, notNullValue());

        Properties p = new Properties();
        p.load(is);
        apiToken = p.getProperty("api.token");
        assertThat(apiToken, notNullValue());

        client = new ApiTokenClient(apiToken);
    }
    
}
