package com.esri.testutil;

import com.esri.geoportal.context.GeoportalContext;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

/**
 * TestGeoportalContext
 * 
 * Utility class for testing that gets around GeoportalContext being a singleton.
 */
@Configuration
public class TestGeoportalContext extends GeoportalContext implements ApplicationContextAware {

    ApplicationContext applicationContext = null;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
    }
    
    @Override
    public boolean getSupportsGroupBasedAccess() {
        return true;
    }

}