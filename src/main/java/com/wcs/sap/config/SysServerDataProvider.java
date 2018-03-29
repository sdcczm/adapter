package com.wcs.sap.config;

import java.util.HashMap;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.conn.jco.ext.Environment;
import com.sap.conn.jco.ext.ServerDataEventListener;
import com.sap.conn.jco.ext.ServerDataProvider;

public class SysServerDataProvider implements ServerDataProvider {

    private Logger logger = LoggerFactory.getLogger(SysServerDataProvider.class);
    private HashMap<String, Properties> SERVER_CONFGS = new HashMap<String, Properties>();
    @SuppressWarnings("unused")
    private ServerDataEventListener serverDataEventListener;
    private static SysServerDataProvider instance;

    private SysServerDataProvider() {
    }

    private void register() {
        try {
            Environment.unregisterServerDataProvider(this);
            Environment.registerServerDataProvider(this);
            logger.info("Server registered with JCo enviroment.");
        } catch (Exception ex) {
            logger.error("Error registering server with JCo environment. Exception was: " + ex.getMessage());
        }
    }

    public void unregister() {
        try {
            Environment.unregisterServerDataProvider(this);
            logger.info("Server unregistered with JCo enviroment.");
        } catch (Exception ex) {
            logger.error("Error unregistering server with JCo environment. Exception was: " + ex.getMessage());
        }
    }

    public static SysServerDataProvider getInstance() {
        if (instance == null) {
            instance = new SysServerDataProvider();
            instance.register();
        }
        return instance;
    }

    /**
     * 在连接管理器中注册一个新的服务器配置信息
     * @param serverName
     * @param serverProps
     */
    public void registerServer(String serverName, Properties serverProps) {
        if (SERVER_CONFGS.containsKey(serverName)) {
            logger.warn("There already is a server configuration named: " + serverName);
        } else {
            SERVER_CONFGS.put(serverName, serverProps);
        }
    }

    @Override
    public Properties getServerProperties(String arg0) {
        return SERVER_CONFGS.get(arg0);
    }

    @Override
    public void setServerDataEventListener(ServerDataEventListener arg0) {
        serverDataEventListener = arg0;
    }

    @Override
    public boolean supportsEvents() {
        return false;
    }

}
