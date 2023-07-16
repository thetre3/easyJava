package com.easyjava.builder;

import com.easyjava.Utils.StringUtils;
import com.easyjava.bean.Constants;
import com.easyjava.bean.FieldInfo;
import com.easyjava.bean.TableInfo;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;


public class BuildQuery {
    private static final Logger logger = LoggerFactory.getLogger(BuildQuery.class);

    private static void getAndSetProcess(BufferedWriter bw, List<FieldInfo> infoList) throws IOException {
        for (FieldInfo info : infoList) {
            String tempField = StringUtils.uperCaseFirstLetter(info.getPropertyName());
            bw.write("\tpublic void set" + tempField + "(" + info.getJavaType() + " " + info.getPropertyName() + ") {");
            bw.newLine();
            bw.write("\t\tthis." + info.getPropertyName() + " = " + info.getPropertyName() + ";");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();
            bw.write("\tpublic " + info.getJavaType() + " get" + tempField + "() {");
            bw.newLine();
            bw.write("\t\treturn this." + info.getPropertyName() + ";");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();
        }

    }

    public static void execute(TableInfo tableInfo) {

        File folder = new File(Constants.PATH_QUERY);
        File poFile = new File(folder, tableInfo.getBeanParamName() + ".java");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        OutputStream out = null;
        OutputStreamWriter outputStreamWriter = null;
        BufferedWriter bw = null;
        try {
            out = Files.newOutputStream(poFile.toPath());
            outputStreamWriter = new OutputStreamWriter(out, StandardCharsets.UTF_8);
            bw = new BufferedWriter(outputStreamWriter);
            bw.write("package " + Constants.PACKAGE_QUERY + ";");
            bw.newLine();
            bw.newLine();
            //是否存在时间类型
            if (tableInfo.getHaveDate() || tableInfo.getHaveDateTime()) {
                bw.write("import java.time.LocalDateTime;");
                bw.newLine();
            }
            //
            if (tableInfo.getHaveBigDecimal()) {
                bw.write("import java.math.BigDecimal;");
                bw.newLine();
            }
            bw.newLine();
            bw.newLine();
            BuildCommnet.createClassComment(bw, tableInfo.getComment());
            bw.write("public class " + tableInfo.getBeanParamName() + " extends BaseQuery {");
            bw.newLine();

            //属性生成
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                BuildCommnet.createFieldCommnet(bw, fieldInfo.getComment());
                bw.write("\tprivate " + fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName() + ";");
                bw.newLine();
                bw.newLine();
                //String
                String PropertyName = fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_FUZZY;
                if (ArrayUtils.contains(Constants.SQL_STRING_TYPES, fieldInfo.getSqlType())) {
                    bw.write("\tprivate " + fieldInfo.getJavaType() + " " + PropertyName + ";");
                    bw.newLine();
                    bw.newLine();
                }
                if (ArrayUtils.contains(Constants.SQL_DATE_TYPES, fieldInfo.getSqlType()) || ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, fieldInfo.getSqlType())) {
                    PropertyName = fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_TIME_START;
                    bw.write("\tprivate String" + " " + fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_TIME_START + ";");
                    bw.newLine();
                    bw.newLine();

                    PropertyName = fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_TIME_END;
                    bw.write("\tprivate String" + " " + PropertyName + ";");
                    bw.newLine();
                    bw.newLine();

                }

            }
            //getandset
            getAndSetProcess(bw, tableInfo.getExtendFieldList());
            getAndSetProcess(bw, tableInfo.getFieldList());
            bw.newLine();
            bw.write("}");
            bw.flush();


        } catch (Exception e) {
            logger.info("创建Query失败", e);
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStreamWriter != null) {
                try {
                    outputStreamWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
