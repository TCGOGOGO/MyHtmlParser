/**
 * Created by tcgogogo on 16/8/3.
 */
import java.io.*;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.mysql.DBconnect;
import com.mysql.DBconnectImage;
import org.htmlparser.beans.StringBean;
import org.htmlparser.util.ParserException;

public class HtmlParser {

    static Set<String> urlSet = new HashSet<>();
    static String tmp = "";
    static String title, date, tag, author, content;

    public static String getText(String url) throws ParserException {
        StringBean sb = new StringBean();
        sb.setLinks(false);     //设置不需要得到页面所包含的链接信息
        sb.setReplaceNonBreakingSpaces(true);   //设置将不间断空格由正规空格所替代
        sb.setCollapse(true);   //设置将一序列空格由一个单一空格所代替
        sb.setURL(url);     //传入要解析的URL
        return sb.getStrings();     //返回解析后的网页纯文本信息
    }
    static LinkFilter linkFilter = new LinkFilter() {
        @Override
        public boolean accept(String url) {
            if (url.contains("sina")) {
                return true;
            } else {
                return false;
            }
        }
    };

    public static void getLinks() throws ParserException {
        String[][] url = new String[15][2];
        String[] urlPrifix = new String[10];    //存目录链接前缀
        urlPrifix[0] = "http://blog.sina.com.cn/s/articlelist_1195312871_0_";
        //代表要抓取的博主数
        int blognum = 1;
        //代表要抓取的目录页数
        int pagenum = 1;
        for(int i = 0; i < blognum; i++) {
            for(int j = 0; j < pagenum; j++) {
                //循环得到各目录链接
                url[i][j] = urlPrifix[i] + (j + 1) + ".html";
                Set<String> urlSetInit = GetSubLinks.extractLinks(url[i][j], linkFilter);
                Iterator<String> it1 = urlSetInit.iterator();
                while (it1.hasNext()) {
                    String tmpurl = it1.next();
                    //过滤出博文链接
                    if (tmpurl.indexOf("http://blog.sina.com.cn/s/") == 0) {
                        tmp = tmpurl;
                        //加入链接集合
                        urlSet.add(tmpurl);
                        System.out.println(tmpurl);
                    }
                }
            }
        }
    }

    public static String strRev(String str) {
        StringBuffer sb = new StringBuffer(str);
        String newstr = sb.reverse().toString();
        return newstr;
    }

    public static boolean getTitleAndDate(String t) {
        int i = t.indexOf("正文");
        if (i == -1)
            return false;
        while (i < t.length()) {
            if (t.charAt(i) == '\n')
                break;
            i++;
        }
        i++;
        title = "";
        date = "";
        while (i < t.length()) {
            if (t.charAt(i) == '\n')
                break;
            i++;
        }
        i -= 2;
        for (; t.charAt(i) != '('; i--) {
            date += t.charAt(i);
        }
        i--;
        for (; t.charAt(i) != '\n'; i--) {
            title += t.charAt(i);
        }
        title = strRev(title);
        date = strRev(date);
        title = title.trim();
        date = date.trim();
        if (title.length() > 60)
            return false;
        if (!title.isEmpty()) {
            System.out.print("文章标题: " + title + "\n" + "发表时间: " + date + "\n");
            return true;
        }
        return false;
    }

    public static boolean getTag(String t) {
        tag = "";
        int st = t.indexOf("标签"), ed = t.indexOf("分类");
        if ((ed - st > 3 && ed - st <= 30) && ed != -1) {
            for (int i = st + 3; i < Math.min(t.length(), ed); i++) {
                if (t.charAt(i) == '\n')
                    tag += " ";
                else
                    tag += t.charAt(i);
            }
        }
        tag = tag.trim();
        System.out.print("文章标签: " + tag + "\n");
        if(!tag.isEmpty())
            return true;
        return false;
    }

    public static void getAuthor(String t) {
        author = "";
        int a = t.indexOf("_新浪博客");
        if(a < 200) {
            a --;
            while(t.charAt(a) != '_') {
                author += t.charAt(a);
                a --;
            }
            author = strRev(author);
        }
        author = author.trim();
        System.out.print("博主名: " + author + "\n");
    }

    public static void getContent(String t) {
        content = "";
        int st = 0, ed = 0;
        int flpos = t.indexOf("分类");
        int zzpos = t.indexOf("转载");
        if(flpos > 0 && flpos > st)
            st = flpos;
        if(zzpos > 0 && zzpos > st)
            st = zzpos;
        int fxpos = t.indexOf("分享");
        if(fxpos == -1)
            return;
        ed = fxpos - 1;
        while(t.charAt(st) != '\n')
            st ++;
        for(int i = st; i <= ed; i++) {
            content += t.charAt(i);
        }
        content = content.trim();
    }

    public static void getData() throws ParserException, SQLException, IOException {
        //得到网页的博客子链接
        getLinks();
        Iterator<String> it = urlSet.iterator();
        int count = 0;
        //连接数据库,进行与文本相关的数据库操作
        DBconnect db = new DBconnect();
        //链接数据库,进行与图片相关的数据库操作
        DBconnectImage dbi = new DBconnectImage();
        //it是博文子链接集合的迭代器
        while(it.hasNext()) {
            Set<String> imageUrlSet = new HashSet<>();
            String subUrl = it.next();
            //得到图片的子链接存入图片子链接集合
            imageUrlSet = GetImageLinks.extractLinks(subUrl, linkFilter);
            Iterator<String> it2 = imageUrlSet.iterator();
            //得到正文的纯文本信息
            String text = getText(subUrl);
            //若正确得到文章的标题和日期则继续后续操作
            if (getTitleAndDate(text)) {
                //手动设置延迟,防止IP被封
                int delay = 0;
                while(delay < 10000000)
                    delay ++;
                //得到标签
                getTag(text);
                //得到博主名
                getAuthor(text);
                //得到正文内容
                getContent(text);
                //将信息写入数据库
                String id = db.WriteToMysql(author, title, date, tag, content);
                //遍历图片链接集合处理图片链接
                while(it2.hasNext()) {
                    String picUrl = it2.next();
                    //System.out.println("url = " + picUrl);
                    //下载图片并得到存放于的本地地址
                    String path = DownLoadUtils.downLoadPic(picUrl, author, title);
                    //System.out.println("imagepath = " + path);
                    //将图片写入数据库
                    dbi.wirteToMysql(path, author, id);
                }
                //计数器用于调试
                count ++;
            }
            if (count == 5)
                break;
            //System.out.println();
        }
        System.out.println("count = " + count);
    }
}
