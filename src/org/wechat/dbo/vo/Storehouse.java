package org.wechat.dbo.vo;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.wechat.dbo.DBAccess;
import org.wechat.dbo.DBConst;

public class Storehouse {
	/** 仓库标识 */
	private String TypeID;
	/** 仓库名称 */
	private String FullName;
	
	public static final String sendType = "send";
	public static final String receiveType = "receive";

	public String getDefaultReciveStorehouseInfo(DBAccess dba, String BranchID, String OperatorID ,String type) {
		JSONObject result = new JSONObject();
		try {
			//采购订单默认收货仓库
			String sql = "SELECT TypeID,FullName FROM dbo.Stock  WHERE BranchID=? AND Deleted='0' AND IsDefault='1'  AND EXISTS(SELECT 1 FROM dbo.FN_GetStockListOfBranch(?,'','') WHERE TypeID = KtypeId)  ";
			if(sendType.equals(type)){//销售订单默认发货仓库
				sql = "SELECT TypeID,FullName FROM dbo.Stock  WHERE BranchID=? AND Deleted='0' AND IsDefault='1' ";
			}
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(BranchID);
			if(receiveType.equals(type)){
				paramList.add(OperatorID);
			}
			ResultSet rs = dba.executeQuery(sql, paramList);
			Storehouse reciveStorehouse = new Storehouse();
			if (rs.next()) {
				String typeID = rs.getString("TypeID");// 部门编号
				String fullName = rs.getString("FullName");// 部门名称
				reciveStorehouse.setTypeID(typeID);
				reciveStorehouse.setFullName(fullName);
			}
			result = JSONObject.fromObject(reciveStorehouse);
		} catch (Exception e) {
			e.printStackTrace();
			if (dba != null) {
				dba.close();
			}
		}
		return result.toString();
	}
	
	/**
	 * 
	 * @param dba
	 * @param BranchID
	 * @param OperatorID
	 * @param hastransferstock 销售订单为0，采购订单为0，库存状况表为1
	 * @return
	 */
	public String getStorehouseInfo(DBAccess dba, String BranchID, String OperatorID, int hastransferstock) {
		JSONArray result = new JSONArray();
		try {
			String sql = "exec FN_TCGetKtypeTreeForWhere @usertype=31,@szparid=N'00000',@OperatorID=?,@BranchId=?,@where=N' 1=1 ',@hastransferstock=?";
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(OperatorID);
			paramList.add(BranchID);
			paramList.add(new Integer(hastransferstock));
			ResultSet rs = dba.executeQuery(sql, paramList);
			List<Storehouse> reciveStorehouseList = new ArrayList<Storehouse>();
			while (rs.next()) {
				String ktypeid = rs.getString("ktypeid");// 仓库标识
				String kfullname = rs.getString("kfullname");// 仓库名称
				Storehouse reciveStorehouse = new Storehouse();
				reciveStorehouse.setTypeID(ktypeid);
				reciveStorehouse.setFullName(kfullname);
				reciveStorehouseList.add(reciveStorehouse);
			}
			result = JSONArray.fromObject(reciveStorehouseList);
		} catch (Exception e) {
			e.printStackTrace();
			if (dba != null) {
				dba.close();
			}
		}
		return result.toString();
	}

	public String getTypeID() {
		return TypeID;
	}

	public void setTypeID(String typeID) {
		TypeID = typeID;
	}

	public String getFullName() {
		return FullName;
	}

	public void setFullName(String fullName) {
		FullName = fullName;
	}

	public static void main(String args[]) throws Exception {
		DBAccess dba = new DBAccess();
		Storehouse reciveStorehouse = new Storehouse();
		String result = reciveStorehouse.getDefaultReciveStorehouseInfo(dba, DBConst.default_orgnization, DBConst.default_OperatorID,sendType);
		System.out.println("默认收货仓库信息：" + result);
		result = reciveStorehouse.getStorehouseInfo(dba, DBConst.default_orgnization, DBConst.default_OperatorID,0);//销售订单为0，采购订单为0，库存状况表为1
		System.out.println("收货仓库列表信息：" + result);
		dba.close();
	}
}
