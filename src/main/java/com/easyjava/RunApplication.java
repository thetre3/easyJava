package com.easyjava;


import com.easyjava.bean.TableInfo;
import com.easyjava.builder.*;

import java.util.List;


public class RunApplication {
    public static void main(String[] args) {
        List<TableInfo> tableInfoList = BuildTable.getTables();
        for (TableInfo tableInfo : tableInfoList) {
            BuildBase.execute();
            BuildPo.execute(tableInfo);
            BuildQuery.execute(tableInfo);
            BuildMapperDao.execute(tableInfo);
            BuildMapperDaoxml.execute(tableInfo);
            BuildService.execute(tableInfo);
            BuildServiceImpl.execute(tableInfo);
            BuildController.execute(tableInfo);
        }
    }
}
