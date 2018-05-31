package com.esri.geoportal.base.security;

import java.lang.reflect.Field;

import javax.json.Json;
import javax.json.JsonObject;

import com.esri.geoportal.context.GeoportalContext;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Rule;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;

public abstract class ArcGISAuthenticationProviderTest {
    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this, 5000);
    protected MockServerClient mockServer;

    @AfterClass
    public static void tearDownAfterClass() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        // We have to clear out the Geoportal Context singleton in order for subsequent tests to work.
        Field field = GeoportalContext.class.getDeclaredField("SINGLETON");
        field.setAccessible(true);

        field.set(null, null);
        
        field.setAccessible(false);
    }
    
    @After
    public void tearDown() {
        mockServer.reset();
    }

    protected JsonObject generateUserResponse() {
        return Json
            .createObjectBuilder()
                .add("username", "gptadmin")
                .add("role", "org_admin")
                .add("groups", Json.createArrayBuilder()
                    .add(Json.createObjectBuilder()
                            .add("id", "1234admin")
                            .add("title", "admin")
                        )
                    .add(Json.createObjectBuilder()
                            .add("id", "1234publisher")
                            .add("title", "publisher")
                        )
                )
            .build();
    }
}