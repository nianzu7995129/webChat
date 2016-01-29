package org.wechat.utils.base64;
import javax.servlet.http.HttpServletRequest;



public class Base64Util {
	
	/**
	 * @author Bruce
	 * @date 2009-8-14
	 * @param paramValue 要加密的变量
	 * 说明：加密方法
	 */
	
	public static String Base64Encode(String paramValue){
		String result = "";
		if(paramValue!=null && paramValue.trim().length()!=0){
			result = Base64.encode(paramValue,"UTF-8");
			if(result.indexOf("+")!=-1 || result.indexOf("/")!=-1 || result.indexOf("=")!=-1)
				result = result.replaceAll("[+]","_").replaceAll("[/]","`").replaceAll("[=]",",");
		}
		return result;
	}
	/**
	 * @author Bruce
	 * @param paramName,request
	 * @return 解密完的结果
	 * 说明：解密方法
	 */
	public static String Base64Decode(String paramName , HttpServletRequest request ) {
		String result = "";
		String paramValue = request.getParameter(paramName);
		if(paramValue!=null && paramValue.trim().length()!=0){
			if(paramValue.indexOf("_")!=-1 || paramValue.indexOf("`")!=-1 || paramValue.indexOf(",")!=-1)
				paramValue = paramValue.replaceAll("[_]","+").replaceAll("[`]","/").replaceAll("[,]","=");
			result = new String(Base64.decode(paramValue,"UTF-8"));
		}
		return result;
	}
	/**
	 * @author Bruce
	 * @param paramValue
	 * @return 解密完的结果
	 * 说明：解密方法
	 */
	public static String Base64Decode(String paramValue) {
		String result = "";
		if(paramValue!=null){
			if(paramValue.indexOf("_")!=-1 || paramValue.indexOf("`")!=-1 || paramValue.indexOf(",")!=-1)
				paramValue = paramValue.replaceAll("[_]","+").replaceAll("[`]","/").replaceAll("[,]","=");
			result = new String(Base64.decode(paramValue,"UTF-8"));
		}
		return result;
	}
	
	public static void main(String[] args) {
		System.out.println(Base64Util.Base64Encode("/mis2"));
		System.out.println(Base64Util.Base64Decode("L_aWsOW7uuaKpeihqF96aGFuZ3Nhbl8zVU5TQVZFRA,,"));
	}
}
