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
public class DateSort {
    
    private static final Logger logger = Logger.getLogger(DateSort.class);

    private HtmlOutput[] htmlOutputs;
    private int htmlNum;

    public DateSort(String[] _htmlOutputs, Date[] _htmlDates, int _htmlNum) {
        
        htmlNum = _htmlNum;
        htmlOutputs = new HtmlOutput[htmlNum];
        for (int i = 0; i < htmlNum; i++) {
            htmlOutputs[i] = new HtmlOutput(_htmlOutputs[i], _htmlDates[i]);
        }
        this.sortHtmlOutput(htmlOutputs,0,htmlNum);
    }

    public HtmlOutput getHtmlOutput(int index) {
        return htmlOutputs[index];
    }

    public void sortHtmlOutput(HtmlOutput array[],int low, int n) {

        int lo = low;
        int hi = n-1;
        if (lo >= n) {
            return;
        }
        HtmlOutput mid = array[(lo + hi) / 2];
        while (lo < hi) {
            while (lo < hi && (array[lo].compareTo(mid) == -1)) {
                lo++;
            }
            while (lo < hi && (array[hi].compareTo(mid) == 1)) {
                hi--;
            }
            if (lo < hi) {
                HtmlOutput T = array[lo];
                array[lo] = array[hi];
                array[hi] = T;
            }
            lo++;
        }
        if (hi < lo) {
            int T = hi;
            hi = lo;
            lo = T;
        }
        /*if (hi < lo) {
            HtmlOutput T = array[hi];
            array[hi] = array[lo];
            array[lo] = T;
        }*/
        sortHtmlOutput(array, low, lo);
        sortHtmlOutput(array, lo == low ? lo + 1 : lo, n);
    }
}
