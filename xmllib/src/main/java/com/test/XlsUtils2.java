package com.test;

import com.example.tools.XlsToTxtUtils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class XlsUtils2 {
    private static String DESKTOP_PATH = "/Users/maxy/Desktop";

    public static void main(String[] args) {
        File deskDir = new File(DESKTOP_PATH);

        File xmlFile = new File(deskDir, "All.xls");
        Sheet sheet = loadExcel(xmlFile.getAbsolutePath());
        if (sheet == null) {
            System.out.println("get sheet is empty : " + xmlFile.getAbsolutePath());
            return;
        }

        List<String>[] allRow = getAllRow(sheet);
//        System.out.println("----------------");
        List<String> nameList = allRow[0];
        List<String> localList = allRow[1];
        List<String> moreList = allRow[3];
//        System.out.println(nameList.toString());
//        System.out.println(localList.toString());
//        System.out.println(moreList.toString());

        File resFile = new File(deskDir, "res");
        if (!resFile.exists())
            resFile.mkdir();
        for (int i = 0; i < nameList.size(); i++) {
            String name = nameList.get(i);
            name = name.replace("-", "-r");
            File valueDir = new File(resFile, "values-"+name);
            if (!valueDir.exists())
                valueDir.mkdir();
            System.out.println(valueDir.getAbsolutePath());
            outPutFile(valueDir, "feed_strings.xml", "home_media_share_local", localList.get(i));
            outPutFile(valueDir, "share_strings.xml", "share_session_app_detect_action_more", moreList.get(i));
        }
    }

    private static void outPutFile(File valueDir, String fileName, String key, String value) {
        File outPutFile = new File(valueDir, fileName);
        try {
            if (outPutFile.exists())
                outPutFile.createNewFile();
        } catch (Exception e){}

        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(outPutFile));
            writerFileHeader(bw);
            String str = "    <string name=\"" + key + "\">" + value + "</string>";
            System.out.println(str);
            bw.write(str);
            bw.flush();
            bw.newLine();

            writerFileEnd(bw);
        } catch (Exception e) {
            System.out.println(outPutFile.getAbsolutePath() + "   " + e.toString());
        } finally {
            try {
                if (bw != null)
                    bw.close();
            } catch (Exception e1){}
        }
    }

    private static void outPutFile(File outFile, List<String>[] allRow) {
        List<String> data = null;
        for (int i = 0; i < allRow.length; i++) {
            System.out.println("--------------" + i + "-------------------");
            List<String> rowList = allRow[i];
            if (rowList == null || rowList.isEmpty())
                continue;
            for (int i1 = 0; i1 < rowList.size(); i1++) {
                String str = rowList.get(i1);
                if (str.contains("<string name=\"share_trans_trun_on_blue\">")) {
                    data = rowList;
                    break;
                }
                System.out.println("===" + i1 + "==  " + rowList.get(i1));
            }
            if (data != null)
                break;
        }

        try {
            if (!outFile.exists())
                outFile.createNewFile();
        } catch (Exception e){}

        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(outFile));
            writerFileHeader(bw);

            String str = data.get(data.size() - 1);
            if (str.contains("share_trans_trun_on_blue")) {
                str = str.replace("share_trans_trun_on_blue", "share_discover_hint_enable_bt");
                bw.write(str);
                bw.flush();
                bw.newLine();
            } else {
                System.out.println(str);
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

    //读取excel文件，创建表格实例
    private static Sheet loadExcel(String filePath) {
        FileInputStream inStream = null;
        try {
            inStream = new FileInputStream(new File(filePath));
            Workbook workBook = WorkbookFactory.create(inStream);

            return workBook.getSheetAt(0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static List<String>[] getAllRow(Sheet sheet) {
        int rowNum = sheet.getLastRowNum() + 1;
        List<String>[] result = new LinkedList[rowNum];
        for (int i = 0; i < rowNum; i++) {
            Row row = sheet.getRow(i);
            if (row == null)
                continue;
            //每有新的一行，创建一个新的LinkedList对象
            result[i] = new LinkedList();
            for (int j = 0; j < row.getLastCellNum(); j++) {
                Cell cell = row.getCell(j);
                //获取单元格的值
                String str = getCellValue(cell);
                //将得到的值放入链表中
                result[i].add(str);
            }
        }
        return result;
    }

    //获取单元格的值
    private static String getCellValue(Cell cell) {
        String cellValue = "";
        DataFormatter formatter = new DataFormatter();
        if (cell != null) {
            //判断单元格数据的类型，不同类型调用不同的方法
            switch (cell.getCellType()) {
                //数值类型
                case Cell.CELL_TYPE_NUMERIC:
                    //进一步判断 ，单元格格式是日期格式
                    if (DateUtil.isCellDateFormatted(cell)) {
                        cellValue = formatter.formatCellValue(cell);
                    } else {
                        //数值
                        double value = cell.getNumericCellValue();
                        int intValue = (int) value;
                        cellValue = value - intValue == 0 ? String.valueOf(intValue) : String.valueOf(value);
                    }
                    break;
                case Cell.CELL_TYPE_STRING:
                    cellValue = cell.getStringCellValue();
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    cellValue = String.valueOf(cell.getBooleanCellValue());
                    break;
                //判断单元格是公式格式，需要做一种特殊处理来得到相应的值
                case Cell.CELL_TYPE_FORMULA: {
                    try {
                        cellValue = String.valueOf(cell.getNumericCellValue());
                    } catch (IllegalStateException e) {
                        cellValue = String.valueOf(cell.getRichStringCellValue());
                    }

                }
                break;
                case Cell.CELL_TYPE_BLANK:
                    cellValue = "";
                    break;
                case Cell.CELL_TYPE_ERROR:
                    cellValue = "";
                    break;
                default:
                    cellValue = cell.toString().trim();
                    break;
            }
        }
        return cellValue.trim();
    }
}
