package com.wcs.sap.broker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wcs.sap.broker.ToNCP;
import com.wcs.sap.broker.ToSAP;

/**
 * <p>Project: sapadapter</p>
 * <p>Description: </p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:gaoyuxiang@wcs-global.com">Gao Yuxiang</a>
 */
@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "NCP2SAPQ") })
public class FromNCP implements MessageListener {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @EJB
    ToSAP toSAP;
    @EJB
    ToNCP toNCP;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void onMessage(Message message) {
        logger.info("Message received from [NCP2SAPQ].");
        Map<String, Object> exportMap = null;
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        ObjectMessage msg = (ObjectMessage) message;
        try {
            exportMap = (Map<String, Object>) msg.getObject();
        } catch (JMSException ex) {
            logger.error("Failed to parse message, with exception: " + ex.getMessage());
        }
        logger.info("Calling ToSAP.postPurchaseDoc()...");
        try {
            list = toSAP.postPurchaseDoc(exportMap);
        } catch (Exception ex) {
            logger.error("Error executing ToSAP.postPurchaseDoc().");
            list = new ArrayList();
            List listItem = (List) exportMap.get("TAB_NCP00");
            for (int i = 0; i < listItem.size(); i++) {
                Map<String, Object> errMap = (Map<String, Object>) listItem.get(i);
                errMap.put("MSG_CODE", "0003"); // 错误状态
                errMap.put("MESSAGE_OUT", "sap端处理出现异常");// 错误消息
                list.add(errMap);
            }
        }
        logger.info("Result list: " + list);
        logger.info("Calling ToNCP.send()...");
        toNCP.send(list, "sap2erp"); // 将sap端处理后的信息反馈回给ncp系统
    }

}
