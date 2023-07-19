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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * 拆分构建方法
 */
public class BuildMapperDaoxml {
    private static Map<String, String> keymap = new HashMap<>();
    private static Map<String, List<FieldInfo>> keyIndexMap = new HashMap<>();
    private static String poclass;
    private static String className;
    private static FileOutputStream fileOutputStream = null;
    private static BufferedWriter bw = null;
    private static final String BASE_COLUMN_LIST = "base_column_list";
    private static final String BASE_QUERY_CONDITION = "base_query_condition";
    private static final String BASE_QUERY_CONDITION_EXTEND = "base_query_condition_extend";
    private static final String QUERY_CONDITION = "query_condition";
    private static final Logger logger = LoggerFactory.getLogger(BuildMapperDaoxml.class);

    public static void execute(TableInfo tableInfo) {
        //输出流
        try {
            StartBufferedWriter(tableInfo);
            //
            createHeader();
            //实体映射
            createResultMap(tableInfo);
            //通用查询结果列
            createBaseColumList(tableInfo);
            //基础查询条件
            createbaseQueryCondition(tableInfo);
            //扩展基础查询条件
            createBaseQueryConditionExtend(tableInfo);
            //通用查询条件
            createQueryCondition();
            //查询列表
            createBaseColumnList(tableInfo);
            //查询数量
            createSelectCount(tableInfo);
            //插入(匹配有值的字段)
            createInsert(tableInfo);
            //插入或更新(匹配有值的字段)
            createInsertOrUpdate(tableInfo);
            //批量新增修改(批量插入)
            createInsertBatchAndOrUpdate(tableInfo);
            //根据唯一键值操作方法
            createUniqueKeyMethods(tableInfo);
            //
            createEndAndClose();
        } catch (IOException e) {
            logger.info("创建mapper.xml文件失败", e);
        }
    }

    private static void createHeader() throws IOException {
        bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
        bw.newLine();
        bw.write("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" ");
        bw.newLine();
        bw.write("\t\t\"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">");
        bw.newLine();
        bw.write("<mapper namespace=\"" + Constants.PACKAGE_MAPPER + "." + className + "\">");
        bw.newLine();
    }

    private static void StartBufferedWriter(TableInfo tableInfo) throws IOException {
        className = tableInfo.getBeanName() + Constants.SUFFIX_MAPPERS;
        File folder = new File(Constants.PATH_RESOURCES_MAPPER);
        File MapperxmlFile = new File(folder, className + ".xml");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        fileOutputStream = new FileOutputStream(MapperxmlFile);
        bw = new BufferedWriter(new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8));
    }

    private static void createResultMap(TableInfo tableInfo) throws IOException {
        bw.write("\t<!--实体映射-->");
        bw.newLine();
        poclass = Constants.PACKAGE_PO + "." + tableInfo.getBeanName();
        bw.write("\t<resultMap id=\"base_result_map\" type=\"" + poclass + "\">");
        bw.newLine();
        FieldInfo idField = null;
        //拿取主键数据
        keyIndexMap = tableInfo.getKeyIndexMap();
        for (Map.Entry<String, List<FieldInfo>> entry : keyIndexMap.entrySet()) {
            if ("PRIMARY".equals(entry.getKey())) {
                List<FieldInfo> fieldInfoList = entry.getValue();
                if (fieldInfoList.size() == 1) {
                    idField = fieldInfoList.get(0);
                    break;
                }
            }
        }
        for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
            bw.write("\t\t<!--" + fieldInfo.getComment() + "-->");
            bw.newLine();
            String key;
            if (idField != null && fieldInfo.getPropertyName().equals(idField.getPropertyName())) {
                key = "id";
            } else {
                key = "result";
            }
            bw.write("\t\t<" + key + " column=\"" + fieldInfo.getFieldName() + "\" property=\"" + fieldInfo.getPropertyName() + "\"/>");
            bw.newLine();
        }
        bw.write("\t</resultMap>");
        bw.newLine();
        bw.newLine();
    }

    private static void createBaseColumList(TableInfo tableInfo) throws IOException {
        bw.write("\t<!--通用查询结果列-->");
        bw.newLine();
        bw.write("\t<sql id=\"" + BASE_COLUMN_LIST + "\">");
        bw.newLine();
        StringJoiner columnBuilder = new StringJoiner(",");
        for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
            columnBuilder.add(fieldInfo.getFieldName());
        }
        bw.write("\t" + columnBuilder);
        bw.newLine();
        bw.write("\t</sql>");
        bw.newLine();
        bw.newLine();
    }

    private static void createbaseQueryCondition(TableInfo tableInfo) throws IOException {
        bw.write("\t<!--基础查询条件-->");
        bw.newLine();
        bw.write("\t<sql id=\"" + BASE_QUERY_CONDITION + "\">");
        bw.newLine();
        for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
            String stringQuery = "";
            if (ArrayUtils.contains(Constants.SQL_STRING_TYPES, fieldInfo.getSqlType())) {
                stringQuery = " and query." + fieldInfo.getPropertyName() + "!=''";
            }
            bw.write("\t\t<if test=\"query." + fieldInfo.getPropertyName() + " !=null " + stringQuery + "\">");
            bw.newLine();
            bw.write("\t\t\tand " + fieldInfo.getFieldName() + " = #{query." + fieldInfo.getPropertyName() + "}");
            bw.newLine();
            bw.write("\t\t</if>");
            bw.newLine();
        }
        bw.write("\t</sql>");
        bw.newLine();
        bw.newLine();
    }

    private static void createBaseQueryConditionExtend(TableInfo tableInfo) throws IOException {
        bw.write("\t<!--扩展基础查询条件-->");
        bw.newLine();
        bw.write("\t<sql id=\"" + BASE_QUERY_CONDITION_EXTEND + "\">");
        bw.newLine();
        for (FieldInfo fieldInfo : tableInfo.getExtendFieldList()) {
            String andWhere = null;
            if (ArrayUtils.contains(Constants.SQL_STRING_TYPES, fieldInfo.getSqlType())) {
                andWhere = "and " + fieldInfo.getFieldName() + " like concat('%', #{query." + fieldInfo.getPropertyName() + "}, '%')";
            } else if (ArrayUtils.contains(Constants.SQL_DATE_TYPES, fieldInfo.getSqlType()) || ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, fieldInfo.getSqlType())) {
                if (fieldInfo.getPropertyName().endsWith(Constants.SUFFIX_BEAN_QUERY_TIME_END)) {
                    andWhere = "<![CDATA[ and " + fieldInfo.getFieldName() + " < date_sub(str_to_date(#{query." + fieldInfo.getPropertyName() + "}, '%Y-%m-%d')," +
                            "interval -1 day)]]>";
                } else if (fieldInfo.getPropertyName().endsWith(Constants.SUFFIX_BEAN_QUERY_TIME_START)) {
                    andWhere = "<![CDATA[ and " + fieldInfo.getFieldName() + " >= str_to_date(#{query." + fieldInfo.getPropertyName() + "}, '%Y-%m-%d') ]]>";
                }
            }
            bw.write("\t\t<if test=\"query." + fieldInfo.getPropertyName() + " !=null and query." + fieldInfo.getPropertyName() + " != ''\">");
            bw.newLine();
            bw.write("\t\t\t" + andWhere);
            bw.newLine();
            bw.write("\t\t</if>");
            bw.newLine();
        }
        bw.write("\t</sql>");
        bw.newLine();
        bw.newLine();
    }

    private static void createQueryCondition() throws IOException {
        bw.write("\t<!--通用查询条件-->");
        bw.newLine();
        bw.write("\t<sql id=\"" + QUERY_CONDITION + "\">");
        bw.newLine();
        bw.write("\t\t<where>");
        bw.newLine();
        bw.write("\t\t\t<include refid=\"" + BASE_QUERY_CONDITION + "\"/>");
        bw.newLine();
        bw.write("\t\t\t<include refid=\"" + BASE_QUERY_CONDITION_EXTEND + "\"/>");
        bw.newLine();
        bw.write("\t\t</where>");
        bw.newLine();
        bw.write("\t</sql>");
        bw.newLine();
        bw.newLine();
    }

    private static void createBaseColumnList(TableInfo tableInfo) throws IOException {
        bw.write("\t<!--查询列表-->");
        bw.newLine();
        bw.write("\t<select id=\"selectList\" resultMap=\"base_result_map\">");
        bw.newLine();
        bw.write("\t\tSELECT <include refid=\"" + BASE_COLUMN_LIST + "\"/> FROM " + tableInfo.getTableName() + " <include refid=\"" + QUERY_CONDITION + "\"/>");
        bw.newLine();
        bw.write("\t\t<if test=\"query.orderBY!=null\">order by ${query.orderBY}</if>");
        bw.newLine();
        bw.write("\t\t<if test=\"query.simplePage!=null\">limit #{query.simplePage.start},#{query.simplePage.end}</if>");
        bw.newLine();
        bw.write("\t</select>");
        bw.newLine();
        bw.newLine();
    }

    private static void createSelectCount(TableInfo tableInfo) throws IOException {
        bw.write("\t<!--查询数量-->");
        bw.newLine();
        bw.write("\t<select id=\"selectCount\" resultType=\"java.lang.Integer\">");
        bw.newLine();
        bw.write("\t\tSELECT count(1) FROM " + tableInfo.getTableName() + " <include refid=\"" + QUERY_CONDITION + "\"/>");
        bw.newLine();
        bw.write("\t</select>");
        bw.newLine();
        bw.newLine();
    }

    private static void createInsert(TableInfo tableInfo) throws IOException {
        bw.write("\t<!--插入(匹配有值的字段)-->");
        bw.newLine();
        bw.write("\t<insert id=\"insert\" parameterType=\"" + poclass + "\">");
        bw.newLine();
        FieldInfo autoIncrementField = null;
        for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
            if (fieldInfo.getAutoIncrement() != null && fieldInfo.getAutoIncrement()) {
                autoIncrementField = fieldInfo;
                break;
            }
        }
        //自动生成主键
        if (autoIncrementField != null) {
            bw.write("\t\t<selectKey keyProperty=\"bean." + autoIncrementField.getPropertyName() + "\" resultType=\"" + autoIncrementField.getJavaType() + "\" order=\"AFTER\">");
            bw.newLine();
            bw.write("\t\t\tSELECT LAST_INSERT_ID()");
            bw.newLine();
            bw.write("\t\t</selectKey>");
        }
        bw.newLine();
        bw.write("\t\t\tINSERT INTO " + tableInfo.getTableName());
        bw.newLine();
        bw.write("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
        bw.newLine();
        for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
            bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " !=null\">");
            bw.newLine();
            bw.write("\t\t\t\t" + fieldInfo.getFieldName() + ",");
            bw.newLine();
            bw.write("\t\t\t</if>");
            bw.newLine();
        }
        bw.write("\t\t</trim>");
        bw.newLine();
        bw.write("\t\t<trim prefix=\"VALUES (\" suffix=\")\" suffixOverrides=\",\">");
        bw.newLine();
        for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
            bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " !=null\">");
            bw.newLine();
            bw.write("\t\t\t\t#{bean." + fieldInfo.getPropertyName() + "},");
            bw.newLine();
            bw.write("\t\t\t</if>");
            bw.newLine();
        }
        bw.write("\t\t</trim>");
        bw.newLine();
        bw.newLine();
        bw.write("\t</insert>");
        bw.newLine();
        bw.newLine();
    }

    private static void createInsertOrUpdate(TableInfo tableInfo) throws IOException {
        bw.write("\t<!--插入或更新(匹配有值的字段)-->");
        bw.newLine();
        bw.write("\t<insert id=\"insertOrUpdate\" parameterType=\"" + poclass + "\">");
        bw.newLine();
        bw.write("\t\t\tINSERT INTO " + tableInfo.getTableName());
        bw.newLine();
        bw.write("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
        bw.newLine();
        for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
            bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " !=null\">");
            bw.newLine();
            bw.write("\t\t\t\t" + fieldInfo.getFieldName() + ",");
            bw.newLine();
            bw.write("\t\t\t</if>");
            bw.newLine();
        }
        bw.write("\t\t</trim>");
        bw.newLine();
        bw.write("\t\t<trim prefix=\"VALUES (\" suffix=\")\" suffixOverrides=\",\">");
        bw.newLine();
        for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
            bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " !=null\">");
            bw.newLine();
            bw.write("\t\t\t\t#{bean." + fieldInfo.getPropertyName() + "},");
            bw.newLine();
            bw.write("\t\t\t</if>");
            bw.newLine();
        }
        bw.write("\t\t</trim>");
        bw.newLine();
        bw.write("\t\t\ton DUPLICATE key update");
        bw.newLine();
        bw.write("\t\t<trim prefix=\"\" suffix=\"\" suffixOverrides=\",\">");
        bw.newLine();
        //唯一性值不允许修改
        for (Map.Entry<String, List<FieldInfo>> entry : keyIndexMap.entrySet()) {
            List<FieldInfo> fieldInfoList = entry.getValue();
            for (FieldInfo item : fieldInfoList) {
                keymap.put(item.getFieldName(), item.getFieldName());
            }
        }
        for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
            if (keymap.get(fieldInfo.getFieldName()) != null) {
                continue;
            }
            bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " !=null\">");
            bw.newLine();
            bw.write("\t\t\t\t" + fieldInfo.getFieldName() + " =VALUES(" + fieldInfo.getFieldName() + "),");
            bw.newLine();
            bw.write("\t\t\t</if>");
            bw.newLine();
        }
        bw.write("\t\t</trim>");
        bw.newLine();
        bw.write("\t</insert>");
        bw.newLine();
        bw.newLine();
    }

    private static void createInsertBatchAndOrUpdate(TableInfo tableInfo) throws IOException {
        bw.write("\t<!--批量插入-->");
        bw.newLine();
        bw.write("\t<insert id=\"insertBatch\" parameterType=\"" + poclass + "\">");
        bw.newLine();
        StringJoiner insertFile = new StringJoiner(",");
        StringJoiner insertProperty = new StringJoiner(",\n");
        StringJoiner bean_proName = new StringJoiner("},#{item.", "#{item.", "}");
        for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
            insertFile.add(fieldInfo.getFieldName());
            bean_proName.add(fieldInfo.getPropertyName());
            if (keymap.get(fieldInfo.getFieldName()) != null) {
                continue;
            }
            insertProperty.add("\t\t" + fieldInfo.getFieldName() + " = VALUES(" + fieldInfo.getFieldName() + ")");
        }
        bw.write("\t\tINSERT INTO " + tableInfo.getTableName() + "(" + insertFile + ")values");
        bw.newLine();
        bw.write("\t\t<foreach collection=\"list\" item=\"item\" separator=\",\">");
        bw.newLine();
        bw.write("\t\t\t(" + bean_proName + ")");
        bw.newLine();
        bw.write("\t\t</foreach>");
        bw.newLine();
        bw.write("\t</insert>");
        bw.newLine();
        bw.newLine();
        //
        bw.write("\t<!--批量新增修改(批量插入)-->");
        bw.newLine();
        bw.write("\t<insert id=\"insertOrUpdateBatch\" parameterType=\"" + poclass + "\">");
        bw.newLine();
        bw.write("\t\tINSERT INTO " + tableInfo.getTableName() + "(" + insertFile + ")values");
        bw.newLine();
        bw.write("\t\t<foreach collection=\"list\" item=\"item\" separator=\",\">");
        bw.newLine();
        bw.write("\t\t\t(" + bean_proName + ")");
        bw.newLine();
        bw.write("\t\t</foreach>");
        bw.newLine();
        bw.write("\t\ton DUPLICATE key update");
        bw.newLine();
        bw.write(String.valueOf(insertProperty));
        bw.newLine();
        bw.write("\t</insert>");
        bw.newLine();
        bw.newLine();
    }

    private static void createUniqueKeyMethods(TableInfo tableInfo) throws IOException {
        for (Map.Entry<String, List<FieldInfo>> entry : keyIndexMap.entrySet()) {
            List<FieldInfo> keyFieldInfoList = entry.getValue();
            StringJoiner methodName = new StringJoiner("And");
            StringJoiner paramName = new StringJoiner(" and ", " where ", "");
            for (FieldInfo fieldInfo : keyFieldInfoList) {
                methodName.add(StringUtils.uperCaseFirstLetter(fieldInfo.getPropertyName()));
                paramName.add(fieldInfo.getFieldName() + "=#{" + fieldInfo.getPropertyName() + "}");
            }
            bw.write("\t<!--根据" + methodName + "查询-->");
            bw.newLine();
            bw.write("\t<select id=\"selectBy" + methodName + "\" resultMap=\"base_result_map\">");
            bw.newLine();
            bw.write("\t\tSELECT <include refid=\"" + BASE_COLUMN_LIST + "\"/> FROM " + tableInfo.getTableName() + paramName);
            bw.newLine();
            bw.write("\t</select>");
            bw.newLine();
            bw.newLine();
            bw.write("\t<!--根据" + methodName + "更新-->");
            bw.newLine();
            bw.write("\t<update  id=\"updateBy" + methodName + "\" parameterType=\"" + poclass + "\">");
            bw.newLine();
            bw.write("\t\tUPDATE " + tableInfo.getTableName());
            bw.newLine();
            bw.write("\t\t<set>");
            bw.newLine();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " !=null\">");
                bw.newLine();
                bw.write("\t\t\t\t" + fieldInfo.getFieldName() + " = #{bean." + fieldInfo.getPropertyName() + "},");
                bw.newLine();
                bw.write("\t\t\t</if>");
                bw.newLine();
            }
            bw.write("\t\t</set>");
            bw.newLine();
            bw.write("\t   " + paramName);
            bw.newLine();
            bw.write("\t</update>");
            bw.newLine();
            bw.newLine();
            bw.write("\t<!--根据" + methodName + "删除-->");
            bw.newLine();
            bw.write("\t<delete id=\"deleteBy" + methodName + "\">");
            bw.newLine();
            bw.write("\t\tdelete from " + tableInfo.getTableName() + paramName);
            bw.newLine();
            bw.write("\t</delete>");
            bw.newLine();
            bw.newLine();
        }
    }

    private static void createEndAndClose() throws IOException {
        bw.newLine();
        bw.write("</mapper>");
        bw.flush();
        if (bw != null) {
            bw.close();
        }
        if (fileOutputStream != null) {
            fileOutputStream.close();
        }
    }
}
