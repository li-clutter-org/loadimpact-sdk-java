package integration_tests;

import com.loadimpact.ApiTokenClient;
import com.loadimpact.RunningTestListener;
import com.loadimpact.exception.AbortTest;
import com.loadimpact.exception.ApiException;
import com.loadimpact.resource.*;
import com.loadimpact.resource.configuration.LoadScheduleStep;
import com.loadimpact.resource.configuration.LoadTrack;
import com.loadimpact.resource.configuration.UserType;
import com.loadimpact.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;


/**
 * Runs a full load-test.
 *
 * @user jens
 * @date 2015-05-15
 */
public class RunningLoadTests extends AbstractIntegrationTestBase {
    public static final String TARGET_URL = "https://loadimpact.com/";

    @org.junit.Test
    public void starting_a_test_and_aborting_directly_should_pass() throws Exception {
        final int scenarioId   = createScenario();
        final int testConfigId = createTestConfig(TARGET_URL, scenarioId);

        try {
            final int testId = client.startTest(testConfigId);
            assertThat(testId, greaterThan(0));

            Test test = client.getTest(testId);
            assertThat(test, notNullValue());
            assertThat(test.id, is(testId));

            waitFor(120, "test has start running", new WaitForClosure() {
                @Override
                public boolean isDone() {
                    Test test = client.getTest(testId);
                    return test.status == Status.RUNNING;
                }
            });

            // abort test
            client.abortTest(testId);

            // verify
            test = client.getTest(testId);
            assertThat(test, notNullValue());
            assertThat(test.status, anyOf(is(Status.ABORTING_BY_USER), is(Status.ABORTED_BY_USER)));
        } finally {
            client.deleteTestConfiguration(testConfigId);
            client.deleteUserScenario(scenarioId);
        }
    }

    @org.junit.Test
    public void starting_a_test_monitoring_it_and_then_aborting_it_should_pass() throws Exception {
        final int scenarioId   = createScenario();
        final int testConfigId = createTestConfig(TARGET_URL, scenarioId);

        try {
            final int testId = client.startTest(testConfigId);
            assertThat(testId, greaterThan(0));

            Test test = client.getTest(testId);
            assertThat(test, notNullValue());
            assertThat(test.id, is(testId));

            waitFor(120, "test has start running", new WaitForClosure() {
                @Override
                public boolean isDone() {
                    Test test = client.getTest(testId);
                    return test.status.isRunning();
                }
            });

            final long deadline = now() + 60 * ONE_SECOND_AS_MILLISECS;
            Test monitoredTest = client.monitorTest(testId, 10, new RunningTestListener() {
                @Override
                public void onProgress(Test test, ApiTokenClient client) {
                    assertThat(test.status.isRunning(), is(true));
                    if (deadline < now()) {
                        throw new AbortTest();
                    }
                }

                @Override
                public void onAborted() {
                    assertTrue("OK", true);
                }

                @Override
                public void onSuccess(Test test) {
                    fail("onSuccess");
                }

                @Override
                public void onFailure(Test test) {
                    fail("onFailure");
                }

                @Override
                public void onError(ApiException error) {
                    fail("onFailure");
                }
            });
            assertThat(monitoredTest, nullValue());

            // verify
            test = client.getTest(testId);
            assertThat(test, notNullValue());
            assertThat(test.status, anyOf(is(Status.ABORTING_BY_USER), is(Status.ABORTED_BY_USER)));
        } finally {
            client.deleteTestConfiguration(testConfigId);
            client.deleteUserScenario(scenarioId);
        }
    }

    @org.junit.Test
    public void starting_a_test_monitoring_it_and_waiting_for_completion_should_pass() throws Exception {
        final int scenarioId   = createScenario();
        final int testConfigId = createTestConfig(TARGET_URL, scenarioId);

        try {
            final int testId = client.startTest(testConfigId);
            assertThat(testId, greaterThan(0));

            Test test = client.getTest(testId);
            assertThat(test, notNullValue());
            assertThat(test.id, is(testId));

            waitFor(120, "test has start running", new WaitForClosure() {
                @Override
                public boolean isDone() {
                    Test test = client.getTest(testId);
                    return test.status.isRunning();
                }
            });

            Test monitoredTest = client.monitorTest(testId, 10, new RunningTestListener() {
                @Override
                public void onProgress(Test test, ApiTokenClient client) {
                    assertThat(test.status.isRunning(), is(true));
                }

                @Override
                public void onAborted() {
                    fail("onAborted");
                }

                @Override
                public void onSuccess(Test test) {
                    assertThat(test.status, is(Status.FINISHED));
                }

                @Override
                public void onFailure(Test test) {
                    fail("onFailure");
                }

                @Override
                public void onError(ApiException error) {
                    fail("onFailure");
                }
            });
            assertThat(monitoredTest, notNullValue());
            assertThat(monitoredTest.id, is(testId));
            assertThat(monitoredTest.status.isSuccessful(), is(true));

            // verify
            test = client.getTest(testId);
            assertThat(test, notNullValue());
            assertThat(test.status.isSuccessful(), is(true));
        } finally {
            client.deleteTestConfiguration(testConfigId);
            client.deleteUserScenario(scenarioId);
        }
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

    private int createTestConfig(String targetUrl, int scenarioId) throws MalformedURLException {
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

        return configuration.id;
    }

}
