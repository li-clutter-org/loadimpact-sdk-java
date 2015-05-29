/*
 * Copyright 2014 Load Impact
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.loadimpact;

import com.loadimpact.exception.*;
import com.loadimpact.resource.DataStore;
import com.loadimpact.resource.HttpMethods;
import com.loadimpact.resource.LoadZone;
import com.loadimpact.resource.Test;
import com.loadimpact.resource.TestConfiguration;
import com.loadimpact.resource.UserScenario;
import com.loadimpact.resource.UserScenarioValidation;
import com.loadimpact.resource.testresult.CustomMetricResult;
import com.loadimpact.resource.testresult.PageMetricResult;
import com.loadimpact.resource.testresult.ServerMetricResult;
import com.loadimpact.resource.testresult.StandardMetricResult;
import com.loadimpact.resource.testresult.UrlMetricResult;
import com.loadimpact.util.ObjectUtils;
import com.loadimpact.util.StringUtils;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jsonp.JsonProcessingFeature;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Primary entry point for the Load Impact API Java SDK. <h2>Sample Usage</h2>
 *
 * @author jens
 */
@SuppressWarnings("UnusedDeclaration")
public class ApiTokenClient {
    private static final String baseUri                   = "https://api.loadimpact.com/v2";
    private static final String HEX_PATTERN               = "[a-fA-F0-9]+";
    private static final int    TOKEN_LENGTH              = 64;
    private static final String USER_SCENARIOS            = "user-scenarios";
    private static final String DATA_STORES               = "data-stores";
    private static final String LOAD_ZONES                = "load-zones";
    private static final String TEST_CONFIGS              = "test-configs";
    private static final String USER_SCENARIO_VALIDATIONS = "user-scenario-validations";
    private static final String TESTS                     = "tests";
    private static final String RESULTS                   = "results";
    private static final String ABORT                     = "abort";
    private static final String BUILD_DATA                = "/buildData.properties";
    private static final String AGENT_REQHDR              = "X-Load-Impact-Agent";

    @Deprecated
    private static List<String> HTTP_METHODS = Arrays.asList("GET", "POST", "HEAD", "PUT", "DELETE");  //excluded: OPTIONS, TRACE

    private final String    apiToken;
    private final Logger    log;
    private       WebTarget wsBase;
    private       String    agentRequestHeaderValue;

    {   // Initialize the logger
        log = Logger.getLogger(this.getClass().getName());
    }

    /**
     * Constructor intended for unit-testing only.
     */
    protected ApiTokenClient() {
        apiToken = null;
        wsBase = null;
    }

    /**
     * Creates a client and initializes the REST client with the given API token.
     *
     * @param apiToken API token to use for authentication
     */
    public ApiTokenClient(String apiToken) {
        checkApiToken(apiToken);
        this.apiToken = apiToken;
        this.wsBase = configure(this.apiToken, false, this.log, 0);
    }

    public Properties getBuildData() {
        Properties  buildData = new Properties();
        InputStream is        = getClass().getResourceAsStream(BUILD_DATA);
        if (is != null) {
            try {
                buildData.load(is);
            } catch (IOException ignore) {
            }
        }
        return buildData;
    }

    public String getVersion() {
        return getBuildData().getProperty("version", "0.0.0");
    }

    protected String getAgentRequestHeaderValue() {
        if (agentRequestHeaderValue == null) {
            agentRequestHeaderValue = String.format("LoadImpactJavaSDK/%s", getVersion());
        }
        return agentRequestHeaderValue;
    }

    public void setAgentRequestHeaderValue(String agentRequestHeaderValue) {
        this.agentRequestHeaderValue = agentRequestHeaderValue;
    }

    /**
     * Enables/disabled REQ/RES debug logging. This method re-configures the web-target, via {@link #configure(String,
     * boolean, java.util.logging.Logger, int)}.
     *
     * @param debug true for logging
     * @return itself (for chaining)
     */
    public ApiTokenClient setDebug(boolean debug) {
        return setDebug(debug, 10000);
    }

    /**
     * Enables REQ/RES debug logging. This method re-configures the web-target, via {@link #configure(String, boolean,
     * java.util.logging.Logger, int)}.
     *
     * @param debug         true for logging
     * @param maxEntitySize max number of chars for dumping the content of an entity (e.g. response body)
     * @return itself (for chaining)
     */
    public ApiTokenClient setDebug(boolean debug, int maxEntitySize) {
        return setDebug(debug, maxEntitySize, log);
    }

    /**
     * Enables REQ/RES debug logging. This method re-configures the web-target, via {@link #configure(String, boolean,
     * java.util.logging.Logger, int)}.
     *
     * @param debug         true for logging
     * @param maxEntitySize max number of chars for dumping the content of an entity (e.g. response body)
     * @param log           non-standard log stream
     * @return itself (for chaining)
     */
    public ApiTokenClient setDebug(boolean debug, int maxEntitySize, Logger log) {
        wsBase = configure(apiToken, debug, log, maxEntitySize);
        return this;
    }

    /**
     * Configures this client, by settings HTTP AUTH filter, the REST URL and logging.
     *
     * @param token  its API Token
     * @param debug  true for Jersey REQ/RES logging
     * @param log    debug log stream (if debug==true)
     * @param maxLog max size of logged entity (if debug==true)
     * @return configured REST URL target
     */
    private WebTarget configure(String token, boolean debug, Logger log, int maxLog) {
        Client client = ClientBuilder.newBuilder()
                .register(MultiPartFeature.class)
                .register(JsonProcessingFeature.class)
                .build();

        client.register(HttpAuthenticationFeature.basic(token, ""));
        if (debug) client.register(new LoggingFilter(log, maxLog));

        return client.target(baseUri);
    }

    /**
     * Invocation closure used to modify the web-target before requests processing.
     */
    static interface QueryClosure {
        /**
         * Intercepts right before requests processing starts.
         *
         * @param webTarget object to modify
         * @return modified object
         */
        WebTarget modify(WebTarget webTarget);
    }

    /**
     * Invocation closure used to setup the request, such as GET, POST etc.
     *
     * @param <JsonType> type such as JsonObject, JsonArray, etc
     */
    static interface RequestClosure<JsonType extends JsonStructure> {
        /**
         * Creates the request and returns the response entity type
         *
         * @param request the request object
         * @return response entity type
         */
        JsonType call(Invocation.Builder request);
    }

    /**
     * Invocation closure used to process the response entity.
     *
     * @param <JsonType>  response entity type (same as for {@link ApiTokenClient.RequestClosure#call(javax.ws.rs.client.Invocation.Builder)}
     *                    )
     * @param <ValueType> return type (classes from the <code>data</code> package)
     */
    static interface ResponseClosure<JsonType extends JsonStructure, ValueType> {
        /**
         * Converts the response JSON into a value-type (package <code>data</code>)
         *
         * @param json JSON type, such as JsonObject, JsonArray
         * @return a value-type object or list of it
         */
        ValueType call(JsonType json);
    }

    public static class OffsetRange implements Serializable {
        public final int start;
        public final int end;

        public OffsetRange(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public String toString() {
            return String.format("|%d:%d", start, end);
        }

        public static OffsetRange mk(int start, int end) {
            return new OffsetRange(start, end);
        }
    }

    /**
     * Performs a REST API invocation. This is wrapper function for {@link #invoke(String, String, String,
     * ApiTokenClient.QueryClosure, ApiTokenClient.RequestClosure,
     * ApiTokenClient.ResponseClosure)}
     *
     * @param operation       operation name, such as <code>tests</code>, <code>data-stores</code>, etc
     * @param requestClosure  closure to create the request, such as GET, POST, PUT, DELETE
     * @param responseClosure closure to convert the response JSON into a value-object
     * @param <JsonType>      JSON type, such as JsonObject, JsonArray
     * @param <ValueType>     value-type, such as {@link com.loadimpact.resource.Test}, {@link
     *                        com.loadimpact.resource.UserScenario}, etc
     * @return a single value-object or a list of it
     * @throws com.loadimpact.exception.ApiException if anything goes wrong, such as the server returns HTTP status not being 20x
     */
    protected <JsonType extends JsonStructure, ValueType>
    ValueType invoke(String operation, RequestClosure<JsonType> requestClosure, ResponseClosure<JsonType, ValueType> responseClosure) {
        return invoke(operation, null, null, null, requestClosure, responseClosure);
    }

    /**
     * Performs a REST API invocation. This is wrapper function for {@link #invoke(String, String, String,
     * ApiTokenClient.QueryClosure, ApiTokenClient.RequestClosure,
     * ApiTokenClient.ResponseClosure)}
     *
     * @param operation       operation name, such as <code>tests</code>, <code>data-stores</code>, etc
     * @param id              resource ID (if fetching a specific resource)
     * @param requestClosure  closure to create the request, such as GET, POST, PUT, DELETE
     * @param responseClosure closure to convert the response JSON into a value-object
     * @param <JsonType>      JSON type, such as JsonObject, JsonArray
     * @param <ValueType>     value-type, such as {@link com.loadimpact.resource.Test}, {@link
     *                        com.loadimpact.resource.UserScenario}, etc
     * @return a single value-object or a list of it
     * @throws com.loadimpact.exception.ApiException if anything goes wrong, such as the server returns HTTP status not being 20x
     */
    protected <JsonType extends JsonStructure, ValueType>
    ValueType invoke(String operation, int id, RequestClosure<JsonType> requestClosure, ResponseClosure<JsonType, ValueType> responseClosure) {
        return invoke(operation, Integer.toString(id), null, null, requestClosure, responseClosure);
    }

    /**
     * Performs a REST API invocation. This is wrapper function for {@link #invoke(String, String, String,
     * ApiTokenClient.QueryClosure, ApiTokenClient.RequestClosure,
     * ApiTokenClient.ResponseClosure)}
     *
     * @param operation       operation name, such as <code>tests</code>, <code>data-stores</code>, etc
     * @param id              resource ID (if fetching a specific resource)
     * @param action          optional action, such as <code>clone</code>, <code>abort</code>, etc
     * @param requestClosure  closure to create the request, such as GET, POST, PUT, DELETE
     * @param responseClosure closure to convert the response JSON into a value-object
     * @param <JsonType>      JSON type, such as JsonObject, JsonArray
     * @param <ValueType>     value-type, such as {@link com.loadimpact.resource.Test}, {@link
     *                        com.loadimpact.resource.UserScenario}, etc
     * @return a single value-object or a list of it
     * @throws com.loadimpact.exception.ApiException if anything goes wrong, such as the server returns HTTP status not being 20x
     */
    protected <JsonType extends JsonStructure, ValueType>
    ValueType invoke(String operation, int id, String action, RequestClosure<JsonType> requestClosure, ResponseClosure<JsonType, ValueType> responseClosure) {
        return invoke(operation, Integer.toString(id), action, null, requestClosure, responseClosure);
    }

    protected <JsonType extends JsonStructure, ResultType>
    List<ResultType> invokeForResults(int testId, final String ids, final OffsetRange range, final Class<ResultType> resultType) {
        return invoke(TESTS, Integer.toString(testId), RESULTS,
                new QueryClosure() {
                    @Override
                    public WebTarget modify(WebTarget webTarget) {
                        return webTarget.queryParam("ids", ids + (range != null ? range : ""));
                    }
                },
                new RequestClosure<JsonObject>() {
                    @Override
                    public JsonObject call(Invocation.Builder request) {
                        return request.get(JsonObject.class);
                    }
                },
                new ResponseClosure<JsonObject, List<ResultType>>() {
                    @Override
                    public List<ResultType> call(JsonObject json) {
                        JsonArray jsonArray = json.getJsonArray(ids);
//                        if (jsonArray == null) throw new ResponseParseException("Expected JSON array '" + ids + "'");
                        if (jsonArray == null) {
                            String[] parts = ids.split(":");
                            String metricId = parts[0];
                            jsonArray = json.getJsonArray(metricId);
                            if (jsonArray == null) {
                                throw new ResponseParseException("Expected JSON array with ID = '" + ids + "'");
                            }
                        }

                        Constructor<ResultType> resultConstructor = ObjectUtils.getConstructor(resultType, JsonObject.class);
                        List<ResultType>        results           = new ArrayList<ResultType>(jsonArray.size());
                        for (int k = 0; k < jsonArray.size(); ++k) {
                            JsonObject jsonObject = jsonArray.getJsonObject(k);
                            ResultType result = ObjectUtils.newInstance(resultConstructor, jsonObject);
                            results.add(result);
                        }
                        return results;
                    }
                }
        );
    }

    @SuppressWarnings("unchecked")
    protected <JsonType extends JsonStructure, ResultType>
    List<? extends StandardMetricResult> invokeForResults(int testId, final String ids, final OffsetRange range, final StandardMetricResult.Metrics metric) {
        return invoke(TESTS, Integer.toString(testId), RESULTS,
                new QueryClosure() {
                    @Override
                    public WebTarget modify(WebTarget webTarget) {
                        return webTarget.queryParam("ids", ids + (range != null ? range : ""));
                    }
                },
                new RequestClosure<JsonObject>() {
                    @Override
                    public JsonObject call(Invocation.Builder request) {
                        return request.get(JsonObject.class);
                    }
                },
                new ResponseClosure<JsonObject, List<? extends StandardMetricResult>>() {
                    @Override
                    public List<? extends StandardMetricResult> call(JsonObject json) {
                        JsonArray jsonArray = json.getJsonArray(ids);
                        if (jsonArray == null) {
                            String[] parts = ids.split(":");
                            String metricId = parts[0];
                            jsonArray = json.getJsonArray(metricId);
                            if (jsonArray == null) {
                                throw new ResponseParseException("Expected JSON array with ID = '" + ids + "'");
                            }
                        }

                        Constructor<? extends StandardMetricResult> resultConstructor = ObjectUtils.getConstructor(metric.resultType, StandardMetricResult.Metrics.class, JsonObject.class);

                        List<StandardMetricResult> results = new ArrayList<StandardMetricResult>(jsonArray.size());
                        for (int k = 0; k < jsonArray.size(); ++k) {
                            JsonObject jsonObject = jsonArray.getJsonObject(k);
                            StandardMetricResult result = ObjectUtils.newInstance(resultConstructor, metric, jsonObject);
                            results.add(result);
                        }
                        return results;
                    }
                }
        );
    }


    /**
     * Performs a REST API invocation.
     *
     * @param operation       operation name, such as <code>tests</code>, <code>data-stores</code>, etc
     * @param id              resource ID (if fetching a specific resource)
     * @param action          optional action, such as <code>clone</code>, <code>abort</code>, etc
     * @param queryClosure    optional closure to modify the web-target before requests processing
     * @param requestClosure  closure to create the request, such as GET, POST, PUT, DELETE
     * @param responseClosure closure to convert the response JSON into a value-object
     * @param <JsonType>      JSON type, such as JsonObject, JsonArray
     * @param <ValueType>     value-type, such as {@link com.loadimpact.resource.Test}, {@link
     *                        com.loadimpact.resource.UserScenario}, etc
     * @return a single value-object or a list of it
     * @throws com.loadimpact.exception.ApiException if anything goes wrong, such as the server returns HTTP status not being 20x
     */
    protected <JsonType extends JsonStructure, ValueType>
    ValueType invoke(String operation, String id, String action, QueryClosure queryClosure, RequestClosure<JsonType> requestClosure, ResponseClosure<JsonType, ValueType> responseClosure) {
        try {
            WebTarget ws = wsBase.path(operation);
            if (id != null) ws = ws.path(id);
            if (action != null) ws = ws.path(action);
            if (queryClosure != null) ws = queryClosure.modify(ws);

            Invocation.Builder request = ws.request(MediaType.APPLICATION_JSON_TYPE);

            request.header(AGENT_REQHDR, getAgentRequestHeaderValue());

            JsonType json = requestClosure.call(request);
            return (responseClosure != null) ? responseClosure.call(json) : null;
        } catch (WebApplicationException e) {
            Response.StatusType status = e.getResponse().getStatusInfo();
            switch (status.getStatusCode()) {
                case 400:
                    throw new BadRequestException(operation, id, action, e);
                case 401:
                    throw new MissingApiTokenException(status.getReasonPhrase());
                case 403:
                    throw new UnauthorizedException(operation, id, action);
                case 404:
                    throw new NotFoundException(operation, id);
                case 409:
                    throw new ConflictException(operation, id, action, e);
                case 422:
                    throw new CoercionException(operation, id, action, e);
                case 427:
                    throw new RateLimitedException(operation, id, action);
                case 429:
                    throw new ResponseParseException(operation, id, action, e);
                case 500: {
                    String message = "";
                    Response response = e.getResponse();
                    String contentType = response.getHeaderString("Content-Type");
                    if (contentType.equals("application/json")) {
                        InputStream is = (InputStream) response.getEntity();
                        JsonObject errJson = Json.createReader(is).readObject();
                        message = errJson.getString("message");
                    } else if (contentType.startsWith("text/html")) {
                        InputStream is = (InputStream) response.getEntity();
                        message = StringUtils.toString(is);
                    }
                    throw new ServerException(status.getReasonPhrase() + ": " + message);
                }
                default:
                    throw new ApiException(e);
            }
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(e);
        }
    }

    /**
     * Syntax checks the API Token.
     *
     * @param apiToken value to check
     * @throws IllegalArgumentException if invalid
     */
    protected void checkApiToken(String apiToken) {
        if (StringUtils.isBlank(apiToken)) throw new MissingApiTokenException("Empty key");
        if (apiToken.length() != TOKEN_LENGTH) throw new MissingApiTokenException("Wrong length");
        if (!apiToken.matches(HEX_PATTERN)) throw new MissingApiTokenException("Not a HEX value");
    }

    /**
     * Returns true if we can successfully logon and fetch some data.
     *
     * @return true     if can logon
     */
    public boolean isValidToken() {
        try {
//            Response response = wsBase.path(TEST_CONFIGS).request(MediaType.APPLICATION_JSON_TYPE).get();
//            return response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL;

            LoadZone zone = getLoadZone(LoadZone.AMAZON_US_ASHBURN.uid);
            return zone == LoadZone.AMAZON_US_ASHBURN;
        } catch (Exception e) {
            log.info("API token validation failed: " + e);
        }
        return false;
    }

    /**
     * Retrieves a single test configuration
     *
     * @param id test configuration     its id
     * @return {@link com.loadimpact.resource.TestConfiguration}
     */
    public TestConfiguration getTestConfiguration(int id) {
        return invoke(TEST_CONFIGS, id,
                new RequestClosure<JsonObject>() {
                    @Override
                    public JsonObject call(Invocation.Builder request) {
                        return request.get(JsonObject.class);
                    }
                },
                new ResponseClosure<JsonObject, TestConfiguration>() {
                    @Override
                    public TestConfiguration call(JsonObject json) {
                        return new TestConfiguration(json);
                    }
                }
        );
    }

    /**
     * Retrieves all test configurations.
     *
     * @return list of {@link com.loadimpact.resource.TestConfiguration}
     */
    public List<TestConfiguration> getTestConfigurations() {
        return invoke(TEST_CONFIGS,
                new RequestClosure<JsonArray>() {
                    @Override
                    public JsonArray call(Invocation.Builder request) {
                        return request.get(JsonArray.class);
                    }
                },
                new ResponseClosure<JsonArray, List<TestConfiguration>>() {
                    @Override
                    public List<TestConfiguration> call(JsonArray json) {
                        List<TestConfiguration> testConfigs = new ArrayList<TestConfiguration>(json.size());
                        for (int k = 0; k < json.size(); ++k) {
                            testConfigs.add(new TestConfiguration(json.getJsonObject(k)));
                        }
                        return testConfigs;
                    }
                }
        );
    }

    /**
     * Makes a copy of an existing test configuration.
     *
     * @param id   id of the config
     * @param name its new name
     * @return {@link com.loadimpact.resource.TestConfiguration}
     */
    public TestConfiguration cloneTestConfiguration(int id, final String name) {
        return invoke(TEST_CONFIGS, id, "clone",
                new RequestClosure<JsonObject>() {
                    @Override
                    public JsonObject call(Invocation.Builder request) {
                        String         json = Json.createObjectBuilder().add("name", name).build().toString();
                        Entity<String> data = Entity.entity(json, MediaType.APPLICATION_JSON_TYPE);
                        return request.post(data, JsonObject.class);
                    }
                },
                new ResponseClosure<JsonObject, TestConfiguration>() {
                    @Override
                    public TestConfiguration call(JsonObject json) {
                        return new TestConfiguration(json);
                    }
                }
        );
    }

    /**
     * Deletes a test configuration.
     *
     * @param id its id
     * @throws com.loadimpact.exception.NotFoundException if it was unsuccessful
     */
    public void deleteTestConfiguration(final int id) {
        invoke(TEST_CONFIGS, id,
                new RequestClosure<JsonStructure>() {
                    @Override
                    public JsonStructure call(Invocation.Builder request) {
                        Response response = request.delete();
                        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                            throw new NotFoundException(TEST_CONFIGS, Integer.toString(id));
                        }
                        return null;
                    }
                },
                null
        );
    }

    /**
     * Updates an existing test configuration.
     *
     * @param testConfiguration a test configuration object
     * @return {@link com.loadimpact.resource.TestConfiguration} stored at the server
     */
    public TestConfiguration updateTestConfiguration(final TestConfiguration testConfiguration) {
        return invoke(TEST_CONFIGS, testConfiguration.id,
                new RequestClosure<JsonObject>() {
                    @Override
                    public JsonObject call(Invocation.Builder request) {
                        String         json = testConfiguration.toJSON().toString();
                        Entity<String> data = Entity.entity(json, MediaType.APPLICATION_JSON_TYPE);
                        return request.put(data, JsonObject.class);
                    }
                },
                new ResponseClosure<JsonObject, TestConfiguration>() {
                    @Override
                    public TestConfiguration call(JsonObject json) {
                        return new TestConfiguration(json);
                    }
                }
        );
    }

    /**
     * Creates a new test configuration.
     *
     * @param testConfiguration a prepared test configuration object (with no ID)
     * @return {@link com.loadimpact.resource.TestConfiguration} stored at the server
     */
    public TestConfiguration createTestConfiguration(final TestConfiguration testConfiguration) {
        return invoke(TEST_CONFIGS,
                new RequestClosure<JsonObject>() {
                    @Override
                    public JsonObject call(Invocation.Builder request) {
                        String         json = testConfiguration.toJSON().toString();
                        Entity<String> data = Entity.entity(json, MediaType.APPLICATION_JSON_TYPE);
                        return request.post(data, JsonObject.class);
                    }
                },
                new ResponseClosure<JsonObject, TestConfiguration>() {
                    @Override
                    public TestConfiguration call(JsonObject json) {
                        return new TestConfiguration(json);
                    }
                }
        );
    }

    /**
     * Retrieves a load-zone.
     *
     * @param id its id
     * @return {@link com.loadimpact.resource.LoadZone}
     */
    public LoadZone getLoadZone(String id) {
        return invoke(LOAD_ZONES, id, null, null,
                new RequestClosure<JsonArray>() {
                    @Override
                    public JsonArray call(Invocation.Builder request) {
                        return request.get(JsonArray.class);
                    }
                },
                new ResponseClosure<JsonArray, LoadZone>() {
                    @Override
                    public LoadZone call(JsonArray json) {
                        return LoadZone.valueOf(json.getJsonObject(0));
                    }
                }
        );
    }

    /**
     * Retrieves all load-zones.
     *
     * @return list of {@link com.loadimpact.resource.LoadZone}
     */
    public List<LoadZone> getLoadZone() {
        return invoke(LOAD_ZONES,
                new RequestClosure<JsonArray>() {
                    @Override
                    public JsonArray call(Invocation.Builder request) {
                        return request.get(JsonArray.class);
                    }
                },
                new ResponseClosure<JsonArray, List<LoadZone>>() {
                    @Override
                    public List<LoadZone> call(JsonArray json) {
                        List<LoadZone> zones = new ArrayList<LoadZone>(json.size());
                        for (int k = 0; k < json.size(); ++k) {
                            zones.add(LoadZone.valueOf(json.getJsonObject(k)));
                        }
                        return zones;
                    }
                }
        );
    }

    /**
     * Retrieves a data store.
     *
     * @param id ist id
     * @return {@link com.loadimpact.resource.DataStore}
     */
    public DataStore getDataStore(int id) {
        return invoke(DATA_STORES, id,
                new RequestClosure<JsonObject>() {
                    @Override
                    public JsonObject call(Invocation.Builder request) {
                        return request.get(JsonObject.class);
                    }
                },
                new ResponseClosure<JsonObject, DataStore>() {
                    @Override
                    public DataStore call(JsonObject json) {
                        return new DataStore(json);
                    }
                }
        );
    }

    /**
     * Retrieves all data stores.
     *
     * @return list of {@link com.loadimpact.resource.DataStore}
     */
    public List<DataStore> getDataStores() {
        return invoke(DATA_STORES,
                new RequestClosure<JsonArray>() {
                    @Override
                    public JsonArray call(Invocation.Builder request) {
                        return request.get(JsonArray.class);
                    }
                },
                new ResponseClosure<JsonArray, List<DataStore>>() {
                    @Override
                    public List<DataStore> call(JsonArray json) {
                        List<DataStore> ds = new ArrayList<DataStore>(json.size());
                        for (int k = 0; k < json.size(); ++k) {
                            ds.add(new DataStore(json.getJsonObject(k)));
                        }
                        return ds;
                    }
                }
        );
    }

    /**
     * Deletes a data store.
     *
     * @param id its id
     * @throws com.loadimpact.exception.ResponseParseException if it was unsuccessful
     */
    public void deleteDataStore(final int id) {
        invoke(DATA_STORES, id,
                new RequestClosure<JsonStructure>() {
                    @Override
                    public JsonStructure call(Invocation.Builder request) {
                        Response response = request.delete();
                        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                            throw new ResponseParseException(DATA_STORES, id, null, null);
                        }
                        return null;
                    }
                },
                null
        );
    }

    /**
     * Creates a new data store.
     *
     * @param file      CSV file that should be uploaded (N.B. max 50MB)
     * @param name      name to use in the Load Impact web-console
     * @param fromline  Payload from this line (1st line is 1). Set to value 2, if the CSV file starts with a headings line
     * @param separator field separator, one of {@link com.loadimpact.resource.DataStore.Separator}
     * @param delimiter surround delimiter for text-strings, one of {@link com.loadimpact.resource.DataStore.StringDelimiter}
     * @return {@link com.loadimpact.resource.DataStore}
     */
    public DataStore createDataStore(final File file, final String name, final int fromline, final DataStore.Separator separator, final DataStore.StringDelimiter delimiter) {
        return invoke(DATA_STORES,
                new RequestClosure<JsonObject>() {
                    @Override
                    public JsonObject call(Invocation.Builder request) {
                        MultiPart form = new FormDataMultiPart()
                                .field("name", name)
                                .field("fromline", Integer.toString(fromline))
                                .field("separator", separator.param())
                                .field("delimiter", delimiter.param())
                                .bodyPart(new FileDataBodyPart("file", file, new MediaType("text", "csv")));

                        return request.post(Entity.entity(form, form.getMediaType()), JsonObject.class);
                    }
                },
                new ResponseClosure<JsonObject, DataStore>() {
                    @Override
                    public DataStore call(JsonObject json) {
                        return new DataStore(json);
                    }
                }
        );
    }

    /**
     * Retrieves a user scenario.
     *
     * @param id its id
     * @return {@link com.loadimpact.resource.UserScenario}
     */
    public UserScenario getUserScenario(int id) {
        return invoke(USER_SCENARIOS, id,
                new RequestClosure<JsonObject>() {
                    @Override
                    public JsonObject call(Invocation.Builder request) {
                        return request.get(JsonObject.class);
                    }
                },
                new ResponseClosure<JsonObject, UserScenario>() {
                    @Override
                    public UserScenario call(JsonObject json) {
                        return new UserScenario(json);
                    }
                }
        );
    }

    /**
     * Retrieves all user scenarios.
     *
     * @return list of {@link com.loadimpact.resource.UserScenario}
     */
    public List<UserScenario> getUserScenarios() {
        return invoke(USER_SCENARIOS,
                new RequestClosure<JsonArray>() {
                    @Override
                    public JsonArray call(Invocation.Builder request) {
                        return request.get(JsonArray.class);
                    }
                },
                new ResponseClosure<JsonArray, List<UserScenario>>() {
                    @Override
                    public List<UserScenario> call(JsonArray json) {
                        List<UserScenario> ds = new ArrayList<UserScenario>(json.size());
                        for (int k = 0; k < json.size(); ++k) {
                            ds.add(new UserScenario(json.getJsonObject(k)));
                        }
                        return ds;
                    }
                }
        );
    }

    /**
     * Makes a copy of a user scenario.
     *
     * @param id   its id
     * @param name new name of the copy
     * @return {@link com.loadimpact.resource.UserScenario}
     */
    public UserScenario cloneUserScenario(int id, final String name) {
        return invoke(USER_SCENARIOS, id, "clone",
                new RequestClosure<JsonObject>() {
                    @Override
                    public JsonObject call(Invocation.Builder request) {
                        String         json = Json.createObjectBuilder().add("name", name).build().toString();
                        Entity<String> data = Entity.entity(json, MediaType.APPLICATION_JSON_TYPE);
                        return request.post(data, JsonObject.class);
                    }
                },
                new ResponseClosure<JsonObject, UserScenario>() {
                    @Override
                    public UserScenario call(JsonObject json) {
                        return new UserScenario(json);
                    }
                }
        );
    }

    /**
     * Deletes a user scenario.
     *
     * @param id its id
     * @throws com.loadimpact.exception.ResponseParseException if unsuccessful
     */
    public void deleteUserScenario(final int id) {
        invoke(USER_SCENARIOS, id,
                new RequestClosure<JsonStructure>() {
                    @Override
                    public JsonStructure call(Invocation.Builder request) {
                        Response response = request.delete();
                        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                            throw new ResponseParseException(USER_SCENARIOS, id, null, null);
                        }
                        return null;
                    }
                },
                null
        );
    }

    /**
     * Updates a user scenario.
     *
     * @param scenario modified scenario
     * @return server stored {@link com.loadimpact.resource.UserScenario}
     */
    public UserScenario updateUserScenario(final UserScenario scenario) {
        return invoke(USER_SCENARIOS, scenario.id,
                new RequestClosure<JsonObject>() {
                    @Override
                    public JsonObject call(Invocation.Builder request) {
                        String         json = scenario.toJSON().toString();
                        Entity<String> data = Entity.entity(json, MediaType.APPLICATION_JSON_TYPE);
                        return request.put(data, JsonObject.class);
                    }
                },
                new ResponseClosure<JsonObject, UserScenario>() {
                    @Override
                    public UserScenario call(JsonObject json) {
                        return new UserScenario(json);
                    }
                }
        );
    }

    /**
     * Creates a new user scenario.
     *
     * @param scenario scenario configuration (no ID)
     * @return server stored {@link com.loadimpact.resource.UserScenario}
     */
    public UserScenario createUserScenario(final UserScenario scenario) {
        return invoke(USER_SCENARIOS,
                new RequestClosure<JsonObject>() {
                    @Override
                    public JsonObject call(Invocation.Builder request) {
                        String         json = scenario.toJSON().toString();
                        Entity<String> data = Entity.entity(json, MediaType.APPLICATION_JSON_TYPE);
                        return request.post(data, JsonObject.class);
                    }
                },
                new ResponseClosure<JsonObject, UserScenario>() {
                    @Override
                    public UserScenario call(JsonObject json) {
                        return new UserScenario(json);
                    }
                }
        );
    }

    /**
     * Creates (starts) a user scenario validation.
     *
     * @param scenarioId id of the scenario that should be validated
     * @return {@link com.loadimpact.resource.UserScenarioValidation}
     */
    public UserScenarioValidation createUserScenarioValidation(final int scenarioId) {
        return invoke(USER_SCENARIO_VALIDATIONS,
                new RequestClosure<JsonObject>() {
                    @Override
                    public JsonObject call(Invocation.Builder request) {
                        JsonObject     json = Json.createObjectBuilder().add("user_scenario_id", scenarioId).build();
                        Entity<String> data = Entity.entity(json.toString(), MediaType.APPLICATION_JSON_TYPE);
                        return request.post(data, JsonObject.class);
                    }
                },
                new ResponseClosure<JsonObject, UserScenarioValidation>() {
                    @Override
                    public UserScenarioValidation call(JsonObject json) {
                        return new UserScenarioValidation(json);
                    }
                }
        );
    }

    /**
     * Retrieves a user scenario validation.
     *
     * @param id its id
     * @return {@link com.loadimpact.resource.UserScenarioValidation}
     */
    public UserScenarioValidation getUserScenarioValidation(int id) {
        return invoke(USER_SCENARIO_VALIDATIONS, id,
                new RequestClosure<JsonObject>() {
                    @Override
                    public JsonObject call(Invocation.Builder request) {
                        return request.get(JsonObject.class);
                    }
                },
                new ResponseClosure<JsonObject, UserScenarioValidation>() {
                    @Override
                    public UserScenarioValidation call(JsonObject json) {
                        return new UserScenarioValidation(json);
                    }
                }
        );
    }

    /**
     * Retrieves the results of a scenario validation and populates the list {@link
     * com.loadimpact.resource.UserScenarioValidation#results}
     *
     * @param scenarioValidation validation object
     * @return the same validation object as passed in, but augmented with the results
     */
    public UserScenarioValidation getUserScenarioValidationResults(final UserScenarioValidation scenarioValidation) {
        return invoke(USER_SCENARIO_VALIDATIONS, scenarioValidation.id, "results",
                new RequestClosure<JsonObject>() {
                    @Override
                    public JsonObject call(Invocation.Builder request) {
                        return request.get(JsonObject.class);
                    }
                },
                new ResponseClosure<JsonObject, UserScenarioValidation>() {
                    @Override
                    public UserScenarioValidation call(JsonObject json) {
                        UserScenarioValidation sc = (UserScenarioValidation) ObjectUtils.copy(scenarioValidation);

                        JsonArray results = json.getJsonArray("results");
                        if (results != null) {
                            for (int k = 0; k < results.size(); ++k) {
                                sc.results.add(new UserScenarioValidation.Result(results.getJsonObject(k)));
                            }
                        }

                        return sc;
                    }
                }
        );
    }

    /**
     * Retrieves a test (instance).
     *
     * @param id its id
     * @return {@link com.loadimpact.resource.Test}
     */
    public Test getTest(int id) {
        return invoke(TESTS, id,
                new RequestClosure<JsonObject>() {
                    @Override
                    public JsonObject call(Invocation.Builder request) {
                        return request.get(JsonObject.class);
                    }
                },
                new ResponseClosure<JsonObject, Test>() {
                    @Override
                    public Test call(JsonObject json) {
                        return new Test(json);
                    }
                }
        );
    }

    /**
     * Retrieves all tests.
     *
     * @return list of {@link com.loadimpact.resource.Test}
     */
    public List<Test> getTests() {
        return invoke(TESTS,
                new RequestClosure<JsonArray>() {
                    @Override
                    public JsonArray call(Invocation.Builder request) {
                        return request.get(JsonArray.class);
                    }
                },
                new ResponseClosure<JsonArray, List<Test>>() {
                    @Override
                    public List<Test> call(JsonArray json) {
                        List<Test> ds = new ArrayList<Test>(json.size());
                        for (int k = 0; k < json.size(); ++k) {
                            ds.add(new Test(json.getJsonObject(k)));
                        }
                        return ds;
                    }
                }
        );
    }


    /**
     * Starts a test.
     *
     * @param testConfigId id of the test configuration
     * @return test-instance ID for the just created load test
     */
    public int startTest(int testConfigId) {
        return invoke(TEST_CONFIGS, testConfigId, "start",
                new RequestClosure<JsonObject>() {
                    @Override
                    public JsonObject call(Invocation.Builder request) {
                        return request.post(null, JsonObject.class);
                    }
                },
                new ResponseClosure<JsonObject, Integer>() {
                    @Override
                    public Integer call(JsonObject json) {
                        return json.getInt("id", -1);
                    }
                }
        );
    }

    /**
     * Aborts a running test.
     *
     * @param testId its id
     * @throws com.loadimpact.exception.ResponseParseException if unsuccessful
     */
    public void abortTest(final int testId) {
        invoke(TESTS, testId, ABORT,
                new RequestClosure<JsonStructure>() {
                    @Override
                    public JsonStructure call(Invocation.Builder request) {
                        Response response = request.post(null);
                        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                            throw new ResponseParseException(TESTS, testId, ABORT, null);
                        }
                        return null;
                    }
                },
                null
        );
    }

    /**
     * Polls a running test for status updates using the given listener. This method blocks until the test has completed
     * and periodically retrieves status info using {@link #getTest(int)}
     *
     * @param testId       test ID
     * @param pollInterval length of poll interval, in seconds
     * @param listener     a {@link RunningTestListener} object
     * @return the last retrieved test-instance object, or null if something went wrong
     * @see RunningTestListener
     * @see com.loadimpact.exception.AbortTest
     */
    public Test monitorTest(int testId, int pollInterval, RunningTestListener listener) {
        try {
            long nextDeadline = System.currentTimeMillis() + pollInterval * 1000;
            Test test = getTest(testId);
            while (test.status.isInProgress()) {
                listener.onProgress(test, this);

                Thread.sleep(Math.max(nextDeadline - System.currentTimeMillis(), 0));
                nextDeadline = System.currentTimeMillis() + pollInterval * 1000;

                test = getTest(testId);
            }

            if (test.status.isSuccessful()) {
                listener.onSuccess(test);
            } else {
                listener.onFailure(test);
            }

            return test;
        } catch (InterruptedException e) {
            abortTest(testId);
            listener.onAborted();
        } catch (AbortTest e) {
            abortTest(testId);
            listener.onAborted();
        } catch (ApiException e) {
            abortTest(testId);
            listener.onError(e);
        } catch (Exception e) {
            abortTest(testId);
            throw new RuntimeException(e);
        }
        return null;
    }


    public List<UrlMetricResult> getUrlMetricResults(int testId, URL url, LoadZone zone, Integer scenarioId, Integer httpStatus, HttpMethods httpMethod, final OffsetRange range) {
        if (url == null) throw new IllegalArgumentException("Missing url");
        if (zone == null) zone = LoadZone.AGGREGATE_WORLD;
        if (scenarioId == null) throw new IllegalArgumentException("Missing scenario_id");
        if (httpStatus == null) httpStatus = 200;
        if (httpMethod == null) httpMethod = HttpMethods.GET;

        String ids = String.format("%s%s:%d:%d:%d:%s", UrlMetricResult.METRIC_ID_PREFIX, StringUtils.md5(url.toString()), zone.id, scenarioId, httpStatus, httpMethod);
        return invokeForResults(testId, ids, range, UrlMetricResult.class);
    }


    public List<PageMetricResult> getPageMetricResults(int testId, String pageName, LoadZone zone, Integer scenarioId, OffsetRange range) {
        if (pageName == null) throw new IllegalArgumentException("Missing pageName");
        if (zone == null) zone = LoadZone.AGGREGATE_WORLD;
        if (scenarioId == null) throw new IllegalArgumentException("Missing scenario_id");

        String ids = String.format("%s%s:%d:%d", PageMetricResult.METRIC_ID_PREFIX, StringUtils.md5(pageName), zone.id, scenarioId);
        return invokeForResults(testId, ids, range, PageMetricResult.class);
    }

    public List<CustomMetricResult> getCustomMetricResults(int testId, String metricName, LoadZone zone, Integer scenarioId, OffsetRange range) {
        if (metricName == null) throw new IllegalArgumentException("Missing metricName");
        if (zone == null) zone = LoadZone.AGGREGATE_WORLD;
        if (scenarioId == null) throw new IllegalArgumentException("Missing scenario_id");

        String ids = String.format("%s%s:%d:%d", CustomMetricResult.METRIC_ID_PREFIX, StringUtils.md5(metricName), zone.id, scenarioId);
        return invokeForResults(testId, ids, range, CustomMetricResult.class);
    }

    public List<ServerMetricResult> getServerMetricResults(int testId, String agentName, String metricName, OffsetRange range) {
        if (agentName == null) throw new IllegalArgumentException("Missing agentName");
        if (metricName == null) throw new IllegalArgumentException("Missing metricName");

        String ids = String.format("%s%s", ServerMetricResult.METRIC_ID_PREFIX, StringUtils.md5(agentName + metricName));
        return invokeForResults(testId, ids, range, ServerMetricResult.class);
    }

    public List<? extends StandardMetricResult> getStandardMetricResults(int testId, StandardMetricResult.Metrics metric, LoadZone zone, OffsetRange range) {
        if (metric == null) throw new IllegalArgumentException("Missing metric");
        if (zone == null) zone = LoadZone.AGGREGATE_WORLD;

        String ids = String.format("%s:%d", metric.id, zone.id);
        return invokeForResults(testId, ids, range, metric);
    }

}
