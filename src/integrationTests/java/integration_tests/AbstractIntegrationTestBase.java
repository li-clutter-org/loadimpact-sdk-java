package integration_tests;

import com.loadimpact.ApiTokenClient;
import com.loadimpact.resource.LoadZone;
import com.loadimpact.resource.TestConfiguration;
import com.loadimpact.resource.UserScenario;
import com.loadimpact.resource.configuration.LoadScheduleStep;
import com.loadimpact.resource.configuration.LoadTrack;
import com.loadimpact.resource.configuration.UserType;
import com.loadimpact.util.StringUtils;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

/**
 * Base class for all integration-tests, that finds the external API token and disables the tests if no token was found.
 *
 * @author jens
 */
public abstract class AbstractIntegrationTestBase {
    public static final String DEFAULT_TOKEN_PATH      = "../loadimpact-token.properties";
    public static final String INFO_MESSAGE            = "how_to_run_integration_tests.txt";
    public static final String SYSPROP_TOKEN           = "loadimpact.token";
    public static final String SYSPROP_TOKEN_FILE      = "loadimpact.token.file";
    public static final String SYSPROP_HTTP_VERBOSE    = "loadimpact.http.verbose";
    public static final String SYSPROP_HTTP_MAX        = "loadimpact.http.max";
    public static final int    DEFAULT_MAX_CHARS       = 10000;
    public static final String ENV_TOKEN_FILE          = "LOADIMPACT_TOKEN_FILE";
    public static final int    ONE_SECOND_AS_MILLISECS = 1000;
    public static final String TARGET_URL              = "https://loadimpact.com/";

    protected static String         apiToken;
    protected        ApiTokenClient client;

    @BeforeClass
    public static void checkToken() throws Exception {
        apiToken = findToken();
        if (apiToken == null) showInfoMessage();
        Assume.assumeTrue("Missing API token. No integration tests will be run.", apiToken != null);
    }

    @Before
    public void createClient() {
        client = new ApiTokenClient(apiToken);
        if (showHttp()) {
            client.setDebug(true, maxChars());
        }
    }

    /**
     * Tries to locate the API token, via a set of different ways.
     * <ol>
     *     <li>Direct via sysprop: <code>loadimpact.token</code></li>
     *     <li>Via properties file: <code>../loadimpact-token.properties</code></li>
     *     <li>Via sysprop pointing to properties file: <code>loadimpact.token.file</code></li>
     *     <li>Via environment variable pointing to properties file: <code>LOADIMPACT_TOKEN</code></li>
     * </ol>
     * @return API token, or null
     */
    protected static String findToken() {
        if (System.getProperties().containsKey(SYSPROP_TOKEN)) {
            return System.getProperty(SYSPROP_TOKEN);
        }
        return loadToken(findReader());
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
    protected static Reader findReader()  {
        Reader reader;

        String path = DEFAULT_TOKEN_PATH;
        if ((reader = openReader(path)) != null) {
            return reader;
        }

        path = System.getProperty(SYSPROP_TOKEN_FILE);
        if (path != null && (reader = openReader(path)) != null) {
            return reader;
        }

        path = System.getenv(ENV_TOKEN_FILE);
        if (path != null && (reader = openReader(path)) != null) {
            return reader;
        }

        return null;
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

    protected boolean showHttp() {
        String showHttp = System.getProperty(SYSPROP_HTTP_VERBOSE);
        if (showHttp != null) {
            return Boolean.parseBoolean(showHttp);
        }

        return false;
    }

    protected int maxChars() {
        String maxChars = System.getProperty(SYSPROP_HTTP_MAX);
        if (maxChars != null) {
            try {
                return Integer.parseInt(maxChars);
            } catch (NumberFormatException e) {
                return DEFAULT_MAX_CHARS;
            }
        }

        return DEFAULT_MAX_CHARS;
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


    interface WaitForClosure {
        boolean isDone();
    }

    protected void waitFor(String what, WaitForClosure expr) {
        waitFor(120, what, expr);
    }

    protected void waitFor(int maxWaitingTimeInSeconds, String what, WaitForClosure expr) {
        waitFor(maxWaitingTimeInSeconds, 5, what, expr);
    }

    protected void waitFor(int maxWaitingTimeInSeconds, int sleepTimeInSeconds, String what, WaitForClosure expr) {
        final long deadline = now() + maxWaitingTimeInSeconds * ONE_SECOND_AS_MILLISECS;

        System.out.printf("[%s] Waiting until %s %n", this.getClass().getSimpleName(), what);
        while (now() < deadline) {
            boolean done = false;
            try {
                done = expr.isDone();
            } catch (Exception e) {
                fail(e.toString());
            }
            if (done) break;

            System.out.print(".");
            System.out.flush();
            try {
                Thread.sleep(sleepTimeInSeconds * ONE_SECOND_AS_MILLISECS);
            } catch (InterruptedException e) {
                break;
            }
        }
        System.out.println();

        assertThat("Max waiting time (" + maxWaitingTimeInSeconds + " secs) exceeded", now(), lessThan(deadline));
    }

    protected long now() {
        return System.currentTimeMillis();
    }

    protected UserScenario createScenario() {
        final String scenarioScript = StringUtils.toString(getClass().getResourceAsStream(UsingScenarios.SCENARIO_RESOURCE));
        final String scenarioName   = "integration_test_" + System.nanoTime();

        UserScenario scenarioToBeCreated = new UserScenario();
        scenarioToBeCreated.name = scenarioName;
        scenarioToBeCreated.loadScript = scenarioScript;

        UserScenario scenario = client.createUserScenario(scenarioToBeCreated);
        assertThat(scenario, notNullValue());
        assertThat(scenario.name, is(scenarioName));
        assertThat(scenario.id, greaterThan(0));

        return scenario;
    }

    protected TestConfiguration createTestConfig(String targetUrl, int scenarioId) throws MalformedURLException {
        final String   configurationName = "integration_test_" + System.nanoTime();
        final int      testDuration      = 1;
        final int      testUserCount     = 10;
        final LoadZone trackZone         = LoadZone.AMAZON_US_ASHBURN;
        final int      trackPercentage   = 100;

        final TestConfiguration configurationToBeCreated = new TestConfiguration();
        configurationToBeCreated.name = configurationName;
        configurationToBeCreated.url = new URL(targetUrl);
        configurationToBeCreated.userType = UserType.SBU;
        configurationToBeCreated.loadSchedule.add(new LoadScheduleStep(testDuration, testUserCount));
        final LoadTrack track = new LoadTrack(trackZone);
        track.clip(trackPercentage, scenarioId);
        configurationToBeCreated.tracks.add(track);

        TestConfiguration configuration = client.createTestConfiguration(configurationToBeCreated);
        assertThat(configuration, notNullValue());
        assertThat(configuration.name, is(configurationName));
        assertThat(configuration.id, greaterThan(0));

        return configuration;
    }


}
