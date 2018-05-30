package com.esri.geoportal.base.security;

import static org.mockserver.model.Header.header;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import com.esri.testutil.TestService;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
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
@ContextConfiguration("/config/test-app-security-pki.xml")
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
public class PKITest extends ArcGISAuthenticationProviderTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void testPKILogin() {
        
        String token = "abcde123";

        // /generateToken isn't called when using PKI (the token comes from the user's request). So we just need to handle the group request.

        mockServer.when(
            request()
                .withMethod("GET")
                .withPath("/sharing/rest/community/users/gptadmin")
                .withQueryStringParameter("f", "json")
        )
        .respond(
            response()
                .withStatusCode(HttpStatusCode.OK_200.code())
                .withHeader("Content-Type", "application/json")
                .withBody(generateUserResponse().toString())
        );

        Authentication authToken = new UsernamePasswordAuthenticationToken("gptadmin", "__rtkn__:" + token);
        SecurityContextHolder.getContext().setAuthentication(authToken);
        TestService testService = (TestService) applicationContext.getBean("testService");
        testService.adminMethod();
    }

}