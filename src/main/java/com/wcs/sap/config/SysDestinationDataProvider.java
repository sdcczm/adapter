package com.wcs.sap.config;

import java.util.HashMap;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;
import com.sap.conn.jco.ext.Environment;

public class SysDestinationDataProvider implements DestinationDataProvider {

    private Logger logger = LoggerFactory.getLogger(SysDestinationDataProvider.class);
    private HashMap<String, Properties> CLIENT_CONFGS = new HashMap<String, Properties>();
    @SuppressWarnings("unused")
    private DestinationDataEventListener destinationDataEventListener;
    private static SysDestinationDataProvider instance;

    private SysDestinationDataProvider() {}
    
    private void register() {
        try {
            Environment.unregisterDestinationDataProvider(this);
            Environment.registerDestinationDataProvider(this);
            logger.info("Destination registered with JCo enviroment.");
        } catch (Exception ex) {
            logger.error("Error registering destination with JCo environment. Exception was: " + ex.getMessage());
        }
    }

    public void unregister() {
        try {
            Environment.unregisterDestinationDataProvider(this);
            logger.info("Destination unregistered with JCo enviroment.");
        } catch (Exception ex) {
            logger.error("Error unregistering destination with JCo environment. Exception was: " + ex.getMessage());
        }
    }

    public static SysDestinationDataProvider getInstance() {
        if (instance == null) {
            instance = new SysDestinationDataProvider();
            instance.register();
        }
        return instance;
    }

    /**
     * 在连接管理器中注册一个新的客户端连接信息
     * @param ClinetName
     * @param clientProps
     */
    public void registerClient(String ClinetName, Properties clientProps) {
        if (CLIENT_CONFGS.containsKey(ClinetName)) {
            logger.warn("There already is a client configuration named: " + ClinetName);
        } else {
            CLIENT_CONFGS.put(ClinetName, clientProps);
        }
    }

    @Override
    public Properties getDestinationProperties(String arg0) {
        return CLIENT_CONFGS.get(arg0);
    }

    @Override
    public void setDestinationDataEventListener(DestinationDataEventListener arg0) {
        destinationDataEventListener = arg0;
    }

    @Override
    public boolean supportsEvents() {
        return false;
    }

}
