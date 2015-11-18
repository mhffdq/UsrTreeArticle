package com.jr2jme.UsrTreeArticle;

import com.jr2jme.UsrTreeArticle.Util.Feature;

/**
 * Created by K.H on 2015/07/08.
 */
public class Edge {
    private String label;
    private Integer graphid;
    private String edgeid;
    private Node source;
    private Node target;
    private Float weight;
    private Feature tfidf;

    public Edge(String label,Integer graphid,String edgeid,Node source,Node target,Float weight,Feature tfidf){
        this.edgeid=edgeid;
        this.graphid=graphid;
        this.label=label;
        this.source=source;
        this.target=target;
        this.weight=weight;
        this.tfidf=tfidf;

    }


    public void insert_sql(SqlOp sq){
        sq.insert_edge(label,  edgeid, source.getId(), target.getId(),weight);
    }

    public String getId(){
        return edgeid;
    }

    public Node getSource() {
        return source;
    }

    public Node getTarget() {
        return target;
    }

    public Float getWeight() {
        return weight;
    }

    public Feature getRelterm() {
        return tfidf;
    }

    public Node othernode(Node node){
        if(target.equals(node)){
            return source;
        }else if(source.equals(node)){
            return target;
        }else{
            return  null;
        }
    }

    public void setTarget(Node target) {
        this.target = target;
    }

    public void setSource(Node source) {
        this.source = source;
    }

    public String getLabel() {
        return label;
    }

    public Boolean istargetnode(Node node){
        if(target.equals(node)){
            return true;
        }else if(source.equals(node)){
            return false;
        }else{
            return  null;
        }
    }

}
