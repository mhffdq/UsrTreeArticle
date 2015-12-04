package com.jr2jme.UsrTreeArticle.Util;

import org.atilika.kuromoji.Token;
import org.atilika.kuromoji.Tokenizer;

import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * Created by K.H on 2015/11/04.
 */
public class Categories {
    Set<String> allcat=new HashSet<String>();
    Map<String,Double> catidf = new HashMap<String, Double>();
    Map<String,Double> dfcount = new HashMap<String, Double>();//idfを求めるまで

    public Categories(){
        File file = new File("category.txt");
        Tokenizer tokenizer = Tokenizer.builder().build();
        try {
            FileReader fileleader = new FileReader(file);
            BufferedReader br = new BufferedReader(fileleader);

            String line;
            while((line=br.readLine())!=null){
                List<Token> tokens = tokenizer.tokenize(line);

                for(Token tok:tokens){
                    if(tok.getPartOfSpeech().contains("名詞")){
                        if(dfcount.containsKey(tok.getSurfaceForm())) {
                            dfcount.put(tok.getSurfaceForm(), 1.0 + dfcount.get(tok.getSurfaceForm()));
                        }
                        else{
                            dfcount.put(tok.getSurfaceForm(), 1.0);
                        }
                    }
                }

                allcat.add(line);

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(Map.Entry<String,Double>ent : dfcount.entrySet()){
            catidf.put(ent.getKey(),Math.log((allcat.size()/ent.getValue())));
        }

    }

    public Set<String> getAllCategories(){

        return allcat;
    }

    public Map<String, Double> getCatidf() {
        return catidf;
    }




}
