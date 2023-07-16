package com.easyjava.Utils;

import com.easyjava.bean.Constants;
import com.sun.javafx.collections.MappingChange;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PropertiesUtils {
    private static Properties props = new Properties();
    private static Map<String, String> PROPER_Map = new ConcurrentHashMap<>();


    static {
        InputStream is = null;
        try {
            is = PropertiesUtils.class.getClassLoader().getResourceAsStream("application.properties");
            props.load(new InputStreamReader(is, StandardCharsets.UTF_8));
            Iterator<Object> iterator = props.keySet().iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                PROPER_Map.put(key, props.getProperty(key));
            }
            for (String mapKey : PROPER_Map.keySet()) {
                if (mapKey.matches(".*" + "package." + ".*")) {
                    Constants.RESOURCES_PACKAGENAME_LIST.add(PROPER_Map.get(mapKey));
                }
            }
        } catch (Exception e) {
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static ArrayList<String> getStringList(String key) {
        ArrayList<String> getlist = new ArrayList<>();
        for (String mapKey : PROPER_Map.keySet()) {
            if (mapKey.matches(".*" + key + ".*")) {
                getlist.add(PROPER_Map.get(mapKey));
            }
        }
        return getlist;
    }


    public static String getString(String key) {
        return PROPER_Map.get(key);
    }

}
