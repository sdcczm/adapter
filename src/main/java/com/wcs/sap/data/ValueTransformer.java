package com.wcs.sap.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFieldIterator;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

/**
 * <p>Project: sapadapter</p>
 * <p>Description: </p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:gaoyuxiang@wcs-global.com">Gao Yuxiang</a>
 */
public class ValueTransformer {

    public static void transformReturnValues(JCoParameterList paraList, Map<String, Object> returnValues) {
        if (paraList != null) {
            Iterator<JCoField> it = paraList.iterator();
            JCoField field;
            while (it.hasNext()) {
                field = it.next();
                returnValues.put(field.getName(), field.getString());
            }
        }
    }

    public static void transformReturnStructureValues(JCoParameterList paraList, Map<String, Object> returnValues) {
        if (paraList != null) {
            Iterator<JCoField> it = paraList.iterator();
            JCoField field;
            while (it.hasNext()) {
                List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                field = it.next();
                if (field.isStructure()) {
                    JCoStructure structure = field.getStructure();

                    Map<String, Object> map = new HashMap<String, Object>();
                    JCoFieldIterator it1 = structure.getFieldIterator();
                    while (it1.hasNextField()) {
                        JCoField field1 = it1.nextField();
                        map.put(field1.getName(), field1.getString());
                    }
                    list.add(map);

                }
                returnValues.put(field.getName(), list);
            }
        }
    }

    public static void transformReturnTableValues(JCoParameterList paraList, Map<String, Object> returnValues) {
        if (paraList != null) {
            Iterator<JCoField> it = paraList.iterator();
            JCoField field;
            while (it.hasNext()) {
                List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                field = it.next();
                if (field.isTable()) {
                    JCoTable table = field.getTable();
                    for (int i = 0; i < table.getNumRows(); i++) {
                        table.setRow(i);
                        Map<String, Object> map = new HashMap<String, Object>();
                        JCoFieldIterator it1 = table.getFieldIterator();
                        while (it1.hasNextField()) {
                            JCoField field1 = it1.nextField();
                            map.put(field1.getName(), field1.getString());
                        }
                        list.add(map);
                    }
                }
                returnValues.put(field.getName(), list);
            }
        }
    }

}
