package com.loadimpact.util;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * JSON helper method, used in unit tests.
 *
 * @author jens
 */
public class JsonUtils {
    
    public static JsonObject loadJSON(Object target, String name) {
        InputStream is = target.getClass().getResourceAsStream(name);
        assertThat("Resource not found: " + name, is, notNullValue());

        JsonObject json = Json.createReader(is).readObject();
        assertThat("Failed to read: " + name, json, notNullValue());

        return json;
    }
    
}
