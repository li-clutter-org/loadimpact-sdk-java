package integration_tests;

import com.loadimpact.exception.ApiException;
import com.loadimpact.resource.LoadZone;
import com.loadimpact.resource.TestConfiguration;
import com.loadimpact.resource.UserScenario;
import com.loadimpact.resource.configuration.LoadScheduleStep;
import com.loadimpact.resource.configuration.LoadTrack;
import com.loadimpact.resource.configuration.UserType;
import com.loadimpact.util.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;


/**
 * Verifies it's possible to create/use/delete test-configurations.
 *
 * @user jens
 * @date 2015-05-15
 */
public class UsingTestConfigurations extends AbstractIntegrationTestBase {
    
    @Before
    public void debug() {
        client.setDebug(false);
    }


    @Test
    public void create_configuration_should_pass() throws Exception {
        final String   configurationName = "integration_test_" + System.nanoTime();
        final String   targetUrl         = "https://loadimpact.com/";
        final int      testDuration      = 10;
        final int      testUserCount     = 10;
        final LoadZone trackZone         = LoadZone.AMAZON_US_ASHBURN;
        final int      trackPercentage   = 100;
        int            trackScenarioId   = createScenario();

        // prepare
        final TestConfiguration configurationToBeCreated = new TestConfiguration();
        configurationToBeCreated.name = configurationName;
        configurationToBeCreated.url = new URL(targetUrl);
        configurationToBeCreated.userType = UserType.SBU;
        configurationToBeCreated.loadSchedule.add(new LoadScheduleStep(testDuration, testUserCount));
        final LoadTrack track = new LoadTrack(trackZone);
        track.clip(trackPercentage, trackScenarioId);
        configurationToBeCreated.tracks.add(track);

        // create
        TestConfiguration configuration = client.createTestConfiguration(configurationToBeCreated);
        assertThat(configuration, notNullValue());
        assertThat(configuration.id, greaterThan(0));
        assertThat(configuration.name, is(configurationName));
        final int configurationId = configuration.id;

        // fetch
        configuration = client.getTestConfiguration(configurationId);
        assertThat(configuration, notNullValue());
        assertThat(configuration.id, is(configurationId));
        
        // fetch all
        final List<TestConfiguration> configurations = client.getTestConfigurations();
        assertThat(configurations, notNullValue());
        assertThat(configurations.size(), greaterThanOrEqualTo(1));
        
        // delete
        client.deleteTestConfiguration(configurationId);
        try {
            client.getTestConfiguration(configurationId);
            fail("Expected: 404 Not Found");
        } catch (ApiException ignore) {
        }

        client.deleteUserScenario(trackScenarioId);
    }


    @Test
    public void create_and_clone_should_pass() throws Exception {
        final String   configurationName = "integration_test_" + System.nanoTime();
        final String   targetUrl         = "https://loadimpact.com/";
        final int      testDuration      = 10;
        final int      testUserCount     = 10;
        final LoadZone trackZone         = LoadZone.AMAZON_US_ASHBURN;
        final int      trackPercentage   = 100;
        int            trackScenarioId   = createScenario();

        // prepare
        final TestConfiguration configurationToBeCreated = new TestConfiguration();
        configurationToBeCreated.name = configurationName;
        configurationToBeCreated.url = new URL(targetUrl);
        configurationToBeCreated.userType = UserType.SBU;
        configurationToBeCreated.loadSchedule.add(new LoadScheduleStep(testDuration, testUserCount));
        final LoadTrack track = new LoadTrack(trackZone);
        track.clip(trackPercentage, trackScenarioId);
        configurationToBeCreated.tracks.add(track);

        // create
        TestConfiguration configuration = client.createTestConfiguration(configurationToBeCreated);
        assertThat(configuration, notNullValue());
        assertThat(configuration.id, greaterThan(0));
        assertThat(configuration.name, is(configurationName));
        
        // clone
        final String clonedName = "integration_test_cloned_" + System.nanoTime();
        TestConfiguration configurationCloned = client.cloneTestConfiguration(configuration.id, clonedName);
        assertThat(configurationCloned, notNullValue());
        assertThat(configurationCloned.name, is(clonedName));
        assertThat(configurationCloned.id, greaterThan(0));
        assertThat(configurationCloned.id, not(is(configuration.id)));

        // delete
        client.deleteTestConfiguration(configurationCloned.id);
        client.deleteTestConfiguration(configuration.id);
        client.deleteUserScenario(trackScenarioId);
    }


    @Test
    public void create_and_update_should_pass() throws Exception {
        final String   configurationName = "integration_test_" + System.nanoTime();
        final String   targetUrl         = "https://loadimpact.com/";
        final int      testDuration      = 10;
        final int      testUserCount     = 10;
        final LoadZone trackZone         = LoadZone.AMAZON_US_ASHBURN;
        final int      trackPercentage   = 100;
        int            trackScenarioId   = createScenario();

        // prepare
        final TestConfiguration configurationToBeCreated = new TestConfiguration();
        configurationToBeCreated.name = configurationName;
        configurationToBeCreated.url = new URL(targetUrl);
        configurationToBeCreated.userType = UserType.SBU;
        configurationToBeCreated.loadSchedule.add(new LoadScheduleStep(testDuration, testUserCount));
        final LoadTrack track = new LoadTrack(trackZone);
        track.clip(trackPercentage, trackScenarioId);
        configurationToBeCreated.tracks.add(track);

        // create
        TestConfiguration configuration = client.createTestConfiguration(configurationToBeCreated);
        assertThat(configuration, notNullValue());
        assertThat(configuration.id, greaterThan(0));
        assertThat(configuration.name, is(configurationName));

        // update
        TestConfiguration configurationToBeUpdated = new TestConfiguration(configuration.toJSON());
        configurationToBeUpdated.id = configuration.id;
        configurationToBeUpdated.name = configurationName.toUpperCase();
        configurationToBeUpdated.url = new URL("http://developers.loadimpact.com/api/index.html");
        
        TestConfiguration configurationUpdated = client.updateTestConfiguration(configurationToBeUpdated);
        assertThat(configurationUpdated, notNullValue());
        assertThat(configurationUpdated.name, is(configurationToBeUpdated.name));
        assertThat(configurationUpdated.url, is(configurationToBeUpdated.url));
        
        // delete
        client.deleteTestConfiguration(configuration.id);
        client.deleteUserScenario(trackScenarioId);
    }

    private int createScenario() {
        final String scenarioScript = StringUtils.toString(getClass().getResourceAsStream(UsingScenarios.SCENARIO_RESOURCE));

        final String scenarioName        = "integration_test_" + System.nanoTime();
        UserScenario scenarioToBeCreated = new UserScenario();
        scenarioToBeCreated.name = scenarioName;
        scenarioToBeCreated.loadScript = scenarioScript;

        UserScenario scenario = client.createUserScenario(scenarioToBeCreated);
        assertThat(scenario, notNullValue());
        assertThat(scenario.name, is(scenarioName));
        assertThat(scenario.id, greaterThan(0));

        return scenario.id;
    }


}
