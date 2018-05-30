package com.esri.geoportal.base.security;

import javax.json.Json;
import javax.json.JsonObject;

import org.junit.After;
import org.junit.Rule;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;

public abstract class ArcGISAuthenticationProviderTest {
    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this, 5000);
    protected MockServerClient mockServer;
    
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