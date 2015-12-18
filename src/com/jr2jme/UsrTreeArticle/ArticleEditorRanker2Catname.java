package com.jr2jme.UsrTreeArticle;

import com.jr2jme.UsrTreeArticle.Util.Categories;
import com.jr2jme.UsrTreeArticle.Util.Feature;
import org.atilika.kuromoji.Token;
import org.atilika.kuromoji.Tokenizer;

import java.util.*;

/**
 * Created by K.H on 2015/11/16.
 */
public class ArticleEditorRanker2Catname {
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

    private static List<Map<String,Integer>> mainop(String usrname){
        WikiNote wikinote = new WikiNote();
        WikiUsernote wikiusernotet=wikinote.getnotelist(usrname);//useridと編集したノートページの集合が帰ってくる
        System.out.println(wikiusernotet.getNotemap().size());
        List<Map<String,Integer>> listmap = new ArrayList<Map<String, Integer>>(wikiusernotet.getNotemap().size());//記事ごとのカテゴリの距離
        for(String artname:wikiusernotet.getNotemap()){
            Set<String> name = new HashSet<String>();
            name.add(artname);
            listmap.add(wikinote.widcategories(name));
        }
        return listmap;
    }

    public static Map<String,Double> testuserarticle(String usrname){//userを入力すると，そのユーザの属性をカテゴリで表してくれそうなやつ
        List<Map<String,Integer>> listmap = mainop(usrname);
//全体での出現頻度とか見るゾーン
        Map<String,Double> usercat = catvec(listmap);
        return catdisvec(usercat);
    }

    public static Map<String,Double>  testuserarticletfidf(String usrname){//userを入力すると，そのユーザの属性をカテゴリで表してくれそうなやつ
        //tfidfというかカテゴリに使われている単語の逆頻度
        List<Map<String,Integer>> listmap = mainop(usrname);
        Map<String,Double> usercat = catvec(listmap);
        return catdisvectfidf(usercat);//何かないか
    }

    public static Map<String,Double>  testuserarticlecat(String usrname){//userを入力すると，そのユーザの属性をカテゴリで表してくれそうなやつ

        List<Map<String,Integer>> listmap = mainop(usrname);

        Map<String,Double> usercat = catvec(listmap);//カテゴリの深さを利用するならこれ系
        return catdisveccat(usercat);//難しいことを考えすぎない//ここの関数をいじると変わる
        //やってから考えよう


    }

    public static List<Map<String,Double>> testuserarticleall(String usrname){//全てやって，リストで返す
        List<Map<String,Double>> retlist = new ArrayList<Map<String, Double>>();
        List<Map<String,Integer>> listmap = mainop(usrname);
        Map<String,Double> usercat = catvec(listmap);//カテゴリの深さを利用するならこれ系

        retlist.add(catdisvec(usercat));//よく使われているかだけ
        retlist.add(catdisvectfidf(usercat));//貴重なほど特徴
        retlist.add(catdisveccat(usercat));//深いほど特徴
        retlist.add(catdisvecbunya(usercat));//bunya
        retlist.add(catdisveccatminor1(usercat));//tfidfとcatの組み合わせ
        retlist.add(catdisveccatminor2(usercat));//tfidfとbunyaの組み合わせ
        retlist.add(catdisveccatminor3(usercat));//catと分野
        retlist.add(catdisveccatminor4(usercat));//全部

        return  retlist;

    }

    private static Map<String,Double> catdisvec(Map<String,Double> usercat){//カテゴリの距離をもらって，形態素解析して特徴ベクトルっぽく
//基本計
        Tokenizer tokenizer = Tokenizer.builder().build();

        Feature tf = new Feature(1);

        for(Map.Entry<String,Double> vv:usercat.entrySet()){//カテゴリ名と距離を基にした重み

            List<Token> tokens = tokenizer.tokenize(vv.getKey().substring(9));

            for(Token tok:tokens){
                if(tok.getPartOfSpeech().contains("名詞")){
                    tf.addtfcountweight(tok.getSurfaceForm(),vv.getValue());//多く使われていたかどうか
                }
            }
        }
        return tf.getTf();
    }

    private static Map<String,Double> catdisvectfidf(Map<String,Double> usercat){//カテゴリの距離をもらって，形態素解析して特徴ベクトルっぽく
//カテゴリに使われる単語の頻度を考慮
        Categories cate = new Categories();
        Tokenizer tokenizer = Tokenizer.builder().build();

        Feature tf = new Feature(1);

        for(Map.Entry<String,Double> vv:usercat.entrySet()){//カテゴリ名と距離を基にした重み

            List<Token> tokens = tokenizer.tokenize(vv.getKey().substring(9));

            for(Token tok:tokens){
                if(tok.getPartOfSpeech().contains("名詞")){
                    tf.addtfcountweight(tok.getSurfaceForm(),vv.getValue()*cate.getCatidf().get(tok.getSurfaceForm()));//多分問題ない
                }//
            }
        }
        return tf.getTf();
    }

    private static Map<String,Double> catdisveccat(Map<String,Double> usercat){//カテゴリの距離をもらって，形態素解析して特徴ベクトルっぽく
        WikiNote wikiNote = new WikiNote();
        Map<String,Integer> weightmap = wikiNote.catshitaweight();//上からどれくらいの深さにあるか
        Tokenizer tokenizer = Tokenizer.builder().build();//少し前は4個以上だとどうのこうのやっていた

        Feature tf = new Feature(1);

        for(Map.Entry<String,Double> vv:usercat.entrySet()){//カテゴリ名と距離を基にした重み
            List<Token> tokens = tokenizer.tokenize(vv.getKey().substring(9));
            for(Token tok:tokens){
                if(tok.getPartOfSpeech().contains("名詞")){
                    if(weightmap.containsKey(tok.getSurfaceForm())) {
                        tf.addtfcountweight(tok.getSurfaceForm(), vv.getValue() * weightmap.get(tok.getSurfaceForm()));//shitaの方にあるカテゴリを単純に重く
                    }else{
                        tf.addtfcountweight(tok.getSurfaceForm(), vv.getValue() );
                    }
                }
            }
        }
        return tf.getTf();
    }

    private static Map<String,Double> catdisvecbunya(Map<String,Double> usercat){//カテゴリの距離をもらって，形態素解析して特徴ベクトルっぽく
        WikiNote wikiNote = new WikiNote();
        Map<String,Integer> weightmap = wikiNote.catshitaweight();//カテゴリと深さ
        Tokenizer tokenizer = Tokenizer.builder().build();

        Feature tf = new Feature(1);
        for(Map.Entry<String,Double> vv:usercat.entrySet()){//カテゴリ名と距離を基にした重み

            List<Token> tokens = tokenizer.tokenize(vv.getKey().substring(9));

            for(Token tok:tokens){
                if(tok.getPartOfSpeech().contains("名詞")){
                    if(weightmap.containsKey(tok.getSurfaceForm())) {//カテゴリ名に同じ単語が存在した時
                        int shita =wikiNote.getbunyacount(tok.getSurfaceForm());//分野数
                        tf.addtfcountweight(tok.getSurfaceForm(), vv.getValue() * shita );//分野数を使う いろいろ買えてたから分野数単体のやつがないや
                    }else{
                        tf.addtfcountweight(tok.getSurfaceForm(), vv.getValue());//しないなら何もいない値
                    }
                }
            }
        }
        return tf.getTf();
    }

    private static Map<String,Double> catdisveccatminor1(Map<String,Double> usercat){//カテゴリの距離をもらって，形態素解析して特徴ベクトルっぽく
        WikiNote wikiNote = new WikiNote();
        Map<String,Integer> weightmap = wikiNote.catshitaweight();//カテゴリと深さ
        Tokenizer tokenizer = Tokenizer.builder().build();
        Categories cate = new Categories();

        Feature tf = new Feature(1);
        for(Map.Entry<String,Double> vv:usercat.entrySet()){//カテゴリ名と距離を基にした重み

            List<Token> tokens = tokenizer.tokenize(vv.getKey().substring(9));

            for(Token tok:tokens){
                if(tok.getPartOfSpeech().contains("名詞")){
                    if(weightmap.containsKey(tok.getSurfaceForm())) {
                        tf.addtfcountweight(tok.getSurfaceForm(), vv.getValue() * weightmap.get(tok.getSurfaceForm())*cate.getCatidf().get(tok.getSurfaceForm()) );//分野数を使う いろいろ買えてたから分野数単体のやつがないや
                    }else{
                        tf.addtfcountweight(tok.getSurfaceForm(), vv.getValue()*cate.getCatidf().get(tok.getSurfaceForm()));
                    }
                }
            }
        }
        return tf.getTf();
    }



    private static Map<String,Double> catdisveccatminor2(Map<String,Double> usercat){//カテゴリの距離をもらって，形態素解析して特徴ベクトルっぽく
        Categories cate = new Categories();//カテゴリと分野
        WikiNote wikiNote = new WikiNote();
        Map<String,Integer> weightmap = wikiNote.catshitaweight();
        Tokenizer tokenizer = Tokenizer.builder().build();

        Feature tf = new Feature(1);



        for(Map.Entry<String,Double> vv:usercat.entrySet()){//カテゴリ名と距離を基にした重み

            List<Token> tokens = tokenizer.tokenize(vv.getKey().substring(9));//深さとよくある度を両方使用

            for(Token tok:tokens){
                if(tok.getPartOfSpeech().contains("名詞")){
                    if(weightmap.containsKey(tok.getSurfaceForm())) {
                        int shita =wikiNote.getbunyacount(tok.getSurfaceForm());//分野数
                        tf.addtfcountweight(tok.getSurfaceForm(), vv.getValue() * shita *cate.getCatidf().get(tok.getSurfaceForm()) );
                    }else{
                        tf.addtfcountweight(tok.getSurfaceForm(), vv.getValue()*cate.getCatidf().get(tok.getSurfaceForm()));
                    }
                }
            }
        }
        return tf.getTf();
    }
    private static Map<String,Double> catdisveccatminor3(Map<String,Double> usercat){//カテゴリの距離をもらって，形態素解析して特徴ベクトルっぽく
        WikiNote wikiNote = new WikiNote();
        Map<String,Integer> weightmap = wikiNote.catshitaweight();//tfidfと分野
        Tokenizer tokenizer = Tokenizer.builder().build();

        Feature tf = new Feature(1);



        for(Map.Entry<String,Double> vv:usercat.entrySet()){//カテゴリ名と距離を基にした重み

            List<Token> tokens = tokenizer.tokenize(vv.getKey().substring(9));

            for(Token tok:tokens){
                if(tok.getPartOfSpeech().contains("名詞")){
                    if(weightmap.containsKey(tok.getSurfaceForm())) {
                        int shita =wikiNote.getbunyacount(tok.getSurfaceForm());//分野数
                        tf.addtfcountweight(tok.getSurfaceForm(), vv.getValue() * weightmap.get(tok.getSurfaceForm())*shita );//合成catとtfidfの
                    }else{
                        tf.addtfcountweight(tok.getSurfaceForm(), vv.getValue());
                    }
                }
            }
        }
        return tf.getTf();
    }

    private static Map<String,Double> catdisveccatminor4(Map<String,Double> usercat){//カテゴリの距離をもらって，形態素解析して特徴ベクトルっぽく
        Categories cate = new Categories();//階層の深さ，下にある同じ名前が含まれたカテゴリ数とかを使う版
        WikiNote wikiNote = new WikiNote();
        Map<String,Integer> weightmap = wikiNote.catshitaweight();//全部乗せ
        Tokenizer tokenizer = Tokenizer.builder().build();

        Feature tf = new Feature(1);
        for(Map.Entry<String,Double> vv:usercat.entrySet()){//カテゴリ名と距離を基にした重み

            List<Token> tokens = tokenizer.tokenize(vv.getKey().substring(9));

            for(Token tok:tokens){
                if(tok.getPartOfSpeech().contains("名詞")){
                    if(weightmap.containsKey(tok.getSurfaceForm())) {
                        int shita =wikiNote.getbunyacount(tok.getSurfaceForm());//分野数
                        int depth = weightmap.get(tok.getSurfaceForm());
                        tf.addtfcountweight(tok.getSurfaceForm(), vv.getValue() * depth*shita *cate.getCatidf().get(tok.getSurfaceForm()) );//分野数を使う いろいろ買えてたから分野数単体のやつがないや
                    }else{
                        tf.addtfcountweight(tok.getSurfaceForm(), vv.getValue()*cate.getCatidf().get(tok.getSurfaceForm()));
                    }
                }
            }
        }
        return tf.getTf();
    }


    private static Map<String,Double> catvec(List<Map<String,Integer>> listmap){//記事から近い方が高評価
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
    private static Map<String,Double> catdisveccatmadatukawanai(Map<String,Double> usercat){//カテゴリの距離をもらって，形態素解析して特徴ベクトルっぽく
        WikiNote wikiNote = new WikiNote();
        Map<String,Integer> weightmap = wikiNote.catshitaweight();//catとtfidf
        Tokenizer tokenizer = Tokenizer.builder().build();

        Feature tf = new Feature(1);

        //この辺変更点  カテゴリを細かく分解してる・//とりあえずなかったことに
        Map<String,Integer> newweight = new HashMap<String, Integer>();
        for(Map.Entry<String,Integer>en:weightmap.entrySet()){
            List<Token> tokens = tokenizer.tokenize(en.getKey());
            for(Token tok:tokens){
                if(tok.getPartOfSpeech().contains("名詞")){
                    if(newweight.containsKey(tok.getSurfaceForm())){
                        newweight.put(tok.getSurfaceForm(),newweight.get(tok.getSurfaceForm())+en.getValue());//深いほど高得点
                    }else{
                        newweight.put(tok.getSurfaceForm(),en.getValue());
                    }
                }
            }

        }
        for(Map.Entry<String,Double> vv:usercat.entrySet()){//カテゴリ名と距離を基にした重み

            List<Token> tokens = tokenizer.tokenize(vv.getKey().substring(9));

            for(Token tok:tokens){
                if(tok.getPartOfSpeech().contains("名詞")){
                    if(newweight.containsKey(tok.getSurfaceForm())) {
                        tf.addtfcountweight(tok.getSurfaceForm(), vv.getValue() * newweight.get(tok.getSurfaceForm()));
                    }else{
                        tf.addtfcountweight(tok.getSurfaceForm(), vv.getValue());
                    }
                }
            }
        }
        return tf.getTf();
    }




}
