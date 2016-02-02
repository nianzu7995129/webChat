package org.wechat.dbo.vo;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.wechat.dbo.DBAccess;
import org.wechat.dbo.DBConst;
import net.sf.json.JSONArray;

/** 经手人 */
public class Brokerage {
	/** 经手人编号 */
	private String etypeid;
	/** 经手人姓名 */
	private String efullname;

	public String getBrokerageInfo(DBAccess dba, String BranchId, int bDisplayStop, String OperatorID) {
		JSONArray result = new JSONArray();
		try {
			String sql = "exec FN_TCGetEtypeTree @szparid=N'',@BranchId=?,@bDisplayStop=?,@OperatorID=?,@shopid=N'0',@UserType=0,@where=N' 1=1 '";
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(DBConst.default_orgnization);
			paramList.add(new Integer(bDisplayStop));
			paramList.add(OperatorID);
			ResultSet rs = dba.executeQuery(sql, paramList);
			List<Brokerage> brokerageList = new ArrayList<Brokerage>();
			while (rs.next()) {
				String etypeid = rs.getString("etypeid");// 职员编号
				String efullname = rs.getString("efullname");// 职员名称
				Brokerage brokerage = new Brokerage();
				brokerage.setEtypeid(etypeid);
				brokerage.setEfullname(efullname);
				brokerageList.add(brokerage);
			}
			result = JSONArray.fromObject(brokerageList);
		} catch (Exception e) {
			e.printStackTrace();
			if (dba != null) {
				dba.close();
			}
		}
		return result.toString();
	}

	public String getEtypeid() {
		return etypeid;
	}

	public void setEtypeid(String etypeid) {
		this.etypeid = etypeid;
	}

	public String getEfullname() {
		return efullname;
	}

	public void setEfullname(String efullname) {
		this.efullname = efullname;
	}

	public static void main(String args[]) throws Exception {
		DBAccess dba = new DBAccess(true);
		Brokerage brokerage = new Brokerage();
		String result = brokerage.getBrokerageInfo(dba, DBConst.default_orgnization,1, DBConst.default_OperatorID);
		System.out.println("经手人信息：" + result);
		dba.close();
	}
}
