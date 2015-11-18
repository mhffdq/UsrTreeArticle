package com.jr2jme.UsrTreeArticle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by K.H on 2015/11/10.
 */
public class NoteStructure {
    String notetitle;
    Map<String,List<Comment>> list=new HashMap<String, List<Comment>>();


    public NoteStructure(String notetitle){
        this.notetitle=notetitle;
    }

    public Comment addComment(String topic, String editor,String comment,Comment mae){
        Comment com = new Comment(editor,comment,mae);
        List lis;
        if(list.containsKey(topic)){
            lis=list.get(topic);
        }else {
            lis = new ArrayList();
            list.put(topic,lis);
        }
        lis.add(com);


        return com;
    }


    class Comment{
        String editor;
        String comment;
        Comment mae;
        Comment tugi;
        public Comment(String editor,String comment,Comment mae){
            this.comment=comment;
            this.editor=editor;
            this.mae=mae;
        }
        public void settsugi(Comment tsugi){
            this.tugi=tsugi;
        }
    }
}
