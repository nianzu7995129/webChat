package org.wechat.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONObject;
import org.wechat.vo.AccessToken;


public class AccessTokenUtil {

	public static AccessToken getAccessToken() throws IOException, JSONException{
		AccessToken token = new AccessToken();
		String result = null;
        BufferedReader read=null;//读取访问结果
        JSONObject js = null;
        
        URL realurl;
		try {
			realurl = new URL("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx3a70c3685a49a6a8&secret=cc8846dd241ba772b4946bdd3b0b62ac");
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
            System.out.println(result);
            js = new JSONObject(result);
            token.setToken(js.getString("access_token"));
            token.setExpiresIn(js.getInt("expires_in"));
            
            System.out.println(token.getToken());
            System.out.println(token.getExpiresIn());
            
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		
		return token;
	}
	
}
