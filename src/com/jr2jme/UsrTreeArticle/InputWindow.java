package com.jr2jme.UsrTreeArticle;


import com.jr2jme.UsrTreeArticle.Util.MaptoList;
import com.jr2jme.UsrTreeArticle.Util.Feature;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class InputWindow {

    GraphStructure graph=null;
    SqlOp sq = null;
	public static void main(String[] args) throws IOException {
        //Analyzer an = new Analyzer();

        Set<String> editorlist = new HashSet<String>();

        editorlist.add("みしまるもも");
        editorlist.add("ごまふあざ");
        editorlist.add("Northface");
        editorlist.add("Naruhodou");
        editorlist.add("如月の弥");
        editorlist.add("グローバライ");
        editorlist.add("泉州大夫惟宗朝臣");
        editorlist.add("矢刺幹人");
        editorlist.add("Bokemiann");
        editorlist.add("青鬼よし");
        //prop(editorlist);
        //inter.test();
        //何を迷っている
        //System.out.println(ArticleEditorRanker.articleredrank("UNIX","ごまふあざ"));//こっからどうするか
        //System.out.println(ArticleEditorRanker.editoreditorrank("あるうぃんす","ゴンベイ"));
        //System.out.println(ArticleEditorRanker.artartdrank("江戸しぐさ","Linux"));//こっからどうするか
        //一発逆転の何か
//なにもきにしたきなあぢｆｊｊ何も思いつかないｆｄｆｄｆｄｆｄｆｄｆｄｆｄｆｄｆｄｆｄｆｄｆｄｆｄｆｄｆｄｆｄｆｄｆｄｆｄｆｄｆｄｆｄｆｆｆｆｆｆｆｆｆｆｆｆｆｆｆｆｆｆｆｆｆｆｆｆｆｆｆｆｆｆｆｆｆｆｆｆｆｆｆｆｆｆｆｆｆｆｆｆｆｆｆｆｆｆｆｆ
        /*WikiNote wikiNote = new WikiNote();
        for(Map.Entry<String,Integer> en:wikiNote.catshitaweight().entrySet()){
            System.out.println(en);
        }*/
//明日は一通り試してみるか
//単語が登録されているかどうかをみて登録する仕組み
        //上から20個とランダムに下の方も





        //考えてるだけじゃわかる兄から何かやってみる
        //何をやったらいいかな
        //なんか目に見える部分
        //表示を考える

        //何がしたいのか考える
//何したらいいかな
//なんで他の人は何をしたらいいのかわかるのか 全然わからない
//悩んでも悩んでも

        //明日，誰と議論しているかわかるようにkoreka

        //改良 どうなったら改良になるのか

        //編集している記事のカテゴリが近いかどうか求めることも可能
        //いろいろやってみはした．
        //足りないのは実験とかその辺の重要な部分とか

//何ができたらいいのかな
        //どうつくればいいかな
        //人の

        //WikiNote wikinote = new WikiNote();

        /*WikiNote wikinote = new WikiNote();
        Set<String> hoge = new HashSet<String>();
        hoge.add("イギリス");
        wikinote.widcategories(hoge);
        for(Map.Entry<String,Integer> name:catdepth.entrySet()){
            System.out.println(name);
        }

        Set<String> hoge2 = new HashSet<String>();
        hoge2.add("中華人民共和国");
        wikinote.widcategories(hoge2);
        for(Map.Entry<String,Integer> name:catdepth2.entrySet()){
            System.out.println(name);
        }

        System.out.println(wikinote.catrel(catdepth,catdepth2));*/
        //inter.mainstream();
        for(int c=0;c<8;c++) {
            System.out.println("最終"+results(editorlist,c));
        }

		System.exit(0);
	}

    public static void prop(Set<String> editorset){
        InputWindow inter = new InputWindow();
        for(String editor:editorset){
            Feature editfe = new Feature(ArticleEditorRanker.testuserarticle(editor));//編集者の特徴ベクトル的なもの
            List<Feature> editall = new ArrayList<Feature>();
            for(Map<String,Double> ma :ArticleEditorRanker.testuserarticleall(editor)){//本当はArticleRankerのほうでやったほうがいいけどまあいいや
                editall.add(new Feature(ma));
            }
            SqlOp sq = new SqlOp(1);
            try {
                if(!sq.get_person(editor).next()) {
                    sq.insert_person(editor);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            int id=sq.get_personid(editor);
            int x = 0;
            Set<String> list = new HashSet<String>();
            Set<Integer> intset =inter.genrand(20);
            List<Map.Entry>hoge =MaptoList.valueSort(editfe.getTfidf());
            if(!sq.termcheck(id)) {
                for (Map.Entry<String, Double> entry : hoge) {
                    if (intset.contains(hoge.indexOf(entry))) {
                        sq.insert_term(entry.getKey(), id);
                        System.out.println(entry.getKey());
                        x++;
                        list.add(entry.getKey());
                        //System.out.println("<li class=\"ui-state-default\">" + entry.getKey() + "<span>" + x + "</span>");
                    }
                    if (x > 20) {
                        break;
                    }
                }
            }else{
                list=sq.get_term(id);
            }
            //sq.insert_batch();
            for(Feature fe:editall){
                try {
                    if(!sq.get_termranktype(id,editall.indexOf(fe)).next()) {
                        List<Map.Entry> listma = MaptoList.valueSort((fe.getTfidf()));
                        int c = 1;
                        for (Map.Entry<String, Double> entry : listma) {
                            if (list.contains(entry.getKey())) {
                                sq.insert_termpropose(entry.getKey(), id, c, editall.indexOf(fe));
                                c++;
                            }
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }



            sq.insert_batch();
        }
    }

    //やりたいのは，nanika
    //もっと簡単に考える
    //簡単とは何か
    public Set<Integer> genrand(int x){//数字の数だけ生成（0から99）X個
        Set<Integer> randint = new HashSet<Integer>(x);
        Random random = new Random();
        int c=0;
        while(c<x){
            int temp = random.nextInt(100);
            if(randint.add(temp)){
                c++;
            }
        }
        return randint;
    }


    public void test(){
        File file = new File("useronly.txt");
        FileReader fileleader = null;
        try {
            fileleader = new FileReader(file);
            BufferedReader br = new BufferedReader(fileleader);

            String line;
            while((line=br.readLine())!=null) {
                ArticleEditorRanker.testuserarticle(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public double result(String editor,int c){
        SqlOp sqop = new SqlOp(1);
        int personid =sqop.get_personid(editor);

        ResultSet rs = sqop.get_termrankresult(personid);
        ResultSet rsexp = sqop.get_termranktype(personid,c);
        List<String> rank = new ArrayList<String>();
        Map<String,Integer> blackrx = new HashMap<String, Integer>();
        try {
            while(rs.next()){
                rank.add(rs.getString("term"));
                //System.out.println(rs.getString("term"));
            }
            while(rsexp.next()){
                blackrx.put(rsexp.getString("term"),rsexp.getInt("rank"));
                //System.out.println(rsexp.getString("term"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        int temp = 0;
        for(Map.Entry<String,Integer> entry:blackrx.entrySet()){
            //System.out.println(entry.getKey()+entry.getValue()+":"+(rank.indexOf(entry.getKey())+1));
            temp+=Math.pow((entry.getValue()-(rank.indexOf(entry.getKey())+1)),2);
        }
        return 1-(6*temp/(Math.pow(rank.size(),3)-rank.size()));

    }

    public static double results(Set<String> editors,int c){
        SqlOp sqop = new SqlOp(1);
        int temp = 0;
        int sumrank=0;
        for(String editor:editors) {
            System.out.println(editor);
            int personid = sqop.get_personid(editor);

            ResultSet rs = sqop.get_termrankresult(personid);
            ResultSet rsexp = sqop.get_termranktype(personid, c);
            List<String> rank = new ArrayList<String>();
            Map<String, Integer> blackrx = new HashMap<String, Integer>();
            try {

                while (rs.next()) {
                    rank.add(rs.getString("term"));
                    //System.out.println(rs.getString("term"));
                }
                while (rsexp.next()) {
                    blackrx.put(rsexp.getString("term"), rsexp.getInt("rank"));
                    //System.out.println(rsexp.getString("term"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            int pretemp = 0;
            for (Map.Entry<String, Integer> entry : blackrx.entrySet()) {
                //System.out.println(entry.getKey()+entry.getValue()+":"+(rank.indexOf(entry.getKey())+1));
                pretemp += Math.pow((entry.getValue() - (rank.indexOf(entry.getKey()) + 1)), 2);
            }
            System.out.println(1-(6*pretemp/(Math.pow(rank.size(),3)-rank.size())));
            temp+=pretemp;
            sumrank+=rank.size();
        }
        return 1-(6*temp/(Math.pow(sumrank,3)-sumrank));

    }




    private Map<String,Nodecalc> maketreemap(Set<String> note,String usrname){
        Map<String,Nodecalc> artreemap = new HashMap<String, Nodecalc>();
        for(String name:note) {
            System.out.println(name);
            MakeTree tree = new MakeTree(name, usrname,graph,sq);//記事名と編集者名を入れると編集者が書いた部分を形態素解析して求めたtfidfが帰ってくる
            artreemap.putAll(tree.getNodemap());//少し変わってグラフへのノードの追加もお行っている
        }
        return artreemap;
    }



    private void calcrel(Map<String,Nodecalc>artreemap){//類似度計算?

        Set<String> strongterm = new HashSet<String>();

        for(Node tate:graph.getNodesset()){//entryは記事名，tfidf
            for (Node yoko:graph.getNodesset()){
                String[] strarray= {tate.getId(),yoko.getId()};//順列
                String[] gyakuarray = {yoko.getId(),tate.getId()};//確認用逆
                if(!strongterm.contains(Arrays.toString(gyakuarray))&&tate.getId()!=yoko.getId()) {
                    strongterm.add(Arrays.toString(strarray));
                    double ue = 0;
                    Map<String,Double> termrel = new HashMap<String, Double>(yoko.getTermweight().size());
                    double dor=0;
                    for (String yokokey : yoko.getTermweight().keySet()) {
                        if (tate.getTermweight().containsKey(yokokey)) {
                            double kagi = yoko.getTermweight().get(yokokey) *tate.getTermweight().get(yokokey);
                            ue +=  kagi;
                            termrel.put(yokokey,kagi);
                            dor+=Math.pow(kagi,2);
                        }
                    }

                    List<Map.Entry> edgerel = MaptoList.valueSort(termrel);
                    String edgelabel = "";
                    int i =0;
                    for(Map.Entry<String,Double>ssss :edgerel){
                        edgelabel+=ssss.getKey();
                        i++;
                        if(i>3){
                            break;
                        }
                    }

                    Feature tfidf = new Feature(termrel,Math.sqrt(dor));

                    if (artreemap.get(tate.getId()).getNorm() != 0 && artreemap.get(yoko.getId()).getNorm() != 0) {
                        if(artreemap.get(tate.getId()).getIfrule().equals(artreemap.get(yoko.getId()).getIfrule())) {
                            double cos = ue / (artreemap.get(tate.getId()).getNorm() * artreemap.get(yoko.getId()).getNorm());//似ているほど大きくなる
                            graph.makeedge(edgelabel, strarray[0] + strarray[1], strarray[0], strarray[1], Double.valueOf(cos).floatValue(),tfidf );

                        }else{

                        }
                    }
                    //label



                }
            }
        }

    }

    private void graphedit(){


        /*for(Set<Node> settnode:DBSCANNode.dbscan(graph.getNodes())){
            graph.mergenodes(settnode);
        }*/
        Propose1.proposemethod(graph).insertsql(sq);

    }

    public void mainalluser(){//記事で議論したユーザ全てを使って何かする用の関数
        InputStreamReader is = new InputStreamReader(System.in);       //（１）
        BufferedReader br = new BufferedReader(is);                    //（２）
        System.out.println("環境を入力してください.development : 1, prodution : 2");

        String article = null;
        Integer env = null;
        try {
            env = Integer.parseInt(br.readLine());
            sq = new SqlOp( env );
            System.out.println("記事名を入力してください");
            article = br.readLine();
            System.out.println(article);
        } catch (IOException e) {
            e.printStackTrace();
        }

        getallediteduser(article,env);//渡す


    }

    public void getallediteduser(String article,Integer env){//渡された
        com.jr2jme.UsrTreeArticle.Util.WikiNote wikiNote = new com.jr2jme.UsrTreeArticle.Util.WikiNote();
        com.jr2jme.UsrTreeArticle.Util.WikiNote.Histories editors = wikiNote.gethistory(article,true);//あるノートページを編集した人全て入手
        for(com.jr2jme.UsrTreeArticle.Util.WikiNote.Histories.History editor:editors.getHistories()){
            mainact(editor.getEditor(),env);//とりあえず全ての人のグラフを作る
        }
    }


    public void mainstream(){
        InputStreamReader is = new InputStreamReader(System.in);       //（１）
        BufferedReader br = new BufferedReader(is);                    //（２）

        System.out.println("環境を入力してください.development : 1, prodution : 2");

        String usrname = null;
        Integer env = null;
        try {
            env = Integer.parseInt(br.readLine());
            sq = new SqlOp( env );
            System.out.println("ユーザー名を入力してください");
            usrname = br.readLine();
            System.out.println(usrname);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mainact(usrname,env);


    }

    private void mainact(String usrname,Integer env){
        WikiNote wikinote = new WikiNote();
        WikiUsernote wikiusernotet=wikinote.getnotelist(usrname);//useridと編集したノートページの集合が帰ってくる

        graph = new GraphStructure(usrname,wikiusernotet.getUserid());


        Map<String,Nodecalc> artreemap = maketreemap(wikiusernotet.getNotemap(), usrname);//記事名とtfとか見出しとか
        sq.insert_batch();
        calcrel(artreemap);
        graph.insertsql(sq);
        sq = new SqlOp(env);
        graphedit();

    }



	//jawiki-latest-category.sql.gz                      03-Nov-2015 06:15             3182856
	//カテゴリ日本の文学において重要なのは文学

		
	

}