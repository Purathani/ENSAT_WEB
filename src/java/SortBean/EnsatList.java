/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SortBean;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author astell
 */
public class EnsatList {
    
    private static final Logger logger = Logger.getLogger(EnsatList.class);
    
    private int listSize;
    private String[] centerList;
    private int[] centerNumberList;
    
    public EnsatList(int _listSize, String[] _centerList, int[] _centerNumberList){        
        listSize = _listSize;
        centerList = _centerList;
        centerNumberList = _centerNumberList;
    }
    
    public void setListSize(int _listSize){
        listSize = _listSize;
    }
    
    public int getListSize(){        
        return listSize;
    }
    
    public void setCenterList(String[] _centerList){
        centerList = _centerList;
    }
    
    public String[] getCenterList(){        
        return centerList;
    }
    
    public String getCenter(int index){
        return centerList[index];
    }
    
    public void setCenterNumberList(int[] _centerNumberList){
        centerNumberList = _centerNumberList;
    }
    
    public int[] getCenterNumberList(){        
        return centerNumberList;
    }
    
    public int getCenterNumber(int index){
        return centerNumberList[index];
    }
}
