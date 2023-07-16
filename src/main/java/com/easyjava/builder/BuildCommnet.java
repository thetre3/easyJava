package com.easyjava.builder;

import com.easyjava.Utils.TimeUtils;
import com.easyjava.bean.Constants;

import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class BuildCommnet {
    public static void createClassComment(BufferedWriter bw, String comment) throws IOException {
        bw.write("/**");
        bw.newLine();
        if (comment.equals("")) {
            bw.write(" * @Description(描述): 未添加表注释");
            bw.newLine();
        } else {
            bw.write(" * @Description(描述):" + comment);
            bw.newLine();
        }
        bw.write(" * @author:" + Constants.AUTHER_COMMENT);
        bw.newLine();
        bw.write(" * @date(日期):" + TimeUtils.format(LocalDateTime.now(), TimeUtils.JUST_DATE_SLASH));
        bw.newLine();
        bw.write(" */");
        bw.newLine();
    }

    public static void createFieldCommnet(BufferedWriter bw, String comment) throws IOException {
        bw.write("\t/**");
        bw.newLine();
        if (comment.equals("")) {
            bw.write("     * @Description(描述): 未添加列注释");
            bw.newLine();
        } else {
            bw.write("     * @Description(描述):" + comment);
            bw.newLine();
        }
        bw.write("\t */");
        bw.newLine();
    }
}
