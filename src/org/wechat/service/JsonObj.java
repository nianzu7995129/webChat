package org.wechat.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.json.JSONException;
import org.json.JSONObject;




public class JsonObj {
	// 自定义json（返回JSONObject）
	public static JSONObject createobj() throws JSONException, IOException {
		/*JSONObject obj = new JSONObject();

		// 菜单1
		JSONObject list[] = new JSONObject[3];
		list[0] = new JSONObject();
		list[0].put("type", "click");
		list[0].put("name", "报表目录目录");
		list[0].put("key", "10051");

		// 菜单2
		list[1] = new JSONObject();
		list[1].put("name", "子菜单选项");
		JSONObject childlist[] = new JSONObject[3];
		// 子菜单1
		childlist[0] = new JSONObject();
		childlist[0].put("type", "scancode_waitmsg");
		childlist[0].put("name", "扫一扫绑定");
		childlist[0].put("key", "BINDING");
		// 子菜单2
		childlist[1] = new JSONObject();
		childlist[1].put("type", "view");
		childlist[1].put("name", "BINDING");
		childlist[1].put("url", "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx3a70c3685a49a6a8&redirect_uri=http://www.shujubaosong.com/reportmis/mis2/wechat/SRWechatGateway.jsp&response_type=code&scope=snsapi_base&state=binding#wechat_redirect");
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
		JSONObject rep = new JSONObject();
		rep.put("resId", "12121");
		list[2].put("reportParam", rep);

		obj.put("button", list);
		// reportServer地址
		obj.put("reportServer",
				"http://www.shujubaosong.com/reportmis/appEntry.url");

		String a =  obj.toString();*/
		/*
		String menuFilePath = ReadConfInfo.getPropery("conf_pathRoot") + "/geeznmobile/config/weChatMenus.json";
	    IDataWriter idw = IOManagerUtils.getWriter();
	    IDataReader idr = IOManagerUtils.getReader();
	    InputStream is = idr.getInputStream(menuFilePath);
	    */
		InputStream is = null;
	      ByteArrayOutputStream baos = new ByteArrayOutputStream();
	      int i = -1;
	      while ((i = is.read()) != -1) {
	        baos.write(i);
	      }
	      System.out.println(baos.toString());
	      //转换为微信格式json
	      JSONObject s1 = new JSONObject(baos.toString());
		 
	      s1.put("reportServer", "http://www.shujubaosong.com/reportmis/appEntry.url");
	      
	    return s1;
	}
	/*
	 * public static void main(String[] args) { System.out.print(createobj()); }
	 */
}
