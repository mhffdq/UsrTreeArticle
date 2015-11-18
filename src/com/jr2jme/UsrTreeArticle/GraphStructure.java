package com.jr2jme.UsrTreeArticle;

import com.jr2jme.UsrTreeArticle.Util.MaptoList;
import com.jr2jme.UsrTreeArticle.Util.Feature;

import java.util.*;

/**
 * Created by K.H on 2015/07/08.
 */
public class GraphStructure {
    String graphtitle;
    Integer graphid;
    Map<String,Edge> edges = new HashMap<String,Edge>();
    Map<String,Node> nodes = new HashMap<String,Node>();
    Set<Node> nodesset = new HashSet<Node>();

    public GraphStructure(String usrname,Integer graphid){
        this.graphtitle=usrname;
        this.graphid=graphid;
    }

    public void setGraphtitle(String graphtitle) {
        this.graphtitle = graphtitle;
    }

    public Map<String, Edge> getEdges() {
        return edges;
    }

    public Integer getGraphid() {
        return graphid;
    }

    public String getGraphtitle() {
        return graphtitle;
    }

    public void insertsql(SqlOp sq){
        sq.insert_graphtitle(graphtitle, graphid);
        for(Node node:nodesset){
           node.insert_sql(sq);
        }
        sq.insert_batch();

        for(Map.Entry<String,Edge>edggg:edges.entrySet()){
            edggg.getValue().insert_sql(sq);
        }
        sq.insert_batch();
    }

    public void makenode(String label,String nodeid,Map<String,Double> termweight){
        Node node = new Node(label,graphid,nodeid,termweight);
        nodes.put(node.getId(), node);
        nodesset.add(node);
    }
    public void addnode(Node node){
        node.setGraphid(graphid);
        nodes.put(node.getId(), node);
        nodesset.add(node);
    }

    public void makeedge(String label,String edgeid,String source,String target,Float weight,Feature tfidf){
        Edge edge = new Edge(label,graphid,edgeid,getnode(source),getnode(target),weight,tfidf);
        edges.put(edge.getId(), edge);
        //Node targetnode = nodes.get(edge.getTarget());
        //Node sourcenode = nodes.get(edge.getSource());
        edge.getSource().addedge(edge, edge.getTarget());
        edge.getTarget().addedge(edge, edge.getSource());
    }

    public void addedge(Edge edge){
        edges.put(edge.getId(),edge);
        edge.getSource().addedge(edge, edge.getTarget());
        edge.getTarget().addedge(edge, edge.getSource());
    }

    public Edge getedge(String edgeid){
        return edges.get(edgeid);
    }



    public Node getnode(String nodeid){
        return nodes.get(nodeid);
    }


    public void mergenodes(Set<Node> mernodes){
        Map<String,Double> newtfidf = new HashMap<String, Double>();
        Set<Node> connodes = new HashSet<Node>();
        String newnodeid="";
        String nodelabel = "";
        Double step = 1.0/mernodes.size();
        for(Node node:mernodes){
            nodes.remove(node.getId());
            nodesset.remove(node);
            for(Edge edge:node.getedges()){
                edges.remove(edge);
                edge.othernode(node).getedges().remove(edge);
            }
            for(Map.Entry<String,Double> tfidf:node.getTermweight().entrySet()){
                if(newtfidf.containsKey(tfidf.getKey())){
                    newtfidf.put(tfidf.getKey(),newtfidf.get(tfidf.getKey())+tfidf.getValue()*step);
                }else{
                    newtfidf.put(tfidf.getKey(),tfidf.getValue()*step);
                }
            }
            nodelabel += node.getLabel().substring(0,1)+",";
            connodes.addAll(node.getRinsetunode());
            newnodeid=node.getId();
        }

        nodelabel+=" : ";
        List<Map.Entry> list = MaptoList.valueSort(newtfidf);
        int c=0;
        for(Map.Entry<String,Double> wordentry:list){
            nodelabel+=wordentry.getKey();
            c++;
            if(c>=3){
                break;
            }
        }

        Double norm = 0d;
        System.out.println(nodelabel);
        Node nnode = new Node(nodelabel,graphid,newnodeid,newtfidf);
        nodes.put(nnode.getId(),nnode);
        nodesset.add(nnode);

        for(Map.Entry<String,Double>sss:newtfidf.entrySet()){
            norm+=Math.pow(sss.getValue(),2);
        }
        norm=Math.sqrt(norm);


        connodes.removeAll(mernodes);
        for(Node node:connodes){
            Double dor = 0d;
            Double ue =0d;
            for(Map.Entry<String,Double>tm:node.getTermweight().entrySet()){
                dor+=Math.pow(tm.getValue(),2);
                Double newnohou = 0d;
                if(newtfidf.containsKey(tm.getKey())){
                    newnohou=newtfidf.get(tm.getKey());
                }
                ue+=tm.getValue()*newnohou;
            }
            dor = Math.sqrt(dor);
            Double weight = ue/(dor*norm);
            Feature tttt = new Feature(newtfidf);
            Edge edggg = new Edge("",graphid,nnode.getId()+node.getId(),nnode,node,weight.floatValue(),tttt);
            edges.put(edggg.getId(), edggg);
            edggg.getTarget().addedge(edggg, edggg.getTarget());
            edggg.getTarget().addedge(edggg, edggg.getSource());
        }


    }

    public Map<String, Node> getNodes() {
        return nodes;
    }

    public Set<Node> getNodesset() {
        return nodesset;
    }
}
