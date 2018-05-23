package com.esri.testutil;

import org.springframework.security.access.annotation.Secured;

public class TestService {
    
    @Secured("ROLE_ADMIN")
    public void adminMethod() {
        System.out.println("AdminMethod called");
    }
}