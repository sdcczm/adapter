package com.wcs.sap.broker;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ToNCP {

    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @Resource(name = "jms/ConnectionFactory", mappedName = "jms/ConnectionFactory", type = javax.jms.ConnectionFactory.class)
    private ConnectionFactory connectionFactory;
    
    public void send(List<Map<String, Object>> payload, String messageType) {
        Connection connection = null;

        try {
            connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queueSend = session.createQueue("SAP2NCPQ");
            MessageProducer producer = session.createProducer(queueSend);
            ObjectMessage message = session.createObjectMessage();
            message.setJMSType(messageType);
            message.setObject((Serializable) payload);
            producer.send(message);
            logger.info("Message sent to [SAP2NCPQ].");
        } catch (Exception ex) {
            logger.error("Error sending message to [SAP2NCPQ] with exception: " + ex.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception ex) {
                    // IGNORED
                }
            }
        }
    }
}
