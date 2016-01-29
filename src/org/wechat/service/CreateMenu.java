package org.wechat.service;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wechat.utils.AccessTokenUtil;

public class CreateMenu {
	public static String createMenu(String params, String accessToken) {
		StringBuffer bufferRes = new StringBuffer();
		String err = null;
		try {
			URL realUrl = new URL(
					"https://api.weixin.qq.com/cgi-bin/menu/create?access_token="
							+ accessToken);
			HttpURLConnection conn = (HttpURLConnection) realUrl
					.openConnection();

			// 连接超时
			conn.setConnectTimeout(25000);
			// 读取超时 --服务器响应比较慢，增大时间
			conn.setReadTimeout(25000);

			HttpURLConnection.setFollowRedirects(true);
			// 请求方式
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestProperty("Referer", "https://api.weixin.qq.com/");
			conn.setRequestProperty("Accept-Charset", "utf-8");
			conn.setRequestProperty("contentType", "utf-8");
			conn.connect();
			// 获取URLConnection对象对应的输出流
			OutputStreamWriter out = new OutputStreamWriter(
					conn.getOutputStream(),"utf-8");
			// 发送请求参数
			// out.write(URLEncoder.encode(params,"UTF-8"));
			System.out.println(params);
			out.write(params);
			out.flush();
			out.close();

			InputStream in = conn.getInputStream();
			BufferedReader read = new BufferedReader(new InputStreamReader(in,
					"utf-8"));
			String valueString = null;
			while ((valueString = read.readLine()) != null) {
				bufferRes.append(valueString);
			}
			err = bufferRes.toString();
			System.out.println(err);
			
			
			in.close();
			if (conn != null) {
				// 关闭连接
				conn.disconnect();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return err;
	}
	
	public static String changejson() throws JSONException, IOException
	{
		String s = null;
		//获取菜单json
		String menuFilePath = "";//ReadConfInfo.getPropery("conf_pathRoot") + "/geeznmobile/config/weChatMenus.json";
	    //IDataWriter idw = IOManagerUtils.getWriter();
	    //IDataReader idr = IOManagerUtils.getReader();
	    InputStream is = null;//idr.getInputStream(menuFilePath);
	    
	      ByteArrayOutputStream baos = new ByteArrayOutputStream();
	      int i = -1;
	      while ((i = is.read()) != -1) {
	        baos.write(i);
	      }
	      System.out.println(baos.toString());
	      //转换为微信格式json
	      JSONObject s1 = new JSONObject(baos.toString());
	      String menus = s1.getString("button");
	      JSONArray menu = null;
		  menu = new JSONArray(menus);
		  String temp = null;
		  JSONArray menutemp = null;
		  JSONObject childmenutemp = null;
		  String type = null;
		  String url1 = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx3a70c3685a49a6a8&redirect_uri=http://www.shujubaosong.com/reportmis/mis2/wechat/SRWechatGateway.jsp&response_type=code&scope=snsapi_base&state=resid#wechat_redirect";
		  String url2 = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx3a70c3685a49a6a8&redirect_uri=http://www.shujubaosong.com/reportmis/mis2/wechat/SRWechatGateway.jsp&response_type=code&scope=snsapi_base&state=binding#wechat_redirect";
		  String res = null;
		  String u = null;
		  
		  for(int m = 0;m<(menu.length()-1)&&i<3;m++)
		  {
			  temp = menu.getString(m);
			  if(temp.indexOf("sub_button")>0)
			  {
				  JSONObject menutemp1 = new JSONObject(temp);
				  String ms = menutemp1.getString("sub_button");
				  menutemp = new JSONArray(ms);
				  for(int n = 0;n<menutemp.length();n++)
				  {
					  temp = menutemp.getString(n);
					  childmenutemp = new JSONObject(temp);
					  type = childmenutemp.getString("type");
					  if(type.equals("click"))
					  {
						  childmenutemp.remove("value");
						  menutemp.put(n, childmenutemp);
					  }
					  else if(type.equals("view") && childmenutemp.getString("name").equals("登    录"))
					  {
						  childmenutemp.remove("value");
						  childmenutemp.remove("key");
						  childmenutemp.put("url", url2);
						  menutemp.put(n, childmenutemp);
					  }
					  else if(type.equals("scancode_push"))
					  {
						  childmenutemp.put("type", "scancode_waitmsg");
						  menutemp.put(n, childmenutemp);
					  }
					  else
					  {
						  res = childmenutemp.getString("key");
						  u = url1.replace("resid", res);
						  System.out.println(url1);
						  childmenutemp.remove("value");
						  childmenutemp.remove("key");
						  childmenutemp.put("url", u);
						  menutemp.put(n, childmenutemp);
					  }
					  menutemp1.put("sub_button", menutemp);
				  }
				  menu.put(m, menutemp1);
			  }
			  else
			  {
				  childmenutemp = new JSONObject(temp);
				  type = childmenutemp.getString("type");
				  if(type.equals("click"))
				  {
					  childmenutemp.remove("value");
					  menu.put(m, childmenutemp);
				  }
				  else if(type.equals("view") && childmenutemp.getString("key").equals("登    录"))
				  {
					  childmenutemp.remove("value");
					  childmenutemp.remove("key");
					  childmenutemp.put("url", url2);
					  menu.put(m, childmenutemp);
				  }
				  else if(type.equals("scancode_push"))
				  {
					  childmenutemp.put("type", "scancode_waitmsg");
					  menu.put(m, childmenutemp);
				  }
				  else
				  {
					  res = childmenutemp.getString("key");
					  u = url1.replace("resid", res);
					  System.out.println(childmenutemp.toString());
					  childmenutemp.remove("value");
					  childmenutemp.remove("key");
					  childmenutemp.put("url", u);
					  menu.put(m, childmenutemp);
				  }
			  }
		  }
		  menu.remove(menu.length()-1);
		  JSONObject result = new JSONObject();
		  result.put("button", menu);
	      s = result.toString();
	      System.out.println(s);
	      return s;
	}
	
	//测试使用
	public static String cccc() throws JSONException{
		JSONObject obj = new JSONObject();

		// 菜单1
		JSONObject list[] = new JSONObject[3];
		list[0] = new JSONObject();
		list[0].put("type", "click");
		list[0].put("name", "报表目录1");
		list[0].put("key", "10052");

		// 菜单2
		list[1] = new JSONObject();
		list[1].put("name", "子菜单");
		JSONObject childlist[] = new JSONObject[3];
		// 子菜单1
		childlist[0] = new JSONObject();
		childlist[0].put("type", "click");
		childlist[0].put("name", "Submenu1");
		childlist[0].put("key", "GEEZN_CLICK_2");
		// 子菜单2
		childlist[1] = new JSONObject();
		childlist[1].put("type", "click");
		childlist[1].put("name", "Submenu2");
		childlist[1].put("key", "GEEZN_CLICK_3");
		// 子菜单3
		childlist[2] = new JSONObject();
		childlist[2].put("type", "click");
		childlist[2].put("name", "Submenu3");
		childlist[2].put("key", "GEEZN_CLICK_4");
		list[1].put("sub_button", childlist);

		// 菜单3
		list[2] = new JSONObject();
		list[2].put("type", "view");
		list[2].put("name", "现金日报表");
		list[2].put("url", "http://www.baidu.com/");

		obj.put("button", list);
		//System.out.println(obj.toString());
		String s = obj.toString();
		
		return s;
	}

	/**
	 * @param args
	 * @throws JSONException
	 * @throws IOException 
	 */
	public static void create() throws JSONException, IOException {
				String s = changejson();
				//System.out.println(TokenThread.appid);
				//System.out.println("aaaaaaa");
				String token = AccessTokenUtil.getAccessToken().getToken();
				createMenu(s,token);
	}
    
	public static void main(String []args) throws JSONException, IOException
	{
		create();
	}
}
