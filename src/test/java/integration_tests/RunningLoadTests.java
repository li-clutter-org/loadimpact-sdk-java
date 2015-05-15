package integration_tests;

import com.loadimpact.resource.*;
import com.loadimpact.resource.configuration.LoadScheduleStep;
import com.loadimpact.resource.configuration.LoadTrack;
import com.loadimpact.resource.configuration.UserType;
import com.loadimpact.util.StringUtils;
import org.junit.Before;

import java.net.MalformedURLException;
import java.net.URL;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * DESCRIPTION
 *
 * @user jens
 * @date 2015-05-15
 */
public class RunningLoadTests extends AbstractIntegrationTestBase {

    @Before
    public void debug() {
        client.setDebug(true);
    }


    @org.junit.Test
    public void starting_and_aborting_test_should_pass() throws Exception {
        // prepare
        final String targetUrl    = "https://loadimpact.com/";
        final int    scenarioId   = createScenario();
        final int    testConfigId = createTestConfig(targetUrl, scenarioId);

        // start
        int testId = client.startTest(testConfigId);
        assertThat(testId, greaterThan(0));

        delay(20);
        Test test = client.getTest(testId);
        assertThat(test, notNullValue());
        assertThat(test.id, is(testId));
        assertThat(test.url, is(new URL(targetUrl)));

        // abort test
        client.abortTest(testId);

        // verify
        test = client.getTest(testId);
        assertThat(test, notNullValue());
        assertThat(test.status, anyOf(is(Status.ABORTING_BY_USER), is(Status.ABORTED_BY_USER)));

        // clean-up
        client.deleteTestConfiguration(testConfigId);
        client.deleteUserScenario(scenarioId);
    }


    private void delay(int numSecs) {
        System.out.printf("Waiting %s seconds for the test to launch", numSecs);
        do {
            System.out.print('.');
            System.out.flush();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignore) {
            }
        } while (numSecs-- > 0);
    }

    private int createTestConfig(String targetUrl, int scenarioId) throws MalformedURLException {
        final String   configurationName = "integration_test_" + System.nanoTime();
        final int      testDuration      = 10;
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

        return configuration.id;
    }

    private int createScenario() {
        final String scenarioScript = StringUtils.toString(getClass().getResourceAsStream(UsingScenarios.SCENARIO_RESOURCE));
        final String scenarioName   = "integration_test_" + System.nanoTime();

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
