package com.jr2jme.UsrTreeArticle;


import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.jr2jme.UsrTreeArticle.Util.Output;
import com.jr2jme.UsrTreeArticle.Util.Feature;
import org.atilika.kuromoji.Token;
import org.atilika.kuromoji.Tokenizer;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class MakeTree {
    GraphStructure graph;
    private String usrname = "";
    private Map<String,Nodecalc> nodemap = new HashMap<String, Nodecalc>();
    String artname;
    static Analyzer analyzer = new Analyzer();

	public String parsexml(String xml){//wikipediaのapiを用いてxmlを取得し、本文(wiki記法)を得る

        File dirfile;
        File artefile;
        dirfile = new File("Article");
        if (!dirfile.exists()) {
            dirfile.mkdir();
        }

        artefile = new File("./Article/" +xml);
        String artegories=null;
        if(artefile.exists()) {
            try {
                FileInputStream input = new FileInputStream(artefile);
                ObjectInputStream inObject = new ObjectInputStream(input);

                artegories = (String) inObject.readObject();
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
            final String TARGET_HOST = "https://ja.wikipedia.org/w/api.php?action=query&prop=revisions&rvprop=content&format=xml&titles=";
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;
            Document root = null;
            try {
                builder = factory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
            try {
                String url = URLEncoder.encode(xml, "utf-8");
                System.out.println(TARGET_HOST + url);//%で書かれた奴にエンコード
                root = builder.parse(TARGET_HOST + url);

            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            NodeList gchildren = root.getElementsByTagName("rev");//本文取得
            String retstr = "";
            if (gchildren.item(0) != null) {
                retstr = gchildren.item(0).getTextContent();
            }

            artegories = retstr;
            Output.outputbinary(artefile,artegories);
        }
        return artegories;
	}



	public void maketree(String str,SqlOp sql){//目次らへんのパース
        File dirfile;
        File artefile;
        dirfile = new File("NoteStructure");
        if (!dirfile.exists()) {
            dirfile.mkdir();
        }
        NoteStructure ns;
        artefile = new File("./NoteStructure/" +artname);
        Map<String, Feature> commenttf = new HashMap<String, Feature>();
        if(artefile.exists()) {

            try {
                FileInputStream input = new FileInputStream(artefile);
                ObjectInputStream inObject = new ObjectInputStream(input);

                ns = (NoteStructure) inObject.readObject();
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
            ns = new NoteStructure(artname);

            String topicregex = "==(.+)==\n";
            Pattern topicp = Pattern.compile(topicregex);
            Matcher topicm;
            List<String[]> blocklist = new ArrayList<String[]>();
            String topi = "";

            while (true) {
                topicm = topicp.matcher(str);
                if (topicm.find()) {
                    String[] topicarray = {topi, str.substring(0, topicm.start())};
                    blocklist.add(topicarray);
                    str = str.substring(topicm.end());
                    topi = topicm.group(1);
                } else {
                    break;
                }
            }
            String[] starra = {topi, str + "\n"};//章題，そこにあるテキスト全部
            blocklist.add(starra);
            int c = 0;//内容より後から話題のノードが作られるので外でもう一度　もう一個文字列用意してやれば中でやれそう
            Matcher m = null;
            for (String[] block : blocklist) {//話題ごとに分割されたものを渡す。
                //String regex  = ".{22}\\(UTC\\)(?<=\\[\\[利用者:.+?\\|.*?\\]\\][|(（\\[利用者.+?\\]）)])|\\[\\[特別:.+\\]\\].{22}\\(UTC\\)|--\\{\\{Unsig.+\\}\\}|\n\\*+";
                //String regex  = "(--|)\\[\\[利用者:[^\\]|\\|]+?(\\|[^\\]]+?)\\]\\](|(（\\[\\[利用者.+?\\]\\]）)).{17,23}\\(UTC\\)(?<=\\(UTC\\))|\\[\\[特別:.+\\]\\].{23}\\(UTC\\)|\\{\\{Unsig.+\\}\\}|\n[\\*:]+|--.+? \\(UTC\\)\n";
                String regex = "(?:--|)(?:以上の署名の無いコメントは、|\\[\\[(?:利用者:|特別:投稿記録/)|\\{\\{Unsigned[^\\|]+?\\|)([^\\]\\|\\}]+)(?:(?:|\\|[^\\]\\|\\}]+?)\\]\\](?:|（\\[\\[利用者.+?\\]\\]）)(.{17,23})\\((?:UTC|JST)\\)|\\|.{17,23}\\((?:UTC|JST)\\)[^\\]\\}]*?\\}\\}|.+?に投稿したものです.+?。)";
                Pattern p = Pattern.compile(regex);
                String blotext = block[1].replaceAll("\\<(.+?)\\>", "");
                Tokenizer tokenizer = Tokenizer.builder().mode(org.atilika.kuromoji.Tokenizer.Mode.SEARCH).build();

                NoteStructure.Comment mae = null;

                while (p.matcher(blotext).find()) {//コメントごとに分割
                    c++;
                    String check = m.group(0);

                    String talk = blotext.substring(0, m.start()).replaceFirst("\n", "").replaceAll("[!-/:-@≠\\[-`\\{-~]", " ");//Wikipeidaで使われる記号を除去
                    String comall = blotext.substring(0, m.start());
                    blotext = blotext.substring(m.end());
                    NoteStructure.Comment shita = ns.addComment(block[0], check, talk, mae);
                    mae.settsugi(shita);

                    List<Token> tokens = tokenizer.tokenize(talk);

                    Feature tfidf = new Feature((double) tokens.size());

                    if (check.indexOf(usrname) != -1 && m.group(1) != null && m.group(2) != null) {
                        check = m.group(1) + m.group(2);
                        Nodecalc noddd = new Nodecalc(check, comall, artname);
                        nodemap.put(check, noddd);
                        noddd.addmidashi(block[0]);

                        sql.insert_comment(m.group(1), m.group(2), artname, comall);//疲れた

                        for (Token token : tokens) {
                            //if ((token.getAllFeaturesArray()[0].equals("名詞") && !(token.getAllFeaturesArray()[1].equals("非自立") || token.getAllFeaturesArray()[1].equals("代名詞")|| token.getAllFeaturesArray()[1].equals("数"))) || token.getAllFeaturesArray()[0].equals("動詞")) {
                            if (token.getAllFeaturesArray()[0].equals("名詞") && !(token.getAllFeaturesArray()[1].equals("非自立") || token.getAllFeaturesArray()[1].equals("代名詞") || token.getAllFeaturesArray()[1].equals("固有名詞") || token.getAllFeaturesArray()[1].equals("数"))) {
                                String s = token.getSurfaceForm();
                                tfidf.addidfcount(s);
                                tfidf.addtfcount(s);
                            }
                        }

                        commenttf.put(check, tfidf);

                    } else {
                        for (Token token : tokens) {
                            if ((token.getAllFeaturesArray()[0].equals("名詞") && !(token.getAllFeaturesArray()[1].equals("非自立") || token.getAllFeaturesArray()[1].equals("代名詞") || token.getAllFeaturesArray()[1].equals("数"))) || token.getAllFeaturesArray()[0].equals("動詞")) {
                                String s = token.getSurfaceForm();
                                tfidf.addidfcount(s);
                            }
                        }
                    }

                    mae = shita;

                }


            }//本文の分解ここで終わり

            Output.outputbinary(artefile, ns);

            for (Map.Entry<String, Double> idf : Feature.getIdf().entrySet()) {
                Feature.getIdf().put(idf.getKey(), Math.log(c / idf.getValue()) + 1.0);
            }


            for (Map.Entry<String, Feature> commap : commenttf.entrySet()) {
                commap.getValue().maketfidf();

                Nodecalc node = nodemap.get(commap.getKey());
                node.setTermweight(commap.getValue().getTfidf());
                node.setNorm(Math.sqrt(commap.getValue().getDor()));
                /*List<Map.Entry> list = MaptoList.valueSort(tid);
                String label = artname + " : " + node.getMidashi();

                label += " : ";
                if (list.size() > 3) {
                    label += list.get(0).getKey() + "," + list.get(1).getKey() + "," + list.get(2).getKey();
                }
                node.setLabel(label);
                if (!analyzer.analyzegiron(node.getComment())) {
                    node.setIfrule(1);

                } else {
                    node.setIfrule(0);
                }*/
                graph.addnode(node);
            }
        }
	}

    public Map<String, Nodecalc> getNodemap() {
        return nodemap;
    }

    public MakeTree(String str, String user,GraphStructure graph,SqlOp sql) {//コンストラクタ
        usrname = user;
        this.graph=graph;
        artname = str;
		maketree(parsexml("ノート:"+str),sql);

	}

	public synchronized void sleep(long msec)
	{	//指定ミリ秒実行を止めるメソッド
		try
		{
			wait(msec);
		}catch(InterruptedException e){}
	}



    //編集者とかかわった議論を結びつける関数が欲しい

}