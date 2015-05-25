# Load Impact Java SDK [![Build Status](https://travis-ci.org/loadimpact/loadimpact-sdk-java.png?branch=master,develop)](https://travis-ci.org/loadimpact/loadimpact-sdk-java)

This Java SDK provides Java APIs to the Load Impact platform for running 
and managing performance tests in the cloud.

## Requirements

The Load Impact Java SDK works with Java 6 or later. It has two dependencies (both open-source):
* [Jersey](https://jersey.java.net/)
* [Joda-Time](http://www.joda.org/joda-time/)

# How to build

## Gradle

[Gradle](https://gradle.org/) is required for building this SDK. The gradle wrapper is configured, which
means there is no need to download and install gradle just to build this SDK. Just use the provided `gradlew` script.

    ./gradlew tasks         //*NIX
    gradlew.bat tasks       //Windows

## JAR file(s)

Compile and build the JAR file(s)

    gradlew assemble

The JAR files can then be found in the `./build/libs/` directory

    $ ls -lhF build/libs/
    .... 133k ... loadimpact-sdk-java-1.4-lib.jar
    .... 3.9M ... loadimpact-sdk-java-1.4-withDepends.jar

The '*-withDepends.jar' file contains this SDK together with all of its dependencies. Add this JAR file
to the class-path of your application and you're good to go. 

Use the '*-lib.jar' file if you plan to add the dependencies yourself. Check the `dependencies` block in
`./build.gradle` to figure out which dependencies to add.

## Check your API token

The JAR file contains a small Java application that you can use to verify your API token.
To obtain your API token, you can find it on your [loadimpact.com account page](https://loadimpact.com/account/).
Run the app by

    java -jar ./build/libs/loadimpact-sdk-java-1.4-withDepends.jar {your API token here}

If it was successful you will see a list of load-zones, else you will see a MissingApiToken exception.

## Run unit tests

Run the unit tests suite by

    gradlew test
    gradlew test --tests '*UrlMetric*'      //choose one or more test classes (omit the quotes on Windows)

Gradle will generate a HTML test report in `./build/reports/tests/index.html`

## Run integration tests

This SDK also has a suite of integration tests, which all need to logon to a valid LoadImpact account, given a
valid API token. If you try running the integration tests without a proper API token, you will see the following message

    In order for the integration tests to run, it requires a valid API token to run. Follow these steps:
    
    (1) Create a LoadImpact account, unless you already have it.
    
    (2) Get the API token.
        (a) Open the user profile (click a username)
        (b) Choose tab "API Token"
        (c) Generate a token, if needed
        (d) Save the token to a Java properties file
        (e) Add one single line (including EOL)
            api.token=<your API token string here>
    
    (3) Save the token file 
        (alt 1) Save it with the file path "../loadimpact-token.properties" (i.e. in the parent director of this project)
        (alt 2) Set the Java system property "loadimpact.token.file" with the file path
        (alt 3) Set the environment variable "LOADIMPACT_TOKEN_FILE" with the file path

Choose your preferred way of providing the API token and run the integration tests suite by

    gradlew integrationTest 
    gradlew integrationTest --tests '*ApiToken*'    //choose one or more test classes (omit the quotes on Windows)

Gradle will generate a HTML test report in `./build/reports/integration-tests/index.html`

## Generate JavaDocs

Generate the JavaDocs by

    gradlew javadoc

Gradle will generate the docs into `./build/docs/javadoc/index.html`

## Generate a gradle project report

Run the following command

    gradlew projectReport

This will generate a HTML file of all dependencies and their transitive dependencies in `./build/reports/project/dependencies/root.html`.
In addition, there are some text file reports in `./build/reports/project/*.txt`

## Cleaning up

Run the following command to remove the build directory and all generated files

    gradlew clean


# Using the SDK


## Installation

If the SDK is packaged as a zip-file, then unpack it and grab the JAR file. Add the jar file to the class-path.

Check the exact artifact names and versions used in the generated dependency documentation. It's probably easiest to
use Maven, Gradle or a similar tool to just add the dependency of this SDK and the tool will take care of
downloading and caching all dependent JAR files.

The SDK JAR is also available through BinTray jCenter.

If you are using Gradle, add this configuration:

```
repositories {
    jcenter()
}

dependencies {
    compile group: 'com.loadimpact', name: 'loadimpact-sdk-java', version: '1.4', classifier: 'withDepends'
}
```



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
