package com.jr2jme.UsrTreeArticle;

/**
 * Created by K.H on 2015/06/29.
 */

import java.sql.*;

public class SqlOp {
    PreparedStatement stedge =null;
    PreparedStatement sttitle = null;
    PreparedStatement stnode = null;
    PreparedStatement stcomment =null;

    Connection conn = null;
    Integer id;
    int j = 0;
    int i=0;
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
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
