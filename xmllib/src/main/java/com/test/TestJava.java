package com.test;

import com.wutian.xml.file.FileUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TestJava {
    private static String SHAREIT_resPath = "/Users/maxy/Android/workspace/SHAREit/App/src/main/res";

    private static final String DESKTOP = "/Users/maxy/Desktop";
    public static void main(String[] args) {
        String path = DESKTOP + "/adb2.txt" ;
        File file = new File(path);
        List<String> list = new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line = null;
            int count =  0;
            StringBuffer sb = new StringBuffer();

            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
                count ++;
                sb.append("\"" + line + "\", ");
                list.add(line);
            }
            System.out.println(sb.toString());
            System.out.println(count);
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        System.out.println(list.size());

        String line1 = list.get(0);
        List<String> list1 = new ArrayList<>();
        for (String s : line1.split("\\) ")) {
            list1.add(s);
        }

        String line2 = list.get(2);
        System.out.println(list1.size());
        System.out.println("------------------------");
        for (String s : line2.split("\\) ")) {
            if (!list1.contains(s))
                System.out.println(s);
        }
//        BufferedWriter bf;
//        try {
//            path = DESKTOP + "/adb3.txt" ;
//            File file1 = new File(path);
//            if (!file1.exists())
//                file1.createNewFile();
//            bf = new BufferedWriter(new FileWriter(path));
//
//            for (String s : list) {
//                bf.write(s);
//                bf.newLine();
//                bf.flush();
//                bf.newLine();
//                bf.newLine();
//                bf.newLine();
//                bf.newLine();
//
//                String[] split = s.split("\\) ");
//                for (String s1 : split) {
//                    bf.write(s1);
//                    bf.newLine();
//                    bf.flush();
//                }
//            }
//
//        } catch (Exception e) {
//
//        }
    }


}
