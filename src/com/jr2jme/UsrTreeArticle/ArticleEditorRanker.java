package com.jr2jme.UsrTreeArticle;

import com.jr2jme.UsrTreeArticle.Util.Categories;
import com.jr2jme.UsrTreeArticle.Util.MaptoList;
import com.jr2jme.UsrTreeArticle.Util.Feature;
import org.atilika.kuromoji.Token;
import org.atilika.kuromoji.Tokenizer;

import java.util.*;

/**
 * Created by K.H on 2015/11/16.
 */
public class ArticleEditorRanker {
    static Double articleredrank(String article,String editor){//記事と編集者の類似度を求める＝得意度合

        WikiNote wikiNote = new WikiNote();
        Set<String> arname = new HashSet<String>();
        arname.add(article);
        List<Map<String,Integer>>list = new ArrayList<Map<String, Integer>>();
        list.add(wikiNote.widcategories(arname));


        Feature arfe = new Feature(catdisvec(catvec(list)));//記事のカテゴリを使った特徴ベクトル)
        Feature editfe = new Feature(testuserarticle(editor));//編集者の特徴ベクトル的なもの
        return arfe.calccosrel(editfe);

    }

    static Double artartdrank(String article1,String article2){//記事と編集者の類似度を求める＝得意度合

        WikiNote wikiNote = new WikiNote();
        Set<String> arname = new HashSet<String>();
        arname.add(article1);
        List<Map<String,Integer>>list = new ArrayList<Map<String, Integer>>();
        list.add(wikiNote.widcategories(arname));
        Feature arfe = new Feature(catdisvec(catvec(list)));//記事のカテゴリを使った特徴ベクトル)

        Set<String> arname2 = new HashSet<String>();
        arname2.add(article2);
        List<Map<String,Integer>>list2 = new ArrayList<Map<String, Integer>>();
        list2.add(wikiNote.widcategories(arname2));
        Feature arfe2 = new Feature(catdisvec(catvec(list2)));//記事のカテゴリを使った特徴ベクトル)
        return arfe.calccosrel(arfe2);

    }

    static Double editoreditorrank(String editor1,String editor2){
        Feature editfe1 = new Feature(testuserarticle(editor1));//編集者の特徴ベクトル的なもの
        Feature editfe2 = new Feature(testuserarticle(editor2));//編集者の特徴ベクトル的なもの
        return editfe1.calccosrel(editfe2);
    }

    public static Map<String,Double> testuserarticle(String usrname){//userを入力すると，そのユーザの属性をカテゴリで表してくれそうなやつ


        WikiNote wikinote = new WikiNote();
        WikiUsernote wikiusernotet=wikinote.getnotelist(usrname);//useridと編集したノートページの集合が帰ってくる
        System.out.println(wikiusernotet.getNotemap().size());
        List<Map<String,Integer>> listmap = new ArrayList<Map<String, Integer>>(wikiusernotet.getNotemap().size());//記事ごとのカテゴリの距離
        for(String artname:wikiusernotet.getNotemap()){
            Set<String> name = new HashSet<String>();
            name.add(artname);
            listmap.add(wikinote.widcategories(name));
        }



// この２行で解析できる

        //List<Map.Entry> val = valueSort(usercat);
//全体での出現頻度とか見るゾーン
        Map<String,Double> usercat = catvec(listmap);




        return catdisvec(usercat);



    }

    public static Map<String,Double>  testuserarticletfidf(String usrname){//userを入力すると，そのユーザの属性をカテゴリで表してくれそうなやつ


        WikiNote wikinote = new WikiNote();
        WikiUsernote wikiusernotet=wikinote.getnotelist(usrname);//useridと編集したノートページの集合が帰ってくる
        List<Map<String,Integer>> listmap = new ArrayList<Map<String, Integer>>(wikiusernotet.getNotemap().size());
        for(String artname:wikiusernotet.getNotemap()){
            Set<String> name = new HashSet<String>();
            name.add(artname);
            listmap.add(wikinote.widcategories(name));
        }

        Map<String,Double> usercat = catvec(listmap);
// この２行で解析できる



        return catdisvectfidf(usercat);


    }

    private static Map<String,Double> catdisvec(Map<String,Double> usercat){//カテゴリの距離をもらって，形態素解析して特徴ベクトルっぽく

        Tokenizer tokenizer = Tokenizer.builder().build();

        Feature tf = new Feature(1);

        for(Map.Entry<String,Double> vv:usercat.entrySet()){//カテゴリ名と距離を基にした重み

            List<Token> tokens = tokenizer.tokenize(vv.getKey().substring(9));

            for(Token tok:tokens){
                if(tok.getPartOfSpeech().contains("名詞")){
                    tf.addtfcountweight(tok.getSurfaceForm(),vv.getValue());
                }
            }
        }
        return tf.getTf();
    }

    private static Map<String,Double> catdisvectfidf(Map<String,Double> usercat){//カテゴリの距離をもらって，形態素解析して特徴ベクトルっぽく

        Categories cate = new Categories();
        Tokenizer tokenizer = Tokenizer.builder().build();

        Feature tf = new Feature(1);

        for(Map.Entry<String,Double> vv:usercat.entrySet()){//カテゴリ名と距離を基にした重み

            List<Token> tokens = tokenizer.tokenize(vv.getKey().substring(9));

            for(Token tok:tokens){
                if(tok.getPartOfSpeech().contains("名詞")){
                    tf.addtfcountweight(tok.getSurfaceForm(),vv.getValue()*cate.getCatidf().get(tok.getSurfaceForm()));                }
            }
        }
        return tf.getTf();
    }


    private static Map<String,Double> catvec(List<Map<String,Integer>> listmap){
        Map<String,Double> usercat = new HashMap<String, Double>();
        for (Map<String,Integer> arrrai:listmap){
            for(Map.Entry<String,Integer> ent:arrrai.entrySet()){
                if(usercat.containsKey(ent.getKey())) {
                    usercat.put(ent.getKey(), (3.0-ent.getValue()) + usercat.get(ent.getKey()));
                }
                else{
                    usercat.put(ent.getKey(),3.0-ent.getValue());
                }
            }
        }
        return usercat;
    }



}
