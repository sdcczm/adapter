package com.wcs.sap.config;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Startup
@Singleton
@ApplicationScoped
public class JCoConfig {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Properties props = new Properties();
    
    @PostConstruct
    public void init() {
        URL url = Thread.currentThread().getContextClassLoader().getResource("jco.properties");
        if (url != null) {
            try {
                props.load(url.openStream());
            } catch (IOException e) {
                logger.error("Failed to load jco.properties, with exception: " + e.getMessage());
            }
        }
    }
    
    public Properties getPropertiesObject() {
        return props;
    }
}
