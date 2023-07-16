package com.easyjava.builder;

import com.easyjava.Utils.StringUtils;
import com.easyjava.Utils.TimeUtils;
import com.easyjava.bean.Constants;
import com.easyjava.bean.FieldInfo;
import com.easyjava.bean.TableInfo;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import java.time.format.DateTimeFormatter;

public class BuildPo {
    private static final Logger logger = LoggerFactory.getLogger(BuildPo.class);

    public static void execute(TableInfo tableInfo) {

        try {
            File folder = new File(Constants.PATH_PO);
            File poFile = new File(folder, tableInfo.getBeanName() + ".java");
            if (!folder.exists()) {
                folder.mkdirs();
            }
            OutputStream outputStream = Files.newOutputStream(poFile.toPath());
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
            bw.write("package " + Constants.PACKAGE_PO + ";");
            bw.newLine();
            bw.newLine();
            //导包
            bw.write("import java.io.Serializable;");
            bw.newLine();
            //是否存在时间类型
            Boolean haveLocalDateTime = false;
            if (tableInfo.getHaveDate() || tableInfo.getHaveDateTime()) {
                bw.write("import java.time.LocalDateTime;");
                bw.newLine();
                bw.newLine();
                bw.write("import " + Constants.PACKAGE_BASE + ".Enums.TimeFormatEnums;");
                bw.newLine();
                bw.write("import " + Constants.PACKAGE_BASE + ".Utils.TimeUtils;");
                bw.newLine();
                bw.write(Constants.BEAN_DATE_FORMAT_CLASS + ";");
                bw.newLine();
                bw.write(Constants.BEAN_DATE_UNFORMAT_CLASS + ";");
                bw.newLine();
                haveLocalDateTime = true;
            }
            //
            if (tableInfo.getHaveBigDecimal()) {
                bw.write("import java.math.BigDecimal;");
                bw.newLine();
            }
            //忽略属性
            Boolean haveIgnore = false;
            if (!Constants.IGNORE_BEAN_TOJSON_FILED[0].equals("")) {
                bw.write(Constants.IGNORE_BEAN_TOJSON_CLASS + ";");
                haveIgnore = true;
            }
            bw.newLine();
            bw.newLine();
            BuildCommnet.createClassComment(bw, tableInfo.getComment());
            bw.write("public class " + tableInfo.getBeanName() + " implements Serializable {");
            bw.newLine();
            //属性生成
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                BuildCommnet.createFieldCommnet(bw, fieldInfo.getComment());
                //时间序列化和反序列化
                if (haveLocalDateTime && ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, fieldInfo.getSqlType())) {
                    bw.write("\t" + String.format(Constants.BEAN_DATE_FORMAT_EXPRESSION, TimeUtils.DATE_AND_TIME_HYPHEN));
                    bw.newLine();
                    bw.write("\t" + String.format(Constants.BEAN_DATE_UNFORMAT_EXPRESSION, TimeUtils.DATE_AND_TIME_HYPHEN));
                    bw.newLine();
                }
                if (haveLocalDateTime && ArrayUtils.contains(Constants.SQL_DATE_TYPES, fieldInfo.getSqlType())) {
                    bw.write("\t" + String.format(Constants.BEAN_DATE_FORMAT_EXPRESSION, DateTimeFormatter.ISO_LOCAL_DATE));
                    bw.newLine();
                    bw.write("\t" + String.format(Constants.BEAN_DATE_UNFORMAT_EXPRESSION, DateTimeFormatter.ISO_LOCAL_DATE));
                    bw.newLine();
                }
                //忽略序列化
                if (haveIgnore && ArrayUtils.contains(Constants.IGNORE_BEAN_TOJSON_FILED, fieldInfo.getFieldName())) {
                    bw.write("\t" + Constants.IGNORE_BEAN_TOJSON_EXPRESSION);
                    bw.newLine();
                }
                bw.write("\tprivate " + fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName() + ";");
                bw.newLine();
                bw.newLine();
            }
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                //
                String tempField = StringUtils.uperCaseFirstLetter(fieldInfo.getPropertyName());
                bw.write("\tpublic void set" + tempField + "(" + fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName() + ") {");
                bw.newLine();
                bw.write("\t\tthis." + fieldInfo.getPropertyName() + " = " + fieldInfo.getPropertyName() + ";");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();
                //
                bw.write("\tpublic " + fieldInfo.getJavaType() + " get" + tempField + "() {");
                bw.newLine();
                bw.write("\t\treturn this." + fieldInfo.getPropertyName() + ";");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();
            }
            StringBuffer toString = new StringBuffer();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                String propertyNameString = fieldInfo.getPropertyName();
                if (ArrayUtils.contains(Constants.SQL_DATE_TYPES, fieldInfo.getSqlType())) {
                    propertyNameString = "TimeUtils.format(" + fieldInfo.getPropertyName() + ", TimeFormatEnums.ISO_LOCAL_DATE.getFormat())";
                } else if (ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, fieldInfo.getSqlType())) {
                    propertyNameString = "TimeUtils.format(" + fieldInfo.getPropertyName() + ", TimeFormatEnums.ISO_LOCAL_DATE_TIME_REPLACET2SPACE.getFormat())";
                }
                toString.append(fieldInfo.getComment() + ":\"" + " + " + "(" + fieldInfo.getPropertyName() + " == null ? \"空\" : " + propertyNameString + ")" + " + " + "\"");
                toString.append(",");
            }
            String String = toString.substring(0, toString.lastIndexOf("+"));
            //toString
            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic String toString() {");
            bw.newLine();
            bw.write("\t\treturn \"" + String + ";");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.write("}");
            bw.flush();
        } catch (Exception e) {
            logger.info("创建po失败", e);
        }
    }
}
