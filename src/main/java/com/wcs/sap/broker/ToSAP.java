package com.wcs.sap.broker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.conn.jco.JCoFunction;
import com.wcs.sap.data.PurchaseDoc;
import com.wcs.sap.data.ValueTransformer;

@Stateless
public class ToSAP {
    
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @Inject
    private JCoHolder connectionHolder;
    
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> postPurchaseDoc(Map<String, Object> payload) {
        logger.info("ToSAP.postPurchaseDoc() called, payload is :" + payload);
        System.out.println("payload >>: " + payload);
        JCoFunction function = PurchaseDoc.prepare(connectionHolder.getFunction("ZFUN_NCP_IMPORT"), payload);
        connectionHolder.executeFunction(function);
        Map<String, Object> exportMap = new HashMap<String, Object>();
        ValueTransformer.transformReturnTableValues(function.getTableParameterList(), exportMap);
        logger.info("ToSAP.postPurchaseDoc() called, result from SAP is : "+exportMap);
        logger.info("TAB_NCP00 is : "+exportMap.get("TAB_NCP00"));
        System.out.println("exportMap >>: " + exportMap);
        System.out.println("TAB_NCP00 >>: " + exportMap.get("TAB_NCP00"));
        return (List<Map<String, Object>>) exportMap.get("TAB_NCP00");
    }
}
