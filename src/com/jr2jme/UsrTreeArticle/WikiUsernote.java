package com.jr2jme.UsrTreeArticle;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by K.H on 2015/06/29.
 */
public class WikiUsernote implements Serializable {
    String uname;
    Integer userid;
    Set<String> notemap = new HashSet<String>();//
    public WikiUsernote(String uname,Integer userid,Set<String> notemap){
        this.userid=userid;
        this.uname=uname;
        this.notemap=notemap;
    }

    public Integer getUserid(){
        return userid;
    }
    public Set<String> getNotemap(){
        return notemap;
    }

}
