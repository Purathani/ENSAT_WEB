/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SortBean;

import java.util.Date;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


/**
 *
 * @author astell
 */
public class HtmlOutput {
    
    private static final Logger logger = Logger.getLogger(HtmlOutput.class);
    
    private String htmlOutput;
    private Date htmlDate;    

    public HtmlOutput(String _htmlOutput, Date _htmlDate) {        
        
        htmlOutput = _htmlOutput;
        htmlDate = _htmlDate;        
    }
    
    public String getHtmlOutput(){
        return htmlOutput;
    }
    
    public Date getHtmlDate(){
        return htmlDate;
    }
    
    public int compareTo(HtmlOutput _htmlIn){
        return _htmlIn.getHtmlDate().compareTo(this.getHtmlDate());        
    }
}
