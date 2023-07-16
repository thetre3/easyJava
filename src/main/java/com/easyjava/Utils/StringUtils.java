package com.easyjava.Utils;

import java.util.Arrays;
import java.util.List;

public class StringUtils {
    public static String uperCaseFirstLetter(String field) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(field)) {
            return field;
        }
        return field.substring(0, 1).toUpperCase() + field.substring(1);
    }

    public static String lowerCaseFirstLetter(String field) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(field)) {
            return field;
        }
        return field.substring(0, 1).toLowerCase() + field.substring(1);
    }

    public static List<String> templateInformationGet(String primaryString) {
        if (primaryString.isEmpty()){
            return null;
        }
        int startIndex = primaryString.indexOf("@") + 1;
        int endIndex = primaryString.indexOf("@", 2);
        primaryString = primaryString.substring(startIndex, endIndex);
        List<String> transferredString  = Arrays.asList(primaryString.split(","));
        return transferredString;
    }
}
