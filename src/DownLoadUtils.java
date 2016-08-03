/**
 * Created by tcgogogo on 16/8/3.
 */
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DownLoadUtils {

    public static String downLoadPic(String picUrl, String author, String title) throws IOException {
        URL url = new URL(picUrl);
        URLConnection uc = url.openConnection();
        //得到图片的字节流
        InputStream is = uc.getInputStream();
        //得到图片名
        String picName = picUrl.split("/")[picUrl.split("/").length - 1];
        if(picName.indexOf("jpg") == -1)
            picName += ".jpg";
        //对不同作者的不同博文分别创建文件夹
        File dir = new File("/Users/tcgogogo/desktop/image/" + author + "/" + title);
        if(!dir.exists()){
            //创建多级目录
            dir.mkdirs();
        }
        String path = "/Users/tcgogogo/desktop/image/" + author + "/" + title + "/" + picName;
        File imageFile = new File(path);
        //文件输出字节流对象
        FileOutputStream out = new FileOutputStream(imageFile);
        int i = 0;
        //将图片的字节流输出到文件
        while ((i = is.read()) != -1) {
            out.write(i);
        }
        is.close();
        out.close();
        return path;
    }
}

