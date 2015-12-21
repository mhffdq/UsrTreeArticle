package com.jr2jme.UsrTreeArticle;

import java.io.*;
import java.net.URLEncoder;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.jr2jme.UsrTreeArticle.Util.Output;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class WikiNote {
    static Integer uid=null;
    //隠しカテゴリを抜きたい
	public WikiUsernote getnotelist(String usrname){

		return  notelist(usrname,"");
	}
    private WikiUsernote wikinoteope(String usrname,String con){
        final String TARGET_HOST = "https://ja.wikipedia.org/w/api.php?action=query&list=usercontribs&format=xml&uclimit=500&rawcontinue&ucuser=";
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        Document root = null;
        String url = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        try {
            url=URLEncoder.encode(usrname,"utf-8")+con;
            System.out.println(TARGET_HOST+url);//%で書かれた奴にエンコード
            root = builder.parse(TARGET_HOST+url);

        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        NodeList gchildren = root.getElementsByTagName("item");

        Set<String> notemap = new HashSet<String>();

        for(int c = 0;c<gchildren.getLength();c++){
            String title = gchildren.item(c).getAttributes().getNamedItem("title").getNodeValue();

            if(title.startsWith("ノート:")){
                if(!title.contains("/")&&!title.contains("\\\\")&&!title.contains(",_")) {
                    notemap.add(title.substring(4));
                    getcategoriesarticle(title.substring(4));
                }
            }
            if(uid==null){
                uid = Integer.parseInt(gchildren.item(c).getAttributes().getNamedItem("userid").getNodeValue());
            }

        }
        Node connode = root.getElementsByTagName("usercontribs").item(0).getAttributes().getNamedItem("uccontinue");
        String nextcon="";
        if(connode!=null){
            nextcon ="&uccontinue="+connode.getNodeValue();
        }

        if(nextcon.equals("")){

            return new WikiUsernote(usrname,uid,notemap);
        }else {
            WikiUsernote re = notelist(usrname, nextcon);
            re.getNotemap().addAll(notemap);
            return  new WikiUsernote(usrname,uid,re.getNotemap());
        }

    }

	private  WikiUsernote notelist(String usrname,String con){
        File dirfile = new File("Note");
        if(!dirfile.exists()){
            dirfile.mkdir();
        }

        File notefile = new File("./Note/"+usrname);
        WikiUsernote wikiUsernote = null;

        if(notefile.exists()){
            try {
                FileInputStream input = new FileInputStream(notefile);
                ObjectInputStream inObject = new ObjectInputStream(input);

                wikiUsernote= (WikiUsernote) inObject.readObject();
                inObject.close();
                input.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }


        }else {
            wikiUsernote= wikinoteope(usrname,con);
            Output.outputbinary(notefile,wikiUsernote);

        }
        return wikiUsernote;

	}

	public Set<String> getcategoriesarticle(String catename){//編集した記事のカテゴリーを取得(記事専用にする予定)
        File dirfile;
        File catefile;
        Set<String> categories = null;
        dirfile = new File("ArticleCategory");
        if (!dirfile.exists()) {
            dirfile.mkdir();
        }

        catefile = new File("./ArticleCategory/" + catename);



        if(catefile.exists()){
            try {
                FileInputStream input = new FileInputStream(catefile);
                ObjectInputStream inObject = new ObjectInputStream(input);

                categories= (Set<String>) inObject.readObject();
                inObject.close();
                input.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }


        }else {
            categories= getcatope(catename);
            Output.outputbinary(catefile,categories);
        }
        return categories;
	}

    //カテゴリの上にある度合を保存

    public Category getcategoriescat(String catename){//編集した記事のカテゴリーを取得(カテゴリ専用)
        File dirfile;
        File catefile;
        Category categories = null;
        dirfile = new File("Category");
        if (!dirfile.exists()) {
            dirfile.mkdir();
        }

        catefile = new File("./Category/" + catename.substring(9));



        if(catefile.exists()){
            try {
                FileInputStream input = new FileInputStream(catefile);
                ObjectInputStream inObject = new ObjectInputStream(input);

                categories= (Category) inObject.readObject();
                inObject.close();
                input.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }


        }else {
            categories= makecate(catename);
            Output.outputbinary(catefile,categories);
        }
        return categories;
    }

    private Category makecate(String catename){
        return new Category(getcatope(catename),opecatmenber(catename,""));//(カテゴリが属してるカテゴリ,カテゴリに含まれるカテゴリ)
    }

    private Set<String> getcatope(String catename){//カテゴリが属している
        System.out.println(catename);
        final String TARGET_HOST = "https://ja.wikipedia.org/w/api.php?action=query&prop=categories&format=xml&climit=500&titles=";
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        Document root = null;
        String url = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        try {
            url=URLEncoder.encode(catename,"utf-8");
            root = builder.parse(TARGET_HOST+url);

        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Set<String> categories = new HashSet<String>();
        NodeList gchildren = root.getElementsByTagName("cl");
        for(int c = 0;c<gchildren.getLength();c++){
            String title = gchildren.item(c).getAttributes().getNamedItem("title").getNodeValue();
            if(!title.equals("Category:隠しカテゴリ")&&!title.contains("\\\\")&&!title.contains("/")&&!title.contains(",_")) {
                categories.add(title);
            }
        }
        return categories;
    }

    private void catope(Set<String> names,Map<String,Integer> categories,Integer depth){//上に行く
        Set<String> catna = new HashSet<String>();
        for(String name:names){//記事名ごとに

            Set<String> cats ;
            if(name.startsWith("Category:")) {
                cats = getcategoriescat(name).getAbovecat();//カテゴリ取得
            }else{
                cats=getcategoriesarticle(name);
            }
            cats.removeAll(categories.keySet());
            catna.addAll(cats);//他の記事で取ったカテゴリと合わせる．
            for(String c:cats){
                categories.put(c,depth);//返り値用のに追加
            }

        }
        if(depth<2&&!catna.isEmpty()) {
            catope(catna, categories, depth + 1);
        }
    }

    public int getbunyacount(String cat){//分野名っぽいカテゴリを取得
        int count = 1;//下に同じ名前を含んだカテゴリがあるかどうかを確かめる
        for(String bcat:getcategoriescat("Category:"+cat).getBelowcat()){
            if(bcat.contains(cat)){
              count++;
            }
        }
        return count;
    }//どうしようなあほんま
    //理想 だんだん 細かくなっていく感じ
    //一つ見つけたら下げる？ひとつにしぼるのはどうなのか


    public Map<String,Integer> catshitaweight(){//深いほど高い重み 同じ分野のカテゴリを持つてきな関数はgetbunyacountを使う
        Map<String,Integer> weightmap = new HashMap<String, Integer>(80000);
        File catweightfile = new File("./Catshitaweight");
        if(catweightfile.exists()) {
            try {
                FileInputStream input = new FileInputStream(catweightfile);
                ObjectInputStream inObject = new ObjectInputStream(input);
                weightmap= (Map<String,Integer>) inObject.readObject();
                inObject.close();
                input.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }else{
            Set<String> set = new HashSet<String>();
            set.add("Category:主要カテゴリ");
            catshitaweightopwidth(weightmap, set, 0);//widthで幅優先 depthにすると深さ優先 一番近いのを深さとしているから幅優先
            Output.outputbinary(catweightfile,weightmap);//どうでもいいことを考えすぎているのかな
        }
        return weightmap;
    }



    private void catshitaweightopdepth(Map<String,Integer>weightmap,String category,int depthstart){//カテゴリを下にさかのぼる(深さ優先)
        int count = 0;
        if(depthstart<5){
            if(!weightmap.containsKey(category)&&!category.equals("Category:隠しカテゴリ")&&!category.contains("\\\\")&&!category.contains("/")&&!category.contains(",_")) {
                for(String next:getcategoriescat(category).getBelowcat()){
                        catshitaweightopdepth(weightmap,next,depthstart+1);
                        if(next.contains(category.substring(9))&&!next.contains("\\\\")&&!next.contains("/")&&!next.contains(",_")){
                            count++;
                        }
                }

            }
        }if(count>4) {
            weightmap.put(category.substring(9),depthstart);//Category:って部分抜きで記憶
        }
    }

    private void catshitaweightopwidth(Map<String,Integer>weightmap,Set<String> categories,int depthstart){//カテゴリを下にさかのぼる(幅優先)
        Set<String> nextset = new HashSet<String>();
        for(String cate:categories){
            if(!cate.equals("Category:隠しカテゴリ")&&!cate.contains("\\\\")&&!cate.contains("/")&&!cate.contains(",_")&&!cate.contains("*")&&!cate.contains(",_")&&!cate.contains("?")) {
                Set<String> tempset = getcategoriescat(cate).getBelowcat();
                weightmap.put(cate.substring(9), depthstart);
                nextset.addAll(tempset);
            }
        }
        nextset.removeAll(weightmap.keySet());
        if(depthstart<6) {
            catshitaweightopwidth(weightmap, nextset, depthstart + 1);
        }
    }

    public Map<String,Integer> widcategories(Set<String> names){//対象の記事からの深さ
        Map<String,Integer> categories =new HashMap<String, Integer>();
        catope(names,categories,0);
        return categories;
    }

    public Integer catrel(Map<String,Integer> rel1,Map<String,Integer> rel2){
        Integer res = 0;
        Map<String,Integer> rel = new HashMap<String, Integer>();
        for(Map.Entry<String,Integer> ent1:rel1.entrySet()){
            if(rel2.containsKey(ent1.getKey())){
                rel.put(ent1.getKey(),(2-ent1.getValue())*(2-rel2.get(ent1.getKey())));
                res+=(2-ent1.getValue())*(2-rel2.get(ent1.getKey()));
            }
        }
        return res;
    }

    public synchronized void sleep(long msec)
    {	//指定ミリ秒実行を止めるメソッド
        try
        {
            wait(msec);
        }catch(InterruptedException e){}
    }



    public Set<String> opecatmenber(String catname,String con){//カテゴリに含まれるカテゴリ
        final String TARGET_HOST = "https://ja.wikipedia.org/w/api.php?action=query&list=categorymembers&format=xml&cmlimit=500&rawcontinue&cmtitle=";
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        Document root = null;
        String url = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        try {
            url=URLEncoder.encode(catname,"utf-8")+con;
            root = builder.parse(TARGET_HOST+url);

        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        NodeList gchildren = root.getElementsByTagName("cm");

        Set<String> undercat = new HashSet<String>();
        for(int c = 0;c<gchildren.getLength();c++){
            String title = gchildren.item(c).getAttributes().getNamedItem("title").getNodeValue();
            String ns = gchildren.item(c).getAttributes().getNamedItem("ns").getNodeValue();//
            if(ns.equals("14")){//カテゴリだったら
                //なんかする
                undercat.add(title);
            }

        }
        Node connode = root.getElementsByTagName("categorymembers").item(0).getAttributes().getNamedItem("cmcontinue");
        String nextcon="";
        if(connode!=null){
            nextcon ="&cmcontinue="+connode.getNodeValue();
        }

        if(nextcon.equals("")){

            return undercat;
        }else {
            undercat.addAll(opecatmenber(catname, nextcon));
            return  undercat;
        }
    }

}
