package com.jr2jme.UsrTreeArticle;

/**
 * Created by K.H on 2015/06/29.
 */

import com.teradata.jdbc.jdbc_4.ifsupport.Result;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class SqlOp {
    PreparedStatement stedge =null;
    PreparedStatement sttitle = null;
    PreparedStatement stnode = null;
    PreparedStatement stcomment =null;
    PreparedStatement stperson =null;
    PreparedStatement stterm =null;
    PreparedStatement sttermpropose = null;


    Connection conn = null;
    Integer id;
    int j = 0;
    int i=0;
    Boolean isdev=true;
    public SqlOp(int flag){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            if(flag==1) {
                conn = DriverManager.getConnection("jdbc:mysql://localhost", "user_development", "pass_development");
                stedge = conn.prepareStatement(
                        "INSERT INTO db_development.useredges (graphid,label,edgeid,source,target,weight,created_at,updated_at) VALUES (?,?,?,?,?,?,?,?)"
                );
                sttitle = conn.prepareStatement("INSERT INTO db_development.graphtitles(graphid,title,graphtype,created_at,updated_at) VALUES (?,?,?,?,?)",java.sql.Statement.RETURN_GENERATED_KEYS);
                stnode = conn.prepareStatement("INSERT INTO db_development.usernodes (graphid,label,nodeid,created_at,updated_at) VALUES (?,?,?,?,?)");
                stcomment = conn.prepareStatement(
                        "INSERT INTO db_development.comments (name,date,arttitle,text,created_at,updated_at) VALUES (?,?,?,?,?,?)"
                );
                stperson = conn.prepareStatement("INSERT INTO db_development.people (name,created_at,updated_at) VALUES (?,?,?)");
                stterm = conn.prepareStatement("INSERT INTO db_development.terms (term,personid,created_at,updated_at) VALUES (?,?,?,?)");
                sttermpropose = conn.prepareStatement("INSERT INTO db_development.termproposes (term,personid,rank,typeid,created_at,updated_at) VALUES (?,?,?,?,?,?)");

                isdev=true;
            }
            else if(flag==2){
                conn = DriverManager.getConnection("jdbc:mysql://localhost", "user_production", "pass_production");
                stedge = conn.prepareStatement(
                        "INSERT INTO db_production.useredges (graphid,label,edgeid,source,target,weight,created_at,updated_at) VALUES (?,?,?,?,?,?,?,?)"
                );
                sttitle = conn.prepareStatement("INSERT INTO db_production.graphtitles(graphid,title,graphtype,created_at,updated_at) VALUES (?,?,?,?,?)");
                stnode = conn.prepareStatement("INSERT INTO db_production.usernodes (graphid,label,nodeid,created_at,updated_at) VALUES (?,?,?,?,?)");
                stcomment = conn.prepareStatement(
                        "INSERT INTO db_production.comments (name,date,arttitle,text,created_at,updated_at) VALUES (?,?,?,?,?,?)"
                );

                stperson = conn.prepareStatement("INSERT INTO db_production.people (name,created_at,updated_at) VALUES (?,?,?)");
                stterm = conn.prepareStatement("INSERT INTO db_production.terms (term,personid,created_at,updated_at) VALUES (?,?,?,?)");
                sttermpropose = conn.prepareStatement("INSERT INTO db_production.termproposes (term,personid,rank,typeid,created_at,updated_at) VALUES (?,?,?,?,?.?)");

                isdev=false;

            }
            conn.setAutoCommit(false);
        } catch (ClassNotFoundException e) {
            System.out.println("a" + e);
        } catch (SQLException e) {
            System.out.println("j" + e);
        }
    }



    public void insert_graphtitle(String title,Integer graphid){
        try {

            sttitle.setInt(1, graphid);
            sttitle.setString(2, title);
            sttitle.setInt(3, 2);
            sttitle.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            sttitle.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            sttitle.execute();


            conn.commit();
            ResultSet rs = sttitle.getGeneratedKeys();
            if(rs==null){
                System.out.println("null");
            }
            rs.next();
            id=rs.getInt(1);
            System.out.println(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }


    public void insert_node(String label,String nodeid){
        try {
            stnode.setInt(1, id);
            stnode.setString(2, label);
            stnode.setString(3, nodeid);
            stnode.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            stnode.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            stnode.addBatch();
            if(i>100){
                stnode.executeBatch();
                conn.commit();
                i=0;
            }
            i++;

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public void insert_edge(String label,String edgeid,String target,String source,float weight){
        try {
            stedge.setInt(1,id);
            stedge.setString(2, label);
            stedge.setString(3, edgeid);
            stedge.setString(4, source);
            stedge.setString(5, target);
            stedge.setFloat(6, weight);
            stedge.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
            stedge.setTimestamp(8,new Timestamp(System.currentTimeMillis()));
            stedge.addBatch();
            if(j>1000){
                stedge.executeBatch();

                conn.commit();
                j=0;
            }
            j++;
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public void insert_comment(String name,String date,String title,String text){
        try {
            stcomment.setString(1, name);
            stcomment.setString(2, date);
            stcomment.setString(3,title);
            stcomment.setString(4, text);
            stcomment.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            stcomment.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            stcomment.addBatch();
            if(i>100){
                stcomment.executeBatch();
                conn.commit();
                i=0;
            }
            i++;

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public void insert_batch(){
        try {
            stedge.executeBatch();
            stnode.executeBatch();
            stcomment.executeBatch();
            stterm.executeBatch();
            sttermpropose.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    public void insert_term(String term,Integer personid){//単語登録
        try {
            stterm.setString(1, term);
            stterm.setInt(2,personid);
            stterm.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            stterm.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            stterm.addBatch();
            if(i>100){
                stterm.executeBatch();
                conn.commit();
                i=0;
            }
            i++;

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insert_termpropose(String term,int personid,int rank,int type){//単語登録
        try {
            sttermpropose.setString(1,term);
            sttermpropose.setInt(2,personid);
            sttermpropose.setInt(3,rank);
            sttermpropose.setInt(4,type);
            sttermpropose.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            sttermpropose.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            sttermpropose.addBatch();
            if(i>100){
                sttermpropose.executeBatch();
                conn.commit();
                i=0;
            }
            i++;

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insert_person(String name){//単語で表される人登録
        try {
            stperson.setString(1, name);
            stperson.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            stperson.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            stperson.execute();
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet get_person(String name){//単語で表される人登録
        PreparedStatement stgetperson =null;
        try {
            if(isdev) {
                stgetperson = conn.prepareStatement("SELECT * FROM db_development.people WHERE name = ?");
            }
            else{
                stgetperson = conn.prepareStatement("SELECT * FROM production.people WHERE name = ?");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ResultSet rs=null;
        try {
            stgetperson.setString(1, name);
            rs = stgetperson.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  rs;

    }

    public int get_personid(String name){//単語で表される人登録
        ResultSet rs = get_person(name);
        int id =0;
        try {
            rs.next();
            id = rs.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;

    }

    public ResultSet get_termrankresult(Integer name){//人が選んだ単語の順番取得(引数は知りたい編集者名)
        PreparedStatement stgettermrank =null;//キーは

        try {
            if(isdev) {
                stgettermrank = conn.prepareStatement("SELECT term ,SUM(rank) as sumrank FROM db_development.termresults WHERE termeditor = ? GROUP BY term ORDER BY sumrank ASC");
            }
            else{
                stgettermrank = conn.prepareStatement("SELECT term,SUM(rank)  as sumrank FROM db_production.termresults WHERE termeditor = ? GROUP BY term ORDER BY sumrank ASC");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ResultSet rs=null;


        try {
            stgettermrank.setInt(1,  name);
            rs = stgettermrank.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  rs;

    }

    public ResultSet get_termranktype(int name,int type){//人が選んだ単語の順番取得(引数は知りたい編集者名)
         PreparedStatement stgettermrank =null;//こっちは手法で求めたランク
//いまのところ不整合があるから，挿入のやつをもっとシンプルに
        try {
            if(isdev) {
                stgettermrank = conn.prepareStatement("SELECT term,rank FROM db_development.termproposes WHERE personid = ? AND typeid = ?");
            }
            else{
                stgettermrank = conn.prepareStatement("SELECT term,rank FROM db_production.termproposes WHERE personid = ? AND typeid = ?");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ResultSet rs=null;
        try {
            stgettermrank.setInt(1, name);
            stgettermrank.setInt(2,type);
            rs = stgettermrank.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  rs;

    }

    public boolean termcheck(int name){//人が選んだ単語の順番取得(引数は知りたい編集者名)
        PreparedStatement checkkoubun =null;//こっちは手法で求めたランク
//いまのところ不整合があるから，挿入のやつをもっとシンプルに
        try {
            if(isdev) {
                checkkoubun = conn.prepareStatement("SELECT * FROM db_development.terms WHERE personid = ?");
            }
            else{
                checkkoubun = conn.prepareStatement("SELECT * FROM db_production.terms WHERE personid = ?");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ResultSet rs=null;
        boolean yes=false;
        try {
            checkkoubun.setInt(1, name);
            rs = checkkoubun.executeQuery();
            yes=  rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return yes;

    }

    public Set<String> get_term(int name){//人が選んだ単語の順番取得(引数は知りたい編集者名)
        PreparedStatement checkkoubun =null;//こっちは手法で求めたランク
//いまのところ不整合があるから，挿入のやつをもっとシンプルに
        try {
            if(isdev) {
                checkkoubun = conn.prepareStatement("SELECT * FROM db_development.terms WHERE personid = ?");
            }
            else{
                checkkoubun = conn.prepareStatement("SELECT * FROM db_production.terms WHERE personid = ?");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ResultSet rs=null;
        Set<String> sset = new HashSet<String>();
        try {
            checkkoubun.setInt(1, name);
            rs = checkkoubun.executeQuery();
            while(rs.next()){
                sset.add(rs.getString("term"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sset;

    }





}
