package com.wcs.sap.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.conn.jco.server.DefaultServerHandlerFactory;
import com.sap.conn.jco.server.JCoServer;
import com.sap.conn.jco.server.JCoServerContextInfo;
import com.sap.conn.jco.server.JCoServerErrorListener;
import com.sap.conn.jco.server.JCoServerExceptionListener;
import com.sap.conn.jco.server.JCoServerFactory;
import com.sap.conn.jco.server.JCoServerState;
import com.wcs.sap.broker.FromSAP;
import com.wcs.sap.config.JCoConfig;
import com.wcs.sap.config.SysDestinationDataProvider;
import com.wcs.sap.config.SysServerDataProvider;

@Startup
@Singleton
@ApplicationScoped
public class ServerManager {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Map<String, JCoServer> servers = new HashMap<String, JCoServer>();

    @EJB
    FromSAP handler;
    @EJB
    JCoConfig jcoConfig;

    @PostConstruct
    private void init() {
        logger.info("Initializing ServerManager...");
        try {
            registerServer("cmdpms_ncp");
        } catch (Exception ex) {
            logger.error("Error initializing ServerManager, with exception: " + ex.getMessage());
        }
    }

    public void registerServer(String serverName) throws Exception {
        initializeJCo();
        JCoServer server;
        try {
            server = JCoServerFactory.getServer(serverName);
            servers.put(serverName, server);
            logger.info("JCo server created.");
        } catch (Exception ex) {
            logger.error("Error registering server [" + serverName + "].  " , ex);
            return;
        }
        DefaultServerHandlerFactory.FunctionHandlerFactory factory = new DefaultServerHandlerFactory.FunctionHandlerFactory();
        try {
            factory.registerHandler("ZFUN_NCP_WB1", handler);
            factory.registerHandler("ZFUN_NCP_WB2", handler);
            logger.info("JCo function handlers registered.");
        } catch (Exception ex) {
            logger.error("Error registering functions [ZFUN_NCP_WB1] & [ZFUN_NCP_WB2]. Exception: " + ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException("Failed to register functions.", ex);
        }
        try {
            server.setCallHandlerFactory(factory);
            JCoThrowableListener eListener = new JCoThrowableListener();
            server.addServerErrorListener(eListener);
            server.addServerExceptionListener(eListener);
            server.start();
            logger.info(">>>>>>>>>> JCo server started successfully. <<<<<<<<<<");
        } catch (Exception ex) {
            logger.error("Error starting server [" + serverName + "]. Excepion: " + ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException("Failed to start server.", ex);
        }

    }

    private void initializeJCo() {
        Properties props = jcoConfig.getPropertiesObject();
        SysDestinationDataProvider.getInstance().registerClient(props.getProperty("client"), props);
        logger.info("JCo client registered @ [" + props.getProperty("jco.client.ashost") + "].");
        SysServerDataProvider.getInstance().registerServer(props.getProperty("server"), props);
        logger.info("JCo server registered @ [" + props.getProperty("jco.server.gwhost") + "].");
    }

    /**
     * 关闭所有已经启动的jco服务器
     */
    @PreDestroy
    public void cleanUp() {
        logger.info("Stopping All Servers Now...");
        if (servers != null && !servers.isEmpty()) {
            Set<String> sns = servers.keySet();
            for (String sname : sns) {
                JCoServer jserver = servers.get(sname);
                JCoServerState state = jserver.getState();
                if (state.equals(JCoServerState.STARTED) || state.equals(JCoServerState.ALIVE)) {
                    jserver.stop();
                }
            }
        }
        logger.info("Unregistering data providers with JCo environment...");
        SysDestinationDataProvider.getInstance().unregister();
        SysServerDataProvider.getInstance().unregister();
        logger.info("Unregistered data providers with JCo environment.");
    }

    class JCoThrowableListener implements JCoServerErrorListener, JCoServerExceptionListener {
        @Override
		public void serverErrorOccurred(JCoServer jcoServer, String connectionId, JCoServerContextInfo serverCtx, Error error) {
            System.out.println(">>> Error occured on " + jcoServer.getProgramID() + " connection " + connectionId);
            error.printStackTrace();
        }

        @Override
		public void serverExceptionOccurred(JCoServer jcoServer, String connectionId, JCoServerContextInfo serverCtx,
                Exception error) {
            System.out.println(">>> Error occured on " + jcoServer.getProgramID() + " connection " + connectionId);
            error.printStackTrace();
        }
    }

}
