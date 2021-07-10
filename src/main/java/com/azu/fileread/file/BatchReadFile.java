package com.azu.fileread.file;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author zzs
 * @date 2021/7/9 16:34
 */
@Slf4j
@SuppressWarnings("all")
public class BatchReadFile {

    static final String FILE_PATH = System.getProperty("user.dir") + "/" + "files/";

    static String[] FILE_NAMES = null;

    public static void main(String[] args) {
        FILE_NAMES = new File(FILE_PATH).list();
        // 单线程读取文件
        commonReadFile();
        // 多线程读取文件
        multipartReadFile();
    }

    static double totalNum1 = 0;
    static double totalNum2 = 0;

    /**
     * 单线程读取文件
     */
    static void commonReadFile() {
        long startTime = System.currentTimeMillis();
        log.info("单线程读取文件");
        for (String fileName : FILE_NAMES) {
            try {
                File file = new File(FILE_PATH + fileName);
                InputStream inputStream = new FileInputStream(file);
                HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
                HSSFSheet sheet = workbook.getSheetAt(0);
                int rowNum = sheet.getLastRowNum() + 1;
                int columnNum = sheet.getRow(0).getPhysicalNumberOfCells();
                for (int rowIndex = 0; rowIndex < rowNum; rowIndex++) {
                    HSSFRow row = sheet.getRow(rowIndex);
                    for (int columnIndex = 0; columnIndex < columnNum; columnIndex++) {
                        double cellValue1 = row.getCell(1).getNumericCellValue();
                        totalNum1 = totalNum1 + cellValue1;
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        log.info("totalNum {}", totalNum1);
        log.info("cost time {}", System.currentTimeMillis() - startTime);
    }

    static void multipartReadFile() {
        log.info("多线程读取文件");
        long startTime = System.currentTimeMillis();
        int limit = 100;
        int threadNum = FILE_NAMES.length / limit;
        final CountDownLatch latch = new CountDownLatch(threadNum);
        Thread[] threads = new Thread[threadNum];
        List files = Arrays.asList(FILE_NAMES);
        for (int i = 0; i < threadNum; i++) {
            int startIndex = limit * i;
            int endIndex = startIndex + limit;
            List<String> files1 = files.subList(startIndex, endIndex);
            if (endIndex > files.size()) {
                endIndex = files.size();
            }
            List<String> fileNames = files.subList(startIndex, endIndex);
            System.out.println("reading file" + startIndex + "-" + endIndex);
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
//                    System.out.println(Thread.currentThread().getName() + " is working");
                    doRead(fileNames);
                    latch.countDown();
                }
            });
            threads[i].start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("totalNum {}", totalNum2);
        log.info("cost time {}", System.currentTimeMillis() - startTime);
    }

    private synchronized  static void doRead(List<String> fileNames) {
        for (String fileName : fileNames) {
            try {
                File file = new File(FILE_PATH + fileName);
                InputStream inputStream = new FileInputStream(file);
                HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
                HSSFSheet sheet = workbook.getSheetAt(0);
                int rowNum = sheet.getLastRowNum() + 1;
                int columnNum = sheet.getRow(0).getPhysicalNumberOfCells();
                for (int rowIndex = 0; rowIndex < rowNum; rowIndex++) {
                    HSSFRow row = sheet.getRow(rowIndex);
                    for (int columnIndex = 0; columnIndex < columnNum; columnIndex++) {
                        double cellValue1 = row.getCell(1).getNumericCellValue();
                        totalNum2 += cellValue1;
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        log.info("thread: {}, totalNum: {}", Thread.currentThread().getName(), totalNum2);
    }
}


