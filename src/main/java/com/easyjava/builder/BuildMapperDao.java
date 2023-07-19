package com.easyjava.builder;

import com.easyjava.Utils.StringUtils;
import com.easyjava.bean.Constants;
import com.easyjava.bean.FieldInfo;
import com.easyjava.bean.TableInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class BuildMapperDao {
    private static final Logger logger = LoggerFactory.getLogger(BuildMapperDao.class);

    public static void execute(TableInfo tableInfo) {
        {
            File folder = new File(Constants.PATH_MAPPER);
            String className = tableInfo.getBeanName() + Constants.SUFFIX_MAPPERS;
            File mapperFile = new File(folder, className + ".java");
            if (!folder.exists()) {
                folder.mkdirs();
            }
            try {
                OutputStream outputStream = Files.newOutputStream(mapperFile.toPath());
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                bw.write("package " + Constants.PACKAGE_MAPPER + ";");
                bw.newLine();
                bw.newLine();
                bw.newLine();
                bw.write("import org.apache.ibatis.annotations.Param;");
                bw.newLine();
                //是否存在时间类型
                if (tableInfo.getHaveDate() || tableInfo.getHaveDateTime()) {
                    bw.write("import java.time.LocalDateTime;");
                    bw.newLine();
                }
                bw.newLine();
                BuildCommnet.createClassComment(bw, tableInfo.getComment());
                bw.write("public interface " + className + "<T,P> extends BaseMapper {");
                bw.newLine();
                //属性生成
                Map<String, List<FieldInfo>> keyIndexMap = tableInfo.getKeyIndexMap();
                for (Map.Entry<String, List<FieldInfo>> entry : keyIndexMap.entrySet()) {
                    List<FieldInfo> keyFieldInfoList = entry.getValue();
                    StringJoiner methodName = new StringJoiner("And");
                    StringJoiner methodParams = new StringJoiner(", ");
                    for (FieldInfo fieldInfo : keyFieldInfoList) {
                        methodName.add(StringUtils.uperCaseFirstLetter(fieldInfo.getPropertyName()));
                        methodParams.add("@Param(\"" + fieldInfo.getPropertyName() + "\") " + fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName());

                    }
                    BuildCommnet.createFieldCommnet(bw, "根据" + methodName + "查询");
                    bw.write("\tT selectBy" + methodName + "(" + methodParams + ");");
                    bw.newLine();
                    bw.newLine();

                    BuildCommnet.createFieldCommnet(bw, "根据" + methodName + "更新");
                    bw.write("\tInteger updateBy" + methodName + "(@Param(\"bean\") T t, " + methodParams + ");");
                    bw.newLine();
                    bw.newLine();

                    BuildCommnet.createFieldCommnet(bw, "根据" + methodName + "删除");
                    bw.write("\tInteger deleteBy" + methodName + "(" + methodParams + ");");
                    bw.newLine();
                    bw.newLine();
                }
                bw.newLine();
                bw.write("}");
                bw.flush();
            } catch (Exception e) {
                logger.info("创建Mapper失败", e);
            }
        }

    }
}
