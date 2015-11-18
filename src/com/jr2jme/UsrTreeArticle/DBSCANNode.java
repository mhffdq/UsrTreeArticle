package com.jr2jme.UsrTreeArticle;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by K.H on 2015/07/10.
 */
public class DBSCANNode {

    private static final Double minweight = 0.34;
    private static final Integer mincount = 3;


    public static Set<Set<Node>> dbscan(Set<Node> nodes){//距離の近いのをまとめる
        Set<Node> visited = new HashSet<Node>();//まとまっていないのを消す
        Set<Node> clusted = new HashSet<Node>();
        Set<Set<Node>> allacuster = new HashSet<Set<Node>>();

        for(Node node:nodes){
            Set<Node> dennode = new HashSet<Node>(nodes.size());
            if(!visited.contains(node)) {
                visited.add(node);
                int count = 0;
                for (Edge edge : node.getedges()) {
                    if (edge.getWeight() > minweight) {
                        count++;
                        dennode.add(edge.othernode(node));
                    }
                }
                if (count >= mincount) {
                    allacuster.add(expandcluster(node, dennode,visited,clusted));
                }
            }
        }
        return allacuster;
    }

    private static Set<Node> expandcluster(Node node,Set<Node> neighbor,Set<Node> visited,Set<Node>clusted){
        Set<Node> cluster = new HashSet<Node>();
        cluster.add(node);
        clusted.add(node);
        expand(neighbor,cluster,visited,clusted);
        return cluster;
    }

    private static void expand(Set<Node> neighbor,Set<Node> cluster,Set<Node>visited,Set<Node>clusted){
        Set<Node> tuikabunn = new HashSet<Node>();
        for(Node neinode : neighbor){
            if(!visited.contains(neinode)){
                visited.add(neinode);
                int count =0;
                Set<Node> kouho = new HashSet<Node>();
                for(Edge edge:neinode.getedges()){
                    if (edge.getWeight() > minweight) {
                        count++;
                        kouho.add(edge.othernode(neinode));
                    }
                }
                if(count>=mincount){
                    tuikabunn.addAll(kouho);
                }
            }
            if(!clusted.contains(neinode)){
                cluster.add(neinode);
                clusted.add(neinode);
            }
        }
        if(tuikabunn.size()>0) {
            expand(tuikabunn, cluster, visited, clusted);
        }
    }

}
