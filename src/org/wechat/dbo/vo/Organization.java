package org.wechat.dbo.vo;

import java.util.List;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.wechat.dbo.DBAccess;
import org.wechat.dbo.DBConst;

import net.sf.json.*;

/**
 * 机构以及结算机构
 */
public class Organization {

	private String BCtypeid;// 机构标识
	private String BCFullName;// 机构名称
	private String BCUserCode;// 机构编号
	private List<Organization> subList;// 下级机构

	private static final String sql = "exec FN_TCGetBCtypeTree @szparid=?,@OperatorID=?,@usertype=?,@where=?";

	public String getOrganizationInfo(DBAccess dba, String OperatorID, int userType) {
		JSONArray result = new JSONArray();
		try {
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(DBConst.root_orgnization);
			paramList.add(OperatorID);
			paramList.add(new Integer(userType)); // 销售订单为23,往来账目表为15，采购订单为15,库存状况表 31
			if(userType==23){
				paramList.add(" 1=1  And selfhelp=0 "); // 销售订单为 1=1 And selfhelp=0 ,往来账目表为 1=1,采购订单为 1=1, 库存状况 1=1
			}else{
				paramList.add(" 1=1 "); // 销售订单为 1=1 And selfhelp=0 ,往来账目表为 1=1,采购订单为 1=1, 库存状况 1=1
			}
			
			ResultSet rs = dba.executeQuery(sql, paramList);
			List<Organization> deptList = resultSetToList(rs);
			for (Organization tmp : deptList) {
				tmp.setSubList(findNext(dba, tmp.BCtypeid));
			}
			result = JSONArray.fromObject(deptList);
		} catch (Exception e) {
			e.printStackTrace();
			if (dba != null) {
				dba.close();
			}
		}
		String tmpResult = result.toString();
		return tmpResult;
	}

	/** 这不可能多余，后续可能去掉 */
	private List<Organization> resultSetToList(ResultSet rs) throws SQLException {
		List<Organization> tmpList = new ArrayList<Organization>();
		while (rs.next()) {
			String BCtypeid = rs.getString("BCtypeid");// 机构标识
			String BCFullName = rs.getString("BCFullName");// 机构名称
			String BCUserCode = rs.getString("BCUserCode");// 机构编号
			Organization dbDept = new Organization();
			dbDept.setBCtypeid(BCtypeid);
			dbDept.setBCFullName(BCFullName);
			dbDept.setBCUserCode(BCUserCode);
			tmpList.add(dbDept);
		}
		return tmpList;
	}

	private List<Organization> findNext(DBAccess dba, String BCtypeid) throws SQLException {
		List<Organization> list = new ArrayList<Organization>();
		List<Object> paramList = new ArrayList<Object>();
		paramList.add(BCtypeid);
		paramList.add(DBConst.default_OperatorID);
		paramList.add(new Integer(23)); // 销售订单为23,往来账目表为15，采购订单为15
		paramList.add(" 1=1  And selfhelp=0 "); // 销售订单为 1=1 And selfhelp=0 ,往来账目表为 1=1,采购订单为 1=1
		ResultSet rs = dba.executeQuery(sql, paramList);
		List<Organization> deptList = resultSetToList(rs);
		for (Organization tmp : deptList) {
			if (BCtypeid.equals(tmp.BCtypeid))
				break;// 子机构编码等于上一级则退出循环
			tmp.setSubList(findNext(dba, tmp.BCtypeid));
			list.add(tmp);
		}
		return list;
	}

	public String getBCtypeid() {
		return BCtypeid;
	}

	public void setBCtypeid(String ctypeid) {
		BCtypeid = ctypeid;
	}

	public String getBCFullName() {
		return BCFullName;
	}

	public void setBCFullName(String fullName) {
		BCFullName = fullName;
	}

	public String getBCUserCode() {
		return BCUserCode;
	}

	public void setBCUserCode(String userCode) {
		BCUserCode = userCode;
	}

	public List<Organization> getSubList() {
		return subList;
	}

	public void setSubList(List<Organization> subList) {
		this.subList = subList;
	}

	public static void main(String args[]) throws Exception {
		DBAccess dba = new DBAccess(true);
		Organization dept = new Organization();
		String result = dept.getOrganizationInfo(dba, DBConst.default_OperatorID, 15);
		System.out.println("机构信息：" + result);
		dba.close();
	}
}
