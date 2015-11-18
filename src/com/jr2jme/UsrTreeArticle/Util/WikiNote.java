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
    public History gethistory(String xml,boolean noteflag){//wikipedia
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

        History his =null;
        if(notefile.exists()){
            try {
                FileInputStream input = new FileInputStream(notefile);
                ObjectInputStream inObject = new ObjectInputStream(input);
                his= (History) inObject.readObject();
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
                TARGET_HOST = "http://ja.wikipedia.org/w/api.php?action=query&prop=revisions&rvlimit=500&maxlag=5&format=xml&rvprop=user|timestamp&titles=%E3%83%8E%E3%83%BC%E3%83%88:";
            }else{
                TARGET_HOST = "http://ja.wikipedia.org/w/api.php?action=query&prop=revisions&rvlimit=500&maxlag=5&format=xml&rvprop=user|timestamp&titles=";
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

            his = new History();

            for (int c = 0; c < gchildren.getLength(); c++) {
                try {
                    String name = gchildren.item(c).getAttributes().getNamedItem("user").getNodeValue();
                    Date date = sdf.parse(gchildren.item(c).getAttributes().getNamedItem("timestamp").getNodeValue());
                    his.addedit(date,name);
                } catch (DOMException e) {
                    //
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
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
                for (int c = 0; c < gchildren.getLength(); c++) {
                    try {
                        String name = gchildren.item(c).getAttributes().getNamedItem("user").getNodeValue();
                        Date date = sdf.parse(gchildren.item(c).getAttributes().getNamedItem("timestamp").getNodeValue());
                        his.addedit(date,name);

                    } catch (DOMException e) {
                        //
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
            Output.outputbinary(notefile,his);
        }
        return his;
    }

    /*public static Set<String> onlysetuser(String xml){//
        //sleep(1000);
        final String TARGET_HOST = "http://ja.wikipedia.org/w/api.php?action=query&prop=revisions&rvlimit=500&maxlag=5&format=xml&rvprop=user|timestamp&titles=%E3%83%8E%E3%83%BC%E3%83%88:";
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
            url=URLEncoder.encode(xml,"utf-8");
            System.out.println(TARGET_HOST+url);
            root = builder.parse(TARGET_HOST+url);

        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        NodeList gchildren = root.getElementsByTagName("rev");

        Set<String> editors = new HashSet<String>();

        for(int c = 0;c<gchildren.getLength();c++){
            try {
                editors.add(gchildren.item(c).getAttributes().getNamedItem("user").getNodeValue());


            } catch (DOMException  e) {
                e.printStackTrace();
            }
        }
        while(root.getElementsByTagName("continue").getLength()!=0){
            try {
                root = builder.parse(TARGET_HOST+url+"&rvcontinue="+ root.getElementsByTagName("continue").item(0).getFirstChild().getAttributes().getNamedItem("rvcontinue").getNodeValue());
            } catch (DOMException  e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            gchildren = root.getElementsByTagName("rev");
            System.out.println(Integer.toString(gchildren.getLength()));
            for(int c = 0;c<gchildren.getLength();c++){
                try {
                    editors.add(gchildren.item(c).getAttributes().getNamedItem("user").getNodeValue());
                } catch (DOMException  e) {
                    e.printStackTrace();
                }
            }
        }
        return editors;

    }*/
    public class History{
        Map<String,List<Date>> editormain = new HashMap<String, List<Date>>();
        List<Date> alldatelist = new ArrayList<Date>();
        List<String> editors = new ArrayList<String>();

        public void addedit(Date date,String editor){
            List<Date> datelist = editormain.get(editor);
            alldatelist.add(date);
            if (datelist != null) {
                datelist.add(date);
            } else {
                List<Date> list = new ArrayList<Date>();
                list.add(date);
                editormain.put(editor, list);
            }
        }

        public List<Date> getAlldatelist() {
            return alldatelist;
        }

        public List<String> getEditors() {
            return editors;
        }

        public Map<String, List<Date>> getEditormain() {
            return editormain;
        }
    }

}
