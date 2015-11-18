package com.jr2jme.UsrTreeArticle.Util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by K.H on 2015/07/29.
 */
public class Feature {//特徴ベクトルとか求める用の関数
    private Map<String,Double> tfidf = new HashMap<String, Double>();
    private Double norm;
    private static Map<String,Double> idf = new HashMap<String, Double>();
    private Map<String,Double> tf=new HashMap<String, Double>();
    private Double size;
    private static Set<String> memo = new HashSet<String>();
    double dor = 0;


    public Feature(double size) {
        this.size=size;
        memo= new HashSet<String>((int)size);
        tf =  new HashMap<String, Double>((int)size);
    }

    public Feature(Map<String,Double> tfidf, Double norm){
        this.tfidf=tfidf;
        this.norm=norm;
    }

    public Feature(Map<String,Double> tfidf){
        this.tfidf=tfidf;
        double dor = 0;
        for(Map.Entry<String,Double>iff:tfidf.entrySet()){
            dor+=Math.pow(iff.getValue(),2);
        }
        norm=Math.sqrt(dor);
    }

    public Double getNorm() {
        return norm;
    }

    public  void addidfcount(String str){
        if(!memo.contains(str)) {
            if (idf.containsKey(str)) {
                idf.put(str, idf.get(str) + 1.0);
            } else {
                idf.put(str, 1.0);
            }
            memo.add(str);
        }
    }

    public void addtfcount(String str){
        if (tf.containsKey(str)) {
            tf.put(str, tf.get(str) + (1/size));
        } else {
            tf.put(str, 1/size);
        }
    }

    public static Map<String,Double> getIdf(){
        return idf;
    }

    public Map<String, Double> getTf() {
        return tf;
    }

    public void maketfidf(){
        tfidf = new HashMap<String, Double>(size.intValue());

        for (String strk : tf.keySet()) {
            double tfidfi =tf.get(strk) * idf.get(strk);
            tfidf.put(strk, tfidfi);
            dor += Math.pow(tfidfi, 2);
        }
    }

    public double getDor() {
        return dor;
    }

    public void addtfcountweight(String str,double weight){
        if (tf.containsKey(str)) {
            tf.put(str, tf.get(str) + (weight/size));
        } else {
            tf.put(str, weight/size);
        }
    }

    public Map<String, Double> getTfidf() {
        return tfidf;
    }

    public double calccosrel(Feature anofe){
        double cos = 0;
        for(Map.Entry<String,Double>entry1: tfidf.entrySet()){
            if(anofe.getTfidf().containsKey(entry1.getKey())){
                cos+=(entry1.getValue()*anofe.getTfidf().get(entry1.getKey()))/(this.norm*anofe.getNorm());
            }
        }
        return cos;
    }

}
