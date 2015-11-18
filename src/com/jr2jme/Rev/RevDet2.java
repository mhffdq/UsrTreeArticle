package com.jr2jme.Rev;

import com.jr2jme.st.UnBzip2;


import org.atilika.kuromoji.Token;
import org.atilika.kuromoji.Tokenizer;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

//import org.atilika.kuromoji.Token;

//import net.java.sen.dictionary.Token;

//import org.atilika.kuromoji.Token;


public class RevDet2 {//Wikipediaのログから差分をとって誰がどこを書いたかを保存するもの リバート対応
    //private static JacksonDBCollection<WhoWrite,String> coll2;
    //private static JacksonDBCollection<InsertedTerms,String> coll3;//insert
    //private static JacksonDBCollection<DeletedTerms,String> coll4;//del&
    //private String wikititle = null;//タイトル
    public RevDet2(String[] args){
       // Set<String> aiming=fileRead("input.txt");

        Set<String> AimingArticle = fileRead("input.txt");
        XMLStreamReader reader = null;
        BufferedInputStream stream = null;
        XMLInputFactory factory = XMLInputFactory.newInstance();
        try {
            reader = factory.createXMLStreamReader(UnBzip2.unbzip2(args[0]));
            // 4. イベントループ
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Boolean inrev = false;
            Boolean incon = false;
            String title = "";
            String name = "";
            String text;

            int version = 0;
            Boolean isAimingArticle = false;
            assert reader != null;
            List<String> prev_text = new ArrayList<String>();
            Map<String,List<DelPos>> delmap = new HashMap<String, List<DelPos>>();
            List<List<String>> difflist = new ArrayList<List<String>>();
            WhoWrite prevwrite=new WhoWrite();
            List<Integer> editlist=new ArrayList<Integer>();
            Boolean endflag=false;
            while(reader.hasNext()) {
                // 4.1 次のイベントを取得
                int eventType = reader.next();
                // 4.2 イベントが要素の開始であれば、名前を出力する
                if (eventType == XMLStreamReader.START_ELEMENT) {
                    if ("title".equals(reader.getName().getLocalPart())) {
                        //System.out.println(reader.getElementText());
                        title = reader.getElementText();
                        if(endflag){
                            break;
                        }
                        //System.out.println(title);
                        if (AimingArticle.contains(title)) {//title.equals("エジプト")
                            //logger.config(title);
                            version = 0;
                            isAimingArticle = true;

                            prev_text = new ArrayList<String>();
                            delmap = new HashMap<String, List<DelPos>>();
                            difflist = new ArrayList<List<String>>();
                            editlist=new ArrayList<Integer>();
                            endflag=true;

                        } else {
                            //System.out.println(reader.getElementText());
                            isAimingArticle = false;
                        }

                    }
                    if (isAimingArticle) {
                        File dir = new File(title);//
                        if(!dir.exists()){
                            dir.mkdirs();
                        }
                        File ed = new File(title+"/ed");//
                        if(!ed.exists()){
                            ed.mkdirs();
                        }
                        File tex = new File(title+"/tex");//
                        if(!tex.exists()){
                            tex.mkdirs();
                        }

                        Date date = null;
                        if ("revision".equals(reader.getName().getLocalPart())) {
                            inrev = true;
                        }
                        if ("id".equals(reader.getName().getLocalPart())) {
                            if (inrev && !incon) {
                                //id = Integer.valueOf(reader.getElementText());
                            }
                        }
                        if ("comment".equals(reader.getName().getLocalPart())) {
                            //comment = reader.getElementText();

                        }
                        if ("timestamp".equals(reader.getName().getLocalPart())) {
                            System.out.println(reader.getElementText());
                            try {
                                date = sdf.parse(reader.getElementText());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        if ("ip".equals(reader.getName().getLocalPart())) {
                            //System.out.println(reader.getElementText());
                            name = reader.getElementText();
                            incon = true;
                        }
                        if ("username".equals(reader.getName().getLocalPart())) {
                            //System.out.println(reader.getElementText());
                            name = reader.getElementText();
                            incon = true;
                        }
                        if ("text".equals(reader.getName().getLocalPart())) {


                            version++;
                            text=reader.getElementText();
                            //System.out.println(text);
                            //List<Future<List<String>>> futurelist = new ArrayList<Future<List<String>>>(NUMBER+1);
                            List<String> current_text=kaisekikuro(text);//形態素解析
                            Levenshtein3 d = new Levenshtein3();
                            List<String> diff = d.diff(prev_text, current_text);//差分（Wu）
                            List<InsTerm> instermlist = new ArrayList<InsTerm>();//記事誰がどこを書いたか
                            WhoWrite whowrite = new WhoWrite();//なんだっけ？たぶん
                            Map<Integer,Integer> editmap = new HashMap<Integer, Integer>();//リバートした距離の記録
                            Map<Integer,List<DelPos>> delposmap = new HashMap<Integer, List<DelPos>>();//リバートしたときにリストから消す
                            diffroop(diff,name,whowrite,current_text,version,instermlist,editlist,prevwrite,editmap,prev_text,delmap,difflist,delposmap);//いろいろやる
                            prev_text=current_text;//差分とかようひとつまえ
                            prevwrite = whowrite;//だれがどこを書いたか？
                            if(version>22){
                                difflist.set(version-22,new LinkedList<String>());//メモリ削減？のため
                            }

                            for(Map.Entry<Integer,Integer> entry:editmap.entrySet()){//元にもどした単語数が一緒のときリバート
                                if(version==707){
                                    System.out.println((entry.getValue()));
                                    System.out.println(editlist.get(entry.getKey()-1));
                                }
                                if(entry.getValue().equals(editlist.get(entry.getKey() - 1))){
                                    if(delposmap.containsKey(entry.getKey())) {//削除を追加していたら
                                        List<DelPos> lisdel = delposmap.get(entry.getKey());
                                        for (DelPos del : lisdel) {//リバートだったら削除のマップから消す
                                            delmap.get(del.getTerm()).remove(del);
                                            //term.revertterm(del);
                                            //whowrite.revert(term.getPos(), delpos.getOriversion(), delpos.getDelededitor());
                                        }
                                    }
                                }
                            }
                            System.out.println(title + " : " + version);
                            //この辺を書いていく
                            FileOutputStream outed = new FileOutputStream("./"+title+"/ed/"+date.toString());
                            FileOutputStream outtex = new FileOutputStream("./"+title+"/tex/"+date.toString());
                            PrintWriter writered = new PrintWriter(outed);
                            PrintWriter writertex = new PrintWriter(outed);
                            for(String editor:whowrite.getEditorList()){
                                writered.println(editor);
                            }
                            for(String sssss:whowrite.getWikitext()){
                                writertex.println(sssss);
                            }

                        }

                    }
                }
            }
        } catch (IOException ex) {
            System.err.println(ex + " が見つかりません");
        } catch (XMLStreamException ex) {
            System.err.println(ex + " の読み込みに失敗しました");
        } finally {
            // 5. パーサ、ストリームのクローズ
            if (reader != null) {
                try {
                    reader.close();
                } catch (XMLStreamException ex) {
                    ex.printStackTrace();
                }
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        }



    }

    public static void diffroop(List<String> diff, String name,WhoWrite whowrite,List<String> current_text,int version,List<InsTerm> instermlist,List<Integer>editlist,WhoWrite prevwrite,Map<Integer,Integer>editmap,List<String> prev_text,Map<String,List<DelPos>>delmap,List<List<String>> difflist,Map<Integer,List<DelPos>> delposmap){//差分に対する処理をとりあえずすべて突っ込んだもの
        int a=0;
        int b=0;
        int edit=0;//編集距離
        int tmp=-1;
        List<String> yoyaku = new ArrayList<String>();
        List<String> yoyakued = new ArrayList<String>();
        List<Integer> yoyakuver = new ArrayList<Integer>();
        difflist.add(diff);
        for (String type : diff) {
            if (type.equals("+")) {
                whowrite.add(current_text.get(a),name,version);//誰がどこを書いたか あってるはず
                instermlist.add(new InsTerm(current_text.get(a), a, name));//追加された単語と位置のリスト
                delrevdet(new InsTerm(current_text.get(a), a, name), delmap, version, difflist, editmap, whowrite,delposmap);//追加が差し戻しかどうか
                edit++;
                a++;
            } else if (type.equals("-")) {
                yoyakued.add(prevwrite.getEditorList().get(b));//下待ち
                yoyakuver.add(prevwrite.getVerlist().get(b));
                if(version-prevwrite.getVerlist().get(b)<21) {//削除は絶対差し戻し
                    int cc = 1;
                    if (editmap.containsKey(prevwrite.getVerlist().get(b))) {
                        cc = editmap.get(prevwrite.getVerlist().get(b)) + 1;
                    }
                    editmap.put(prevwrite.getVerlist().get(b), cc);
                }
                yoyaku.add(prev_text.get(b));
                edit++;
                b++;
            } else if (type.equals("|")) {
                for (int p = 0; p < yoyaku.size(); p++) {//これ，何回もリスト追加することになってる バグ 多分直した
                    if (delmap.containsKey(yoyaku.get(p))) {//削除マップに追加
                        List<DelPos> list = delmap.get(yoyaku.get(p));
                        DelPos pos = new DelPos(version, tmp, a, yoyaku.get(p), yoyakuver.get(p), yoyakued.get(p));
                        list.add(pos);
                    } else {
                        List<DelPos> list = new LinkedList<DelPos>();
                        DelPos pos = new DelPos(version, tmp, a, yoyaku.get(p), yoyakuver.get(p), yoyakued.get(p));
                        list.add(pos);
                        delmap.put(yoyaku.get(p), list);
                    }
                }
                yoyaku=new ArrayList<String>();
                yoyakuver=new ArrayList<Integer>();
                yoyakued=new ArrayList<String>();

                whowrite.add(prevwrite.getWikitext().get(b),prevwrite.getEditorList().get(b),prevwrite.getVerlist().get(b));
                tmp = a;
                a++;
                b++;
            }

        }
        for (int p = 0; p < yoyaku.size(); p++) {//これ，何回もリスト追加することになってる バグ 多分直した
            if (delmap.containsKey(yoyaku.get(p))) {//削除マップに追加
                List<DelPos> list = delmap.get(yoyaku.get(p));
                DelPos pos = new DelPos(version, tmp, a, yoyaku.get(p), yoyakuver.get(p), yoyakued.get(p));
                list.add(pos);
            } else {
                List<DelPos> list = new LinkedList<DelPos>();
                DelPos pos = new DelPos(version, tmp, a, yoyaku.get(p), yoyakuver.get(p), yoyakued.get(p));
                list.add(pos);
                delmap.put(yoyaku.get(p), list);
            }
        }
        editlist.add(edit);

    }

    /*public static List<String> kaiseki(String text){
        StringTagger tagger = SenFactory.getStringTagger(null);
        CompositeTokenFilter ctFilter = new CompositeTokenFilter();

        try {
            ctFilter.readRules(new BufferedReader(new StringReader("名詞-数")));
            tagger.addFilter(ctFilter);

            ctFilter.readRules(new BufferedReader(new StringReader("記号-アルファベット")));
            tagger.addFilter(ctFilter);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Token> tokens = new ArrayList<Token>();
        try {
            tokens=tagger.analyze(text, tokens);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> current_text = new ArrayList<String>(tokens.size()+1);

        for(Token token:tokens){
                current_text.add(token.getSurface());

        }
        return  current_text;


    }*/

    public static List<String> kaisekikuro(String text){

        Tokenizer tokenizer = Tokenizer.builder().mode(org.atilika.kuromoji.Tokenizer.Mode.SEARCH).build();

        List<Token> tokens = tokenizer.tokenize(text);



        List<String> current_text = new ArrayList<String>(tokens.size()+1);

        for(Token token:tokens){
            /*String regex = "^[ -/:-@\\[-\\`\\{-\\~！”＃＄％＆’（）＝～｜‘｛＋＊｝＜＞？＿－＾￥＠「；：」、。・]+$";
            Pattern p1 = Pattern.compile(regex);
            Matcher m = p1.matcher(token.getSurface());
            if(!m.find()) {*/
            current_text.add(token.getSurfaceForm());
            //}
        }
        return  current_text;


    }

    public static void delrevdet(InsTerm term,Map<String,List<DelPos>> delmap,int version,List<List<String>> difflist,Map<Integer,Integer>editmap,WhoWrite whowrite,Map<Integer,List<DelPos>> delposmap){
        //今追加した単語が
        if(delmap.containsKey(term.getTerm())) {//消されたものだったか
            List<DelPos> del = delmap.get(term.getTerm());//確かめて
            for (ListIterator<DelPos> i = del.listIterator(del.size()); i.hasPrevious();) {
                DelPos delpos = i.previous();
                if(delpos.getRevert()!=version) {
                    if (delpos.getOriversion() < (version - 20)) {
                        i.remove();
                    } else {
                        int ue = delpos.getue();//文章の上と
                        int shita = delpos.getshita();//下で
                        int preue = delpos.getue();
                        int preshita = delpos.getshita();
                        for (int x = delpos.getVersion(); x < version; x++) {//矛盾が出ないか確かめる
                            int a = 0;
                            int b = 0;
                            ue=-1;
                            Boolean isbreak = false;
                            for (int y = 0; y < difflist.get(x).size(); y++) {
                                String type = difflist.get(x).get(y);
                                if (type.equals("+")) {
                                    a++;
                                } else if (type.equals("-")) {
                                    b++;
                                } else if (type.equals("|")) {
                                    if (b <= preue) {
                                        ue = a;
                                    }
                                    if (b >= preshita) {
                                        shita = a;
                                        isbreak = true;
                                        break;
                                    }
                                    a++;
                                    b++;
                                }
                            }
                            if (!isbreak) {
                                shita = a;
                            }
                            preue = ue;
                            preshita = shita;
                        }
                        delpos.setShita(shita);
                        delpos.setUe(ue);
                        delpos.setVersion(version);

                        if (term.getPos() > ue && term.getPos() < shita) {
                            int cc = 1;
                            delpos.setRevert(version);
                            if (editmap.containsKey(delpos.getOriversion())) {
                                cc = editmap.get(delpos.getOriversion()) + 1;
                            }
                            editmap.put(delpos.getOriversion(), cc);
                            if (delposmap.containsKey(delpos.getOriversion())) {
                                delposmap.get(delpos.getOriversion()).add(delpos);
                            } else {
                                List<DelPos> delposlist = new LinkedList<DelPos>();
                                delposlist.add(delpos);
                                delposmap.put(delpos.getOriversion(), delposlist);
                            }

                            //System.out.println("delrev:" + term.getTerm() + version + " " + delpos.getOriversion());
                            //i.remove();
                            return;
                        }
                    }
                }
                //System.out.println(term.getTerm());
            }
        }
    }

    public static Set fileRead(String filePath) {

        FileReader fr = null;
        BufferedReader br = null;
        Set<String> aiming= new HashSet<String>(350);
        try {
            fr = new FileReader(filePath);
            br = new BufferedReader(fr);

            String line;
            while ((line = br.readLine()) != null) {
                //System.out.println(line);
                aiming.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return aiming;
    }
}


class InsTerm {
    String term;
    int pos;
    Boolean isRevert=false;
    Integer revedver=null;
    public InsTerm(String term,int pos,String editor){
        this.term=term;
        this.pos=pos;
    }
    public void revertterm(DelPos delpos){
        isRevert=true;
        revedver=delpos.getVersion();
    }

    public String getTerm() {
        return term;
    }

    public int getPos() {
        return pos;
    }
}

class DelPos{
    int ue;
    int shita;
    int version;
    String term;
    int deledver;
    String delededitor;
    int oriue;
    int orishita;
    int oriversion;
    int revert=0;
    public DelPos(int version,int ue,int shita,String term,int deledver,String delededitor){
        this.oriue=ue;
        this.orishita=shita;
        this.oriversion=version;
        this.ue=ue;
        this.shita=shita;
        this.version=version;
        this.delededitor=delededitor;
        this.term=term;
        this.deledver=deledver;
    }
    public int getRevert(){return revert;}

    public void setRevert(int revert) {
        this.revert = revert;
    }

    public int getue() {
        return ue;
    }

    public int getshita() {
        return shita;
    }

    public int getVersion() {
        return version;
    }

    public String getTerm() {
        return term;
    }

    public int getDeledver() {
        return deledver;
    }

    public int getOriversion() {
        return oriversion;
    }

    public String getDelededitor() {
        return delededitor;
    }

    public void setUe(int ue) {
        this.ue = ue;
    }

    public void setShita(int shita) {
        this.shita = shita;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}

class WhoWrite{
    List<String> wikitext=new ArrayList<String>();
    List<String> editorList=new ArrayList<String>();
    List<Integer> verlist= new ArrayList<Integer>();
    /*public void delete(int pos,String editor,int version){
        wikitext.remove(pos);
        editorList.remove(pos);
        verlist.remove(pos);
    }*/
    public void add(String term,String editor,int ver){
        wikitext.add(term);
        editorList.add(editor);
        verlist.add(ver);
    }

    public List<Integer> getVerlist() {
        return verlist;
    }

    public List<String> getEditorList() {
        return editorList;
    }
    public void revert(int pos,int revver,String reved){
        editorList.set(pos,reved);
        verlist.set(pos,revver);
    }

    public List<String> getWikitext() {
        return wikitext;
    }
}