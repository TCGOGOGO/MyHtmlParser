/**
 * Created by tcgogogo on 16/8/3.
 */
import com.mysql.DBconnect;
import org.htmlparser.util.ParserException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by tcgogogo on 16/6/29.
 */
public class UI {
    static String stdate = "";
    static String eddate = "";
    static String keyword = "";
    static String author = "";
    static String str = "";
    public static void main(String[] args) {
        JFrame frame = new JFrame("HtmlParser");
        JButton button1 = new JButton("爬取信息");
        frame.setLayout(null);
        frame.setSize(500, 500);
        button1.setBounds(200, 50, 100, 50);
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HtmlParser htmlParser = new HtmlParser();
                try {
                    htmlParser.getData();
                } catch (ParserException e1) {
                    e1.printStackTrace();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        frame.add(button1);

        JRadioButton DateOpt = new JRadioButton("按日期查询 (年-月-日 时:分:秒)");
        DateOpt.setBounds(50, 120, 300, 25);
        frame.add(DateOpt);

        JLabel DateOpt1 = new JLabel("开始时间:");
        DateOpt1.setBounds(60, 150, 60, 25);
        frame.add(DateOpt1);

        JTextField DateTxt1 = new JTextField();
        DateTxt1.setBounds(120, 150, 120, 25);
        frame.add(DateTxt1);

        JLabel DateOpt2 = new JLabel("结束时间:");
        DateOpt2.setBounds(250, 150, 60, 25);
        frame.add(DateOpt2);

        JTextField DateTxt2 = new JTextField();
        DateTxt2.setBounds(310, 150, 120, 25);
        frame.add(DateTxt2);

        JRadioButton TagOpt = new JRadioButton("按关键词查询");
        TagOpt.setBounds(50, 200, 120, 25);
        frame.add(TagOpt);

        JLabel TagOpt1 = new JLabel("输入一个关键词:");
        TagOpt1.setBounds(60, 230, 100, 25);
        frame.add(TagOpt1);

        JTextField TagTxt = new JTextField();
        TagTxt.setBounds(160, 230, 100, 25);
        frame.add(TagTxt);

        JRadioButton AuthorOpt = new JRadioButton("按博主名查询");
        AuthorOpt.setBounds(50, 280, 120, 25);
        frame.add(AuthorOpt);

        JLabel AuthorOpt1 = new JLabel("输入博主名称:");
        AuthorOpt1.setBounds(60, 310, 90, 25);
        frame.add(AuthorOpt1);

        JTextField AuthorTxt = new JTextField();
        AuthorTxt.setBounds(150, 310, 100, 25);
        frame.add(AuthorTxt);

        JButton button2 = new JButton("查询");
        button2.setBounds(200, 370, 100, 50);
        frame.add(button2);

        //将三个单选按钮加入一个按钮组
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(DateOpt);
        buttonGroup.add(TagOpt);
        buttonGroup.add(AuthorOpt);

        //这里的str用来告诉查询的button按钮选中的是哪个单选键,下同
        DateOpt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(DateOpt.isSelected()){
                    str = "按日期查询 (年-月-日 时:分:秒)";
                    //System.out.println("1");
                }
            }
        });

        TagOpt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(TagOpt.isSelected()){
                    str = "按关键词查询";
                    //System.out.println("2");
                }
            }
        });

        AuthorOpt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(AuthorOpt.isSelected()) {
                    str = "按博主名查询";
                    //System.out.println("3");
                }
            }
        });

        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(str == "按日期查询 (年-月-日 时:分:秒)") {
                    try {
                        stdate = DateTxt1.getText().trim();
                        eddate = DateTxt2.getText().trim();
                        //System.out.println("st = " + stdate + "ed = " + eddate);
                        DBconnect db = new DBconnect();
                        db.queryByDate(stdate, eddate);
                    } catch (SQLException e1) {
                        e1.printStackTrace();}
                }
                else if(str == "按关键词查询") {
                    try {
                        keyword = TagTxt.getText().trim();
                        //System.out.println("keyword = " + keyword);
                        DBconnect db = new DBconnect();
                        db.queryByTag(keyword);
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }
                else if(str == "按博主名查询") {
                    try {
                        author = AuthorTxt.getText().trim();
                        //System.out.println("author = " + author);
                        DBconnect db = new DBconnect();
                        db.queryByAuthor(author);
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        frame.setVisible(true);
    }
}

