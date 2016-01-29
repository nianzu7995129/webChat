package org.wechat.dbo.vo;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;

import org.wechat.dbo.DBAccess;
import org.wechat.dbo.DBConst;

public class GoodsType {
	/** 收货标识 */
	private String OpID;
	/** 收货类型 */
	private String OperName;

	/**
	 * 
	 * @param dba
	 * @param opType 1:(销售订单)发货类型，2:(采购订单)收货类型
	 * @param BranchId
	 * @return
	 */
	public String getGoodsTypeInfo(DBAccess dba, int opType , String BranchId) {
		JSONArray result = new JSONArray();
		try {
			String sql = "select * from OperationInfo where OpType=? and IsDel=0 and branchid like ? order by opid";
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(new Integer(opType));//  1:(销售订单)发货类型，2:(采购订单)收货类型
			if(BranchId!=null && BranchId.length()>5){
				BranchId = BranchId.substring(0, 5);
			}
			paramList.add(BranchId + "%");
			ResultSet rs = dba.executeQuery(sql, paramList);
			List<GoodsType> receiveGoodsTypeList = new ArrayList<GoodsType>();
			while (rs.next()) {
				String opID = rs.getString("OpID");// 收货标识
				String operName = rs.getString("OperName");// 收货类型
				GoodsType receiveGoodsType = new GoodsType();
				receiveGoodsType.setOpID(opID);
				receiveGoodsType.setOperName(operName);
				receiveGoodsTypeList.add(receiveGoodsType);
			}
			result = JSONArray.fromObject(receiveGoodsTypeList);
		} catch (Exception e) {
			e.printStackTrace();
			if (dba != null) {
				dba.close();
			}
		}
		return result.toString();
	}

	public String getOpID() {
		return OpID;
	}

	public void setOpID(String opID) {
		OpID = opID;
	}

	public String getOperName() {
		return OperName;
	}

	public void setOperName(String operName) {
		OperName = operName;
	}

	public static void main(String args[]) throws Exception {
		DBAccess dba = new DBAccess(true);
		GoodsType receiveGoodsType = new GoodsType();
		String result = receiveGoodsType.getGoodsTypeInfo(dba, 2,"00004");
		System.out.println("收货类型信息：" + result);
		dba.close();
	}
}
