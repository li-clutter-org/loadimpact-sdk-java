package com.loadimpact;

import com.loadimpact.exception.MissingApiTokenException;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;


/**
 * Unit tests of the LoadImpactClient, using no network calls.
 *
 * @author jens
 */
public class ApiTokenClient_UnitTest {
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

        client = new ApiTokenClient();
    }

    @Test
    public void properKeyShouldPass() throws Exception {
        client.checkApiToken(apiToken);
    }

    @Test(expected = MissingApiTokenException.class)
    public void nullKeyShouldFail() throws Exception {
        client.checkApiToken(null);
    }

    @Test(expected = MissingApiTokenException.class)
    public void emptyKeyShouldFail() throws Exception {
        client.checkApiToken("");
    }

    @Test(expected = MissingApiTokenException.class)
    public void wrongSizedKeyShouldFail() throws Exception {
        client.checkApiToken(apiToken.substring(10));
    }

    @Test(expected = MissingApiTokenException.class)
    public void notHexKeyShouldFail() throws Exception {
        client.checkApiToken(apiToken.replaceAll("[0-9]", "X"));
    }


    
    
    
}
