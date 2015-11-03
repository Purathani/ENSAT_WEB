/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author chris
 */
public class SearchExportTest {
    
    public SearchExportTest() {
    }

    private static String BASEDIR;
    private static String BASENAME;
    
    @BeforeClass
    public static void onlyOnce() {
        BASEDIR = "/tmp/";
        BASENAME = (System.currentTimeMillis()/1000)+"_"+Math.abs((new Random()).nextInt());
        System.err.println("Testing temp file prefix: "+BASENAME);
    }
    
    @Test
    public void testCSV() throws Exception {
        String[] headers = {"header1","header2","header3"};
        String[] rowA = {"rowA1",null,"rowA3"};
        String[] rowB = {"rowB1","rowB2","rowB3"};
        String[] rowC = {"rowC1","rowC2","rowC3"};
        String fileName = BASEDIR+BASENAME+".csv";
        GenericExporter export = new CsvExporter(fileName,headers);
        export.writeLine(rowA);
        export.writeLine(rowB);
        export.writeLine(rowC);
        assertTrue(export.close());
        BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
        assertEquals("header1,header2,header3",br.readLine());
        assertEquals("rowA1,,rowA3",br.readLine());
        assertEquals("rowB1,rowB2,rowB3",br.readLine());
        assertEquals("rowC1,rowC2,rowC3",br.readLine());
        
    }
    @Test
    public void testXLS() throws Exception {
        String[] headers = {"header1","header2","header3"};
        String[] rowA = {"rowA1",null,"rowA3"};
        String[] rowB = {"rowB1","rowB2","rowB3"};
        String[] rowC = {"rowC1","rowC2","rowC3"};
        String fileName = BASEDIR+BASENAME+".xlsx";
        GenericExporter export = new ExcelExporter(fileName,headers);
        export.writeLine(rowA);
        export.writeLine(rowB);
        export.writeLine(rowC);
        assertTrue(export.close());
        FileInputStream fis = new FileInputStream(new File(fileName));
        Workbook wb = new XSSFWorkbook(fis);
        Sheet sh = wb.getSheetAt(0);
        assertEquals("header1,header2,header3",sheetRowToString(sh.getRow(0)));
        assertEquals("rowA1,,rowA3",sheetRowToString(sh.getRow(1)));
        assertEquals("rowB1,rowB2,rowB3",sheetRowToString(sh.getRow(2)));
        assertEquals("rowC1,rowC2,rowC3",sheetRowToString(sh.getRow(3)));
        fis.close();
    }
    
    @Test
    public void testCellFormatting() throws Exception {
        String[] headers = {"String","Numeric","Date","Null","EmptyString","String"};
        String[] row = {"Row start","3","2012-06-15",null,"","rowEnd"};
        String fileName = BASEDIR+BASENAME+".xlsx";
        GenericExporter export = new ExcelExporter(fileName,headers);
        export.writeLine(row);
        assertTrue(export.close());
        FileInputStream fis = new FileInputStream(new File(fileName));
        Workbook wb = new XSSFWorkbook(fis);
        Sheet sh = wb.getSheetAt(0);
        assertEquals("String,Numeric,Date,Null,EmptyString,String",sheetRowToString(sh.getRow(0)));
        Row dataRow = sh.getRow(1);
        Cell cell;
        
        cell = dataRow.getCell(0);
        assertEquals(cell.getCellType(),Cell.CELL_TYPE_STRING);
        assertEquals(cell.getStringCellValue(),"Row start");
        
        cell = dataRow.getCell(1);
        assertEquals(cell.getCellType(),Cell.CELL_TYPE_NUMERIC);
        assertEquals(cell.getNumericCellValue(),3,0.05);
        
        cell = dataRow.getCell(2);
        assertEquals(cell.getCellType(),Cell.CELL_TYPE_NUMERIC);
        assertEquals(cell.getDateCellValue(),new Date(2012-1900,06-1,15));
        cell.setCellType(Cell.CELL_TYPE_STRING);
        
        cell = dataRow.getCell(3);
        assertEquals(cell.getCellType(),Cell.CELL_TYPE_BLANK);
        
        cell = dataRow.getCell(4);
        assertEquals(cell.getCellType(),Cell.CELL_TYPE_BLANK);
        
        cell = dataRow.getCell(5);
        assertEquals(cell.getCellType(),Cell.CELL_TYPE_STRING);
        assertEquals(cell.getStringCellValue(),"rowEnd");
        
    }
    
    @Test
    public void testSearchUtilitiesUserFragment() throws Exception {
        //Integer[] users = {1,2,40};
        String[] users = null;
        SearchUtilities sutil = new SearchUtilities();
        sutil.setExportUserList(users);
        assertEquals("AND (ensat_id = 1 OR ensat_id = 2 OR ensat_id = 40) ",sutil.getUserListQueryFragment(null));
    }
    
        @Test
    public void testSearchUtilitiesUserFragmentWithPrefix() throws Exception {
        //Integer[] users = {1,2,40};
        String[] users = null;
        SearchUtilities sutil = new SearchUtilities();
        sutil.setExportUserList(users);
        assertEquals("AND (A.ensat_id = 1 OR A.ensat_id = 2 OR A.ensat_id = 40) ",sutil.getUserListQueryFragment("A"));
    }
    
    @Test
    public void testSearchUtilitiesUserFragmentEmpty() throws Exception {
        //Integer[] users = {};
        String[] users = null;
        SearchUtilities sutil = new SearchUtilities();
        sutil.setExportUserList(users);
        assertEquals("",sutil.getUserListQueryFragment(""));
    }
    
    @Test
    public void testSearchUtilitiesUserFragmentNull() throws Exception {
        //Integer[] users = null;
        SearchUtilities sutil = new SearchUtilities();
        String[] users = null;
        sutil.setExportUserList(users);
        assertEquals("",sutil.getUserListQueryFragment(null));
    }
    
    private String sheetRowToString(Row row) {
        String rowString = "";
        Iterator<Cell> iter = row.cellIterator();
        while (iter.hasNext()) {
            Cell cell = iter.next();
            rowString += cell.getStringCellValue()+",";
        }
        return rowString.substring(0, rowString.length()-1);
        
    }
    
    

}


