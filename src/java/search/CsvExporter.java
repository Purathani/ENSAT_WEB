/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package search;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Writes the results to a CSV file, assumes header array is in the same order
 * and length as the writeLine array
 * @author chris
 */
public final class CsvExporter extends GenericExporter {
    
    private BufferedWriter bw;
    
    public CsvExporter(String location, String[] headers) throws IOException {
        super(location, headers);
        bw = new BufferedWriter(new OutputStreamWriter(fos));
        this.writeLine(headers);
        
    }
    
    @Override
    public void writeLine(String[] row) {
        StringBuilder sb = new StringBuilder();
        for (String entry : row) {
            sb.append(entry==null?"":entry); // TODO blank if null? 
            sb.append(',');
        }
        sb.deleteCharAt(sb.length()-1);
        try {
        bw.write(sb.toString());
        bw.newLine();
        } catch (IOException ioe) {
            ioe.printStackTrace();
    }
    
    }
    
    @Override
    public boolean close() {
        try {
            bw.close();
            super.close();
        } catch (IOException ex) {
            return false;
        }
        return true;
    }
     
}
