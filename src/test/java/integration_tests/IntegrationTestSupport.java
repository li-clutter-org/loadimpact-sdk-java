package integration_tests;

import com.loadimpact.ApiTokenClient;
import org.junit.Assume;
import org.junit.Before;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * DESCRIPTION
 *
 * @author jens
 */
public abstract class IntegrationTestSupport {
    protected String         apiToken;
    protected ApiTokenClient client;

    @Before
    public void init() throws Exception {
        File       tokenFile      = new File("../loadimpact-token.properties");
        if (tokenFile.canRead()) {
            FileReader in = new FileReader(tokenFile);
            Properties p = new Properties();
            p.load(in);
            in.close();

            apiToken = p.getProperty("api.token");
            Assume.assumeNotNull(apiToken);

            if (apiToken != null) {
                client = new ApiTokenClient(apiToken);
            } else {
                System.err.println("Missing API token in token properties file. Should be: api.token=<valid API token>");
            }
//            client.setDebug(true);
        } else {
            System.err.println("Missing token file. " +
                    "If you want to run the integration test, you need to provide the file '../loadimpact-token.properties' " +
                    "containing a valid LI API token");
            Assume.assumeTrue(false);
        }
    }
    
}
