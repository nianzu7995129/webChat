package org.wechat.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.FileOutputStream;
import javax.servlet.http.HttpServletRequest;

/** 操作auth.json */
public class AuthUtil {
	/**
	 * 判断登录是否成功
	 * @param username	用户名
	 * @param password	密码
	 * @return
	 * @throws JSONException
	 */
	public static String isLoginSuccess (String username,String password,HttpServletRequest request) throws JSONException{
		String authContent = FileUtil.readAuthFileByChars();
		JSONObject jo = new JSONObject(authContent);
		JSONArray ja = jo.getJSONArray("users");
		int size = ja.length();
		boolean isError = true;
		String info = "";
		for(int i=0;i<size;i++){
			JSONObject tjo = ja.getJSONObject(i);
			String tmpUserName = tjo.getString("un");
			if(username.equals(tmpUserName)){
				String tmpPwd = tjo.getString("pd");
				if(password.equals(tmpPwd)){
					isError = false;
					info = "登录成功";
					break;
				}else{
					isError = true;
					info = "密码错误";
					break;
				}
			}
		}
		if("".equals(info)){
			info = "用戶名不存在";
		}
		if(!isError){
			String OperatorID = "00002";//默认
			if(username.toLowerCase().equals("admin")){
				OperatorID = "00000";
			}else{
				OperatorID = username;
			}
			request.getSession().setAttribute("OperatorID", OperatorID);
		}
		return resultJson(isError,info);
	}
	
	/** 读取auth.json的内容 */
	public static String readUserInfo(){
		return resultJson(false,FileUtil.readAuthFileByChars());
	}
	
	/**
	 * 修改用户权限
	 * @param username 
	 * @param auth 如,xsdd,spxsfxb,yfkcx,yskcx
	 * @return
	 * @throws Exception 
	 */
	public static String saveUserAuth(String username,String auth) throws Exception{
		String authContent = FileUtil.readAuthFileByChars();
		JSONObject jo = new JSONObject(authContent);
		JSONArray ja = jo.getJSONArray("users");
		int size = ja.length();
		for(int i=0;i<size;i++){
			JSONObject tjo = ja.getJSONObject(i);
			String tmpUserName = tjo.getString("un");
			if(username.equals(tmpUserName)){
				tjo.put("auth", auth);//修改为现有权限
				break;
			}
		}
		boolean isError = writeAuthConfig2File(jo.toString());
		String info = "用户权限修改成功";
		if(isError){
			info = "用户权限修改失败";
		}
		return resultJson(isError,info);
	}
	
	/**
	 * 删除用户
	 * @param username 
	 * @return
	 * @throws Exception 
	 */
	public static String deleteUser(String username) throws Exception{
		String authContent = FileUtil.readAuthFileByChars();
		JSONObject jo = new JSONObject(authContent);
		JSONArray ja = jo.getJSONArray("users");
		int size = ja.length();
		for(int i=size-1;i>=0;i--){
			JSONObject tjo = ja.getJSONObject(i);
			String tmpUserName = tjo.getString("un");
			if(username.equals(tmpUserName)){
				ja.remove(i);
				break;
			}
		}
		boolean isError = writeAuthConfig2File(jo.toString());
		String info = "用户删除成功";
		if(isError){
			info = "用户删除失败";
		}
		return resultJson(isError,info);
	}
	
	/**
	 * 增加用户或修改管理员密码，修改管理员密码，仅为第一次管理员密码为空的情况
	 * @param username 
	 * @return
	 * @throws Exception 
	 */
	public static String addUser(String username,String pwd) throws Exception{
		String authContent = FileUtil.readAuthFileByChars();
		JSONObject jo = new JSONObject(authContent);
		JSONArray ja = jo.getJSONArray("users");
		int size = ja.length();
		boolean isError = false;
		String info = "用户添加成功";
		for(int i=size-1;i>=0;i--){
			JSONObject tjo = ja.getJSONObject(i);
			String tmpUserName = tjo.getString("un");
			if(username.equals(tmpUserName)){
				if(!"admin".equals(username)){
					isError = true;
					break;
				}else{
					tjo.put("pd", pwd);
				}
			}
		}
		if(isError){
			info = "已存在相同用户";
		}else{
			if(!"admin".equals(username)){
				JSONObject njo = new JSONObject();
				njo.put("un", username);
				njo.put("pd", pwd);
				njo.put("auth", "");
				ja.put(njo);
			}
			isError = writeAuthConfig2File(jo.toString());
		}
		return resultJson(isError,info);
	}
	
	/** 
	 * 验证是否有权访问 
	 * @param username	用户名 如00002
	 * @param report 报表名 如cgdd,kczkb
	 * @return
	 */
	public static boolean checkValid(String username,String report) {
		if(username.toLowerCase().equals("00000")) return true;// 00000即为admin
		boolean hasValid = false;
		try{
			String authContent = FileUtil.readAuthFileByChars();
			JSONObject jo = new JSONObject(authContent);
			JSONArray ja = jo.getJSONArray("users");
			int size = ja.length();
			for(int i=size-1;i>=0;i--){
				JSONObject tjo = ja.getJSONObject(i);
				String tmpUserName = tjo.getString("un");
				if(username.equals(tmpUserName)){
					String auth = tjo.getString("auth");
					if(auth.indexOf(report)!=-1){
						hasValid = true;
						break;
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return hasValid;
	}
	
	private static String resultJson(boolean isError, String result) {
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
	
	/**
	 * 修改auth.json文件全部內容
	 * @param content
	 * @return
	 * @throws Exception
	 */
	private static synchronized boolean writeAuthConfig2File(String content) throws Exception {
		FileOutputStream out = null;
		boolean isError = false;
		try{
			out = new FileOutputStream(FileUtil.getAutoFilePath(),false); //如果追加方式用true        
			StringBuffer sb = new StringBuffer(content);
			out.write(sb.toString().getBytes("utf-8"));//注意需要转换对应的字符集
		}catch(Exception e){
			e.printStackTrace();
			isError = true;
			throw new Exception("权限设置文件写入失败");
		}finally{
			if(out!=null){
				out.close();
			}
		}
		return isError;
	}

}
