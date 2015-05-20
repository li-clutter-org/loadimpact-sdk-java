package integration_tests;

import com.loadimpact.exception.ApiException;
import com.loadimpact.resource.UserScenario;
import com.loadimpact.resource.UserScenarioValidation;
import com.loadimpact.util.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;


/**
 * Verifies that it can create/fetch/delete a scenario.
 *
 * @user jens
 * @date 2015-05-15
 */
public class UsingScenarios extends AbstractIntegrationTestBase {
    public static final String SCENARIO_RESOURCE = "scenario.txt";

    @Test
    public void create_get_delete_of_scenario_should_pass() throws Exception {
        // Prepare
        final String scenarioScript = StringUtils.toString(getClass().getResourceAsStream(SCENARIO_RESOURCE));

        // Populate 
        final String scenarioName        = "integration_test_" + System.nanoTime();
        UserScenario scenarioToBeCreated = new UserScenario();
        scenarioToBeCreated.name = scenarioName;
        scenarioToBeCreated.loadScript = scenarioScript;

        // Upload/create
        UserScenario scenario = client.createUserScenario(scenarioToBeCreated);
        assertThat(scenario, notNullValue());
        assertThat(scenario.name, is(scenarioName));
        assertThat(scenario.id, greaterThan(0));

        // Fetch it
        final int    scenarioId      = scenario.id;
        UserScenario scenarioFetched = client.getUserScenario(scenarioId);
        assertThat(scenarioFetched, notNullValue());
        assertThat(scenarioFetched.name, is(scenarioName));

        // Fetch all
        List<UserScenario> scenarios = client.getUserScenarios();
        assertThat(scenarios, notNullValue());
        assertThat(scenarios.size(), greaterThanOrEqualTo(1));

        // Delete it
        client.deleteUserScenario(scenarioId);
        try {
            client.getDataStore(scenarioId);
            fail("Expected exception: NotFound");
        } catch (ApiException ignore) {
        }
    }


    @Test
    public void validating_a_newly_created_scenario_should_work() throws Exception {
        // Prepare
        final String scenarioScript = StringUtils.toString(getClass().getResourceAsStream(SCENARIO_RESOURCE));

        // Populate 
        final String scenarioName        = "integration_test_" + System.nanoTime();
        UserScenario scenarioToBeCreated = new UserScenario();
        scenarioToBeCreated.name = scenarioName;
        scenarioToBeCreated.loadScript = scenarioScript;

        // Upload/create
        UserScenario scenario = client.createUserScenario(scenarioToBeCreated);
        assertThat(scenario, notNullValue());
        assertThat(scenario.name, is(scenarioName));
        assertThat(scenario.id, greaterThan(0));
        final int scenarioId = scenario.id;

        
        // Start validate
        UserScenarioValidation validation = client.createUserScenarioValidation(scenarioId);
        assertThat(validation, notNullValue());
        assertThat(validation.scenarioId, is(scenarioId));
        final int validationId = validation.id;


        // Wait for validation completed
        final int  maxRetries = 30;
        final long delay      = 10 * 1000;
        int        retry      = 0;
        do {
            validation = client.getUserScenarioValidation(validationId);
            if (validation.status == UserScenarioValidation.Status.FINISHED) break;

            System.out.println("*** Waiting for scenario validation '" + scenarioName + "' to become ready. ");
            Thread.sleep(delay);
        } while (++retry < maxRetries);
        assertThat(validation.status, is(UserScenarioValidation.Status.FINISHED));
        assertThat(validation.ended, notNullValue());

        UserScenarioValidation results = client.getUserScenarioValidationResults(validation);
        assertThat(results, notNullValue());
        assertThat(results.results.size(), greaterThanOrEqualTo(1));
        assertThat(results.results.get(0).timestamp, notNullValue());
        assertThat(results.results.get(0).message, containsString("finished"));


        // Delete it
        client.deleteUserScenario(scenarioId);
        try {
            client.getUserScenario(scenarioId);
            fail("Expected exception: NotFound");
        } catch (ApiException ignore) {
        }
    }
    
}
