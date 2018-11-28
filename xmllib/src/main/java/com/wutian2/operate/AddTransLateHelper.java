package com.wutian2.operate;

import com.wutian.utils.ReplaceSpecialCharUtils;
import com.wutian.xml.file.FileUtils;
import com.wutian.xml.file.task.AddTranslateTask;
import com.wutian.xml.file.task.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AddTransLateHelper implements AddTranslateTask.AddTranslateListener {
    private ExecutorService mFixedThreadPool = Executors.newFixedThreadPool(5);
    private List<Task> mTaskList = new ArrayList<>();

    private Map<String, List<String>> mValuesData = new HashMap<>();
    public void addTranslate(File translateResFile, File resDir) {
        startCheckPathIsRight(translateResFile);
        startAddTranslate(translateResFile, resDir);
        endThreadPool();
    }

    private boolean startCheckPathIsRight(File translateResFile) {
        File[] valueDirArrays = translateResFile.listFiles();
        for (File valueDir : valueDirArrays) {
            if (valueDir.isHidden())
                continue;
            for (File file : valueDir.listFiles()) {
                if (file.isDirectory()) {
                    //has error file
                    System.out.println(file.getAbsolutePath());
                    throw new RuntimeException("Translate File Path Error!");
                }
            }
        }
        return true;
    }

    private void startAddTranslate(File translateFileDir, File originDir) {
        if (originDir == null || !originDir.exists())
            return;

        if (translateFileDir == null || !translateFileDir.exists())
            return;

        for (File translateFile : translateFileDir.listFiles()) {
            if (translateFile.isHidden())
                continue;
            if (translateFile.isDirectory()) {
                File origin = new File(originDir, translateFile.getName());
                if (!origin.exists()) {
                    System.out.println(origin.getName());
                    continue;
                }
                startAddTranslate(translateFile, origin);
            } else {
                readValuesFile(translateFile, originDir);
                AddTranslateTask addTranslateTask = new AddTranslateTask(this, translateFile, originDir);
                mTaskList.add(addTranslateTask);
                mFixedThreadPool.submit(addTranslateTask);
            }
        }
    }

    private void readValuesFile(File translateFile, File originDir) {
        String translateFileName = translateFile.getName();
        if (mValuesData.containsKey(translateFileName))
            return;


        File resDir = originDir.getParentFile();
        if (!resDir.getName().equals("res"))
            throw new RuntimeException("can not find res Dir :" + resDir.getAbsolutePath());

        File valuesDir = new File(resDir, "values");
        if (valuesDir == null || !valuesDir.exists())
            throw new RuntimeException("can not find values Dir :" + valuesDir.getAbsolutePath());

        File valueFile = new File(valuesDir, translateFileName);
        if (valueFile == null || !valueFile.exists())
            throw new RuntimeException(translateFile.getAbsolutePath() + "     " + "can not find values file :" + valueFile.getAbsolutePath());
        List<String> lines = FileUtils.readXmlToList(valueFile);
        mValuesData.put(translateFileName, lines);
    }

    @Override
    public void doAddTranslate(File translateFile, File originDir) {
        File valueXXFile = new File(originDir, translateFile.getName());
        if (!valueXXFile.exists()) {
            try {
                valueXXFile.createNewFile();
                FileUtils.copyFile(translateFile, valueXXFile);
                return;
            } catch (IOException e) {
            }
        }

        List<String> lines = mValuesData.get(translateFile.getName());
        if (lines.isEmpty())
            throw new RuntimeException("can not get values file: " + translateFile.getName());
        Map<String, String> transMap = FileUtils.readStringToMap(translateFile);
        Map<String, String> valueXXMap = FileUtils.readStringToMap(valueXXFile);
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(valueXXFile)));
            String defaultValue;
            for (String line : lines) {
                String[] strs = line.trim().split("\">");
                if (line.contains("<string-array") || line.contains("<integer-array"))
                    strs = line.trim().split("\n");

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

                    //TODO check
                    defaultValue = ReplaceSpecialCharUtils.replaceSpecialChar(defaultValue);
//                    tryWriteErrorKey(line, defaultValue, translateFile);
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



    private void endThreadPool() {
        if (mTaskList.isEmpty())
            return;
        try{
            TimeUnit.MILLISECONDS.sleep(1000);
        } catch (Exception e) {}
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

                        if (i == mTaskList.size() && mFixedThreadPool != null) {
                            mFixedThreadPool.shutdownNow();
                            mFixedThreadPool = null;
                            System.out.println("      FixedThreadPool.shutdownNow()      ");
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
}