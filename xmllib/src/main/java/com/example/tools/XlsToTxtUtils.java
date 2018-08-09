package com.example.tools;

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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.LinkedList;

/*
*
* Note :  只能读 xls 文件。
* */
public class XlsToTxtUtils {


    private Sheet sheet;    //表格类实例
    LinkedList[] result;    //保存每个单元格的数据 ，使用的是一种链表数组的结构

    //读取excel文件，创建表格实例
    private void loadExcel(String filePath) {
        FileInputStream inStream = null;
        try {
            inStream = new FileInputStream(new File(filePath));
            Workbook workBook = WorkbookFactory.create(inStream);

            sheet = workBook.getSheetAt(0);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                if(inStream!=null){
                    inStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //获取单元格的值
    private String getCellValue(Cell cell) {
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
                case Cell.CELL_TYPE_FORMULA:{
                    try{
                        cellValue = String.valueOf(cell.getNumericCellValue());
                    }catch(IllegalStateException e){
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



    //初始化表格中的每一行，并得到每一个单元格的值
    public  void init(){
        int rowNum = sheet.getLastRowNum() + 1;
        result = new LinkedList[rowNum];
        for(int i=0;i<rowNum;i++){
            Row row = sheet.getRow(i);
            //每有新的一行，创建一个新的LinkedList对象
            result[i] = new LinkedList();
            for(int j=0;j<row.getLastCellNum();j++){
                Cell cell = row.getCell(j);
                //获取单元格的值
                String str = getCellValue(cell);
                //将得到的值放入链表中
                result[i].add(str);
            }
        }
    }
    //控制台打印保存的表格数据
    public void show(String path){
        try {
            if (path == null || "".equalsIgnoreCase(path))
                return;
            File outFile = new File(path);
            if (!outFile.exists())
                outFile.createNewFile();

            BufferedWriter writer = null;

            try {
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile)));

                for(int i=0;i<result.length;i++) {
                    LinkedList<String> linkedList = result[i];
                    if (linkedList.isEmpty())
                        continue;
                    StringBuilder sb = new StringBuilder();
                    for (String str : linkedList) {
                        sb.append(str + ",");
                    }

                    String str = sb.toString().substring(0, sb.length() - 1);

//                    <string name="about_name">About</string>
                    String xmlStr = "<string name=\"" + linkedList.get(0) + "\">" + linkedList.get(2)+ "</string>" ;
                    writer.write(xmlStr);
                    writer.flush();
                    writer.newLine();
                }
            }catch (Exception e1){
            }finally {
                if (writer != null)
                    writer.close();
            }
        } catch (Exception e) {}
    }

    public static void main(String[] args) {
        XlsToTxtUtils poi = new XlsToTxtUtils();
        String path = "/Users/maxy/Desktop/country2.xls";
        String outPath = "/Users/maxy/Desktop/aaaaaa.xml";
        poi.loadExcel(path);
        poi.init();
        poi.show(outPath);
    }
}