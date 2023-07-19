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

public class BuildService {
    private static final Logger logger = LoggerFactory.getLogger(BuildService.class);

    public static void execute(TableInfo tableInfo) {
        {
            //
            File folder = new File(Constants.PATH_SERVICE);
            String className = tableInfo.getBeanName() + "Service";
            String QueryName = tableInfo.getBeanName() + Constants.SUFFIX_BEAN_QUERY;
            File mapperFile = new File(folder, className + ".java");
            if (!folder.exists()) {
                folder.mkdirs();
            }

            try {
                OutputStream outputStream = Files.newOutputStream(mapperFile.toPath());
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                bw.write("package " + Constants.PACKAGE_SERVICE + ";");
                bw.newLine();
                bw.newLine();
                bw.newLine();
                bw.write("import " + Constants.PACKAGE_PO + "." + tableInfo.getBeanName() + ";");
                bw.newLine();
                bw.write("import " + Constants.PACKAGE_QUERY + "." + QueryName + ";");
                bw.newLine();
                bw.write("import " + Constants.PACKAGE_ENTITY_VO + ".PaginationResultVO;");
                bw.newLine();
                bw.newLine();
                bw.write("import java.util.List;");
                bw.newLine();
                //是否存在时间类型
                if (tableInfo.getHaveDate() || tableInfo.getHaveDateTime()) {
                    bw.write("import java.time.LocalDateTime;");
                    bw.newLine();
                }
                bw.newLine();
                BuildCommnet.createClassComment(bw, className);
                bw.write("public interface " + className + " {");
                bw.newLine();
                bw.newLine();
                BuildCommnet.createFieldCommnet(bw, "根据条件查询列表");
                bw.write("\tList<" + tableInfo.getBeanName() + "> findListByParam(" + QueryName + " query);");
                bw.newLine();
                bw.newLine();
                bw.newLine();
                BuildCommnet.createFieldCommnet(bw, "根据条件查询数量");
                bw.write("\tInteger findCountByParam(" + QueryName + " query);");
                bw.newLine();
                bw.newLine();
                BuildCommnet.createFieldCommnet(bw, "分页查询");
                bw.write("\tPaginationResultVO<" + tableInfo.getBeanName() + "> findListByPage(" + QueryName + " query);");
                bw.newLine();
                bw.newLine();
                BuildCommnet.createFieldCommnet(bw, "新增");
                bw.write("\tInteger add(" + tableInfo.getBeanName() + " bean);");
                bw.newLine();
                BuildCommnet.createFieldCommnet(bw, "批量新增");
                bw.write("\tInteger addBatch(List<" + tableInfo.getBeanName() + "> listBean);");
                bw.newLine();
                BuildCommnet.createFieldCommnet(bw, "批量新增或修改");
                bw.write("\tInteger addOrUpdateBatch(List<" + tableInfo.getBeanName() + "> listBean);");
                bw.newLine();
                for (Map.Entry<String, List<FieldInfo>> entry : tableInfo.getKeyIndexMap().entrySet()) {
                    List<FieldInfo> keyFieldInfoList = entry.getValue();
                    StringJoiner methodName = new StringJoiner("And");
                    StringJoiner methodParams = new StringJoiner(", ");
                    for (FieldInfo fieldInfo : keyFieldInfoList) {
                        methodName.add(StringUtils.uperCaseFirstLetter(fieldInfo.getPropertyName()));
                        methodParams.add(fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName());

                    }
                    BuildCommnet.createFieldCommnet(bw, "根据" + methodName + "查询");
                    bw.write("\t" + tableInfo.getBeanName() + " getBy" + methodName + "(" + methodParams + ");");
                    bw.newLine();
                    bw.newLine();

                    BuildCommnet.createFieldCommnet(bw, "根据" + methodName + "更新");
                    bw.write("\tInteger updateBy" + methodName + "(" + tableInfo.getBeanName() + " bean, " + methodParams + ");");
                    bw.newLine();
                    bw.newLine();

                    BuildCommnet.createFieldCommnet(bw, "根据" + methodName + "删除");
                    bw.write("\tInteger deleteBy" + methodName + "(" + methodParams + ");");
                    bw.newLine();
                    bw.newLine();
                }

                bw.write("}");
                bw.flush();


            } catch (Exception e) {
                logger.info("创建Service失败", e);
            }
        }

    }
}
