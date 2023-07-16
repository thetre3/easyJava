package com.easyjava.bean;

import com.easyjava.Utils.PropertiesUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Constants {

    public final static String[] SQL_DATE_TIME_TYPES = new String[]{"datetime", "timestamp"};
    public final static String[] SQL_DATE_TYPES = new String[]{"date"};
    public final static String[] SQL_DECIMAL_TYPES = new String[]{"decimal", "double", "float"};
    public final static String[] SQL_STRING_TYPES = new String[]{"char", "varchar", "text", "mediumtext", "longtext"};
    public final static String[] SQL_INTEGER_TYPES = new String[]{"int", "tinyint"};
    public final static String[] SQL_LONG_TYPES = new String[]{"bigint"};
    //
    public static Map<String, List<String>> TEMPLATE_IMPORT_NAME = new HashMap<>();
    public static List<String> RESOURCES_PACKAGENAME_LIST = new ArrayList<>();
    public static Map<String, String> TEMPLATE_PACKAGE = new HashMap<>();
    //
    public static Boolean IGNORE_TABLE_PREFIX;
    public static Boolean UPERCASE_FIST_LATTER;
    public static String AUTHER_COMMENT;
    //后缀
    public static String SUFFIX_BEAN_QUERY;
    public static String SUFFIX_BEAN_QUERY_FUZZY;
    public static String SUFFIX_BEAN_QUERY_TIME_START;
    public static String SUFFIX_BEAN_QUERY_TIME_END;
    public static String SUFFIX_MAPPERS;
    //ignore
    public static String[] IGNORE_BEAN_TOJSON_FILED;
    public static String IGNORE_BEAN_TOJSON_EXPRESSION;
    public static String IGNORE_BEAN_TOJSON_CLASS;
    //date序列化
    public static String BEAN_DATE_FORMAT_EXPRESSION;
    public static String BEAN_DATE_FORMAT_CLASS;
    public static String BEAN_DATE_UNFORMAT_EXPRESSION;
    public static String BEAN_DATE_UNFORMAT_CLASS;
    //url
    public static String PATH_BASE;
    public static String PATH_JAVA = "java/";
    public static String PATH_QUERY;
    public static String PATH_PO;
    public static String PATH_UTILS;
    public static String PACKAGE_BASE;
    public static String PACKAGE_PO;
    public static String PACKAGE_QUERY;
    public static String PACKAGE_UTILS;
    public static String PACKAGE_ENUMS;
    public static String PATH_ENUMS;
    public static String PACKAGE_SERVICE;
    public static String PATH_SERVICE;
    public static String PACKAGE_SERVICE_IMPL;
    public static String PATH_SERVICE_IMPL;
    public static String PACKAGE_ENTITY_VO;
    public static String PATH_ENTITY_VO;
    public static String PACKAGE_MAPPER;
    public static String PATH_MAPPER;
    public static String PACKAGE_CONTROLLER;
    public static String PATH_CONTROLLER;
    //
    public static String PATH_RESOURCES_MAPPER;

    static {
        //
        AUTHER_COMMENT = PropertiesUtils.getString("auther.comment");
        //
        IGNORE_TABLE_PREFIX = Boolean.valueOf(PropertiesUtils.getString("ignore.table.prefix"));
        //
        UPERCASE_FIST_LATTER = Boolean.valueOf(PropertiesUtils.getString("upercase.fist.latter"));
        //后缀
        SUFFIX_BEAN_QUERY = PropertiesUtils.getString("suffix.bean.query");
        SUFFIX_BEAN_QUERY_FUZZY = PropertiesUtils.getString("suffix.bean.query.fuzzy");
        SUFFIX_BEAN_QUERY_TIME_START = PropertiesUtils.getString("suffix.bean.query.time.start");
        SUFFIX_BEAN_QUERY_TIME_END = PropertiesUtils.getString("suffix.bean.query.time.end");
        SUFFIX_MAPPERS = PropertiesUtils.getString("suffix.mappers");

        //
        IGNORE_BEAN_TOJSON_EXPRESSION = PropertiesUtils.getString("ignore.bean.tojson.expression");
        IGNORE_BEAN_TOJSON_FILED = PropertiesUtils.getString("ignore.bean.tojson.filed").split(",");
        IGNORE_BEAN_TOJSON_CLASS = PropertiesUtils.getString("ignore.bean.tojson.class");
        //
        BEAN_DATE_FORMAT_EXPRESSION = PropertiesUtils.getString("bean.date.format.expression");
        BEAN_DATE_UNFORMAT_EXPRESSION = PropertiesUtils.getString("bean.date.unformat.expression");
        BEAN_DATE_FORMAT_CLASS = PropertiesUtils.getString("bean.date.format.class");
        BEAN_DATE_UNFORMAT_CLASS = PropertiesUtils.getString("bean.date.unformat.class");

        //



        PACKAGE_BASE = PropertiesUtils.getString("package.base");
        PACKAGE_UTILS = PACKAGE_BASE + "." + PropertiesUtils.getString("package.utils");
        PACKAGE_QUERY = PACKAGE_BASE + "." + PropertiesUtils.getString("package.query");
        PACKAGE_PO = PACKAGE_BASE + "." + PropertiesUtils.getString("package.po");
        PACKAGE_ENUMS = PACKAGE_BASE + "." + PropertiesUtils.getString("package.enums");
        PACKAGE_SERVICE = PACKAGE_BASE + "." + PropertiesUtils.getString("package.service");
        PACKAGE_SERVICE_IMPL = PACKAGE_BASE + "." + PropertiesUtils.getString("package.service.impl");
        PACKAGE_ENTITY_VO = PACKAGE_BASE + "." + PropertiesUtils.getString("package.entity.vo");
        PACKAGE_MAPPER = PACKAGE_BASE + "." + PropertiesUtils.getString("package.Mapper");
        PACKAGE_CONTROLLER = PACKAGE_BASE + "." + PropertiesUtils.getString("package.controller");

        PATH_BASE = PropertiesUtils.getString("path.base");
        PATH_ENUMS = PATH_BASE + PATH_JAVA + PACKAGE_ENUMS.replace(".", "/");
        PATH_QUERY = PATH_BASE + PATH_JAVA + PACKAGE_QUERY.replace(".", "/");
        PATH_PO = PATH_BASE + PATH_JAVA + PACKAGE_PO.replace(".", "/");
        PATH_UTILS = PATH_BASE + PATH_JAVA + PACKAGE_UTILS.replace(".", "/");
        PATH_SERVICE = PATH_BASE + PATH_JAVA + PACKAGE_SERVICE.replace(".", "/");
        PATH_SERVICE_IMPL = PATH_BASE + PATH_JAVA + PACKAGE_SERVICE_IMPL.replace(".", "/");
        PATH_ENTITY_VO = PATH_BASE + PATH_JAVA + PACKAGE_ENTITY_VO.replace(".", "/");
        PATH_MAPPER = PATH_BASE + PATH_JAVA + PACKAGE_MAPPER.replace(".", "/");
        PATH_CONTROLLER = PATH_BASE + PATH_JAVA + PACKAGE_CONTROLLER.replace(".", "/");
        //
        PATH_RESOURCES_MAPPER = PATH_BASE + "resources/" + PropertiesUtils.getString("resources.mapper");
    }

    public static Map<String, String> getPackageAndPath(String package_name) {
        Map<String, String> mapUrl = new HashMap<>();
        String key = null;
        String value = null;
        key = "PACKAGE";
        value = Constants.PACKAGE_BASE + "." + package_name;
        mapUrl.put(key, value);
        key = "PATH";
        value = PATH_BASE + PATH_JAVA + value.replace(".", "/");
        mapUrl.put(key, value);
        return mapUrl;
    }

    public static String getResouseTemplatePackege(String package_name) {
        return PATH_BASE + "resources/" + package_name;
    }

    public static void main(String[] args) {
        System.out.println(PATH_MAPPER);
        System.out.println(PACKAGE_MAPPER);
    }

}
