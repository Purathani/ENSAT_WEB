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
 * @author anthony
 */
import SortBean.*;

public class SortBean {
    
    private static final Logger logger = Logger.getLogger(SortBean.class);

    private EnsatList centers;

    public SortBean() {        
    }
    
    private int[] numbers;
    private String[] centerStrs;
    private int number;

    public EnsatList sort(EnsatList _centers) {

        centers = _centers;
        
        this.numbers = centers.getCenterNumberList();
        this.centerStrs = centers.getCenterList();
        
        number = centers.getListSize();

        mergesort(0,number - 1);
        
        centers.setCenterList(centerStrs);
        centers.setCenterNumberList(numbers);

        return centers;
    }

    private void mergesort(int low, int high) {
        // Check if low is smaller then high, if not then the array is sorted
        if (low < high) {
            // Get the index of the element which is in the middle
            int middle = (low + high) / 2;
            // Sort the left side of the array
            mergesort(low, middle);
            // Sort the right side of the array
            mergesort(middle + 1, high);
            // Combine them both
            merge(low, middle, high);
        }
        
        
        
    }

    private void merge(int low, int middle, int high) {

        // Helper arrays
        int[] helper1 = new int[number];
        String[] helper2 = new String[number];

        // Copy both parts into the helper array
        for (int i = low; i <= high; i++) {
            helper1[i] = numbers[i];            
            helper2[i] = centerStrs[i];            
        }

        int i = low;
        int j = middle + 1;
        int k = low;
        // Copy the smallest values from either the left or the right side back
        // to the original array
        while (i <= middle && j <= high) {
            if (helper1[i] <= helper1[j]) {                
                numbers[k] = helper1[i];
                centerStrs[k] = helper2[i];
                i++;
            } else {
                numbers[k] = helper1[j];
                centerStrs[k] = helper2[j];
                j++;
            }
            k++;
        }
        // Copy the rest of the left side of the array into the target array
        while (i <= middle) {
            numbers[k] = helper1[i];
            centerStrs[k] = helper2[i];
            k++;
            i++;
        }
        helper1 = null;
        helper2 = null;

    }
}

