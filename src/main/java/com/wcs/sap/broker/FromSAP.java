package com.wcs.sap.broker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;
import com.sap.conn.jco.server.JCoServerContext;
import com.sap.conn.jco.server.JCoServerFunctionHandler;
import com.wcs.sap.data.ValueTransformer;

@Stateless
@LocalBean
@TransactionManagement(TransactionManagementType.BEAN)
public class FromSAP implements JCoServerFunctionHandler {
    
    @EJB
    private JMSHolder jmsHolder;

    @Override
    public void handleRequest(JCoServerContext context, JCoFunction function) throws AbapException {
        if (function.getName().equals("ZFUN_NCP_WB1")) { // 第一二次通讯
            dealWithFirstMessage(function);
        } else if (function.getName().equals("ZFUN_NCP_WB2")) { // 第三四次通讯
            dealWithSecondMessage(function);
        }
    }

    @SuppressWarnings("unchecked")
    private void dealWithFirstMessage(JCoFunction function) {
        JCoTable icTable = function.getTableParameterList().getTable("ITAB_DB01_OUT");
        JCoTable messageTable = function.getTableParameterList().getTable("ITAB_DB01_IN01");
        JCoTable itemTable = function.getTableParameterList().getTable("ITAB_DB01_IN02");
        JCoTable packTable = function.getTableParameterList().getTable("ITAB_DB01_IN03");
        if (icTable.getNumRows() <= 0) {
            System.out.println("JCo request from SAP has empty IC table. Request ignored.");
            return;
        }
        for (int i = 0; i < icTable.getNumRows(); i++) {
            icTable.setRow(i);
            String icCode = icTable.getString("IC_CODE");
            System.out.println("ROWNUM " + icTable.getNumRows() + " with IC [" + icCode + "].");
            Map<String, Object> exportMap = new HashMap<String, Object>();
            exportMap.put("icCode", icCode);
            Message message = jmsHolder.send(exportMap, "ZFUN_NCP_WB1");
            String correlationId = "";
            try {
                correlationId = message.getJMSMessageID();
            } catch (Exception ex){
            	ex.printStackTrace();
                System.out.println("Failed to send [ZFUN_NCP_WB1] message to NCP.");
                messageTable.appendRow();
                messageTable.setValue("IC_CODE", icCode);
                messageTable.setValue("MSG_CODE", "0001");
                messageTable.setValue("MSG", "消息处理失败");
                return;
            }
            int timeout = 60000;
            String filter = "JMSCorrelationID = '" + correlationId + "'";
            System.out.println("Preparing to receive [ZFUN_NCP_WB1] response by filter [ " + filter + " ]...");
            Message result = jmsHolder.receive(filter, timeout);
            if (result == null) {
                System.out.println("NCP failed to respond to [ZFUN_NCP_WB1] within " + timeout / 1000 + " seconds. IC_CODE [" + icCode + "].");
                messageTable.appendRow();
                messageTable.setValue("IC_CODE", icCode);
                messageTable.setValue("MSG_CODE", "0001");
                messageTable.setValue("MSG", "消息处理失败");
                return;
            }
            System.out.println("Received [ZFUN_NCP_WB1] response by filter [ " + filter + " ].");
            try {
                Map<String, Object> returnMap = (Map<String, Object>)((ObjectMessage)result).getObject();
                System.out.println(returnMap);
                handleFirstMessageReturnMap(returnMap, messageTable, itemTable, packTable);
            } catch (Exception ex){
            	ex.printStackTrace();
                System.out.println("Failed to handle [ZFUN_NCP_WB1] return message from NCP.");
                messageTable.appendRow();
                messageTable.setValue("IC_CODE", icCode);
                messageTable.setValue("MSG_CODE", "0001");
                messageTable.setValue("MSG", "消息处理失败");
                return;
            }
            System.out.println("[ZFUN_NCP_WB1] handled successfully.");
        }
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void handleFirstMessageReturnMap(Map<String, Object> returnMap, JCoTable messageTable, JCoTable itemTable, JCoTable packTable) {
        // ------------处理ITAB_DB01_IN01--------------------"
        List listItem = (List) returnMap.get("ITAB_DB01_IN01");
        if (listItem != null) for (int j = 0; j < listItem.size(); j++) {
            Map<String, Object> map = (Map<String, Object>) listItem.get(j);
            messageTable.appendRow();
            messageTable.setValue("IC_CODE", map.get("IC_CODE"));
            messageTable.setValue("MSG_CODE", map.get("MSG_CODE"));
            messageTable.setValue("MSG", map.get("MSG"));
            messageTable.setValue("VEHICLE_NO", map.get("VEHICLE_NO"));
            messageTable.setValue("RC_NO", map.get("RC_NO"));
        }
        // -----------处理ITAB_DB01_IN02-------------------"
        listItem = (List) returnMap.get("ITAB_DB01_IN02");
        if (listItem != null) for (int j = 0; j < listItem.size(); j++) {
            Map<String, Object> map = (Map<String, Object>) listItem.get(j);
            itemTable.appendRow();
            itemTable.setValue("IC_CODE", map.get("IC_CODE"));
            itemTable.setValue("SUB_NO", map.get("SUB_NO"));
            itemTable.setValue("MATERIAL_DESC", map.get("MATERIAL_DESC"));
            itemTable.setValue("MATERIAL_CODE", map.get("MATERIAL_CODE"));
        }
        // ------------处理ITAB_DB01_IN03--------------------"
        listItem = (List) returnMap.get("ITAB_DB01_IN03");
        if (listItem != null) for (int j = 0; j < listItem.size(); j++) {
            Map<String, Object> map = (Map<String, Object>) listItem.get(j);
            packTable.appendRow();
            packTable.setValue("IC_CODE", map.get("IC_CODE"));
            packTable.setValue("SUB_NO", map.get("SUB_NO"));
            packTable.setValue("PACKING_CODE", map.get("PACKING_CODE"));
            packTable.setValue("PACKING_NAME", map.get("PACKING_NAME"));
            packTable.setValue("WEIGHT_DEDUCTION_TYPE", map.get("WEIGHT_DEDUCTION_TYPE"));
            packTable.setValue("WEIGHT", map.get("WEIGHT"));
            packTable.setValue("WEIGHT_DEDUCTION_RATIO", map.get("WEIGHT_DEDUCTION_RATIO"));
        }
    }

    @SuppressWarnings("unchecked")
    private void dealWithSecondMessage(JCoFunction function) {
        System.out.println("Dealing with [ZFUN_NCP_WB2]...");
        Map<String, Object> exportMap = new HashMap<String, Object>();
        Map<String, Object> returnMap = new HashMap<String, Object>();
        ValueTransformer.transformReturnTableValues(function.getTableParameterList(), exportMap);
        System.out.println("exportMap with size: " + exportMap.size());
        JCoTable messageTable = function.getTableParameterList().getTable("ITAB_DB02_IN01");
        Message message = jmsHolder.send(exportMap, "ZFUN_NCP_WB2");
        String correlationId = "";
        try {
            correlationId = message.getJMSMessageID();
        } catch (Exception ex){
        	ex.printStackTrace();
            System.out.println("Failed to send [ZFUN_NCP_WB2] message to NCP.");
            messageTable.appendRow();
            messageTable.setValue("MSG_CODE", "0001");
            messageTable.setValue("MSG", "消息处理失败");
            return;
        }
        int timeout = 60000;
        String filter = "JMSCorrelationID = '" + correlationId + "'";
        System.out.println("Preparing to receive [ZFUN_NCP_WB2] response by filter [ " + filter + " ]...");
        Message result = jmsHolder.receive(filter, timeout);
        if (result == null) {
            System.out.println("NCP failed to respond to [ZFUN_NCP_WB2] within " + timeout / 1000 + " seconds.");
            messageTable.appendRow();
            messageTable.setValue("MSG_CODE", "0001");
            messageTable.setValue("MSG", "创建地磅单出现未知错误，请联系管理员");
            return;
        }
        System.out.println("Received [ZFUN_NCP_WB2] response by filter [ " + filter + " ].");
        try {
            returnMap = (Map<String, Object>)((ObjectMessage)result).getObject();
            messageTable.appendRow();
            messageTable.setValue("IC_CODE", returnMap.get("IC_CODE"));
            messageTable.setValue("MSG_CODE", returnMap.get("MSG_CODE"));
            messageTable.setValue("MSG", returnMap.get("MSG"));
        } catch (Exception ex){
        	ex.printStackTrace();
            System.out.println("Failed to handle [ZFUN_NCP_WB2] return message from NCP.");
            messageTable.appendRow();
            messageTable.setValue("MSG_CODE", "0001");
            messageTable.setValue("MSG", "消息处理失败");
            return;
        }
        System.out.println("[ZFUN_NCP_WB2] handled successfully.");
    }

}
