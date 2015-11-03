/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package search;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.sql.Connection;

/**
 *
 * @author astell
 */
public class ExportThread implements Runnable{
    
    private static final Logger logger = Logger.getLogger(ExportThread.class);
    private String username = "";
    private SearchUtilities searchQuery = null;
    private Connection conn = null;
    
    public ExportThread(SearchUtilities _searchQuery, String _username, Connection _conn){                
        username = _username;
        searchQuery = _searchQuery;        
        conn = _conn;
    }
    
    public void run(){
        
        //Run the export processing here
        String exportHtml = "";
        try{
            exportHtml = searchQuery.processExport(conn);                        
        }catch(Exception e){
            logger.debug("Export error (" + username + "): " + e.getMessage());
        }
        
        logger.debug("Thread finishing...");
    }
    
}
