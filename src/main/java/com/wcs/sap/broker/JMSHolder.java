package com.wcs.sap.broker;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class JMSHolder {
    
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @Resource(name = "jms/ConnectionFactory", mappedName = "jms/ConnectionFactory", type = javax.jms.ConnectionFactory.class)
    private ConnectionFactory connectionFactory;
    
    public Message send(Map<String, Object> exportMap, String messageType) {
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        ObjectMessage message = null;
        try {
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(session.createQueue("SAP2PMSREQ"));
            message = session.createObjectMessage();
            message.setJMSType(messageType);
            if (messageType.equals("ZFUN_NCP_WB1")) { // //第一二次通讯
                message.setObject((String)exportMap.get("icCode"));
            } else {// 第三四次通讯
                message.setObject((Serializable)exportMap);
            }
            producer.send(message);
            logger.info("Message sent: " + message.getJMSMessageID());
        } catch (JMSException ex) {
            logger.error("Error sending message to [SAP2PMSREQ] with exception: " + ex.getMessage());
        } finally {
            if (producer != null) {
                try {
                    producer.close();
                } catch (Exception ex) {
                    // IGNORED
                }
            }
            if (session != null) {
                try {
                    session.close();
                } catch (Exception ex) {
                    // IGNORED
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception ex) {
                    // IGNORED
                }
            }
        }
        return message;
    }
    
    public Message receive(String filter, int timeout) {
        Connection connection = null;
        Session session = null;
        MessageConsumer consumer = null;
        Message message = null;
        try {
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            consumer = session.createConsumer(session.createQueue("PMS2SAPRES"), filter);
            message = consumer.receive(timeout);
            logger.info("Message received from [PMS2SAPRES]: " + message);
        } catch (JMSException ex) {
            logger.error("Error receiving message from [PMS2SAPRES] with exception: " + ex.getMessage());
        } finally {
            if (consumer != null) {
                try {
                    consumer.close();
                } catch (Exception ex) {
                    // IGNORED
                }
            }
            if (session != null) {
                try {
                    session.close();
                } catch (Exception ex) {
                    // IGNORED
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception ex) {
                    // IGNORED
                }
            }
        }
        return message;
    }
}
