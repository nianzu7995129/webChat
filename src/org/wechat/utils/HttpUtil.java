package org.wechat.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


public class HttpUtil {

	public static String getUrl(String url) throws IOException{
        String result = null;
        BufferedReader read=null;//读取访问结果
        
        URL realurl;
		try {
			realurl = new URL(url);
			URLConnection conn=realurl.openConnection();
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:21.0) Gecko/20100101 Firefox/21.0");
			conn.setRequestProperty("Referer", "https://api.weixin.qq.com/");
			conn.connect();
			
            // 定义 BufferedReader输入流来读取URL的响应
            read = new BufferedReader(new InputStreamReader(
                    conn.getInputStream(),"UTF-8"));
            String line;//循环读取
            StringBuilder sb = new StringBuilder();
            while ((read.read()) != -1) {
            	sb.append(read.readLine());
            }
            result = new String(sb);
            result = "{" + result;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
        
        return result;
    }
	
	
	
}
