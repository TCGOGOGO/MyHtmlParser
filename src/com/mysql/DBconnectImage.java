package com.mysql;

import java.io.File;
import java.io.FileInputStream;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class DBconnectImage {
    static Map<String, Boolean> mp = new HashMap<String, Boolean>();
    static Connection conn = null;
    static PreparedStatement ps;
    static Statement statement = null;
    static String sql;
    public DBconnectImage() {
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

    public static boolean storeImg(String strFile, String author, String Id) throws Exception {
        //written用来判断记录是否被正确写入
        boolean written = false;
        if (conn == null)
            written = false;
        else {
            int id = 0;
            File file = new File(strFile);
            //文件输入字节流
            FileInputStream fis = new FileInputStream(file);
            statement = conn.createStatement();
            try {
                //建表的Mysql语句
                sql = "create table " + author + "(Id int auto_increment primary key, Fid int, " + "File_desc varchar(100)," +
                        "Image mediumblob, foreign key(Fid) references blog(Id))auto_increment = 1";
                //这里用来判断一个表是否已经被创建过了,若没有则执行创建语句
                if(mp.isEmpty() || mp.get(author) == false) {
                    mp.put(author, true);
                    statement.executeUpdate(sql);
                }
            }catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                ps = conn.prepareStatement("select max(Id) from " + author);
                ResultSet rs = ps.executeQuery();
                if(rs != null) {
                    while(rs.next()) {
                        id = rs.getInt(1)+1;
                    }
                } else {
                    return written;
                }
                String sqlstat = "";
                //插入图片的Mysql语句,通过prepareStatement实现
                sqlstat = "insert into " + author + "(Fid, File_desc, Image) values (?,?,?)";
                ps = conn.prepareStatement(sqlstat);
                //第一列为Id
                ps.setString(1, Id);
                //第二列为文件名
                ps.setString(2, file.getName());
                //第三列为图片的字节流
                ps.setBinaryStream(3, fis, (int) file.length());
                //执行插入操作
                ps.executeUpdate();
                written = true;
            } catch (SQLException e) {
                written = false;
                System.out.println("SQLException: "
                        + e.getMessage());
                System.out.println("SQLState: "
                        + e.getSQLState());
                System.out.println("VendorError: "
                        + e.getErrorCode());
                e.printStackTrace();
            } finally {
                ps.close();
                fis.close();
            }
        }
        return written;
    }

    public static void wirteToMysql(String path, String author, String Id) {
        boolean flag = false;
        try {
            flag = storeImg(path, author, Id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(flag) {
            System.out.println("Picture uploading is successful.");
        } else {
            System.out.println("Picture uploading is failed.");
        }
    }
}
