package org.wechat.dbo.vo;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.wechat.dbo.DBAccess;
import org.wechat.dbo.DBConst;


public class Department {
	/** 部门编号 */
	private String dtypeid;
	/** 部门名称 */
	private String dfullname;

	public String getDefaultDepartment(DBAccess dba, String BrokerageID) {
		JSONObject result = new JSONObject();
		try {
			String sql = "Select typeid as dtypeid, fullname as dfullname, usercode as dusercode, sonnum as dsonnum, rec as drec, parid From department a Where typeid = (Select dtypeid From Employee Where typeid=?)";
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(BrokerageID);
			ResultSet rs = dba.executeQuery(sql, paramList);
			Department dept = new Department();
			if (rs.next()) {
				String dtypeid = rs.getString("dtypeid");// 部门编号
				String dfullname = rs.getString("dfullname");// 部门名称
				dept.setDtypeid(dtypeid);
				dept.setDfullname(dfullname);
			}
			result = JSONObject.fromObject(dept);
		} catch (Exception e) {
			e.printStackTrace();
			if (dba != null) {
				dba.close();
			}
		}
		return result.toString();
	}
	
	public String getDepartmentInfo(DBAccess dba, String OperatorID,String BranchId) {
		JSONArray result = new JSONArray();
		try {
			String sql = "exec FN_TCGetDeptTree @szparid=N'00000',@OperatorID=?,@BranchId=?,@where=N' 1=1 '";
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(OperatorID);
			paramList.add(BranchId);
			ResultSet rs = dba.executeQuery(sql, paramList);
			List<Department> deptList = new ArrayList<Department>();
			while (rs.next()) {
				String dtypeid = rs.getString("dtypeid");// 部门编号
				String dfullname = rs.getString("dfullname");// 部门名称
				Department dept = new Department();
				dept.setDtypeid(dtypeid);
				dept.setDfullname(dfullname);
				deptList.add(dept);
			}
			result = JSONArray.fromObject(deptList);
		} catch (Exception e) {
			e.printStackTrace();
			if (dba != null) {
				dba.close();
			}
		}
		return result.toString();
	}

	public String getDtypeid() {
		return dtypeid;
	}

	public void setDtypeid(String dtypeid) {
		this.dtypeid = dtypeid;
	}

	public String getDfullname() {
		return dfullname;
	}

	public void setDfullname(String dfullname) {
		this.dfullname = dfullname;
	}

	public static void main(String args[]) throws Exception {
		DBAccess dba = new DBAccess(true);
		Department brokerage = new Department();
		String result = brokerage.getDefaultDepartment(dba, DBConst.default_BrokerageID);
		String result1 = brokerage.getDepartmentInfo(dba, "00012","00004");
		System.out.println("经手人所在部门信息：" + result1);
		dba.close();
	}
}
