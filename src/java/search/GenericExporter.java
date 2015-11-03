/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package search;

import java.io.*;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author chris
 */
public abstract class GenericExporter {

    protected File location;
    protected String[] headers;
    protected FileOutputStream fos; // this is the level POI wants for 
                                    // interaction with the filesystem
    
    public GenericExporter(String location, String[] headers) throws IOException {
        this.location = new File(location);
        this.headers = headers;
        fos = new FileOutputStream(this.location);
    }
    
    public abstract void writeLine(String[] row);
    
    public boolean close() {
        try {
            fos.close();
        } catch (IOException ioe) {
            System.err.println("Unable to close Exporter file. "+ioe.getMessage());
            return false;
        }
        return true;
    }
}
