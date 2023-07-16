package com.easyjava.bean;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TableInfo {
//    表名
    private String tableName;
//    bean名称
    private String beanName;
//    参数名称
    private String beanParamName;
//    表注释
    private String comment;
//    字段信息
    private List<FieldInfo> fieldList;
    //扩展字段信息
    private List<FieldInfo> extendFieldList;
//    唯一索引集合
    private Map<String,List<FieldInfo>> KeyIndexMap=new LinkedHashMap<>();
    //
    private Boolean haveAutoIncrement=false;
    private  Boolean haveDate=false;
    private Boolean haveDateTime=false;
    private Boolean haveBigDecimal=false;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanParamName() {
        return beanParamName;
    }

    public void setBeanParamName(String beanParamName) {
        this.beanParamName = beanParamName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<FieldInfo> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<FieldInfo> fieldList) {
        this.fieldList = fieldList;
    }

    public Map<String, List<FieldInfo>> getKeyIndexMap() {
        return KeyIndexMap;
    }

    public void setKeyIndexMap(Map<String, List<FieldInfo>> keyIndexMap) {
        KeyIndexMap = keyIndexMap;
    }

    public Boolean getHaveDate() {
        return haveDate;
    }

    public void setHaveDate(Boolean haveDate) {
        this.haveDate = haveDate;
    }

    public Boolean getHaveDateTime() {
        return haveDateTime;
    }

    public void setHaveDateTime(Boolean haveDateTime) {
        this.haveDateTime = haveDateTime;
    }

    public Boolean getHaveBigDecimal() {
        return haveBigDecimal;
    }

    public List<FieldInfo> getExtendFieldList() {
        return extendFieldList;
    }

    public void setExtendFieldList(List<FieldInfo> extendFieldList) {
        this.extendFieldList = extendFieldList;
    }

    public void setHaveBigDecimal(Boolean haveBigDecimal) {
        this.haveBigDecimal = haveBigDecimal;
    }

}
