package com.wutian.maxy.xml.operate;

public class TranslateHelper {
    private static String resPath = "/Users/maxy/Android/workspace/App/res";

    public static void main(String[] args) {
        getTranslateMethod();
//        addTranslateMethod();
    }

    private static void addTranslateMethod() {
        String transPath = "/Users/maxy/Downloads/LXQ-0062_Clean_0704";
        com.wutian.xml.file.TranslateHelper translateHelper = new com.wutian.xml.file.TranslateHelper();
        translateHelper.addTranslateToValues(resPath, transPath);
    }

    private static void getTranslateMethod() {
        String valuePath = resPath + "/" + "values";
        String valuePath_Ar = resPath + "/" + "values-ar";
        String savePath = "/Users/maxy/Desktop/res2";
        com.wutian.xml.file.TranslateHelper translateHelper = new com.wutian.xml.file.TranslateHelper();
        translateHelper.getTranslateStrings(valuePath, valuePath_Ar, savePath, true);
    }

}
