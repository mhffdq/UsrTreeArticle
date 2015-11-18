package com.jr2jme.UsrTreeArticle;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by K.H on 2015/07/23.
 */
public class Nodecalc extends Node{
    private String artname = "" ;
    private String comment = "";
    private String midashi = "";
    private double norm;
    private Integer ifrule;
    public void putcom(String key,String value){
        super.nodeid=key;
        comment=value;
    }

    public void addmidashi(String midashi){
        this.midashi=midashi;
    }

    public Nodecalc(String key,String value,String artname){
        super(key);
        this.artname=artname;
        comment=value;
    }

    public void setIfrule(Integer ifrule) {
        this.ifrule = ifrule;
    }

    public Integer getIfrule() {
        return ifrule;
    }

    public String getArtname() {
        return artname;
    }

    public String getId() {
        return super.nodeid;
    }

    public String getComment() {
        return comment;
    }


    public String getMidashi() {
        return midashi;
    }

    public void setNorm(double norm) {
        this.norm = norm;
    }

    public double getNorm() {
        return norm;
    }
}
