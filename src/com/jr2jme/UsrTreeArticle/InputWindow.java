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
        InputWindow inter = new InputWindow();
        String personname = "ごまふあざ";
        SqlOp sqlOp = new SqlOp(1);
        System.out.println(sqlOp.termcheck(8,"家"));
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



        Feature editfe = new Feature(ArticleEditorRanker.testuserarticle(personname));//編集者の特徴ベクトル的なもの
        Feature editfecat = new Feature(ArticleEditorRanker.testuserarticlecat(personname));//カテゴリの出現頻度の逆頻度をかける
        Feature editfe1 = new Feature(ArticleEditorRanker.testuserarticlecat(personname));//
        SqlOp sq = new SqlOp(1);
        sq.insert_person(personname);
        int id=sq.get_personid(personname);
//おどろくほど進まない
        int x = 0;
        List<String> list = new ArrayList<String>();
        Set<Integer> intset =inter.genrand(20);
        List<Map.Entry>hoge =MaptoList.valueSort(editfe.getTfidf());
        for(Map.Entry<String,Double> entry:hoge){
            if(intset.contains(hoge.indexOf(entry))) {
                if(!sq.termcheck(id,entry.getKey())){
                    sq.insert_term(entry.getKey(),id);
                }
            //sq.insert_termpropose(entry.getKey(),id,c,1);
                System.out.println(entry.getKey());
                x++;
                list.add(entry.getKey());
            //System.out.println("<li class=\"ui-state-default\">" + entry.getKey() + "<span>" + x + "</span>");
            }
            if(x>20){
                break;
            }
        }
        //sq.insert_batch();
        int c=0;
        for(Map.Entry<String,Double> entry:MaptoList.valueSort(editfe.getTfidf())){
            if(list.contains(entry.getKey())){
                sq.insert_termpropose(entry.getKey(),id,c,1);
                c++;
            }

        }
        for(Map.Entry<String,Double> entry:MaptoList.valueSort(editfecat.getTfidf())){
            if(list.contains(entry.getKey())){
                sq.insert_termpropose(entry.getKey(),id,c,2);
                c++;
            }

        }
        sq.insert_batch();

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
        //System.out.println(inter.result("ごまふあざ"));

		System.exit(0);
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

    public double result(String editor){
        SqlOp sqop = new SqlOp(1);
        int personid =sqop.get_personid(editor);

        ResultSet rs = sqop.get_termrankresult(personid);
        ResultSet rsexp = sqop.get_termranktype(personid,1);
        List<String> rank = new ArrayList<String>();
        Map<String,Integer> blackrx = new HashMap<String, Integer>();
        try {
            while(rs.next()){
                rank.add(rs.getString("term"));
                System.out.println(rs.getString("term"));
            }
            while(rsexp.next()){
                blackrx.put(rsexp.getString("term"),rsexp.getInt("rank"));
                System.out.println(rsexp.getString("term"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        int temp = 0;
        for(Map.Entry<String,Integer> entry:blackrx.entrySet()){
            System.out.println(entry.getValue()+":"+rank.indexOf((entry.getKey())));
            temp+=Math.pow((entry.getValue()-(rank.indexOf(entry.getKey())+1)),2);
        }
        return 1-(6*temp/(Math.pow(rank.size(),3)-rank.size()));

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