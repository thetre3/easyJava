package com.easyjava.builder;


import com.easyjava.Utils.PropertiesUtils;
import com.easyjava.Utils.StringUtils;
import com.easyjava.bean.Constants;
import com.easyjava.bean.FieldInfo;
import com.easyjava.bean.TableInfo;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuildTable {
    private static Connection conn = null;
    private static final Logger logger = LoggerFactory.getLogger(BuildTable.class);
    private static String SQL_SHOW_TABLE_STATUS = "show table status";
    private static String SQL_SHOW_TABLE_FIELDS = "show full FIELDS FROM %s";
    private static String SQL_SHOW_TABLE_INDEXS = "show index FROM %s";

    static {
        //读取配置文件
        String driverName = PropertiesUtils.getString("db.driver.name");
        String url = PropertiesUtils.getString("db.url");
        String user = PropertiesUtils.getString("db.username");
        String password = PropertiesUtils.getString("db.password");
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException | ClassNotFoundException e) {
            logger.error("数据库连接失败", e);
        }
    }

    //读取表信息
    public static List<TableInfo> getTables() {
        List<FieldInfo> fieldInfoList = new ArrayList<>();
        List<FieldInfo> extendFieldInfoList = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet tableResult = null;
        List<TableInfo> tableInfoList = new ArrayList<>();
        try {
            ps = conn.prepareStatement(SQL_SHOW_TABLE_STATUS);
            tableResult = ps.executeQuery();
            while (tableResult.next()) {
                String tablename = tableResult.getString("name");
                String comment = tableResult.getString("comment");
                String beanName = tablename;
                if (Constants.IGNORE_TABLE_PREFIX) {
                    beanName = tablename.substring(beanName.indexOf("_") + 1);
                }
                beanName = processFiled(beanName, Constants.UPERCASE_FIST_LATTER);
                TableInfo tableInfo = new TableInfo();
                tableInfo.setTableName(tablename);
                tableInfo.setBeanName(beanName);
                tableInfo.setComment(comment);
                tableInfo.setBeanParamName(beanName + Constants.SUFFIX_BEAN_QUERY);
                //获取表信息
                fieldInfoList = readFiedlInfo(tableInfo);
                tableInfo.setFieldList(fieldInfoList);
                //获取索引
                getKeyIndexInfo(tableInfo);
                //封装添加
                tableInfoList.add(tableInfo);
            }
        } catch (Exception e) {
            logger.error("读取表失败", e);
        } finally {
            if (tableResult != null) {
                try {
                    tableResult.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return tableInfoList;
    }

    //读取表列的信息
    private static List<FieldInfo> readFiedlInfo(TableInfo tableInfo) {
        PreparedStatement ps = null;
        ResultSet fieldResult = null;
        List<FieldInfo> fieldInfoList = new ArrayList<>();
        List<FieldInfo> extendFieldInfoList = new ArrayList<>();
        //变量
        boolean haveDate = true;
        boolean haveDateTime = true;
        boolean haveBigDecimal = true;
        try {
            ps = conn.prepareStatement(String.format(SQL_SHOW_TABLE_FIELDS, tableInfo.getTableName()));
            fieldResult = ps.executeQuery();
            while (fieldResult.next()) {
                String filed = fieldResult.getString("field");
                String type = fieldResult.getString("type");
                String extra = fieldResult.getString("extra");
                String comment = fieldResult.getString("comment");

                //处理filed转成beanname
                String propertyName = processFiled(filed, false);
                //处理type
                if (type.indexOf("(") > 0) {
                    type = type.substring(0, type.indexOf("("));
                }
                FieldInfo fieldInfo = new FieldInfo();
                fieldInfoList.add(fieldInfo);
                fieldInfo.setFieldName(filed);
                fieldInfo.setComment(comment);
                fieldInfo.setSqlType(type);
                fieldInfo.setAutoIncrement("auto_increment".equalsIgnoreCase(extra));
                fieldInfo.setPropertyName(propertyName);
                fieldInfo.setJavaType(processJavaType(type));
                if (ArrayUtils.contains(Constants.SQL_STRING_TYPES, type)) {
                    FieldInfo fuzzyField = new FieldInfo();
                    fuzzyField.setPropertyName(propertyName + Constants.SUFFIX_BEAN_QUERY_FUZZY);
                    fuzzyField.setJavaType(fieldInfo.getJavaType());
                    fuzzyField.setFieldName(filed);
                    fuzzyField.setSqlType(type);
                    extendFieldInfoList.add(fuzzyField);
                }
                if (ArrayUtils.contains(Constants.SQL_DATE_TYPES, type) || ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, type)) {
                    String PropertyName = propertyName + Constants.SUFFIX_BEAN_QUERY_TIME_START;
                    FieldInfo timeStartField = new FieldInfo();
                    timeStartField.setJavaType("String");
                    timeStartField.setPropertyName(PropertyName);
                    timeStartField.setFieldName(filed);
                    timeStartField.setSqlType(type);
                    extendFieldInfoList.add(timeStartField);
                    PropertyName = propertyName + Constants.SUFFIX_BEAN_QUERY_TIME_END;
                    FieldInfo timeEndField = new FieldInfo();
                    timeEndField.setFieldName(filed);
                    timeEndField.setJavaType("String");
                    timeEndField.setPropertyName(PropertyName);
                    timeEndField.setSqlType(type);
                    extendFieldInfoList.add(timeEndField);
                }


                //判断是否有日期
                if (haveDateTime && ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, type)) {
                    tableInfo.setHaveDateTime(true);
                    haveDateTime = false;
                }
                if (haveDate && ArrayUtils.contains(Constants.SQL_DATE_TYPES, type)) {
                    tableInfo.setHaveDate(true);
                    haveDate = false;
                }
                if (haveBigDecimal && ArrayUtils.contains(Constants.SQL_DECIMAL_TYPES, type)) {
                    tableInfo.setHaveDate(true);
                    haveBigDecimal = false;
                }
            }
            //
            tableInfo.setExtendFieldList(extendFieldInfoList);


        } catch (Exception e) {
            logger.error("读取表失败", e);
        } finally {
            if (fieldResult != null) {
                try {
                    fieldResult.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

        }
        return fieldInfoList;
    }
    //读取表的索引
    private static void getKeyIndexInfo(TableInfo tableInfo) {
        PreparedStatement ps = null;
        ResultSet fieldResult = null;
        try {
            Map<String, FieldInfo> map = new HashMap<>();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                map.put(fieldInfo.getFieldName(), fieldInfo);
            }
            ps = conn.prepareStatement(String.format(SQL_SHOW_TABLE_INDEXS, tableInfo.getTableName()));
            fieldResult = ps.executeQuery();
            while (fieldResult.next()) {
                String keyName = fieldResult.getString("key_name");
                Integer nonUnique = fieldResult.getInt("non_unique");
                String columnName = fieldResult.getString("column_name");
                if (nonUnique == 1) {
                    continue;
                }
                List<FieldInfo> keyFieldList = tableInfo.getKeyIndexMap().get(keyName);
                if (null == keyFieldList) {
                    keyFieldList = new ArrayList();
                    tableInfo.getKeyIndexMap().put(keyName, keyFieldList);
                }
                keyFieldList.add(map.get(columnName));
            }
        } catch (Exception e) {
            logger.error("读取索引失败", e);
        } finally {
            if (fieldResult != null) {
                try {
                    fieldResult.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    //bean转换函数
    private static String processFiled(String filed, Boolean upercaseFistlatter) {
        StringBuffer sb = new StringBuffer();
        String[] fields = filed.split("_");
        sb.append(upercaseFistlatter ? StringUtils.uperCaseFirstLetter(fields[0]) : fields[0]);
        for (int i = 1, len = fields.length; i < len; i++) {
            sb.append(StringUtils.uperCaseFirstLetter(fields[i]));
        }
        return sb.toString();
    }

    private static String processJavaType(String sqltype) {
        if (ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, sqltype) || ArrayUtils.contains(Constants.SQL_DATE_TYPES, sqltype)) {
            return "LocalDateTime";
        } else if (ArrayUtils.contains(Constants.SQL_INTEGER_TYPES, sqltype)) {
            return "Integer";
        } else if (ArrayUtils.contains(Constants.SQL_LONG_TYPES, sqltype)) {
            return "Long";
        } else if (ArrayUtils.contains(Constants.SQL_STRING_TYPES, sqltype)) {
            return "String";
        } else if (ArrayUtils.contains(Constants.SQL_DECIMAL_TYPES, sqltype)) {
            return "BigDecimal";
        } else {
            throw new RuntimeException("无法识别" + sqltype);
        }
    }


}
