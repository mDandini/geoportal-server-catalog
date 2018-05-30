package com.esri.geoportal.base.security;

import static org.mockserver.model.Header.header;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.util.Date;

import javax.json.Json;
import javax.json.JsonObject;

import com.esri.testutil.TestService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockserver.model.HttpStatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration("/config/test-app-security.xml")
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
public class UsernamePasswordTest extends ArcGISAuthenticationProviderTest {

    // Set up spring classes
    @Autowired
    private ApplicationContext applicationContext;

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

}