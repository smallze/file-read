package com.azu.fileread.file;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author zzs
 * @date 2021/7/9 16:36
 */
@Slf4j
public class CreateFile {

    public static void main(String[] args) {
        try {
            int fileNum = 1000;
            for (int fileIndex = 0; fileIndex < fileNum; fileIndex++) {
                //这里也可以设置sheet的Name
                HSSFWorkbook workbook = new HSSFWorkbook();
                //创建工作表对象
                HSSFSheet sheet = workbook.createSheet();
                //创建工作表的行
                for (int rowIndex = 0; rowIndex < 1000; rowIndex++) {
                    //设置第一行，从零开始
                    HSSFRow row = sheet.createRow(rowIndex);
                    row.createCell(0).setCellValue(fileIndex + "-" + rowIndex);
                    row.createCell(1).setCellValue(1);
                    row.createCell(2).setCellValue(2);
                }
                //设置sheet的Name
                workbook.setSheetName(0, "first_sheet");
                //文档输出
                FileOutputStream out = new FileOutputStream(System.getProperty("user.dir") + "/files/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()).toString() + fileIndex + ".xls");
                workbook.write(out);
                out.close();
                log.info("文件" + fileIndex + "生成完成");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
