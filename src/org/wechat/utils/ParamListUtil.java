package org.wechat.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.wechat.dbo.vo.param.SQLParam;
import java.util.*;
import org.wechat.dbo.vo.param.*;

public class ParamListUtil {
	public static List<SQLParam>JSONArrayToList(JSONArray ja){
		List<SQLParam> sqlParamList = new ArrayList<SQLParam>();
		if(ja==null) return sqlParamList;
		int len = ja.length();
		for(int i=0;i<len;i++){
			JSONObject jo = ja.optJSONObject(i);
			int index = jo.optInt("index");
			Object value = jo.opt("value");
			int dataType = jo.optInt("dataType");
			if(jo.has("value")){
				InParam inParam = new InParam(index,value);
				sqlParamList.add(inParam);
			}else{
				OutParam outParam = new OutParam(index,dataType);
				sqlParamList.add(outParam);
			}
		}
		return sqlParamList;
	}
}
