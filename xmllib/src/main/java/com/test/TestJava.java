package com.test;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TestJava {
    private static String DESKTOP_PATH = "/Users/maxy/Desktop";
    public static void main(String[] args) {
        File desktopFile = new File(DESKTOP_PATH);


        File file = new File(desktopFile, "res");
        if (!file.exists()) {
            System.out.println("File not exists");
            return;
        }

        for (File listFile : file.listFiles()) {
            if (listFile.isHidden())
                continue;

            File settingFile = new File(listFile, "setting_strings.xml");
            if (!settingFile.exists())
                continue;

            File renameFile = new File(settingFile.getParentFile(), "theme_strings.xml");
            settingFile.renameTo(renameFile);
        }
    }
}
