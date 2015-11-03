/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package search;

import java.io.IOException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Writes the results to a xls(x) file, assumes header array is in the same order
 * and length as the writeLine array
 * 
 * TODO Assumption - empty cell for null??
 * @author chris
 */
public final class ExcelExporter extends GenericExporter {
    
    private Workbook excelWorkbook;
    private Sheet excelSheet;
    private int maxRowIndex; // 0..n
    private CellStyle normalStyle;
    private CellStyle boldStyle;
    private CellStyle dateStyle;
    private Pattern datePattern;
    private Pattern numericPattern;
    
    public ExcelExporter(String location, String[] headers) throws IOException {
        super(location, headers);
        
        datePattern = Pattern.compile("^(\\d{4})-(\\d{2})-(\\d{2})$");
        numericPattern = Pattern.compile("^\\d+\\.?\\d*$");
        
        excelWorkbook = new XSSFWorkbook();
        excelSheet = excelWorkbook.createSheet();
        
        //set up formatting
        Font boldFont = excelWorkbook.createFont();
        boldFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        normalStyle = excelWorkbook.createCellStyle();
        boldStyle = excelWorkbook.createCellStyle();
        boldStyle.setFont(boldFont);
        
        dateStyle = excelWorkbook.createCellStyle();
        DataFormat df = excelWorkbook.createDataFormat();
        //dateStyle.setDataFormat(df.getFormat("yyyy/mm/dd"));
        dateStyle.setDataFormat(df.getFormat("dd/mm/yyyy"));

        maxRowIndex = 0;
        //maxRowIndex = 1;
        
        writeLine(headers,true);
        Row header = excelSheet.getRow(0);
    }
    
    @Override
    public void writeLine(String[] row) {
        writeLine(row, false);
    }
    
    public void writeLine(String[] row, boolean makeBold) {
        Row excelRow = excelSheet.createRow(maxRowIndex++);
        for (int i = 0; i < row.length; i++) {            
            Cell cell = excelRow.createCell(i);
            cell.setCellStyle(makeBold?boldStyle:normalStyle);
                        
            switch (getValueType(row[i])) {
                case NUMERIC:
                    cell.setCellValue(Double.parseDouble(row[i]));
                    break;
                case DATE:
                    Matcher dateMatcher = datePattern.matcher(row[i]);
                    dateMatcher.find();
                    int year = Integer.parseInt(dateMatcher.group(1))-1900;
                    int month = Integer.parseInt(dateMatcher.group(2))-1;
                    int day = Integer.parseInt(dateMatcher.group(3));
                    Date date = new Date(year,month,day);
                    cell.setCellValue(date);
                    cell.setCellStyle(dateStyle);
                    break;
                case STRING:
                    cell.setCellValue(row[i]==null?"":row[i]);
                    break;
                default:
                    cell.setCellType(Cell.CELL_TYPE_BLANK);
                    break;
            }
        }
    }
    
    private CellType getValueType(String _value) {
        String value = _value==null?"":_value;
        
        if (value.length()<=0) {
            return CellType.BLANK;
        }
        
        if (numericPattern.matcher(value).matches()) {
            return CellType.NUMERIC;
        }
        if (datePattern.matcher(value).matches()) {    
            return CellType.DATE;
        }
        
        return CellType.STRING;
    }
    /**
     * This writes the workbook to the output stream - if you do not
     * close you will not have an Excel sheet. This seems inconsistent
     * compared to the CSV exporter, but as that uses BufferedWriter, the
     * same behaviour is exhibited.
     * @return 
     */
    @Override
    public boolean close() {
        try {
            //autofit columns, here for performance
            Row excelRow = excelSheet.getRow(0);
            int rowLength = excelRow.getLastCellNum();
            for (int i = 0; i < rowLength; i++) {
                excelSheet.autoSizeColumn(i);
            }
            
            excelWorkbook.write(fos);
            super.close();
        } catch (IOException ex) {
            return false;
        }
        return true;
    }
    
    public Workbook getWorkbook() {
        return excelWorkbook;
    }
    
    private enum CellType {
        DATE, BLANK, NUMERIC, STRING
    }
}
