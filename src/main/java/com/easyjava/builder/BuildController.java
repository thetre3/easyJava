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

public class BuildController {
    private static final Logger logger = LoggerFactory.getLogger(BuildController.class);

    public static void execute(TableInfo tableInfo) {
        {
            //
            File folder = new File(Constants.PATH_CONTROLLER);
            String QueryName = tableInfo.getBeanName() + Constants.SUFFIX_BEAN_QUERY;
            String className = tableInfo.getBeanName() + "Controller";
            String serviceName = tableInfo.getBeanName() + "Service";
            String servicBeanName = StringUtils.lowerCaseFirstLetter(serviceName);


            try {
                File mapperFile = new File(folder, className + ".java");
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                OutputStream outputStream = Files.newOutputStream(mapperFile.toPath());
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                bw.write("package " + Constants.PACKAGE_CONTROLLER + ";");
                bw.newLine();
                bw.newLine();
                bw.newLine();
                bw.write("import " + Constants.PACKAGE_QUERY + "." + QueryName + ";");
                bw.newLine();

                bw.write("import " + Constants.PACKAGE_ENTITY_VO + ".ResponseVO;");
                bw.newLine();
                bw.write("import " + Constants.PACKAGE_SERVICE + "." + serviceName + ";");
                bw.newLine();
                bw.write("import " + Constants.PACKAGE_PO + "." + tableInfo.getBeanName() + ";");
                bw.newLine();
                bw.write("import " + Constants.PACKAGE_ENTITY_VO + ".PaginationResultVO;");
                bw.newLine();
                bw.write("import org.springframework.web.bind.annotation.RequestBody;");
                bw.newLine();
                bw.write("import org.springframework.web.bind.annotation.RestController;");
                bw.newLine();
                bw.write("import org.springframework.web.bind.annotation.RequestMapping;");
                bw.newLine();
                bw.newLine();
                bw.write("import javax.annotation.Resource;");
                bw.newLine();
                bw.write("import java.util.List;");
                bw.newLine();
                bw.newLine();
                BuildCommnet.createClassComment(bw, className);
                bw.write("@RestController");
                bw.newLine();
                bw.write("@RequestMapping(\"" + StringUtils.lowerCaseFirstLetter(tableInfo.getBeanName()) + "\")");
                bw.newLine();
                bw.write("public class " + className + " extends BaseController {");
                bw.newLine();
                bw.newLine();
                bw.newLine();
                bw.write("\t@Resource");
                bw.newLine();
                bw.write("\tprivate " + serviceName + " " + servicBeanName + ";");
                bw.newLine();
                bw.newLine();
                BuildCommnet.createFieldCommnet(bw, "根据条件分页查询");
                bw.newLine();
                bw.write("\t@RequestMapping(\"loadDateList\")");
                bw.newLine();
                bw.write("\tpublic ResponseVO<" + tableInfo.getBeanName() + "> loadDateList(" + QueryName + " query) {");
                bw.newLine();
                bw.write("\t\tPaginationResultVO<" + tableInfo.getBeanName() + "> listByPage = " + servicBeanName + ".findListByPage(query);");
                bw.newLine();
                bw.write("\t\tif (listByPage.getTotalCount() == 0) {");
                bw.newLine();
                bw.write("\t\t\treturn getErrorResponseVO(null);");
                bw.newLine();
                bw.write("\t\t}");
                bw.newLine();
                bw.write("\t\treturn getSuccessResponseVO(listByPage);");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();
                BuildCommnet.createFieldCommnet(bw, "新增");
                bw.write("\t@RequestMapping(\"add\")");
                bw.newLine();
                bw.write("\tpublic ResponseVO add(" + tableInfo.getBeanName() + " bean) {");
                bw.newLine();
                bw.write("\t\t" + servicBeanName + ".add(bean);");
                bw.newLine();
                bw.write("\t\treturn getSuccessResponseVO(null);");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();
                BuildCommnet.createFieldCommnet(bw, "批量新增");
                bw.write("\t@RequestMapping(\"addBatch\")");
                bw.newLine();
                bw.write("\tpublic ResponseVO addBatch(@RequestBody List<" + tableInfo.getBeanName() + "> listBean) {");
                bw.newLine();
                bw.write("\t\t" + servicBeanName + ".addBatch(listBean);");
                bw.newLine();
                bw.write("\t\treturn getSuccessResponseVO(null);");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();
                BuildCommnet.createFieldCommnet(bw, "批量新增或修改");
                bw.newLine();
                bw.write("\t@RequestMapping(\"addOrUpdateBatch\")");
                bw.newLine();
                bw.write("\tpublic ResponseVO addOrUpdateBatch(@RequestBody List<" + tableInfo.getBeanName() + "> listBean) {");
                bw.newLine();
                bw.write("\t\t" + servicBeanName + ".addOrUpdateBatch(listBean);");
                bw.newLine();
                bw.write("\t\treturn getSuccessResponseVO(null);");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();
                for (Map.Entry<String, List<FieldInfo>> entry : tableInfo.getKeyIndexMap().entrySet()) {
                    List<FieldInfo> keyFieldInfoList = entry.getValue();
                    StringJoiner methodName = new StringJoiner("And");
                    StringJoiner methodParams = new StringJoiner(", ");
                    StringJoiner propertyName = new StringJoiner(", ");
                    for (FieldInfo fieldInfo : keyFieldInfoList) {
                        methodName.add(StringUtils.uperCaseFirstLetter(fieldInfo.getPropertyName()));
                        methodParams.add(fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName());
                        propertyName.add(fieldInfo.getPropertyName());
                    }
                    BuildCommnet.createFieldCommnet(bw, "根据" + methodName + "查询");
                    bw.write("\t@RequestMapping(\"getBy" + methodName + "\")");
                    bw.newLine();
                    bw.write("\tpublic ResponseVO getBy" + methodName + "(" + methodParams + ") {");
                    bw.newLine();
                    bw.write("\t\t\t"+tableInfo.getBeanName()+" Info = " + servicBeanName + ".getBy" + methodName + "(" + propertyName + ");");
                    bw.newLine();
                    bw.write("\t\t\tif (Info == null) {");
                    bw.newLine();
                    bw.write("\t\t\t\treturn getErrorResponseVO(null);");
                    bw.newLine();
                    bw.write("\t\t\t}");
                    bw.newLine();
                    bw.write("\t\treturn getSuccessResponseVO(" + servicBeanName + ".getBy" + methodName + "(" + propertyName + "));");
                    bw.newLine();
                    bw.write("\t}");
                    bw.newLine();
                    bw.newLine();
                    BuildCommnet.createFieldCommnet(bw, "根据" + methodName + "更新");
                    bw.write("\t@RequestMapping(\"updateBy" + methodName + "\")");
                    bw.newLine();
                    bw.write("\tpublic ResponseVO updateBy" + methodName + "(" + tableInfo.getBeanName() + " bean, " + methodParams + ") {");
                    bw.newLine();
                    bw.write("\t\tInteger isSuccess=" + servicBeanName + ".updateBy" + methodName + "(bean, " + propertyName + ");");
                    bw.newLine();
                    bw.write("\t\t\tif (isSuccess == 0) {");
                    bw.newLine();
                    bw.write("\t\t\t\treturn getErrorResponseVO(null);");
                    bw.newLine();
                    bw.write("\t\t\t}");
                    bw.newLine();
                    bw.write("\t\treturn getSuccessResponseVO(null);");
                    bw.newLine();
                    bw.write("\t}");
                    bw.newLine();
                    bw.newLine();
                    BuildCommnet.createFieldCommnet(bw, "根据" + methodName + "删除");
                    bw.write("\t@RequestMapping(\"deleteBy" + methodName + "\")");
                    bw.newLine();
                    bw.write("\tpublic ResponseVO deleteBy" + methodName + "(" + methodParams + ") {");
                    bw.newLine();
                    bw.write("\t\tInteger isSuccess=" + servicBeanName + ".deleteBy" + methodName + "(" + propertyName + ");");
                    bw.newLine();
                    bw.write("\t\t\tif (isSuccess == 0) {");
                    bw.newLine();
                    bw.write("\t\t\t\treturn getErrorResponseVO(null);");
                    bw.newLine();
                    bw.write("\t\t\t}");
                    bw.newLine();
                    bw.write("\t\treturn getSuccessResponseVO(null);");
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
