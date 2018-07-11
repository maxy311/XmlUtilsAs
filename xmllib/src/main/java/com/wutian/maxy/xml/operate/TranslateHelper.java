package com.wutian.maxy.xml.operate;

import com.wutian.xml.file.FileUtils;

import java.io.File;

public class TranslateHelper {
//    private static String resPath = "/Users/maxy/Android/workspace/App/res";Users/maxy/Android/workspace/SHAREit/App
    private static String SHAREIT_resPath = "/Users/maxy/Android/workspace/SHAREit/App/src/main/res";
    private static String SHAREIT_DAILY_resPath = "/Users/maxy/Android/workspace/SHAREit/App/src/daily/res";

    public static void main(String[] args) {
        getTranslateMethod();
//        addTranslateMethod();
//        getTranslateRes();
//        addDailyTranslateMethod();
    }

    private static void addTranslateMethod() {
        String transPath = "/Users/maxy/Downloads/res";
        com.wutian.xml.file.TranslateHelper translateHelper = new com.wutian.xml.file.TranslateHelper();
        translateHelper.addTranslateToValues(SHAREIT_resPath, transPath);
    }

    private static void getTranslateMethod() {
        String valuePath = SHAREIT_resPath + "/" + "values";
        String valuePath_Ar = SHAREIT_resPath + "/" + "values-ar";
        String savePath = "/Users/maxy/Desktop/res3";
        com.wutian.xml.file.TranslateHelper translateHelper = new com.wutian.xml.file.TranslateHelper();
        translateHelper.getTranslateStrings(valuePath, valuePath_Ar, savePath, true);
    }


    private static void addDailyTranslateMethod() {
        String transPath = "/Users/maxy/Downloads/res2";
        com.wutian.xml.file.TranslateHelper translateHelper = new com.wutian.xml.file.TranslateHelper();
        translateHelper.addTranslateToValues(SHAREIT_DAILY_resPath, transPath);
    }

    private static void getTranslateRes() {
        try {
            String resPath = "/Users/maxy/Downloads/res2";
            File file = new File(resPath);
            if (!file.exists())
                file.mkdir();

            String trasnlatePath = "/Users/maxy/Downloads/LXQ_0085_Clean_37L_0329";
            File transFile1 = new File(trasnlatePath);
            for (File file1 : transFile1.listFiles()) {
//                if (file1.getName().startsWith(.))
                    if (file1.isHidden())
                        continue;
                File resDir = new File(file, file1.getName());
                if (!resDir.exists())
                    resDir.mkdir();

                String path = file1.getAbsolutePath() + "/res3/SHAREit_Daily/values";
                File shareitFile = new File(path);
                for (File file2 : shareitFile.listFiles()) {
                    if (file2.isHidden())
                        continue;
                    File resFile = new File(resDir, file2.getName());
                    if (!resFile.exists())
                        resFile.createNewFile();

                    FileUtils.copyFile(file2, resFile);
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }
}
