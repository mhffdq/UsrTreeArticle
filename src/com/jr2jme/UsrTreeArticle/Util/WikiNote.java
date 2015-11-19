package com.jr2jme.UsrTreeArticle.Util;

import java.io.*;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.jr2jme.UsrTreeArticle.WikiUsernote;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * Created by K.H on 2015/09/18.
 * 編集履歴を使うやつ
 */
public class WikiNote {
    public Histories gethistory(String xml,boolean noteflag){//wikipedia
        //sleep(1000);
        File dirfile =null;
        File notefile = null;
        if(noteflag){
            dirfile = new File("Notehistory");
            if(!dirfile.exists()){
                dirfile.mkdir();
            }
            notefile = new File("./Notehistory/"+xml);
        }else{
            dirfile = new File("Articlehistory");
            if(!dirfile.exists()){
                dirfile.mkdir();
            }
            notefile = new File("./Articlehistory/"+xml);
        }

        Histories his =null;
        if(notefile.exists()){
            try {
                FileInputStream input = new FileInputStream(notefile);
                ObjectInputStream inObject = new ObjectInputStream(input);
                his= (Histories) inObject.readObject();
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

            String TARGET_HOST=null;
            if(noteflag) {
                TARGET_HOST = "http://ja.wikipedia.org/w/api.php?action=query&prop=revisions&rvlimit=500&maxlag=5&format=xml&rvprop=ids|user|timestamp&titles=%E3%83%8E%E3%83%BC%E3%83%88:";
            }else{
                TARGET_HOST = "http://ja.wikipedia.org/w/api.php?action=query&prop=revisions&rvlimit=500&maxlag=5&format=xml&rvprop=ids|user|timestamp&titles=";
            }
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
                url = URLEncoder.encode(xml, "utf-8");
                System.out.println(TARGET_HOST + url);//
                root = builder.parse(TARGET_HOST + url);

            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            NodeList gchildren = root.getElementsByTagName("rev");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

            his = new Histories();

            bimyou(his,gchildren,sdf);

            while (root.getElementsByTagName("continue").getLength() != 0) {
                try {
                    root = builder.parse(TARGET_HOST + url + "&rvcontinue=" + root.getElementsByTagName("continue").item(0).getFirstChild().getAttributes().getNamedItem("rvcontinue").getNodeValue());
                } catch (DOMException e) {
                    //
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                gchildren = root.getElementsByTagName("rev");
                System.out.println(Integer.toString(gchildren.getLength()));
                bimyou(his,gchildren,sdf);
            }
            Output.outputbinary(notefile,his);
        }
        return his;
    }

    private void bimyou(Histories his,NodeList gchildren,SimpleDateFormat sdf){
        for (int c = 0; c < gchildren.getLength(); c++) {
            try {
                String name = gchildren.item(c).getAttributes().getNamedItem("user").getNodeValue();
                Date date = sdf.parse(gchildren.item(c).getAttributes().getNamedItem("timestamp").getNodeValue());
                String id = gchildren.item(c).getAttributes().getNamedItem("revid").getNodeValue();
                his.addedit(date,name,id);
            } catch (DOMException e) {
                //
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }



    public static String parsepageid(String revid){//

        File dirfile = new File("Articletxtid");
        if(!dirfile.exists()){
            dirfile.mkdir();
        }

        File notefile = new File("./Articletxtid/"+revid);
        String txt = null;

        if(notefile.exists()){
            try {
                FileInputStream input = new FileInputStream(notefile);
                ObjectInputStream inObject = new ObjectInputStream(input);

                txt = (String) inObject.readObject();
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
            //sleep(1000);
            final String TARGET_HOST = "https://ja.wikipedia.org/w/api.php?action=query&prop=revisions&rvprop=content&format=xml&revids=";
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
                url = URLEncoder.encode(revid, "utf-8");
                System.out.println(TARGET_HOST + url);
                root = builder.parse(TARGET_HOST + url);

            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            NodeList gchildren = root.getElementsByTagName("rev");
            txt = gchildren.item(0).getTextContent();
        }
        return txt;
    }



    public class Histories{
        Map<String,List<History>> editormain = new HashMap<String, List<History>>();
        List<History> histories = new ArrayList<History>();


        public void addedit(Date date,String editor,String id){
            History his = new History(date,editor,id);
            if(editormain.containsKey(editor)){
                editormain.get(editor).add(his);
            } else {
                List<History> list = new ArrayList<History>();
                list.add(his);
                editormain.put(editor, list);
            }

            histories.add(his);
        }

        public List<History> getHistories(){
            return  histories;
        }

        public class History{
            Date date;
            String editor;
            String id;
            public History(Date date,String editor,String id){
                this.date=date;
                this.editor=editor;
                this.id=id;
            }

            public Date getDate() {
                return date;
            }

            public String getEditor() {
                return editor;
            }

            public String getId() {
                return id;
            }
        }

        public Map<String, List<History>> getEditormain() {
            return editormain;
        }
    }

}
