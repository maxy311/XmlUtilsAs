package com.wutian.xml.file;


import com.wutian.xml.file.task.CompareTask;
import com.wutian.xml.file.task.Task;
import com.wutian.xml.file.task.TranslateTask;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TranslateHelper {
    private ExecutorService mFixedThreadPool = Executors.newFixedThreadPool(5);
    private List<Task> mTaskList = new ArrayList<>();

    /*
     * 比较values 目录。
	 *
	 */
    public void getTranslateStrings(String originPath, String targetPath, String savePath, boolean isCompareAr) {
        getTranslate(originPath, targetPath, savePath, isCompareAr);
        endThreadPool();
    }

    private void getTranslate(String originPath, String targetPath, String savePath, boolean isCompareAr) {
        File originFile = new File(originPath);
        if (!originFile.exists() || !originFile.isDirectory())
            return;

        File targetFile = new File(targetPath);
        if (!targetFile.exists() || !targetFile.isDirectory())
            return;

        File saveFile = new File(savePath);
        if (!saveFile.exists())
            saveFile.mkdirs();

        for (File origin : originFile.listFiles()) {
            String fileName = origin.getName();
            if (!fileName.contains("string") || fileName.contains("filter") || fileName.contains("dimens")
                    || fileName.contains("account") || fileName.contains("product_setting"))
                continue;

            if (origin.isDirectory()) {
                getTranslate(originFile + File.separator + fileName, targetPath + File.separator + fileName, savePath,
                        isCompareAr);
            } else {
                File target = new File(targetFile, fileName);
                CompareTask runnable = new CompareTask(origin, target, saveFile, isCompareAr);
                mTaskList.add(runnable);
                mFixedThreadPool.submit(runnable);
            }
        }
    }

    /*
    *
    * compareFile method to get Translate.
    * originFile
    *
    * targetFile
    *
    * isCompareAr
    *
    * */
    public static void compareFile(File originFile, File targetFile, File saveFile, boolean isCompareAr) {
        if (saveFile.isDirectory()) {
            if (!"values".equals(saveFile.getName())) {
                saveFile = new File(saveFile, "values");
                if (!saveFile.exists())
                    saveFile.mkdirs();
            }

            try {
                saveFile = new File(saveFile, originFile.getName());
                if (!saveFile.exists())
                    saveFile.createNewFile();
            } catch (IOException e) {
            }
        }

        if (!targetFile.exists()) {
            FileUtils.copyFile(originFile, saveFile);
            tryToCopyZhFile(originFile, saveFile);
            return;
        }

        Map<String, String> originMap = FileUtils.readStringToLinkedHasMap(originFile);
        Map<String, String> targetMap = FileUtils.readStringToLinkedHasMap(targetFile);
        List<String> selectKeys = new ArrayList<String>();
        Set<String> originKeys = originMap.keySet();
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(saveFile)));
            writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            writer.newLine();
            writer.write("<resources>");
            writer.flush();
            writer.newLine();
            for (String key : originKeys) {
                if (key.contains("translate") || key.contains("translatable") || key.contains("<plurals name="))
                    continue;
                if (isCompareAr) {
                    // values 与 values-ar 比较
                    if (targetMap.containsKey(key))
                        continue;
                } else {
                    // values 与 values 比较
                    if (targetMap.containsKey(key) && targetMap.get(key).equals(originMap.get(key)))
                        continue;
                }
                writer.write("    " + originMap.get(key));
                writer.flush();
                writer.newLine();
                selectKeys.add(key);
            }
            writer.write("</resources>");
            writer.flush();
        } catch (IOException e) {
        } finally {
            try {
                if (writer != null)
                    writer.close();
            } catch (IOException e) {
            }
        }

        if (!selectKeys.isEmpty())
            tryToGetZhString(originFile, saveFile, selectKeys);
        else
            saveFile.delete();
    }

    private static void tryToGetZhString(File originFile, File saveFile, List<String> selectKeys) {
        try {
            //to get res file
            File resFile = originFile.getParentFile().getParentFile();

            File originZhDir = new File(resFile, "values-zh-rCN");
            if (originZhDir == null || !originFile.exists())
                return;

            // to get origin zh file
            File originZhFile = new File(originZhDir, originFile.getName());
            if (originZhFile == null || !originZhFile.exists())
                return;

            File saveZhDir = new File(saveFile.isDirectory() ? saveFile : saveFile.getParentFile().getParentFile(), "values-zh-rCN");
            if (!saveZhDir.exists())
                saveZhDir.mkdir();

            File saveZhFile = new File(saveZhDir, originFile.getName());
            if (!saveFile.exists())
                saveZhFile.createNewFile();

            Map<String, String> map = FileUtils.readStringToMap(originZhFile);
            BufferedWriter writer = null;

            try {
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(saveZhFile)));
                writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
                writer.newLine();
                writer.write("<resources>");
                writer.flush();
                writer.newLine();
                for (String str : selectKeys) {
                    if (map.containsKey(str)) {
                        writer.write("    " + map.get(str));
                        writer.flush();
                        writer.newLine();
                    } else
                        System.out.println("values--zh        " + originFile.getName() + "     didn't contains :      " + str);
                }
                writer.write("</resources>");
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (writer != null)
                        writer.close();
                } catch (IOException e) {
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void tryToCopyZhFile(File originFile, File saveFile) {
        try {
            //to get res file
            File resFile = originFile.getParentFile().getParentFile();

            File originZhDir = new File(resFile, "values-zh-rCN");
            if (originZhDir == null || !originFile.exists())
                return;

            // to get origin zh file
            File originZhFile = new File(originZhDir, originFile.getName());
            if (originZhFile == null || !originZhFile.exists())
                return;

            File saveZhDir = new File(saveFile.isDirectory() ? saveFile : saveFile.getParentFile().getParentFile(), "values-zh-rCN");
            if (!saveZhDir.exists())
                saveZhDir.mkdir();

            File saveZhFile = new File(saveZhDir, originFile.getName());
            if (!saveFile.exists())
                saveZhFile.createNewFile();

            FileUtils.copyFile(originZhFile, saveZhFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void endThreadPool() {
        if (mTaskList.isEmpty())
            return;
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    int i = 0;
                    try {
                        for (Task task : mTaskList) {
                            if (task.isDone()) {
                                i++;
                                continue;
                            } else
                                break;
                        }

                        if (i == mTaskList.size()) {
                            mFixedThreadPool.shutdownNow();
                            mFixedThreadPool = null;
                            break;
                        }
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void addTranslateToValues(String resPath, String translatePath) {
        addTranslate(resPath, translatePath);
        endThreadPool();
    }

    private void addTranslate(String resPath, String translatePath) {
        File originDir = new File(resPath);
        if (!originDir.exists())
            return;

        File translateDir = new File(translatePath);
        if (!translateDir.exists())
            return;

        for (File translateFile : translateDir.listFiles()) {
            if (translateFile.isDirectory()) {
                File origin = new File(originDir, translateFile.getName());
                if (!origin.exists()) {
                    System.out.println(origin.getName());
                    continue;
                }
                addTranslate(origin.getAbsolutePath(), translateFile.getAbsolutePath());
            } else {
                try {
                    File origin = new File(originDir, translateFile.getName());
                    if (!origin.exists()) {
                        origin.createNewFile();
                        FileUtils.copyFile(translateFile, origin);
                    } else {
                        File resDir = originDir.getParentFile();
                        if (!resDir.getName().equals("res"))
                            continue;

                        File valuesDir = new File(resDir, "values");
                        if (valuesDir == null || !valuesDir.exists())
                            return;

                        File valueFile = new File(valuesDir, origin.getName());
                        if (valueFile == null || !valueFile.exists())
                            continue;

                        TranslateTask translateTask = new TranslateTask(valueFile, origin, translateFile);
                        mTaskList.add(translateTask);
                        mFixedThreadPool.submit(translateTask);
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public static void addTransValuesToRes(File valuesFile, File valueXXFile, File translateFile) {
        List<String> lines = FileUtils.readXmlToList(valuesFile);
        Map<String, String> transMap = FileUtils.readStringToMap(translateFile);
        Map<String, String> valueXXMap = FileUtils.readStringToMap(valueXXFile);
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(valueXXFile)));
            String defaultValue;
            for (String line : lines) {
                String[] strs = line.trim().split("\">");

                if (strs.length >= 2) {
                    String key = strs[0];
                    defaultValue = null;
                    if (transMap.containsKey(key)) {
                        defaultValue = transMap.get(key);
                    } else if (valueXXMap.containsKey(key)) {
                        defaultValue = valueXXMap.get(key);
                    }

                    if (null == defaultValue && valueXXMap.size() != 0) {
                        if (key.contains("translate") || key.contains("translatable")
                                || key.contains("<plurals name="))
                            continue;

                        System.out.println(valueXXFile.getParentFile().getName() + "    " + valueXXFile.getName() + "   don't contains " + key);
                        continue;
                    }

                    writer.write("    " + defaultValue);
                    writer.flush();
                    writer.newLine();
                } else {
                    writer.write(line);
                    writer.flush();
                    writer.newLine();
                }
            }

        } catch (Exception e) {
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
