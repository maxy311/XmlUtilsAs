package com.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StringUpperTest {
    private static String SHAREIT_resPath = "/Users/maxy/Android/workspace/SHAREit/App/src/main/res";
    private static String DESKTOP_PATH = "/Users/maxy/Desktop/res";

    private static List<String> list = new ArrayList<>();
    static {
//        list.add("<string name=\"common_operate_downloading_caps\">@string/common_operate_downloading</string>");
//        list.add("<string name=\"common_operate_installing_caps\">@string/common_operate_installing</string>");
//        list.add("<string name=\"common_operate_play_caps\">@string/common_operate_play</string>");
//        list.add("<string name=\"common_operate_import_caps\">@string/common_operate_import</string>");
//        list.add("<string name=\"common_operate_save_caps\">@string/common_operate_save</string>");
//        list.add("<string name=\"common_operate_more_caps\">@string/common_operate_more</string>");
//        list.add("<string name=\"common_operate_send_caps\">@string/common_operate_send</string>");
//        list.add("<string name=\"common_operate_share_caps\">@string/common_operate_share</string>");
//        list.add("<string name=\"common_operate_copy_caps\">@string/common_operate_copy</string>");
//        list.add("<string name=\"common_operate_done_caps\">@string/common_operate_done</string>");
//        list.add("<string name=\"common_operate_saved_caps\">@string/common_operate_saved</string>");
//        list.add("<string name=\"common_operate_clean_caps\">@string/common_operate_clean</string>");
//
//        list.add("<string name=\"common_string_analyze_caps\">@string/common_string_analyze</string>");
        list.add("<string name=\"help_feedback_caps\">@string/help_feedback</string>");
//        list.add("<string name=\"_caps\">@string/</string>");
//        list.add("<string name=\"_caps\">@string/</string>");
//        list.add("<string name=\"_caps\">@string/</string>");
//        list.add("<string name=\"_caps\">@string/</string>");
    }
    public static void main(String[] args) {

        File file = new File(SHAREIT_resPath);

        File[] resFiles = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                String name = pathname.getName();
                if (!name.contains("values"))
                    return false;
                return true;
            }
        });

        File deskTopDir = new File(DESKTOP_PATH);
        if (!deskTopDir.exists())
            deskTopDir.mkdir();

        for (File resFile : resFiles) {
            File commonFile = new File(resFile, "help_strings.xml");
            if (!commonFile.exists())
                continue;

            File deskRes = new File(deskTopDir, resFile.getName());
            if (!deskRes.exists())
                deskRes.mkdir();

            File deskFile = new File(deskRes, "help_strings.xml");
            try {
                if (!deskFile.exists())
                    deskFile.createNewFile();
            } catch (Exception e) {
            }

            BufferedWriter bw = null;
            try {
                bw = new BufferedWriter(new FileWriter(deskFile));
                writerFileHeader(bw);
                for (String s : list) {
                    bw.write(s);
                    bw.flush();
                    bw.newLine();
                }
                writerFileEnd(bw);
            }catch (Exception e){
            } finally {
                try {
                    if (bw != null)
                        bw.close();
                } catch (Exception e1){}
            }

        }
    }

    private static final void writerFileEnd(BufferedWriter writer) throws IOException {
        writer.write("</resources>");
        writer.flush();
    }

    private static final void writerFileHeader(BufferedWriter writer) throws IOException {
        writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        writer.newLine();
        writer.write("<resources>");
        writer.flush();
        writer.newLine();
    }
}
