package com.example;

import com.example.tools.XlsToTxtUtils;
import com.wutian.xml.file.FileUtils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class LoadActivity {
    private static final String EXCEL_XLS = "xls";
    private static final String EXCEL_XLSX = "xlsx";

    public static void main(String[] args) {
        String path = "/Users/maxy/Android/workspace/SHAREit/App/src/main/AndroidManifest.xml";
        String path2 = "/Users/maxy/Desktop/AndroidManifest.xml";
        List<String> strings = readFile(new File(path2));
        System.out.println(strings.size());
        writeFile(strings, new File(path2));
//        writeExcel(strings, 1, "/Users/maxy/Desktop/Workbook4.xlsx");
    }

    public static void writeFile(List<String> lines, File targetFile) {
        BufferedWriter writer = null;

        try {
            if (!targetFile.exists())
                targetFile.createNewFile();
        } catch (IOException e) {
            targetFile = null;
        }

        if (targetFile == null || !targetFile.exists())
            return;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFile)));

            for (String line : lines) {
                int lastIndexOf= line.lastIndexOf(".");
                String str = line.substring(lastIndexOf + 1);
                System.out.println(str);
//
//                writer.write(str);
//                writer.flush();
//                writer.newLine();
            }

        } catch (Exception e) {
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

    public static List<String> readFile(File originFile) {
        List<String> list = new ArrayList<>();
        BufferedReader reader = null;

        if (originFile == null || !originFile.exists())
            return list;

        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(originFile)));
            String line;
            while (true) {
                line = reader.readLine();
                if (line == null)
                    break;

                String substring = line.substring(line.lastIndexOf(".") + 1);
                list.add(substring);
                System.out.println(substring);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }



    public static void writeExcel(List<String> dataList, int cloumnCount,String finalXlsxPath){
        OutputStream out = null;
        try {
            // 获取总列数
            int columnNumCount = cloumnCount;
            // 读取Excel文档
            File finalXlsxFile = new File(finalXlsxPath);
            Workbook workBook = getWorkbok(finalXlsxFile);
            if (workBook == null)
                return;
            // sheet 对应一个工作页
            Sheet sheet = workBook.getSheetAt(0);
            /**
             * 删除原有数据，除了属性列
             */
            int rowNumber = sheet.getLastRowNum();  // 第一行从0开始算
            System.out.println("原始数据总行数，除属性列：" + rowNumber);
            for (int i = 1; i <= rowNumber; i++) {
                Row row = sheet.getRow(i);
                sheet.removeRow(row);
            }
            // 创建文件输出流，输出电子表格：这个必须有，否则你在sheet上做的任何操作都不会有效
            out =  new FileOutputStream(finalXlsxFile);
            workBook.write(out);
            /**
             * 往Excel中写新数据
             */
            for (int j = 0; j < dataList.size(); j++) {
                // 创建一行：从第二行开始，跳过属性列
                Row row = sheet.createRow(j + 1);
                // 得到要插入的每一条记录
                String line = dataList.get(j);
                for (int k = 0; k < columnNumCount; k++) {
                    // 在一行内循环
                    Cell first = row.createCell(0);
                    first.setCellValue(line);

//                    Cell second = row.createCell(1);
//                    second.setCellValue(address);
//
//                    Cell third = row.createCell(2);
//                    third.setCellValue(phone);
                }
            }
            // 创建文件输出流，准备输出电子表格：这个必须有，否则你在sheet上做的任何操作都不会有效
            out =  new FileOutputStream(finalXlsxPath);
            workBook.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            try {
                if(out != null){
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("数据导出成功");
    }

    /**
     * 判断Excel的版本,获取Workbook
     * @param in
     * @param filename
     * @return
     * @throws IOException
     */
    public static Workbook getWorkbok(File file) throws IOException{
        if (!file.exists()) {
            System.out.print(file.getAbsolutePath());
            return null;
        }
        Workbook wb = null;
        FileInputStream in = new FileInputStream(file);
        if(file.getName().endsWith(EXCEL_XLS)){  //Excel 2003
            wb = new HSSFWorkbook(in);
        }else if(file.getName().endsWith(EXCEL_XLSX)){  // Excel 2007/2010
            wb = new XSSFWorkbook(in);
        }
        return wb;
    }
}
