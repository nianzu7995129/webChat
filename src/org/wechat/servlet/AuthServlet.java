package org.wechat.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.wechat.utils.base64.Base64Util;
import org.wechat.utils.AuthUtil;

public class AuthServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final String action_login = "action_login";// 登录
	private static final String action_get_userlist = "action_get_userlist";// 获得用户列表
	private static final String action_save_auth = "action_save_auth";// 保存用户权限
	private static final String action_delete_user = "action_delete_user";// 删除用户
	private static final String action_add_user = "action_add_user";// 增加用户

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String action = request.getParameter("action");
		String result = "";
		Boolean isError = false;
		try {
			if (action_login.equals(action)) {
				String username = Base64Util.Base64Decode("un", request);
				String password = Base64Util.Base64Decode("pd", request);
				result = AuthUtil.isLoginSuccess(username, password,request);
			} else if (action_get_userlist.equals(action)) {
				result = AuthUtil.readUserInfo();
			} else if (action_save_auth.equals(action)) {
				String username = Base64Util.Base64Decode("un", request);
				String auth = Base64Util.Base64Decode("auth", request);
				result = AuthUtil.saveUserAuth(username, auth);
			} else if (action_delete_user.equals(action)) {
				String username = Base64Util.Base64Decode("un", request);
				result = AuthUtil.deleteUser(username);
			} else if (action_add_user.equals(action)) {
				String username = Base64Util.Base64Decode("un", request);
				String password = Base64Util.Base64Decode("pd", request);
				result = AuthUtil.addUser(username, password);
			} else {
				result = "未知的action:" + action;
				isError = true;
			}
		} catch (Exception e) {
			isError = true;
			result = resultJson(isError, e.getMessage());
		}
		response.getWriter().print(result);
	}
	
	private String resultJson(boolean isError, String result) {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("\"isError\"").append(":\"").append(isError).append("\"");
		sb.append(",");
		try {
			new JSONObject(result);
			sb.append("\"result\"").append(":").append(result).append("");
		} catch (Exception e) {
			try {
				new org.json.JSONArray(result);
				sb.append("\"result\"").append(":").append(result).append("");
			} catch (Exception ee) {
				sb.append("\"result\"").append(":\"").append(result).append("\"");
			}
		}
		sb.append("}");
		return sb.toString();
	}
}
