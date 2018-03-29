package com.wcs.sap.broker;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoRepository;

@ApplicationScoped
public class JCoHolder {
    
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    private JCoDestination connection;
    private JCoRepository repository;
    
    @PostConstruct
    public void init() {
        try {
            connection = JCoDestinationManager.getDestination("cmdpms");
            repository = connection.getRepository();
        } catch (JCoException jce) {
            logger.error("Error establishing connection with SAP: " + jce.getMessage());
        }
    }
    
    public JCoFunction getFunction(String name) {
        if (connection == null || repository == null) {
            init();
        }
        JCoFunction function = null;
        try {
            function = repository.getFunction(name);
        } catch (JCoException jce) {
            logger.error("Error retrieving JCo function [" + name + "] with exception: " + jce.getMessage());
        }
        return function;
    }
    
    public void executeFunction(JCoFunction function) {
        try {
            function.execute(connection);
        } catch (JCoException jce) {
            logger.error("Error executing JCo function [" + function.getName() + "] with message: " + jce.getMessage());
        }
    }

}
