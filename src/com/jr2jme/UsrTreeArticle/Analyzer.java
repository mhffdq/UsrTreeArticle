package com.jr2jme.UsrTreeArticle;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by K.H on 2015/07/23.
 */
public class Analyzer {
    Set<String> wpterm = new HashSet<String>();
    String regex ="";
    Pattern p = null;
    public Analyzer(){

        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader("./wikipediagideline.txt");
            br = new BufferedReader(fr);
            String arlist;
            while((arlist = br.readLine())!=null){
                wpterm.add(arlist);
                regex+=(arlist+"|");
            }
            regex=regex.substring(0,regex.length()-1);
            System.out.println(regex);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        p = Pattern.compile(regex);
    }
    public boolean analyzegiron(String comment){
        Matcher m = p.matcher(comment);
        if(m.find()){
            System.out.println(comment);
            return true;
        }
        else{
            return false;
        }


    }
}
