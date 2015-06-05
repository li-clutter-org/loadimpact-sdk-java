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
    

    @org.junit.Test
    public void starting_a_test_and_aborting_directly_should_pass() throws Exception {
        final UserScenario scenario   = createScenario();
        final TestConfiguration testConfig = createTestConfig(TARGET_URL, scenario.id);

        try {
            final int testId = client.startTest(testConfig.id);
            assertThat(testId, greaterThan(0));

            Test test = client.getTest(testId);
            assertThat(test, notNullValue());
            assertThat(test.id, is(testId));

            waitFor("load-test has state==running", new WaitForClosure() {
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
            client.deleteTestConfiguration(testConfig.id);
            client.deleteUserScenario(scenario.id);
        }
    }

    @org.junit.Test
    public void starting_a_test_monitoring_it_and_then_aborting_it_should_pass() throws Exception {
        final UserScenario scenario   = createScenario();
        final TestConfiguration testConfig = createTestConfig(TARGET_URL, scenario.id);

        try {
            final int testId = client.startTest(testConfig.id);
            assertThat(testId, greaterThan(0));

            Test test = client.getTest(testId);
            assertThat(test, notNullValue());
            assertThat(test.id, is(testId));

            waitFor("load-test has state==running", new WaitForClosure() {
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
            client.deleteTestConfiguration(testConfig.id);
            client.deleteUserScenario(scenario.id);
        }
    }

    @org.junit.Test
    public void starting_a_test_monitoring_it_and_waiting_for_completion_should_pass() throws Exception {
        final UserScenario scenario   = createScenario();
        final TestConfiguration testConfig = createTestConfig(TARGET_URL, scenario.id);

        try {
            final int testId = client.startTest(testConfig.id);
            assertThat(testId, greaterThan(0));

            Test test = client.getTest(testId);
            assertThat(test, notNullValue());
            assertThat(test.id, is(testId));

            waitFor("load-test has state==running", new WaitForClosure() {
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
            client.deleteTestConfiguration(testConfig.id);
            client.deleteUserScenario(scenario.id);
        }
    }


}
