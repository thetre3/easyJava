package com.easyjava.builder;

import com.easyjava.Utils.StringUtils;
import com.easyjava.bean.Constants;
import com.easyjava.bean.FieldInfo;
import com.easyjava.bean.TableInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class BuildServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(BuildServiceImpl.class);

    public static void execute(TableInfo tableInfo) {
        {
            //
            File folder = new File(Constants.PATH_SERVICE_IMPL);
            String QueryName = tableInfo.getBeanName() + Constants.SUFFIX_BEAN_QUERY;
            String interfaceName = tableInfo.getBeanName() + "Service";
            String className = tableInfo.getBeanName() + "ServiceImp";
            String daoName = tableInfo.getBeanName() + Constants.SUFFIX_MAPPERS;
            String mapperBeanName = StringUtils.lowerCaseFirstLetter(daoName);
            File mapperFile = new File(folder, className + ".java");
            if (!folder.exists()) {
                folder.mkdirs();
            }

            try {
                OutputStream outputStream = Files.newOutputStream(mapperFile.toPath());
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                bw.write("package " + Constants.PACKAGE_SERVICE_IMPL + ";");
                bw.newLine();
                bw.newLine();
                bw.newLine();
                bw.write("import " + Constants.PACKAGE_QUERY + "." + QueryName + ";");
                bw.newLine();
                bw.write("import " + Constants.PACKAGE_QUERY + ".SimplePage;");
                bw.newLine();
                bw.write("import " + Constants.PACKAGE_ENUMS + ".PageSizeEnum;");
                bw.newLine();
                bw.write("import " + Constants.PACKAGE_SERVICE + "." + interfaceName + ";");
                bw.newLine();
                bw.write("import " + Constants.PACKAGE_MAPPER + "." + daoName + ";");
                bw.newLine();
                bw.write("import " + Constants.PACKAGE_PO + "." + tableInfo.getBeanName() + ";");
                bw.newLine();
                bw.write("import " + Constants.PACKAGE_ENTITY_VO + ".PaginationResultVO;");
                bw.newLine();
                bw.newLine();
                bw.write("import org.springframework.stereotype.Service;");
                bw.newLine();
                bw.write("import javax.annotation.Resource;");
                bw.newLine();
                bw.write("import java.util.List;");
                bw.newLine();
                bw.newLine();
                BuildCommnet.createClassComment(bw, className);
                bw.write("@Service(\"" + StringUtils.lowerCaseFirstLetter(interfaceName) + "\")");
                bw.newLine();
                bw.write("public class " + className + " implements " + interfaceName + " {");
                bw.newLine();
                bw.newLine();
                bw.newLine();
                bw.write("\t@Resource");
                bw.newLine();
                bw.write("\tprivate " + daoName + "<" + tableInfo.getBeanName() + "," + QueryName + "> " + mapperBeanName + ";");


                bw.newLine();
                BuildCommnet.createFieldCommnet(bw, "根据条件查询列表");
                bw.write("\t@Override");
                bw.newLine();
                bw.write("\tpublic List<" + tableInfo.getBeanName() + "> findListByParam(" + QueryName + " query){");
                bw.newLine();
                bw.write("\t\treturn this." + mapperBeanName + ".selectList(query);");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();
                bw.newLine();
                BuildCommnet.createFieldCommnet(bw, "根据条件查询数量");
                bw.write("\t@Override");
                bw.newLine();
                bw.write("\tpublic Integer findCountByParam(" + QueryName + " query){");
                bw.newLine();
                bw.write("\t\treturn this." + mapperBeanName + ".selectCount(query);");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();
                BuildCommnet.createFieldCommnet(bw, "分页查询");
                bw.write("\t@Override");
                bw.newLine();
                bw.write("\tpublic PaginationResultVO<" + tableInfo.getBeanName() + "> findListByPage(" + QueryName + " query){");
                bw.newLine();
                bw.newLine();
                bw.write("\t\tInteger count = this.findCountByParam(query) ;");
                bw.newLine();
                bw.write("\t\tInteger pageSize = query.getPageSize()==null?PageSizeEnum.SIZE15.getSize():query.getPageSize();");
                bw.newLine();
                bw.write("\t\tSimplePage page=new SimplePage(query.getPageNo(),count,pageSize);");
                bw.newLine();
                bw.write("\t\tList<" + tableInfo.getBeanName() + "> list=this.findListByParam(query);");
                bw.newLine();
                bw.write("\t\tquery.setSimplePage(page);");
                bw.newLine();
                bw.write("\t\tPaginationResultVO<" + tableInfo.getBeanName() + "> result=new PaginationResultVO<>(count,page.getPageSize(), page.getPageNo(),page.getPageTotal(),list);");
                bw.newLine();
                bw.write("\t\treturn result;");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();
                BuildCommnet.createFieldCommnet(bw, "新增");
                bw.write("\t@Override");
                bw.newLine();
                bw.write("\tpublic Integer add(" + tableInfo.getBeanName() + " bean){");
                bw.newLine();
                bw.write("\t\treturn this." + mapperBeanName + ".insert(bean);");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                BuildCommnet.createFieldCommnet(bw, "批量新增");
                bw.write("\t@Override");
                bw.newLine();
                bw.write("\tpublic Integer addBatch(List<" + tableInfo.getBeanName() + "> listBean){");
                bw.newLine();
                bw.write("\t\tif (listBean ==null||listBean.isEmpty()){");
                bw.newLine();
                bw.write("\t\t\treturn 0;");
                bw.newLine();
                bw.write("\t\t}");
                bw.newLine();
                bw.write("\t\treturn this." + mapperBeanName + ".insertBatch(listBean);");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();
                BuildCommnet.createFieldCommnet(bw, "批量新增或修改");

                bw.newLine();
                bw.write("\tpublic Integer addOrUpdateBatch(List<" + tableInfo.getBeanName() + "> listBean){");
                bw.newLine();
                bw.write("\t\tif (listBean ==null||listBean.isEmpty()){");
                bw.newLine();
                bw.write("\t\t\treturn 0;");
                bw.newLine();
                bw.write("\t\t}");
                bw.newLine();
                bw.write("\t\treturn this." + mapperBeanName + ".insertOrUpdateBatch(listBean);");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();
                for (Map.Entry<String, List<FieldInfo>> entry : tableInfo.getKeyIndexMap().entrySet()) {
                    List<FieldInfo> keyFieldInfoList = entry.getValue();
                    StringJoiner methodName = new StringJoiner("And");
                    StringJoiner methodParams = new StringJoiner(", ");
                    StringJoiner propertyName = new StringJoiner(",");
                    for (FieldInfo fieldInfo : keyFieldInfoList) {
                        methodName.add(StringUtils.uperCaseFirstLetter(fieldInfo.getPropertyName()));
                        methodParams.add(fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName());
                        propertyName.add(fieldInfo.getPropertyName());

                    }
                    BuildCommnet.createFieldCommnet(bw, "根据" + methodName + "查询");
                    bw.write("\t@Override");
                    bw.newLine();
                    bw.write("\tpublic " + tableInfo.getBeanName() + " getBy" + methodName + "(" + methodParams + "){");
                    bw.newLine();
                    bw.write("\t\treturn this." + mapperBeanName + ".selectBy" + methodName + "(" + propertyName + ");");
                    bw.newLine();
                    bw.write("\t}");
                    bw.newLine();
                    bw.newLine();

                    BuildCommnet.createFieldCommnet(bw, "根据" + methodName + "更新");
                    bw.write("\t@Override");
                    bw.newLine();
                    bw.write("\tpublic Integer updateBy" + methodName + "(" + tableInfo.getBeanName() + " bean, " + methodParams + "){");
                    bw.newLine();
                    bw.write("\t\treturn this." + mapperBeanName + ".updateBy" + methodName + "(bean," + propertyName + ");");
                    bw.newLine();
                    bw.write("\t}");
                    bw.newLine();
                    bw.newLine();
                    bw.newLine();

                    BuildCommnet.createFieldCommnet(bw, "根据" + methodName + "删除");
                    bw.write("\t@Override");
                    bw.newLine();
                    bw.write("\tpublic Integer deleteBy" + methodName + "(" + methodParams + "){");
                    bw.newLine();
                    bw.write("\t\treturn this." + mapperBeanName + ".deleteBy" + methodName + "(" + propertyName + ");");
                    bw.newLine();
                    bw.write("\t}");
                    bw.newLine();
                    bw.newLine();
                    bw.newLine();
                }

                bw.write("}");
                bw.flush();


            } catch (Exception e) {
                logger.info("创建ServiceImpl失败", e);
            }
        }

    }
}
