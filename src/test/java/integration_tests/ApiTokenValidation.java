package integration_tests;

import com.loadimpact.ApiTokenClient;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Contacts the LI server and validates the provided (real) API token.
 *
 * @user jens
 * @date 2015-04-27
 */
public class ApiTokenValidation  extends AbstractIntegrationTestBase {
    @Test
    public void validKeyShouldPass() throws Exception {
        assertThat(client.isValidToken(), is(true));
    }

    @Test
    public void invalidKeyShouldFail() throws Exception {
        client = new ApiTokenClient(apiToken.replace('4', '9'));
        assertThat(client.isValidToken(), is(false));
    }
    
}
