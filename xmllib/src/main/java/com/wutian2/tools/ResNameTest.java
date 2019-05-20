package com.wutian2.tools;

import com.wutian.xml.file.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ResNameTest {
    private static String DESKTOP_PATH = "/Users/maxy/Desktop";

    public static void main(String[] args) {
        String path = DESKTOP_PATH + File.separator + "translate";
        System.out.println(path);

        File translateFile = new File(path);
        if (!translateFile.exists()) {
            System.out.println(translateFile.getAbsolutePath() + "  : file not exit ");
            return;
        }

        splitFiles(translateFile);
    }

    private static void splitFiles(File originFile) {
        for (File valuesDir : originFile.listFiles()) {
            if (valuesDir.isHidden())
                continue;

            copyToResFile(valuesDir);
        }
    }

    private static void copyToResFile(File originValuesDir) {
        String targetResPath = DESKTOP_PATH + File.separator + "res";
        File targetResDir = checkToCreateDir(targetResPath);

        String targetValueDirPath = targetResDir.getAbsolutePath() + File.separator + originValuesDir.getName();
        File targetValueDir = checkToCreateDir(targetValueDirPath);
        for (File originValuesSubFile : originValuesDir.listFiles()) {
            if (originValuesSubFile.isHidden() || originValuesSubFile.isFile())
                continue;
            System.out.println(originValuesSubFile.getAbsolutePath());
            for (File xmlFile : originValuesSubFile.listFiles()) {
                String xmlFileName = xmlFile.getName();
                if (!xmlFileName.endsWith(".xml")) {
                    continue;
                }

                if (xmlFileName.equals("service_translate_strings.xml")) {
                    startToCopyFile(targetValueDir, xmlFile);
                    continue;
                }

                if (xmlFile.isDirectory()) {
                    throw new RuntimeException("get origin xml file error : " + xmlFile.getAbsolutePath());
                }

                startToCopyFile(targetValueDir, xmlFile);
            }
        }
    }

    private static void startToCopyFile(File targetDir, File originFile) {
        File targetFile = new File(targetDir, originFile.getName());
        FileUtils.copyFile(originFile, targetFile);

        System.out.println(targetFile.getAbsolutePath());
    }

    private static File checkToCreateDir(String resPath) {
        File file = new File(resPath);
        if (!file.exists()) {
            try {
                file.mkdir();
            } catch (Exception e) {
                throw new RuntimeException("create new file error!!!!!   " + file.getAbsolutePath());
            }
        }

        return file;
    }

}
