package com.wcs.sap.data;

import java.util.List;
import java.util.Map;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;

public class PurchaseDoc {
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static JCoFunction prepare(JCoFunction function, Map<String, Object> payload) {
        // ------------处理TAB_NCP00--------------------"
        JCoTable tab00TableItem = function.getTableParameterList().getTable("TAB_NCP00");// 传入TAB_NCP00
        List listItem = (List) payload.get("TAB_NCP00");
        for (int i = 0; i < listItem.size(); i++) {
            Map<String, Object> map = (Map<String, Object>) listItem.get(i);
            tab00TableItem.appendRow();
            tab00TableItem.setValue("SG_ID", map.get("SG_ID"));// 收购单号后10位+收购单行项目序号(2位，不足位的前面补0)
            tab00TableItem.setValue("BUSINESS", map.get("BUSINESS"));// 单据类型
            tab00TableItem.setValue("SAP_PO_IN", map.get("SAP_PO_IN"));// (PO号、STO号)
        }
        // -----------处理TAB_NCP01----------------     增加预付条款   2016/8/29
        JCoTable tab01TableItem = function.getTableParameterList().getTable("TAB_NCP01");// 传入TAB_NCP01
        listItem = (List) payload.get("TAB_NCP01");
        for (int i = 0; i < listItem.size(); i++) {
            Map<String, Object> map = (Map<String, Object>) listItem.get(i);
            tab01TableItem.appendRow();
            tab01TableItem.setValue("SG_ID", map.get("SG_ID"));
            tab01TableItem.setValue("BSART", map.get("BSART"));
            tab01TableItem.setValue("BEDAT", map.get("BEDAT"));
            tab01TableItem.setValue("EKORG", map.get("EKORG"));
            tab01TableItem.setValue("EKGRP", map.get("EKGRP"));
            tab01TableItem.setValue("BUKRS", map.get("BUKRS"));
            tab01TableItem.setValue("SUBMI", map.get("SUBMI"));
            tab01TableItem.setValue("LIFNR", map.get("LIFNR"));
            tab01TableItem.setValue("YWX", map.get("YWX")); 
            tab01TableItem.setValue("ZTERM", map.get("ZTERM"));  //新增应付款添加代码
        }
        // ------------处理TAB_NCP02--------------------"
        JCoTable tab02TableItem = function.getTableParameterList().getTable("TAB_NCP02");// 传入TAB_NCP02
        listItem = (List) payload.get("TAB_NCP02");
        for (int i = 0; i < listItem.size(); i++) {
            Map<String, Object> map = (Map<String, Object>) listItem.get(i);
            tab02TableItem.appendRow();
            tab02TableItem.setValue("SG_ID", map.get("SG_ID"));
            tab02TableItem.setValue("PO_SUB_NO", map.get("PO_SUB_NO"));
            tab02TableItem.setValue("MATNR", map.get("MATNR"));
            tab02TableItem.setValue("MENGE", map.get("MENGE"));
            tab02TableItem.setValue("MEINS", map.get("MEINS"));
            tab02TableItem.setValue("NETPR", map.get("NETPR"));//总价
            tab02TableItem.setValue("KPEIN", map.get("KPEIN"));//数量
            tab02TableItem.setValue("KMEIN", map.get("KMEIN"));//单位
            tab02TableItem.setValue("WERKS", map.get("WERKS"));
            tab02TableItem.setValue("WAERS", map.get("WAERS"));
            tab02TableItem.setValue("MWSKZ", map.get("MWSKZ"));
            tab02TableItem.setValue("LGORT_GI", map.get("LGORT_GI"));
            tab02TableItem.setValue("CHARG_GI", map.get("CHARG_GI"));
            tab02TableItem.setValue("LGORT_GR", map.get("LGORT_GR"));
            tab02TableItem.setValue("EVERS", map.get("EVERS"));
        }
        JCoTable tab03TableItem = function.getTableParameterList().getTable("TAB_NCP03");// 传入TAB_NCP03
        listItem = (List) payload.get("TAB_NCP03");
        for (int i = 0; i < listItem.size(); i++) {
            Map<String, Object> map = (Map<String, Object>) listItem.get(i);
            tab03TableItem.appendRow();
            tab03TableItem.setValue("SG_ID", map.get("SG_ID"));
            tab03TableItem.setValue("PO_SUB_NO", map.get("PO_SUB_NO"));
            tab03TableItem.setValue("SH_SUB_NO", map.get("SH_SUB_NO"));
            tab03TableItem.setValue("BUDAT", map.get("BUDAT"));
            tab03TableItem.setValue("BLDAT", map.get("BLDAT"));
            tab03TableItem.setValue("EBELN", map.get("EBELN"));
            tab03TableItem.setValue("BWART", map.get("BWART"));
            tab03TableItem.setValue("MATNR", map.get("MATNR"));
            tab03TableItem.setValue("MENGE", map.get("MENGE"));
            tab03TableItem.setValue("MEINS", map.get("MEINS"));
            tab03TableItem.setValue("WERKS", map.get("WERKS"));
            tab03TableItem.setValue("LGORT", map.get("LGORT"));
            tab03TableItem.setValue("CHARG", map.get("CHARG"));
            tab03TableItem.setValue("WBELN", map.get("WBELN"));
        }
        
        JCoTable tab05TableItem = function.getTableParameterList().getTable("TAB_NCP05");// 传入TAB_NCP05
        List listItem05 = (List) payload.get("TAB_NCP05");
        for (int i = 0; i < listItem05.size(); i++) {
            Map<String, Object> map = (Map<String, Object>) listItem05.get(i);
            tab05TableItem.appendRow();
            tab05TableItem.setValue("SG_ID", map.get("SG_ID"));
            tab05TableItem.setValue("ZHFWZ", map.get("ZHFWZ"));
            tab05TableItem.setValue("HLSHL", map.get("HLSHL"));
            tab05TableItem.setValue("ZLSCH", map.get("ZLSCH"));
            tab05TableItem.setValue("CGQD", map.get("CGQD"));
            tab05TableItem.setValue("ZYSFS", map.get("ZYSFS"));
            tab05TableItem.setValue("ZYTYPE", map.get("ZYTYPE"));
            tab05TableItem.setValue("ZKPFLIFNR", map.get("ZKPFLIFNR"));
        }
        
        
        // -----------处理TAB_NCP04 付款条款表-------------------"
        JCoTable tab04TableItem = function.getTableParameterList().getTable("TAB_NCP04");// 传入TAB_NCP04
        List listItem04 = (List) payload.get("TAB_NCP04");
        for (int i = 0; i < listItem04.size(); i++) {
            Map<String, Object> map = (Map<String, Object>) listItem04.get(i);
            tab04TableItem.appendRow();
            tab04TableItem.setValue("SG_ID", map.get("SG_ID"));
            tab04TableItem.setValue("ZNUMC", map.get("ZNUMC"));
            tab04TableItem.setValue("ZFKLX", map.get("ZFKLX"));
            tab04TableItem.setValue("ZZTERM", map.get("ZZTERM"));
            tab04TableItem.setValue("ZFKTS", map.get("ZFKTS"));
            tab04TableItem.setValue("ZDTYPE", map.get("ZDTYPE"));
            tab04TableItem.setValue("ZFKBL", map.get("ZFKBL"));
        }
        return function;
    }

}
