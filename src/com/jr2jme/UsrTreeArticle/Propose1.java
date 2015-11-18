package com.jr2jme.UsrTreeArticle;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by K.H on 2015/07/29.
 * なんでこんなに文字化けしてるのか
 */
public class Propose1 {


    static final double kijun=0.2d;
    static Set<Edge> clusterd;
    public static GraphStructure proposemethod(GraphStructure graph){
        graph.setGraphtitle(graph.getGraphtitle()+"テスト");
        clusterd = new HashSet<Edge>();
        Map<String,Edge> edges = graph.getEdges();
        Set<Set<Edge>>clusters=new HashSet<Set<Edge>>();
        for(Map.Entry<String,Edge>edgeent:edges.entrySet()){
            Edge edge = edgeent.getValue();
            if(!clusterd.contains(edge)){
                Set<Edge> cluster = new HashSet<Edge>();
                cluster.add(edge);
                clusterd.add(edge);

                exmawaru(edge, cluster, edge.getSource());
                exmawaru(edge,cluster,edge.getTarget());

                if(cluster.size()>3) {//���̃G�b�W�Ɗ֌W�Ȃ��̂͂���Ȃ�
                    clusters.add(cluster);
                }
            }
        }
        return makenewgraph(graph,clusters);
    }

    private static void exmawaru(Edge edge,Set<Edge> cluster,Node node) {//istarget���Ă����̂͗����ق����C����edge�ɂƂ��ă^�[�Q�b�g���ǂ���
        if (edge.istargetnode(node)) {//�^�[�Q�b�g�ł������ꍇ�C�������͌��Ȃ��Ă����̂ŁC
            Set<Edge> mawaru = new HashSet<Edge>();
            mawaru.addAll(edge.getSource().getedges());//�\�[�X��������
            mawaru.remove(edge);
            extend(mawaru, cluster,  edge.getSource());
        } else {
            Set<Edge> mawaru = new HashSet<Edge>();
            mawaru.addAll(edge.getTarget().getedges());
            mawaru.remove(edge);
            extend(mawaru, cluster,  edge.getTarget());

        }
    }
    private static GraphStructure makenewgraph(GraphStructure graph ,Set<Set<Edge>>clusters){
        int count = 0;

        GraphStructure newgraph = new GraphStructure(graph.getGraphtitle(),graph.graphid);

        for(Set<Edge>smallgraph :clusters){
            count++;
            for(Edge edge:smallgraph){
                Node source = edge.getSource();
                Node newsource =null;
                if(!newgraph.getNodes().containsKey(source.getId()+count)){
                    newsource = new Node(source.getLabel(),graph.getGraphid(),source.getId()+count,source.getTermweight());
                    newgraph.addnode(newsource);
                }else{
                    newsource=newgraph.getnode(source.getId()+count);
                }


                Node target = edge.getTarget();
                Node newtarget =null;
                if(!newgraph.getNodes().containsKey(target.getId()+count)){
                    newtarget = new Node(target.getLabel(),graph.getGraphid(),target.getId()+count,target.getTermweight());
                    newgraph.addnode(newtarget);
                }else{
                    newtarget=newgraph.getnode(target.getId()+count);
                }

                edge.setSource(newsource);
                edge.setTarget(newtarget);
                newgraph.addedge(edge);
            }
        }

        return newgraph;
    }

    private static void extend(Set<Edge> mawaru,Set<Edge> cluster,Node node) {

        for (Edge edge : mawaru) {
            Boolean last = true;
            if (!clusterd.contains(edge)) {
                for (Edge hikaedge : cluster) {
                    Double rui = 0d;
                    for (String str : edge.getRelterm().getTfidf().keySet()) {
                        double tatekagi = 0d;
                        if (hikaedge.getRelterm().getTfidf().containsKey(str)) {
                            tatekagi = hikaedge.getRelterm().getTfidf().get(str);
                        }
                        rui += edge.getRelterm().getTfidf().get(str) * tatekagi;
                    }
                    rui = rui / (hikaedge.getRelterm().getNorm() * edge.getRelterm().getNorm());
                    if (rui < kijun||Double.isNaN(rui)) {
                        last=false;
                        break;
                    }
                }
            }else{
                last=false;
            }
            if(last){
                cluster.add(edge);
                clusterd.add(edge);
                exmawaru(edge,cluster,node);
            }
        }
    }

}
