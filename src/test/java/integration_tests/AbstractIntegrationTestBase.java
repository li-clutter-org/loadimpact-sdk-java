package integration_tests;

import com.loadimpact.ApiTokenClient;
import com.loadimpact.util.StringUtils;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.*;
import java.util.Properties;

/**
 * Base class for all integration-tests, that finds the external API token and disables the tests if no token was found.
 *
 * @author jens
 */
public abstract class AbstractIntegrationTestBase {
    public static final String DEFAULT_TOKEN_PATH = "../loadimpact-token.properties";
    public static final String TOKEN_PROPERTY     = "loadimpact.token.file";
    public static final String TOKEN_ENVIRONMENT  = "LOADIMPACT_TOKEN_FILE";
    public static final String INFO_MESSAGE       = "how_to_run_integration_tests.txt";

    protected static String         apiToken;
    protected        ApiTokenClient client;

    @BeforeClass
    public static void checkToken() throws Exception {
        apiToken = loadToken(findReader());
        if (apiToken == null) showInfoMessage();
        Assume.assumeTrue("Missing API token. No integration tests will be run.", apiToken != null);
    }

    @Before
    public void createClient() {
        client = new ApiTokenClient(apiToken);
    }

    /**
     * Locates a token file and returns a Reader or null if not found.
     * The search order is:
     * <ul>
     * <li>File: ../loadimpact-token.properties</li>
     * <li>System Property: loadimpact.token.file</li>
     * <li>Environment Variable: LOADIMPACT_TOKEN_FILE</li>
     * </ul>
     *
     * @return reader or null
     * @throws FileNotFoundException
     */
    protected static Reader findReader() throws FileNotFoundException {
        Reader reader = null;

        String path = DEFAULT_TOKEN_PATH;
        if ((reader = openReader(path)) != null) {
            return reader;
        }

        path = System.getProperty(TOKEN_PROPERTY);
        if (path != null && (reader = openReader(path)) != null) {
            return reader;
        }

        path = System.getenv(TOKEN_ENVIRONMENT);
        if (path != null && (reader = openReader(path)) != null) {
            return reader;
        }

        return reader;
    }

    /**
     * Opens a file as a reader.
     *
     * @param tokenPath path to file
     * @return reader or null, if cannot open
     */
    protected static Reader openReader(String tokenPath) {
        File tokenFile = new File(tokenPath);
        if (tokenFile.canRead()) {
            try {
                return new FileReader(tokenFile);
            } catch (FileNotFoundException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Loads a properties file and returns the API token value, if any.
     *
     * @param in reader pointing to properties file
     * @return API token or null
     */
    protected static String loadToken(Reader in) {
        if (in == null) return null;

        try {
            Properties p = new Properties();
            p.load(in);
            in.close();
            return p.getProperty("api.token");
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Shows an information message about how the pre-requisites to run the integration tests.
     */
    protected static void showInfoMessage() {
        InputStream is = AbstractIntegrationTestBase.class.getResourceAsStream(INFO_MESSAGE);
        if (is == null) {
            throw new IllegalArgumentException("Cannot find classpath resource: " + INFO_MESSAGE);
        }

        System.err.println("**********************************************************");
        System.err.println(StringUtils.toString(is));
        System.err.println("**********************************************************");
    }


}
