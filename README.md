# Load Impact Java SDK [![Build Status](https://travis-ci.org/loadimpact/loadimpact-sdk-java.png?branch=master,develop)](https://travis-ci.org/loadimpact/loadimpact-sdk-java)

This Java SDK provides Java APIs to the Load Impact platform for running 
and managing performance tests in the cloud.

## Requirements

The Load Impact Java SDK works with Java 6 or later. It has two dependencies (both open-source):
* [Jersey](https://jersey.java.net/)
* [Joda-Time](http://www.joda.org/joda-time/)

## Build

Apache Maven is required for building this SDK.

Compile and build the JAR file

    mvn package

The JAR can then be found in `target/`

Generate the documentation

    mvn site

The docs can then be found in `target/site/`

## Tests

Run the test suite

    mvn test -Duser.timezone=UTC -Dgpg.skip=true

## Installation

If the SDK is packaged as a zip-file, then unpack it and grab the JAR file. Add the jar file to the class-path.

Check the exact artifact names and versions used in the generated dependency documentation. It's probably easiest to
use Maven, Gradle or a similar tool to just add the dependency of this SDK and the tool will take care of
downloading and caching all dependent JAR files.

## Creating an API client

To create an API client instance you need your API token. You can find it on
your [loadimpact.com account page](https://loadimpact.com/account/).

You provide the token as an argument to the constructor of the client.

```java
import com.loadimpact.ApiTokenClient;

ApiTokenClient client = new ApiTokenClient("YOUR_API_TOKEN_GOES_HERE");
```

## Using an API client

All API calls performed by the client are so called synchronous run-to-completion, which means that it connects 
(via HTTPS) to the Load Impact API, sends the request and waits for the JSON response which is transformed
into a value object. The connections is closed after each API call.

### List test configurations
```java
import com.loadimpact.ApiTokenClient;
import com.loadimpact.resource.TestConfiguration;
import java.util.List;

public class ListTestConfigsExample {
    public static void main(String[] args) {
        ApiTokenClient client = new ApiTokenClient("YOUR_API_TOKEN_GOES_HERE");
        List<TestConfiguration> configs = client.getTestConfigurations();
    }
}
```

### Get a specific test configurations
```java
import com.loadimpact.ApiTokenClient;
import com.loadimpact.resource.TestConfiguration;

public class ListTestConfigsExample {
    public static void main(String[] args) {
        ApiTokenClient client = new ApiTokenClient("YOUR_API_TOKEN_GOES_HERE");
        final int testConfigId = 1;
        TestConfiguration config = client.getTestConfiguration(testConfigId);
    }
}
```

### Create a new test configuration
```java
import com.loadimpact.ApiTokenClient;
import com.loadimpact.resource.LoadZone;
import com.loadimpact.resource.TestConfiguration;
import com.loadimpact.resource.configuration.LoadClip;
import com.loadimpact.resource.configuration.LoadScheduleStep;
import com.loadimpact.resource.configuration.LoadTrack;
import com.loadimpact.resource.configuration.UserType;

public class CreateTestConfigExample {
    public static void main(String[] args) {
        final int userScenarioId = 1;
        final int durationSecs = 300;
        final int users = 50;

        TestConfiguration config = new TestConfiguration();
        config.name = "My test configuration";
        config.url = "http://example.com/";
        config.userType = UserType.SBU;
        config.loadSchedule.add(new LoadScheduleStep(durationSecs, users));
        config.tracks.add(new LoadTrack(LoadZone.AMAZON_US_ASHBURN).clip(100, userScenarioId));

        config = client.createTestConfiguration(config);
    }
}
```

The available load zone are as follows:
```java
import com.loadimpact.resource.LoadZone;

// Amazon load zones
LoadZone.AMAZON_US_ASHBURN;
LoadZone.AMAZON_US_ASHBURN;
LoadZone.AMAZON_US_PALOALTO;
LoadZone.AMAZON_IE_DUBLIN;
LoadZone.AMAZON_SG_SINGAPORE;
LoadZone.AMAZON_JP_TOKYO;
LoadZone.AMAZON_US_PORTLAND;
LoadZone.AMAZON_BR_SAOPAULO;
LoadZone.AMAZON_AU_SYDNEY;

// Rackspace load zones
LoadZone.RACKSPACE_US_CHICAGO;
LoadZone.RACKSPACE_US_DALLAS;
LoadZone.RACKSPACE_UK_LONDON;
LoadZone.RACKSPACE_AU_SYDNEY;
```

### Update an existing test configuration
```java
import com.loadimpact.ApiTokenClient;
import com.loadimpact.resource.TestConfiguration;

public class UpdateTestConfigExample {
    public static void main(String[] args) {
        ApiTokenClient client = new ApiTokenClient("YOUR_API_TOKEN_GOES_HERE");
        final int testConfigId = 1;
        TestConfiguration config = client.getTestConfiguration(testConfigId);
        config.name = "My new test configuration name";
        config = client.updateTestConfiguration(config);
    }
}
```

### Delete config
```java
import com.loadimpact.ApiTokenClient;

public class DeleteTestConfigExample {
    public static void main(String[] args) {
        ApiTokenClient client = new ApiTokenClient("YOUR_API_TOKEN_GOES_HERE");
        final int testConfigId = 1;
        client.deleteTestConfiguration(testConfigId);
    }
}
```

### Run test and stream results to STDOUT
```java
import com.loadimpact.ApiTokenClient;
import com.loadimpact.resource.LoadZone;
import com.loadimpact.resource.TestConfiguration;
import com.loadimpact.resource.testresult.StandardMetricResult;

public class RunTestAndStreamResultsExample {
    public static void main(String[] args) {
        ApiTokenClient client = new ApiTokenClient("YOUR_API_TOKEN_GOES_HERE");
        final int testConfigId = 1;
        TestConfiguration config = client.getTestConfiguration(testConfigId);
        final int testId = client.startTest(testConfigId);
        Test test = client.getTest(testId);

        int offset = 0;
        while (test.status.isInProgress()) {
            List<StandardMetricResult> results = client.getStandardMetricResults(
                test.id, StandardMetricResult.Metrics.REQUESTS_PER_SECOND, LoadZone.AMAZON_US_PALOALTO,
                ApiTokenClient.OffsetRange.mk(offset, offset + 100));

            for (StandardMetricResult result : results) {
                System.out.println(result);
            }

            if (results.size()) {
                offset = Math.max(offset, results.get(results.size() - 1).offset);
            }
        }
    }
}
```

### Create a new user scenario
```java
import com.loadimpact.ApiTokenClient;
import com.loadimpact.resource.UserScenario;

public class CreateUserScenarioExample {
    public static void main(String[] args) {
        ApiTokenClient client = new ApiTokenClient("YOUR_API_TOKEN_GOES_HERE");
        String loadScript = "local response = http.get(\"http://example.com\")\n"
                          + "log.info(\"Load time: \"..response.total_load_time..\"s\")\n"
                          + "client.sleep(5)";
        UserScenario userScenario = new UserScenario();
        userScenario.name = "My user scenario";
        userScenario.loadScript = loadScript;
        userScenario = client.createUserScenario(userScenario)
    }
}
```

### Validating a user scenario
```java
import com.loadimpact.ApiTokenClient;
import com.loadimpact.resource.UserScenarioValidation;

public class UserScenarioValidationExample {
    public static void main(String[] args) {
        ApiTokenClient client = new ApiTokenClient("YOUR_API_TOKEN_GOES_HERE");

        final int userScenarioId = 1
        UserScenarioValidation validation = client.createUserScenarioValidation(userScenarioId)

        int retries = 10, sleepTime = 3000;
        for (int k = 1; k <= retries && validation.status != UserScenarioValidation.Status.FINISHED; ++k) {
            Thread.sleep(sleepTime);
            validation = client.getUserScenarioValidationResults(validation);
        }

        for (UserScenarioValidation.Result result : validation.results) {
            System.out.println("[" + result.timestamp "]: " + result.message);
        }
    }
}
```

### Uploading a data store (CSV file with parameterization data)
For more information regarding parameterized data have a look at [this
knowledgebase article](http://support.loadimpact.com/knowledgebase/articles/174258-how-do-i-use-parameterized-data-).

```java
import com.loadimpact.ApiTokenClient;
import com.loadimpact.resource.DataStore;
import java.io.File;

public class CreateDataStoreExample {
    public static void main(String[] args) {
        ApiTokenClient client = new ApiTokenClient("YOUR_API_TOKEN_GOES_HERE");

        File file = new File("data.csv");
        String name = "My data store";
        final int fromLine = 2;
        DataStore dataStore = client.createDataStore(file, name, fromLine, DataStore.Separator.SEMICOLON, DataStore.StringDelimiter.DOUBLEQUOTE);

        int retries = 10, sleepTime = 3000;
        for (int k = 1; k <= retries && dataStore.status != DataStore.Status.READY; ++k) {
            Thread.sleep(sleepTime);
            dataStore = client.getDataStore(dataStore.id);
        }
    }
}
```

### Adding a data store to a user scenario
```java
import com.loadimpact.ApiTokenClient;
import com.loadimpact.resource.DataStore;
import com.loadimpact.resource.UserScenario;

public class AddDataStoreToUserScenarioExample {
    public static void main(String[] args) {
        ApiTokenClient client = new ApiTokenClient("YOUR_API_TOKEN_GOES_HERE");
        UserScenario userScenario = client.getUserScenario(1)
        DataStore dataStore = client.getDataStore(1)
        userScenario.dataStores.add(dataStore.id)
        client.updateUserScenario(userScenario)
    }
}
```
