package com.example;

import com.wutian.maxy.xml.operate.DeleteStringUtils;
import com.wutian.xml.file.FileUtils;
import com.wutian.xml.file.TranslateHelper;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class XMLOperate {
    private static String resPath = "/Users/maxy/Android/workspace/App/res";

    public static void main(String[] args) {
        DeleteStringUtils.startDelete();
//        getTranslateMethod();
//        addTranslateMethod();
    }

    private static void addTranslateMethod() {
        String transPath = "/Users/maxy/Downloads/LXQ-0062_Clean_0704";
        TranslateHelper translateHelper = new TranslateHelper();
        translateHelper.addTranslateToValues(resPath, transPath);
    }

    private static void getTranslateMethod() {
        String valuePath = resPath + "/" + "values";
        String valuePath_Ar = resPath + "/" + "values-ar";
        String savePath = "/Users/maxy/Desktop/res2";
        TranslateHelper translateHelper = new TranslateHelper();
        translateHelper.getTranslateStrings(valuePath, valuePath_Ar, savePath, true);
    }

    private static void testCopyFile() {
        String path = "/Users/maxy/Android/workspace/App/res/values/share_strings.xml";
        String targetPath = "/Users/maxy/Desktop/share_string.xml";
        FileUtils.copyFile(path, targetPath);
    }

    private static void readStringToMap() {
        String path = "/Users/maxy/Android/workspace/App/res/values/share_strings.xml";
        Map<String, String> stringStringMap = FileUtils.readStringToLinkedHasMap(new File(path));
        Set<String> strings = stringStringMap.keySet();
        for (String string : strings) {
            System.out.println(string + "   ");
            System.out.println(stringStringMap.get(string));
            System.out.println("-------------------");
        }
    }

    private static void readStringToList() {
        String path = "/Users/maxy/Android/workspace/App/res/values/share_strings.xml";
        List<String> stringList = FileUtils.readXmlToList(path);
        for (String str : stringList) {
            System.out.println(str);
        }
    }
}
