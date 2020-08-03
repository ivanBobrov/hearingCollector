package org.home.hearing.collector.excel;

import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.home.hearing.collector.Hearing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExcelGenerator {
    private static final Logger log = LoggerFactory.getLogger(ExcelGenerator.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm");

    public void generateExcelForHearings(List<Hearing> hearings, Path file) {
        log.info("Creating workbook with {} hearings", hearings.size());
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("hearings");

        for (int i = 0; i < hearings.size(); i++) {
            Hearing hearing = hearings.get(i);
            HSSFRow row = sheet.createRow(i);

            row.createCell(0).setCellValue(new HSSFRichTextString(hearing.getCourtName()));
            row.createCell(1).setCellValue(new HSSFRichTextString(hearing.getType()));
            row.createCell(2).setCellValue(new HSSFRichTextString(hearing.getDateTime().format(formatter)));
            row.createCell(3).setCellValue(new HSSFRichTextString(hearing.getRoom()));
            row.createCell(4).setCellValue(new HSSFRichTextString(hearing.getLaw()));
            row.createCell(5).setCellValue(new HSSFRichTextString(hearing.getId()));
            row.createCell(6).setCellValue(new HSSFRichTextString(hearing.getInfo()));
            row.createCell(7).setCellValue(new HSSFRichTextString(hearing.getJudge()));
            row.createCell(8).setCellValue(new HSSFRichTextString(hearing.getResult()));
        }

        log.debug("Autosizing columns");
        for (int j = 0; j < 9; j++) {
            sheet.autoSizeColumn(j);
        }

        sheet.setColumnWidth(6, 10000);
        sheet.setColumnWidth(5, 4000);

        writeToFile(workbook, file);
    }

    private void writeToFile(HSSFWorkbook workbook, Path path) {
        log.info("Saving excel workbook to file {}", path);
        try (FileOutputStream outputStream = new FileOutputStream(path.toFile())) {
            workbook.write(outputStream);
        } catch (IOException exception) {
            log.error("Can't save excel workbook to file " + path, exception);
        }
    }

}
