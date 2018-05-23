package com.esri.geoportal.base.security;

import static org.mockserver.model.Header.header;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.io.IOException;
import java.util.Date;

import javax.json.Json;
import javax.json.JsonObject;

import com.esri.testutil.TestService;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpStatusCode;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class ArcGISAuthenticationProviderTest {

    // Test classes
    private static ApplicationContext applicationContext = null;
    private static ClientAndServer mockServer;

    @BeforeClass
    public static void setUpBeforeClass() {
        applicationContext = new ClassPathXmlApplicationContext("config/test-app-security.xml");

        mockServer = new ClientAndServer(5000);
    }

    @After
    public void tearDown() {
        mockServer.reset();
    }

    @AfterClass
    public static void tearDownAfterclass() throws IOException {
        mockServer.stop();
        mockServer.close();
    }

    @Test
    public void test() {
        
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