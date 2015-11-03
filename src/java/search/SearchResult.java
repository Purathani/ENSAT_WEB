package search;

/**
 * Compiles and holds info on individual search query output
 * 
 * @author Anthony Stell
 * @copy University of Melbourne, 2012
 */
import java.util.List;

public class SearchResult {
    
    private List<String> parameters, conditions, comparators, tables;
    
    public SearchResult(){
        
    }
    
    public List<String> getParameters(){
        return parameters;
    }
    
    public void setParameters(List<String> _parameters){
        parameters = _parameters;
    }
    
    public List<String> getConditions(){
        return conditions;
    }
    
    public void setConditions(List<String> _conditions){
        conditions = _conditions;
    }
    
    public List<String> getComparators(){
        return comparators;
    }
    
    public void setComparators(List<String> _comparators){
        comparators = _comparators;
    }
    
    public List<String> getTables(){
        return tables;
    }
    
    public void setTables(List<String> _tables){
        tables = _tables;
    }
    
}
