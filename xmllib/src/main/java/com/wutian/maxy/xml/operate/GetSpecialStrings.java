package com.wutian.maxy.xml.operate;

import com.wutian.xml.file.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GetSpecialStrings {
    private static final String SPLIT = "\">";

    private static final Map<String, List<String>> DELETE_IDS = new HashMap<>();

    ///////1111111111111111//////// App
    private static final String res = "/Users/maxy/Android/workspace/SHAREit/App/src/main/res";

    public static void main(String[] args) {
        GetSpecialStrings getSpecialStrings = new GetSpecialStrings();
        getSpecialStrings.start("content_strings.xml");
//        getSpecialStrings.start("music_strings.xml");
//        getSpecialStrings.start("photo_strings.xml");
//        getSpecialStrings.start("search_strings.xml");
//        getSpecialStrings.start("video_strings.xml");
    }

    private void deleteFiles() {
        File resPath = new File(res);
        int i = 0;
        for (File file : resPath.listFiles()) {
            String name = file.getName();
            if (!name.startsWith("value") || name.contains("hdpi") || name.contains("-v21") || name.contains("-land") || name.contains("-sw600dp")
                    || name.contains("xlarge") || name.contains("-v9") || name.contains("gu"))
                continue;

            File stringFIle = new File(file, "socialshare_strings.xml");
            if (stringFIle.exists()) {
                stringFIle.delete();
                i ++;
            }
        }
        System.out.println(i);
    }

    private void start(String fileName) {
        /////////////2222222222222222////////////////
        String filePath = "/Users/maxy/Android/workspace/SHAREit/BizGame/src/main/res/values/" + fileName;
        List<String> originList = FileUtils.readXmlToList(filePath);

        Set<String> keys = new HashSet<>();
        for (String line : originList) {
            String trim = line.trim();
            String[] split = trim.split(SPLIT);
            if (split.length >= 2) {
                keys.add(split[0]);
            } else
                System.out.println("--------" + trim);
        }

        System.out.println("--------- "  + keys);
        int i = 0;
        File resPath = new File(res);
        for (File file : resPath.listFiles()) {
            Set<String> copySet = new HashSet<>(keys);
            String name = file.getName();
            if (!name.startsWith("value") || name.contains("hdpi") || name.contains("-v21") || name.contains("-land") || name.contains("-sw600dp")
                    || name.contains("xlarge") || name.contains("-v9") || name.contains("gu"))
                continue;
            List<String> getList = new ArrayList<>();
            for (File valueFile : file.listFiles()) {
                if (copySet.isEmpty())
                    break;
                startDelete(valueFile, copySet, getList);
            }
            System.out.println(getList);
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();

            if (getList.isEmpty())
                continue;

            if (!copySet.isEmpty()) {
                System.out.println("Error:::::  " + copySet.size() + "        " + copySet.toString());
            }
            writeToFile(name, fileName, getList);

        }
        System.out.println(i);
    }

    private void writeToFile(String dirName, String fileName, List<String> getList) {
        /////////////3333333333333333333/////////
        File deskResFile = new File("/Users/maxy/Desktop/res");
        if (!deskResFile.exists())
            deskResFile.mkdir();
        File valueFile = new File(deskResFile, dirName);
        if (!valueFile.exists())
            valueFile.mkdir();

        File targeFile = new File(valueFile, fileName);
        if (!targeFile.exists()) {
            try {
                targeFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targeFile)));
            writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<resources>");
            writer.flush();
            writer.newLine();
            for (String line : getList) {
                line = "" + line;
                writer.write(line);
                writer.flush();
                writer.newLine();
            }

            writer.write("</resources>");
            writer.flush();
            writer.newLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null)
                    writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected static void startDelete(File deleteFile, Set<String> copySet, List<String> ids) {
        if (deleteFile == null || !deleteFile.exists())
            return;
        List<String> lines = FileUtils.readXmlToList(deleteFile);
        if (lines == null || lines.isEmpty())
            return;
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(deleteFile)));
            for (String line : lines) {
                String[] strs = line.split(SPLIT);
                if (strs.length >= 2) {
                    if (copySet.contains(strs[0].trim())) {
                        copySet.remove(strs[0].trim());
                        ids.add(line);
                        continue;
                    }
                }
                writer.write(line);
                writer.flush();
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null)
                    writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * deleteFile :要删除的文件
     * del :要删除的字符串id:
     */

    protected static void startDelete(String deleteFilePath, String del) {
        startDelete(new File(deleteFilePath), del);
    }

    protected static void startDelete(File deleteFile, String del) {
        if (deleteFile == null || !deleteFile.exists())
            return;
        List<String> lines = FileUtils.readXmlToList(deleteFile);
        if (lines == null || lines.isEmpty())
            return;
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(deleteFile)));
            for (String line : lines) {
                String[] strs = line.split(SPLIT);
                if (strs.length >= 2) {
                    if (strs[0].trim().equals(del))
                        continue;
                }
                writer.write(line);
                writer.flush();
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null)
                    writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
