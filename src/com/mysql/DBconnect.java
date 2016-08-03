package com.mysql;

import com.sun.tools.javac.util.Name;
import org.htmlparser.util.ParserException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.sql.*;
import java.util.Vector;


public class DBconnect {
    static Connection conn = null;
    static String sql;
    static Statement statement = null;
    public DBconnect() {
        String driver = "com.mysql.jdbc.Driver";
        //URL指向要访问的数据库名sina_blog
        String myurl = "jdbc:mysql://localhost:3306/sina_blog";
        //MySQL配置时的用户名
        String user = "root";
        //MySQL配置时的密码
        String password = "tcmysql";
        try {
            // 之所以要使用下面这条语句，是因为要使用MySQL的驱动，所以我们要把它驱动起来，
            Class.forName(driver);
            // 一个Connection代表一个数据库连接
            conn = DriverManager.getConnection(myurl, user, password);
            if(conn != null)
                System.out.println("MySQL连接成功");

        }catch (SQLException e) {
            System.out.println("MySQL操作错误");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String WriteToMysql(String author, String title, String date, String tag, String content) throws SQLException {
        statement = conn.createStatement();
        //用来得到查询的最大Id,即本条记录对应的Id
        ResultSet resultSet = null;
        String ansid = "error";
        try {
            //将"'"转义
            content = content.replaceAll("'", "\\\\'");
            String data = "('" + author + "','" + title + "','" + date + "','" + tag + "','" + content + "')";
            //Mysql插入语句
            sql = "insert into blog(Author, Title, Date, Tag, Content) values" + data;
            //执行插入操作
            int result = statement.executeUpdate(sql);
            //查询最大Id语句
            sql = "select max(Id) from blog";
            //执行查询语句
            resultSet = statement.executeQuery(sql);
            //得到最大Id
            while (resultSet.next()) {
                ansid = resultSet.getString(1);
            }
        }catch (SQLException e) {
            System.out.println(sql);
            e.printStackTrace();
        }
        return ansid;
    }

    public ResultSet query(String sql) throws SQLException {
        ResultSet resultSet = null;
        try{
            statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultSet = statement.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public void solveQuery(String str) throws SQLException {
        DBconnect db = new DBconnect();
        JFrame f = new JFrame("查询结果");
        Container contentPane = f.getContentPane();
        //得到查询的结果
        ResultSet resultSet = db.query(str);
        //图形界面中的列属性
        Vector vector = new Vector();
        vector.add("Id");
        vector.add("博主");
        vector.add("标题");
        vector.add("发布时间");
        vector.add("标签");
        vector.add("文章内容");
        //创建表模式
        DefaultTableModel tablemodel = new DefaultTableModel(new Vector(), vector);
        Vector value = new Vector();
        try {
            while (resultSet.next()) {
                //将查询得到的内容加入表中
                Vector vt = new Vector();
                vt.add(resultSet.getString(1));
                vt.add(resultSet.getString(2));
                vt.add(resultSet.getString(3));
                vt.add(resultSet.getString(4));
                vt.add(resultSet.getString(5));
                vt.add(resultSet.getString(6));
                value.add(vt);
            }
            tablemodel.setDataVector(value, vector);
            JTable table = new JTable(tablemodel);
            contentPane.add(new JScrollPane(table));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        f.pack();
        f.setVisible(true);
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.exit(0);
            }
        });
    }

    public void queryByDate(String st, String ed) throws SQLException {
        String str = "select * from blog where Date > \"" +  st + "\"" + "&& Date < \"" + ed + "\"";
        //System.out.println(str);
        solveQuery(str);
    }

    public void queryByTag(String tag) throws SQLException {
        String str = "select *from blog where Tag like " + "\"%" + tag + "%\"";
        //System.out.println(str);
        solveQuery(str);
    }

    public void queryByAuthor(String author) throws  SQLException {
        String str = "select *from blog where Author = " + "\'" + author + "\'";
        //System.out.println(str);
        solveQuery(str);
    }
}
