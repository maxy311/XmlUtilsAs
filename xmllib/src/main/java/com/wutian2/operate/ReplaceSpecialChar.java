package com.wutian2.operate;

import com.wutian.utils.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ReplaceSpecialChar {
    private static String SHAREIT_resPath = "/Users/maxy/Android/workspace/SHAREit/App/src/main/res";

    private ExecutorService mFixedThreadPool;
    private int mStartCounter = 0;
    private AtomicInteger mEndCounter;
    private Map<String, List<String>> replaceMap;

    private static long startTime = 0;
    public static void main(String[] args) {
        ReplaceSpecialChar replaceSpecialChar = new ReplaceSpecialChar();
        startTime = System.currentTimeMillis();
        replaceSpecialChar.dealSpecialChar();
        replaceSpecialChar.tryEndThreadPool();
    }

    private ReplaceSpecialChar() {
        mFixedThreadPool = Executors.newFixedThreadPool(8);
        mEndCounter = new AtomicInteger(0);
        replaceMap = new HashMap<>();
    }

    private void dealSpecialChar(){
        File resDir = new File(SHAREIT_resPath);
        File[] valueDirs = resDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                String name = pathname.getName();
                if (!name.contains("values"))
                    return false;
                return true;
            }
        });

        for (File valueDir : valueDirs) {
            File[] valueFiles = valueDir.listFiles(new FileFilter() {
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

            for (File valueFile : valueFiles) {
                if (valueFile.getName().contains(".temp")) {
                    valueFile.delete();
                    continue;
                }
                final File finalFile = valueFile;
                mStartCounter++;
                System.out.println("start --------- " + mStartCounter);
                mFixedThreadPool.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            replaceSpecialChar(finalFile);
                        } catch (Exception e) {
                            System.out.println(e.toString());
                        } finally {
                            mEndCounter.incrementAndGet();
                            System.out.println("end ------------------------ " + mEndCounter);
                        }

                    }
                });
            }
        }
    }

    private void replaceSpecialChar(File originFile) throws Exception {
        File targetFile = new File(originFile.getParentFile(), originFile.getName() + ".temp");
        try {
            if (!targetFile.exists())
                targetFile.createNewFile();
        } catch (Exception e) {
        }

        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(originFile)));
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFile)));

            String line;
            while (true) {
                line = reader.readLine();
                if (line == null)
                    break;

                line = checkAndReplaceSpecialChar(line, "\u2028", " ");
                line = checkAndReplaceSpecialChar(line, "\u2029", " ");
                line = checkAndReplaceSpecialChar(line, "\uFEFF", " ");
                line = checkAndReplaceSpecialChar(line, "\u00A0", " ");
                line = checkAndReplaceSpecialChar2(line); // '-------> \'

                writer.write(line);
                writer.flush();
                writer.newLine();
            }
        } finally {
            Utils.close(reader);

            Utils.close(writer);
        }

        originFile.delete();
        targetFile.renameTo(originFile);
    }


    private String checkAndReplaceSpecialChar(String line, String replaceTarget, String replaceStr) {
        boolean hasChanged = false;
        while (line.indexOf(replaceTarget) != -1) {
            hasChanged = true;
            line = line.replace(replaceTarget, replaceStr);
        }
        if (hasChanged) {
            List<String> replaceList = replaceMap.get(replaceTarget);
            if (replaceList == null)
                replaceList = new ArrayList<>();
            replaceList.add(line);
        }
        return line;
    }

    // '  ------------------>   \'
    private String checkAndReplaceSpecialChar2(String line) {
        boolean hasChanged = false;

        if (line.indexOf("'") != -1) {
            char[] chars = line.toCharArray();
            char lastChar = '1';
            StringBuffer sb = new StringBuffer();
            for (char aChar : chars) {
                if ('\'' == aChar) {
                    if ('\\' != lastChar) {
                        sb.append("\\");
                        hasChanged = true;
                    }
                }
                sb.append(aChar);
                lastChar = aChar;
            }
            if (hasChanged) {
                line = sb.toString();
                sb.setLength(0);
            }
        }

        if (hasChanged) {
            List<String> replaceList = replaceMap.get("'");
            if (replaceList == null)
                replaceList = new ArrayList<>();
            replaceList.add(line);
        }
        return line;
    }


    private void tryEndThreadPool() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {
                        int endIndex = mEndCounter.get();
                        if (mStartCounter == 0 || endIndex == 0)
                            continue;
                        System.out.println("mStartCounter = " + mStartCounter + "        mEndCounter = " + endIndex);
                        if (mStartCounter == endIndex && mFixedThreadPool != null) {
                            mFixedThreadPool.shutdownNow();
                            mFixedThreadPool = null;
                            System.out.println("      FixedThreadPool.shutdownNow()      " + (System.currentTimeMillis() - startTime));
                            break;
                        }
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
