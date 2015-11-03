/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author chris
 */
public class TableHeader {
    
    private String[] columnHeaders;
    private int maxNumberOfEntries;
    private boolean isSubTable;
    private List<TableHeader> subTables;
    
    public TableHeader(String[] columnHeaders, int maxNumberOfEntries) {
        this(columnHeaders, maxNumberOfEntries, false);
    }
    
    public TableHeader(String[] columnHeaders, int maxNumberOfEntries, boolean isSubTable) {
        this.columnHeaders = columnHeaders;
        this.maxNumberOfEntries = maxNumberOfEntries;
        this.isSubTable = isSubTable;
        this.subTables = new ArrayList<TableHeader>();
    }
    
    public void addSubTable(TableHeader subTable) {
        subTables.add(subTable);
    }
    
    public void addSubTable(String[] columnHeaders, int maxNumberOfEntries) {
        addSubTable(new TableHeader(columnHeaders,maxNumberOfEntries,true));
    }
    
    public String[] getHeaders() {
        
        List<String> outString = new ArrayList<String>();
        int identifierCount = isSubTable?4:3;
        
        //loop for maximum number of patient entries
        //i.e. one patient is in 5 times but all others less, generate 5 header sets

        for (int i = 0; i < maxNumberOfEntries; i++) {
            //(subTables.isEmpty()&&maxNumberOfEntries<=1) - should be single line per patient tables
            String addon = (!isSubTable&&subTables.isEmpty()&&maxNumberOfEntries<=1)?"":"_"+(i+1);
            
            if (!isSubTable) {
                if (i == 0) outString.add("patient_id");
                outString.add(columnHeaders[0]+addon);
            }
            
            //loop through remaining columns
            for (int j = identifierCount; j < columnHeaders.length; j++) {
                outString.add(columnHeaders[j] + addon);
            }
            
            //add a set of headers for subtables at end of each set 
            if (!subTables.isEmpty()) {
                for (TableHeader subTable: subTables) {
                    String[] subTableHeaders = subTable.getHeaders();
                    for (int k = 0; k < subTableHeaders.length; k++) {
                        outString.add(generateHeaderWithParentTableContext(subTableHeaders[k], addon));
                    }
                }
            }
        }
        
        return outString.toArray(new String[0]);
    }
    
    
    //String headerWithContext = subTableHeaders[k].substring(0,subTableHeaders[k].length()-2)
    //                            +addon
    //                            +subTableHeaders[k].substring(subTableHeaders[k].length()-2);
    
    //Sometimes there is more than 10 subtable entries - fixed number cutout doesn't work - time to be not lazy
    
    private String generateHeaderWithParentTableContext(String header, String countString) {
        String[] splitHeader = header.split("_");
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < splitHeader.length; i++) {
            output.append(splitHeader[i]);
            if (i == splitHeader.length-2) output.append(countString);
            output.append("_");
        }
        output.deleteCharAt(output.length()-1);
        return output.toString();
    }
    
}
