package base.ExportExcel;

import base.Editor;
import base.legacy.PApplet;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import static base.Editor.signals;
import static base.helpers.BaseHelper.toCollection;

public class ExportExcel {

    private final Editor editor;
    private Workbook workbook;
    private CreationHelper createHelper;
    private Sheet sheet;
    private int col = 0;
    private static String[] columns = {"Форма сигналу", "Період", "Мінімальне значення", "Максимальне значення", "Тривалість фронту"};


    public ExportExcel(Editor editor, File newFolder) throws Exception {
        this.editor = editor;
        FileInputStream fileInputStream = null;
        ArrayList<String> loadedData;
        workbook = new XSSFWorkbook();
        createHelper = workbook.getCreationHelper();
        sheet = workbook.createSheet(editor.getExperiment().getName());
        try {
            fileInputStream = new FileInputStream(editor.getExperiment().getFile());
            loadedData = load(fileInputStream);
        } finally {
            IOUtils.closeQuietly(fileInputStream);
        }
        saveExcel(loadedData);
        for(int i = 0; i < col + 2; i++) {
            sheet.autoSizeColumn(i);
        }
        FileOutputStream fileOut = new FileOutputStream(newFolder);
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
    }

    private void saveExcel(ArrayList<String> loadedData) {
        Row headerRow = sheet.createRow(columns.length);
        for(int i = 0; i < col; i++) {
            Cell cell = headerRow.createCell(i + 2);
            if (i == 0) {
                cell.setCellValue("Сигнал ЦАП");
                continue;
            }
            cell.setCellValue("Модуль АЦП " + i);
        }
        int cl = columns.length, i = 0, j;
        for(String str : loadedData) {
            Row row = sheet.createRow(cl + ++i);
            j = 0;
            for(String s : toCollection(str)) {
                Cell cell = row.createCell(++j + 1);
                cell.setCellValue(s);
            }
        }

    }

    private ArrayList<String> load(FileInputStream fileInputStream) {
        String[] lines = PApplet.loadStrings(fileInputStream);
        if (lines == null) return null;
        ArrayList<String> loadedData = new ArrayList<>();
        for (String line : lines) {
            if (line.length() == 0 || line.charAt(0) == '#') continue;
            int equals = line.indexOf('=');
            if (equals == -1) {
                loadedData.add(line);
            } else {
                parseKey(equals, line);
            }
        }
        return loadedData;
    }

    private void parseKey(int equals, String line) {
        String key = line.substring(0, equals).trim();
        if (key.equals("title")) {
            List<String> values = (ArrayList<String>) toCollection(line.substring(equals + 1).trim());
            col = Integer.parseInt(values.get(1));
            for(int i = 0; i < columns.length; i++) {
                if (i == 4 && !values.get(5).equals("1")) continue;
                Row headerRow = sheet.createRow(i);
                Cell cellH = headerRow.createCell(0);
                cellH.setCellValue(columns[i]);
                Cell cellR = headerRow.createCell(1);
                if (i == 0) {
                    cellR.setCellValue(signals[Integer.parseInt(values.get(i + 5))]);
                    continue;
                }
                cellR.setCellValue(values.get(i + 5));
            }
        }
    }
}
