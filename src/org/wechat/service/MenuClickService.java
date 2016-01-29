package org.wechat.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wechat.message.request.ReqEventClick;
import org.wechat.message.response.Article;
import org.wechat.message.response.RespNewsMessage;
import org.wechat.utils.MessageUtil;

public class MenuClickService {

	public static RespNewsMessage MenuClick(JSONObject resources,
			ReqEventClick reqeventClick, String respContent) throws JSONException, IOException {
		System.out.println("clickService");
		// 返回消息的信息
		RespNewsMessage respnewsMessage = new RespNewsMessage();
		respnewsMessage.setToUserName(reqeventClick.getFromUserName());
		respnewsMessage.setFromUserName(reqeventClick.getToUserName());
		respnewsMessage.setCreateTime(new Date().getTime());
		respnewsMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_NEWS);

		// news列表
		List<Article> articleList = new ArrayList<Article>();
		// System.out.println("a");
		// 通过getResourceList（）得到的信息
		JSONArray re = resources.getJSONArray("resources");
		System.out.println(re.toString());
		// String re = resources.getString("resource");
		// re = re.substring(1, re.length()-1);
		// JSONObject r = JSONObject.fromString(re);
		String reporturl = JsonObj.createobj().getString("reportServer");
		Article article[] = new Article[re.length()];

		for (int i = 0; i < re.length() && i < 10; i++) {
			// 对list中各项内容进行设置
			//System.out.println(i);
			String title = re.getJSONObject(i).getString("Title");
			String description = null;
			if(re.getJSONObject(i).toString().indexOf("Description") > 0)
			{
				description = re.getJSONObject(i).getString("Description");
			}
			else
			{
				description = "暂无资源描述";
			}
			String picurl = "http://www.shujubaosong.com/reportmis/mis2/images/"+ re.getJSONObject(i).getString("picUrl");
			//System.out.println(picurl);
			
			String url = null;
			if(re.getJSONObject(i).toString().indexOf("reportParam") > 0)
			{
				JSONObject reportParam = re.getJSONObject(i).getJSONObject("reportParam");
				String resid = reportParam.getString("resId");
				//url加密
				Map map = new HashMap<String, String>();
				map.put("openId", reqeventClick.getFromUserName());
				map.put("resID",resid);
				map.put("patternID", "WECHATSR");
				//WechatUtil weichat = new WechatUtil();
				//url = reporturl + "?params=" + weichat.encrypt(map);
			}
			if(re.getJSONObject(i).toString().indexOf("url") > 0)
			{
				url = "http://www.shujubaosong.com/reportmis" + re.getJSONObject(i).getString("url");
			}
			System.out.println(url);
			article[i] = new Article();
			article[i].setTitle(title);
			article[i].setDescription(description);
			article[i].setPicUrl(picurl);
			article[i].setUrl(url);

			articleList.add(article[i]);
		}
		// 设置图文消息个数
		respnewsMessage.setArticleCount(articleList.size());
		// 设置图文消息包含的图文集合
		respnewsMessage.setArticles(articleList);

		return respnewsMessage;

	}
	
	/*
	 * public static void main(String[] args) throws JSONException { 
	 
		JSONObject a =getResourceList("a","a"); 
		String g = a.getString("resource");
		System.out.print(g); 
	}*/
	

}
