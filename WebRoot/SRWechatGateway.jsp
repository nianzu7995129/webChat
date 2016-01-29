<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>

<%@page import="com.runqianapp.utils.WechatUtil"%>
<%@page import="org.weichat.course.util.HttpUtil"%>
<%@page import="org.json.JSONObject"%>
<%@page import="org.json.JSONArray"%>
<%@page import="org.json.JSONException"%>
<%@page import="org.weichat.course.service.JsonObj"%>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>SRWechatGateway</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="apple-mobile-web-app-capable" content="yes" />
<meta name="apple-mobile-web-app-status-bar-style" content="black" />
<meta name="viewport" content="user-scalable=no, width=device-width, initial-scale=1.0, maximum-scale=1.0"/>
	
  </head>
  
  <body>
       <%  
       
        String resid = null;
		String openId = null;
		
		String code = request.getParameter("code");
		String state = request.getParameter("state");
		System.out.println(state);

		String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=wx3a70c3685a49a6a8&secret=cc8846dd241ba772b4946bdd3b0b62ac&code="
				+ code + "&grant_type=authorization_code";

		String jsonStr = HttpUtil.getUrl(url);
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(jsonStr);
			openId = jsonObject.getString("openid");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(state.equals("binding"))
		{
			String url2 = "http://www.shujubaosong.com/reportmis/mis2/wechat/binding.jsp?openId=" + openId;
			response.sendRedirect(url2);
		}
		else
		{
			//JSONObject obj = JsonObj.createobj();
			//JSONArray menu = obj.getJSONArray("button");
			//resid = menu.getJSONObject(2).getJSONObject("reportParam").getString("resId");
			
			resid = state;
			
			WechatUtil weicht = new WechatUtil();
			String name = weicht.getUserName(openId);
			
			Map map = new HashMap<String, String>();
			map.put("username", name);
			map.put("resID",resid);
			map.put("patternID", "WECHATSR");
		
			System.out.println(openId);
			System.out.println(resid);
			WechatUtil weichat = new WechatUtil();
			String url1 = "http://www.shujubaosong.com/reportmis/appEntry.url?params=" + weichat.encrypt(map);
			response.sendRedirect(url1);
		}
		
    	  %>
    
  </body>
</html>