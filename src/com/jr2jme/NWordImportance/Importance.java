package com.jr2jme.NWordImportance;

import com.jr2jme.Rev.Levenshtein3;
import com.jr2jme.UsrTreeArticle.Util.WikiNote;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by K.H on 2015/09/18.
 */
//本当にやらなければいけないことは何か
//もういやだ
//っだめなんだっけ
//なんで正直になってはいけないのか

//期間でやるよりは編集回数で見た方がいい
//最後にノートページに書き込んだ人が編集してから10回とか，残りの編集回数の何割とか
//明日だな 疲れた
public class Importance {

    String title = null;
    public void importance(String title) throws IOException {//ここでやりたいのは，反映されたかどうかみて重要度を求める作業
//コメントの間の編集を見る
        //その編集が残ったかどうか
        //ある程度楽な方法があったはず，要調査
        //編集前と編集後，編集前と後々の差分を使うとかそんな感じ
        //編集履歴を取得する関数つくるかー

        //何をしたらいいかな

        WikiNote wikiNote = new WikiNote();
        this.title=title;
        WikiNote.Histories notehis = wikiNote.gethistory(title,true);//
        WikiNote.Histories articlehis = wikiNote.gethistory(title,false);//編集履歴の取得

        for(Map.Entry<String,List<WikiNote.Histories.History>> dates:notehis.getEditormain().entrySet()){//こっちはノートの書き込み
            WikiNote.Histories.History prevdate=null;//ひとつ前に編集したやつを記録する（ノート）
            String editor = dates.getKey();
            for(WikiNote.Histories.History date:dates.getValue()){//editorがノートを編集した日付
                if(prevdate!=null){//一つ前が存在していた場合
                    operation(articlehis,getedits(articlehis,prevdate.getDate(),date.getDate()),editor);

                }
                prevdate = date;
            }
            //ノートページの最後の書き込み

            operation(articlehis,getedits(articlehis,prevdate.getDate(),15),editor);


        }

    }

    private double operation(WikiNote.Histories articlehis,List<Integer> intlist,String editor){
        boolean seq = false;//連続しているかどうか
        boolean exist=false;
        int editmae=1;
        int editorend=0;
        //あんまり間が空いている場合はやめたいが，難しいか

        for(Integer in :intlist){//間にあった編集を取得して
            if(articlehis.getHistories().get(in).getEditor().equals(editor)){
                exist=true;
                if(!seq){
                    editmae=in-1;
                }
                seq=true;
                editorend=in;

            }
            else{
                seq=false;
            }
            //最初と最後とか
        }
        if(exist) {
            int en = 0;
            if (articlehis.getHistories().size() > editorend + 10) {
                en = editorend + 10;

            } else {
                en = articlehis.getHistories().size() - 1;
            }
            return calceditsurvival(articlehis, editmae, editorend, en);//使い方の例
        }
        else{
            return 0;//全く反映されないのと編集していないのは違うから区別したい
        }
    }



    public double calceditsurvival(WikiNote.Histories his,int kaishi,int edited,int recent){
        Levenshtein3 lev = new Levenshtein3();
        char[] vi =WikiNote.parsepageid(his.getHistories().get(edited).getId()).toCharArray();
        char[] vi1;
        if(edited!=0){
            vi1=WikiNote.parsepageid(his.getHistories().get(kaishi).getId()).toCharArray();
        }else{
            vi1="".toCharArray();
        }
        char[] vj = WikiNote.parsepageid(his.getHistories().get(recent).getId()).toCharArray();
        double dvivj = countdiff(lev.diff(Arrays.asList(vi), Arrays.asList(vj)));
        double dvi1vj = countdiff(lev.diff(Arrays.asList(vi1), Arrays.asList(vj)));
        double dvi1vi = countdiff(lev.diff(Arrays.asList(vi1), Arrays.asList(vi)));
        return (dvi1vj-dvivj)/dvi1vi;

    }

    private int countdiff(List<String> lev){
        int count = 0;
        for(String s:lev){
            if(!lev.equals("|")){
                count++;
            }
        }
        return count;
    }

    /*public Map<String,Map<Date,double[]>> getcalc(Map<String, List<Date>> stampname){//ノートページの書き込みの間に含まれる編集を所得してどうのこうの？



        Map<String,Map<Date,Hoge>> returnmap=new HashMap<String,Map<Date,Hoge>>();

        //

        for(String name:stampname.keySet()){

            List<Date>listdate=stampname.get(name);
            int listsize=listdate.size();
            ListIterator<Date> iter = listdate.listIterator(listsize);//リストを逆にすることでいい感じに
            Map<Date,Hoge> importancemap = new HashMap<Date,Hoge>();
            while(iter.hasPrevious()){
                Date date = iter.previous();
                //
                Date nextdate=new Date(date.getTime() + 1000 * 60 * 60 * 24 * CDAYS);
                if(listdate.indexOf(date)!=0){//
                    nextdate = new Date(Math.min(nextdate.getTime(),listdate.get(listdate.indexOf(date)-1).getTime() ));
                }



                double termadc=0;
                Map<String,Double> tf = new HashMap<String,Double>();
                Map<String,Double> clonetf = new HashMap<String,Double>();;
                TreeMap<Date,List<String>> editdatemap=new TreeMap<Date,List<String>>();
                Boolean tempcount =false;

                for(Integer edit:getedits(articlehis,date,nextdate)) {
                    tempcount=true;
                    List<String> list = edit.getwords();
                    editdatemap.put(edit.gettime(),list);

                    for(String str:list){
                        double count=1;
                        if(tf.containsKey(str)){
                            count= tf.get(str)+1;
                        }
                        tf.put(str,count);
                        clonetf.put(str,count);
                    }
                }

                if(tempcount){


                    double noteimportance=0;//
                    double anothernoteimportance=0;//

                    for(String word:tf.keySet()){//
                        tf.put(word,(tf.get(word)/termadc));
                        clonetf.put(word,clonetf.get(word)/termadc);
                        noteimportance+=tf.get(word)*idf.get(word);
                        anothernoteimportance+=clonetf.get(word)*idf.get(word);
                        //	System.out.print(word+tf.get(word)*idf.get(word)+" ");
                    }

                    Hoge hoge = new Hoge(noteimportance,anothernoteimportance,kazu);//

                    importancemap.put(date,hoge);
                    //

                    //
                }
            }
            returnmap.put(name,importancemap);

        }

        return returnmap;
    }*/


    public List<Integer> getedits(WikiNote.Histories his,Date date, Date nextdate){//日付指定良くないのでは
        List intlist = new ArrayList();//日付の間に行った編集があるインデックスのリスト
        for(int c =0;c<his.getHistories().size();c++){
            WikiNote.Histories.History current = his.getHistories().get(c);
            if((date.compareTo(current.getDate()))>0){
                if((nextdate.compareTo(current.getDate()))<=0){
                    intlist.add((c));
                }else{
                    break;
                }
            }
        }
        return intlist;
    }

    public List<Integer> getedits(WikiNote.Histories his,Date date, int number){//日付指定良くないのでは
        List intlist = new ArrayList();//日付の間に行った編集があるインデックスのリスト
        for(int c =0;c<his.getHistories().size();c++){
            WikiNote.Histories.History current = his.getHistories().get(c);
            if((date.compareTo(current.getDate()))>0){
                    intlist.add((c));
                if(intlist.size()>number){
                    break;
                }
            }
        }
        return intlist;
    }



    class Edit{
        List<String> words;
        Date date;
        public List<String> getwords(){
            return words;
        }
        public Date gettime(){
            return date;
        }
    }

    class Hoge{
        double noteimportance;
        double anothernoteimportance;
        public Hoge(double noteimportance,double anothernoteimportance){
            this.noteimportance=noteimportance;
            this.anothernoteimportance=anothernoteimportance;
        }
    }/*
article.setAttribute("title", title);
        article.setAttribute("P", Long.toString(DeDAYS));
        article.setAttribute("T", Long.toString(CDAYS));
        document.appendChild(article);
        for(Map.Entry<String,Map<Date, double[][]>> eset:importancemap.entrySet()){
        if(eset.getValue().size()!=0){
        double addimportance=0;
        double delimportance=0;
        int addcount=0;
        int delcount=0;
        Element editor = document.createElement("editor");
        article.appendChild(editor);
        for(Map.Entry<Date,double[][]> dateset:eset.getValue().entrySet()){
        Element edit = document.createElement("edit");
        edit.setAttribute("date",dateset.getKey().toString());
        if(dateset.getValue()[0]!=null){
        edit.setAttribute("�ǉ��e���x",Double.toString(dateset.getValue()[0][2]));
        addimportance+=dateset.getValue()[0][2];
        addcount++;
        }
        if(dateset.getValue()[1]!=null){
        edit.setAttribute("�폜�e���x",Double.toString(dateset.getValue()[1][2]));
        delimportance+=dateset.getValue()[1][2];
        delcount++;
        }
        editor.appendChild(edit);
        }
        double importance= 0;
        if(addcount!=0){
        importance+=addimportance/addcount;
        }
        if(delimportance!=0){
        importance+=delimportance/delcount;
        }
        editor.setAttribute("importance",Double.toString(importance));
        editor.setAttribute("name",eset.getKey());
        }*/


    /*File[] filetypelist =  new File("./"+articlename).listFiles();
        for(File file:filetypelist){
            try {
                if(date.compareTo(sdf.parse(file.getName()))>0&&nextdate.compareTo(sdf.parse(file.getName()))<=0){//�Ԃ��擾

                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }*/


}
