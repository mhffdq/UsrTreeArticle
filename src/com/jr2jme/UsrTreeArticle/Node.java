package com.jr2jme.UsrTreeArticle;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by K.H on 2015/07/08.
 */
public class Node {
    protected String nodeid;
    private Set<Edge> edges = new HashSet<Edge>();
    private Set<Node> rinsetunode = new HashSet<Node>();
    protected String label;
    private Integer graphid;
    private Map<String,Double> termweight;

    public Node(String label,Integer graphid,String nodeid,Map<String,Double> termweight){
        this.nodeid=nodeid;
        this.label=label;
        this.graphid=graphid;
        this.termweight=termweight;
    }

    public Node(String key){
        
        this.nodeid=key;

    }

    public void setGraphid(Integer graphid) {
        this.graphid = graphid;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getId(){
        return nodeid;
    }

    public Map<String, Double> getTermweight() {
        return termweight;
    }

    public void addedge(Edge edge,Node node){
        edges.add(edge);
        rinsetunode.add(node);
    }
    public void insert_sql(SqlOp sq){
        sq.insert_node(label,  nodeid);
    }

    public void setTermweight(Map<String, Double> termweight) {
        this.termweight = termweight;
    }

    public Set<Edge> getedges(){
        return edges;
    }

    public Set<Node> getRinsetunode() {
        return rinsetunode;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Node node = (Node) obj;
        return this.nodeid.equals(node.getId());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((nodeid == null) ? 0 : nodeid.hashCode());
        return result;
    }

}
