package com.easyjava.builder;

import com.easyjava.RunApplication;
import com.easyjava.Utils.StringUtils;
import com.easyjava.bean.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class BuildBase {
    private static OutputStream outputStream = null;
    private static InputStream inputStream = null;
    private static BufferedReader br = null;
    private static BufferedWriter bw = null;
    private static final Logger logger = LoggerFactory.getLogger(BuildBase.class);

    public static void execute() {
        creatHeader();
        createBody();
        try {
            outputStream.close();
            inputStream.close();
            br.close();
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createBody() {
        //遍历map
        for (Map.Entry<String, String> info : Constants.TEMPLATE_PACKAGE.entrySet()) {
            URL url = RunApplication.class.getResource("/template/" + info.getKey());
            if (url != null) {
                try {
                    File file = new File(url.toURI());
                    inputStream = Files.newInputStream(file.toPath());
                    br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                    br.readLine();
                    br.readLine();
                    //返回导入类名
                    List<String> importClass = Constants.TEMPLATE_IMPORT_NAME.get(info.getKey());
                    //获取PATH
                    String path = Constants.PATH_BASE + Constants.PATH_JAVA + info.getValue().replace(".", "/") + "/" + info.getKey().substring(0, info.getKey().lastIndexOf(".")) + ".java";
                    createFileBody(path, importClass);
                } catch (URISyntaxException | IOException e) {
                    throw new RuntimeException(e);
                }

            }


        }
    }

    private static void createFileBody(String Path, List<String> importClass) {
        try {
            outputStream = new FileOutputStream(Path, true);
            bw = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
            //动态导入依赖类
            if (importClass != null && !importClass.isEmpty()) {
                for (String info : importClass) {
                    String PACKAGE = Constants.TEMPLATE_PACKAGE.get(info + ".txt");
                    String PackageName = "import " + PACKAGE + "." + info + ";";
                    bw.write(PackageName);
                    bw.newLine();
                }
                bw.newLine();
                bw.newLine();
            }
            String line;
            //写入文件
            while ((line = br.readLine()) != null) {
                bw.write(line);
                bw.newLine();
            }
            bw.flush();
        } catch (IOException e) {
            logger.info("创建template主体失败", e);
        }
    }

    public static void creatHeader() {
        URL url = RunApplication.class.getResource("/template/");
        if (url != null) {
            try {
                //获取配置文件中的package包
                List<String> resourcesPackagenameList = Constants.RESOURCES_PACKAGENAME_LIST;
                File[] files = new File(url.toURI()).listFiles();
                if (files != null) {
                    //遍历template文件
                    for (File templateFile : files) {
                        inputStream = Files.newInputStream(templateFile.toPath());
                        br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                        //判断后缀
                        if (templateFile.isFile() && templateFile.getName().endsWith(".txt")) {
                            String templateName = templateFile.getName();
                            String primaryPackageName = br.readLine();
                            String primaryImportClassNames = br.readLine();
                            List<String> PackageNameList;
                            List<String> ImportClassNamesList;
                            PackageNameList = StringUtils.templateInformationGet(primaryPackageName);
                            ImportClassNamesList = StringUtils.templateInformationGet(primaryImportClassNames);
                            String PackageName = null;
                            if (PackageNameList != null) {
                                PackageName = PackageNameList.get(0);
                            }
                            Constants.TEMPLATE_IMPORT_NAME.put(templateName, ImportClassNamesList);
                            //

                            //配置文件和template文件配置进行比对
                            if (!resourcesPackagenameList.contains(PackageName)) {
                                logger.info("配置template:" + PackageName + "失败");
                                PackageName = null;
                            }

                            if (PackageName != null) {
                                //动态获取文件目录
                                Map<String, String> mapPathAndPackage = Constants.getPackageAndPath(PackageName);
                                Constants.TEMPLATE_PACKAGE.put(templateName, mapPathAndPackage.get("PACKAGE"));
                                templateName = templateName.substring(0, templateName.lastIndexOf("."));
                                //创建文件
                                createFileHeader(templateName, mapPathAndPackage.get("PATH"), mapPathAndPackage.get("PACKAGE"));
                            }
                        }

                    }
                }
            } catch (URISyntaxException | IOException e) {
                logger.info("建创错误", e);
            }
        }
    }

    private static void createFileHeader(String fileName, String PATH, String PACKAGE) {
        File folder = new File(PATH);
        File utFile = new File(folder, fileName + ".java");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        try {
            outputStream = Files.newOutputStream(utFile.toPath());
            bw = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
            bw.write("package " + PACKAGE + ";");
            bw.newLine();
            bw.newLine();
            bw.flush();
        } catch (IOException e) {
            logger.info("创建头部失败", e);
        }
    }

}
