package com.jr2jme.UsrTreeArticle.Util;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by K.H on 2015/10/31.
 */
public class PosiNega {

    Map<String,Pndic> pnmap =null;

    public PosiNega(){
        File file = new File("pn_ja.dic");
        try {
            FileReader fileleader = new FileReader(file);
            BufferedReader br = new BufferedReader(fileleader);

            String line;
            while((line=br.readLine())!=null){

                String[] res = line.split(":");
                System.out.println(res[0]+res[res.length-1]);
                pnmap.put(res[0],new Pndic(res[0],Double.parseDouble(res[3]),res[2]));
                pnmap.put(res[0],new Pndic(res[0],Double.parseDouble(res[3]),res[2]));

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readdic(){
        File file = new File("pn_ja.dic");
        try {
            FileReader fileleader = new FileReader(file);
            BufferedReader br = new BufferedReader(fileleader);

            String line;
            while((line=br.readLine())!=null){

                String[] res = line.split(":");
                System.out.println(res[0]+res[res.length-1]);
                pnmap.put(res[0],new Pndic(res[0],Double.parseDouble(res[3]),res[2]));
                pnmap.put(res[0],new Pndic(res[0],Double.parseDouble(res[3]),res[2]));

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public double judge(String term){
        return pnmap.get(term).getPol();
    }


    class Pndic{
        String key;
        double pol;
        String hinshi;
        public Pndic(String k,double p,String s){
            key=k;
            pol=p;
            hinshi=s;
        }
        public double getPol(){
            return pol;
        }
    }
}
