package com.wutian.maxy.xml.operate;

import java.io.File;

public class DeleteDuplicateFile {
    private static final String res = "/Users/maxy/Android/workspace/SHAREit/App/src/main/res";

    public static void main(String[] args) {

        File resFile = new File(res);
        DeleteDuplicateFile deleteDuplicateFile = new DeleteDuplicateFile();
        //BizOnline:Online  ---
        deleteDuplicateFile.startCheck(resFile, "/Users/maxy/Android/workspace/SHAREit/BizOnline/Online/src/main/res");
//        deleteDuplicateFile.startCheck(resFile, "/Users/maxy/Android/workspace/SHAREit/BaseCore/src/main/res");
    }

    private void startCheck(File resFile, String path) {
        File deleteFile = new File(path);
        startDelete(resFile, deleteFile, "layout");
        startDelete(resFile, deleteFile, "color");
        startDelete(resFile, deleteFile, "drawable");
        startDelete(resFile, deleteFile, "drawable-hdpi");
        startDelete(resFile, deleteFile, "drawable-xhdpi");
        startDelete(resFile, deleteFile, "drawable-ldrtl-xhdpi");
        startDelete(resFile, deleteFile, "raw");
        startDelete(resFile, deleteFile, "anim");
    }

    private void startDelete(File resFile, File deleteDir, String resDirName) {
        File resDir = new File(resFile, resDirName);
        if (!resDir.exists())
            return;
        File deleteResDir = new File(deleteDir, resDirName);
        if (!deleteResDir.exists())
            return;
        for (File file : deleteResDir.listFiles()) {
            String name = file.getName();
            File valueFile = new File(resDir, name);
            if (valueFile.exists())
                valueFile.delete();
        }
    }
}
