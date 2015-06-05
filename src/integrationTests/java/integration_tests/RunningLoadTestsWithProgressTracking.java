package integration_tests;

import com.loadimpact.eval.*;
import com.loadimpact.resource.Test;
import com.loadimpact.resource.TestConfiguration;
import com.loadimpact.resource.UserScenario;
import com.loadimpact.util.Debug;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Runs a load-test with progress monitoring.
 *
 * @user jens
 * @date 2015-05-25
 */
public class RunningLoadTestsWithProgressTracking extends AbstractIntegrationTestBase {
    public static final int SECONDS = 1;

    @org.junit.Test
    public void start_a_loadtest_with_monitoring_by_LoadTestListener_should_pass() throws Exception {
//        Debug.setEnabled(true);
//        client.setDebug(true, 100000);

        final UserScenario      scenario   = createScenario();
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
                    return client.getTest(testId).status.isRunning();
                }
            });

            DummyLoadTestParameters params = new DummyLoadTestParameters(testConfig);
            DummyLoadTestLogger logger = new DummyLoadTestLogger();
            DummyLoadTestResultListener resultListener = new DummyLoadTestResultListener();
            
            LoadTestListener loadTestListener = new LoadTestListener(params, logger, resultListener);
            loadTestListener.onSetup(testConfig, client);

            Test monitoredTest = client.monitorTest(testId, 5 * SECONDS, loadTestListener);
            assertThat(monitoredTest, notNullValue());
            assertThat(monitoredTest.status.isSuccessful(), is(true));

            String log = logger.getText();
            assertThat(log, containsString("Load-Test Completed"));
        } finally {
            client.deleteTestConfiguration(testConfig.id);
            client.deleteUserScenario(scenario.id);
        }
    }

    static class DummyLoadTestLogger implements LoadTestLogger {
        private StringWriter buf = new StringWriter(100000);
        private PrintWriter  log = new PrintWriter(buf, false);

        private void emit(String label, String msg) {
            System.out.printf("[%s] %s%n", label, msg);
            System.out.flush();
            
            log.printf("[%s] %s%n", label, msg);
        }

        public String getText() {
            log.flush();
            return buf.toString();
        }

        @Override
        public void started(String msg) {
            emit("started", msg);
        }

        @Override
        public void finished(String msg) {
            emit("finished", msg);
        }

        @Override
        public void failure(String reason) {
            emit("failure", reason);
        }

        @Override
        public void message(String msg) {
            emit("progress", msg);
        }

        @Override
        public void message(String fmt, Object... args) {
            message(String.format(fmt, args));
        }
    }

    static class DummyLoadTestParameters implements LoadTestParameters {
        private TestConfiguration testConfig;

        public DummyLoadTestParameters(TestConfiguration testConfig) {
            this.testConfig = testConfig;
        }

        @Override
        public String getApiToken() {
            fail("unexpected call to LoadTestParameters::getApiToken()");
            return null;
        }

        @Override
        public int getTestConfigurationId() {
            return testConfig.id;
        }

        @Override
        public Threshold[] getThresholds() {
            return new Threshold[0];
        }

        @Override
        public DelayUnit getDelayUnit() {
            return DelayUnit.seconds;
        }

        @Override
        public int getDelayValue() {
            return 3;
        }

        @Override
        public int getDelaySize() {
            return 0;
        }

        @Override
        public boolean isAbortAtFailure() {
            return true;
        }

        @Override
        public int getPollInterval() {
            return 5;
        }

        @Override
        public boolean isLogHttp() {
            return true;
        }

        @Override
        public boolean isLogReplies() {
            return true;
        }

        @Override
        public boolean isLogDebug() {
            return true;
        }
    }
    
    static class DummyLoadTestResultListener implements LoadTestResultListener {
        private LoadTestResult result;
        private String         reason;

        @Override
        public void markAs(LoadTestResult result, String reason) {
            this.result = result;
            this.reason = reason;
        }

        @Override
        public LoadTestResult getResult() {
            return result;
        }

        @Override
        public String getReason() {
            return reason;
        }

        @Override
        public void stopBuild() {
            System.out.println("*** stopBuild()");
        }

        @Override
        public boolean isFailure() {
            return false;
        }

        @Override
        public boolean isNonSuccessful() {
            return false;
        }
    }

}
