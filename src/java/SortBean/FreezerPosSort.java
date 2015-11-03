/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SortBean;

import java.util.Date;
import java.util.Vector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


/**
 *
 * @author astell
 */
public class FreezerPosSort {
    
    private static final Logger logger = Logger.getLogger(FreezerPosSort.class);

    private Vector<Vector> freezerPositions, testList;
    private int posNum;    
    
    public FreezerPosSort(Vector<Vector> _freezerPositions) {
        
        posNum = _freezerPositions.size();
        freezerPositions = new Vector<Vector>();
        for (int i = 0; i < posNum; i++) {
            Vector<String> freezerPositionIn = _freezerPositions.get(i);
            freezerPositions.add(freezerPositionIn);            
        }
        this.sortFreezerPositions(freezerPositions,0,posNum);
        //this.testSort();
    }

    public Vector<String> getFreezerPosition(int index) {
        return freezerPositions.get(index);
    }

    public Vector<Vector> getFreezerPositions() {
        return freezerPositions;
    }
    
    private void testSort(){
        
        testList = new Vector<Vector>();
        
        for(int i=0; i < 11; i++){
            Vector<String> testIn = new Vector<String>();
            testIn.add("217");
            testIn.add("24h_urine");
            testIn.add("2 (aliquot)");
            for(int j=0; j < 5; j++){
                testIn.add("1");
            }
        
            if(i == 0){
                testIn.add("10");
            }else if(i == 1){
                testIn.add("11");
            }else if(i == 2){
                testIn.add("5");
            }else if(i == 3){
                testIn.add("4");
            }else if(i == 4){
                testIn.add("2");
            }else if(i == 5){
                testIn.add("3");
            }else if(i == 6){
                testIn.add("6");
            }else if(i == 7){
                testIn.add("7");
            }else if(i == 8){
                testIn.add("8");
            }else if(i == 9){
                testIn.add("9");
            }else if(i == 10){
                testIn.add("12");
            }
            testList.add(testIn);
        }
        
        //Print test array out
        for(int i=0; i<testList.size(); i++){
            Vector<String> testIn = testList.get(i);
            System.out.println("testList (unsorted): " + testIn);            
        }
        
        this.sortFreezerPositions(testList,0,testList.size());
        
        //Print test array out
        for(int i=0; i<testList.size(); i++){
            Vector<String> testIn = testList.get(i);
            System.out.println("testList (sorted): " + testIn);            
        }        
    }

    //NEED TO SORT ON COLUMN 1 (SHIFT EACH VECTOR TO GO WITH THAT), THEN COL 2, THEN COL 3, ETC UP TO COL 6
    public void sortFreezerPositions(Vector<Vector> array,int low, int n) {

        int lo = low;
        int hi = n-1;
        if (lo >= n) {
            return;
        }
        Vector<String> mid = array.get((lo + hi) / 2);
        while (lo < hi) {
            while (lo < hi && (this.compareFreezerElements(array.get(lo), mid)) == -1) {
                lo++;
            }
            while (lo < hi && (this.compareFreezerElements(array.get(hi), mid)) == 1) {
                hi--;
            }
            if (lo < hi) {
                Vector<String> T = array.get(lo);
                array.set(lo, array.get(hi));
                array.set(hi, T);                
            }
            lo++;            
        }
        if (hi < lo) {
            int T = hi;
            hi = lo;
            lo = T;
        }
        sortFreezerPositions(array, low, lo);
        sortFreezerPositions(array, lo == low ? lo + 1 : lo, n);
    }
    
    private int compareFreezerElements(Vector<String> elem1, Vector<String> elem2){
        
        //System.out.println("elem1: " + elem1);
        //System.out.println("elem2: " + elem2);
        
        int ELEM_NUM = 6;
        int[] comparisons = new int[ELEM_NUM];
        for(int i=3; i < (ELEM_NUM+3); i++){
            int elem1InInt = -1;
            int elem2InInt = -1;
            try{
                elem1InInt = Integer.parseInt(elem1.get(i));
                elem2InInt = Integer.parseInt(elem2.get(i));            
            }catch(NumberFormatException nfe){
                logger.debug("Error: " + nfe.getMessage());
                //System.out.println("Error: " + nfe.getMessage());
            }
            
            if(elem1InInt > elem2InInt){
                comparisons[i-3] = 1;
            }else if(elem1InInt < elem2InInt){
                comparisons[i-3] = -1;
            }else{
                comparisons[i-3] = 0;
            }
        }
        
        int elemCount = 0;
        while(elemCount < ELEM_NUM){            
            if(comparisons[elemCount] != 0){
                return comparisons[elemCount];
            }else{
                elemCount++;
            }
        }
        return 0;
    }
}
