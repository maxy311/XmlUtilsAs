package com.test;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

public class TestJava {
    private static String SHAREIT_resPath = "/Users/maxy/Android/workspace/SHAREit/App/src/main/res";

    public static void main(String[] args) {
//        int anInt = Integer.parseInt("05");
//        System.out.println(anInt);

        String valuePath = SHAREIT_resPath + "/" + "values";

        File valueDir = new File(valuePath);
        File[] files = valueDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                String name = pathname.getName();
                if (!name.contains("string"))
                    return false;
                if (name.contains("filter"))
                    return false;
                if (name.contains("dimens"))
                    return false;
                if (name.contains("account"))
                    return false;
                if (name.contains("product_setting"))
                    return false;
                if (name.contains("country_code_string"))
                    return false;
                return true;
            }
        });
        for (File file : files) {
            System.out.println(file.getName());
        }

    }
}
