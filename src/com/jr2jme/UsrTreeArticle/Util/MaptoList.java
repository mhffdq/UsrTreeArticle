package com.jr2jme.UsrTreeArticle.Util;

import java.util.*;

/**
 * Created by K.H on 2015/11/09.
 */
public class MaptoList {
    static public List<Map.Entry> valueSort(Map<String,Double> tAsMap2){

        List<Map.Entry> entries = new ArrayList<Map.Entry>(tAsMap2.entrySet());
        Collections.sort(entries, new Comparator(){
            public int compare(Object o1, Object o2){
                Map.Entry e1 =(Map.Entry)o1;
                Map.Entry e2 =(Map.Entry)o2;
                return ((Double)e2.getValue()).compareTo((Double)e1.getValue());//降順
//return ((Integer)e1.getValue()).compareTo((Integer)e2.getValue());//昇順
            }
        });
        return entries;
    }

}
