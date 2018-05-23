package com.esri.geoportal.base.security;

import static org.mockserver.model.Header.header;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.util.Date;

import javax.json.Json;
import javax.json.JsonObject;

import com.esri.testutil.TestService;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.HttpStatusCode;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class ArcGISAuthenticationProviderTest {

    // Setup mockserver for testing
    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this, 5000);
    private MockServerClient mockServer;

    // Set up spring classes
    private static ApplicationContext applicationContext = null;

    @BeforeClass
    public static void setUpBeforeClass() {
        applicationContext = new ClassPathXmlApplicationContext("config/test-app-security.xml");
    }

    @After
    public void tearDown() {
        mockServer.reset();
    }

    @Test
    public void testUsernamePassword() {
        
        String token = "abcde123";

        JsonObject generateTokenResponse = Json
            .createObjectBuilder()
                .add("token", token)
                .add("expires", new Date().getTime() + 10000)
            .build();

        mockServer.when(
            request()
                .withMethod("POST")
                .withPath("/sharing/rest/generateToken")
                .withBody(
                    "f=json&username=gptadmin&password=gptadmin&expiration=120&referer=mdandini.esri.com&client=referer"
                )
        )
        .respond(
          response()
            .withStatusCode(HttpStatusCode.OK_200.code())
            .withHeader(header("Content-Type", "application/json"))
            .withBody(generateTokenResponse.toString())
        );

        mockServer.when(
          request()
            .withMethod("GET")
            .withPath("/sharing/rest/community/self")
            .withQueryStringParameter("f", "json")
            .withQueryStringParameter("token", token)
        )
        .respond(
            response()
                .withStatusCode(HttpStatusCode.OK_200.code())
                .withHeader(header("Content-Type", "application/json"))
                .withBody(generateUserResponse().toString())
        );

        Authentication authToken = new UsernamePasswordAuthenticationToken("gptadmin", "gptadmin");
        SecurityContextHolder.getContext().setAuthentication(authToken);
        TestService testService = (TestService) applicationContext.getBean("testService");
        testService.adminMethod();
    }

    private JsonObject generateUserResponse() {
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