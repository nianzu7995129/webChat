package org.wechat.dbo.vo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import net.sf.json.JSONArray;
import org.wechat.dbo.DBAccess;
import org.wechat.dbo.DBConst;
import org.wechat.dbo.vo.param.InParam;
import org.wechat.dbo.vo.param.OutParam;

/**
 * 采购订单中的供货单位 销售订单中的客户 往来账目表中的单位
 */
public class SupplyUnit {
	private String btypeid;// 供货单位标识
	private String bfullname;// 供货单位名称
	private String busercode;// 供货单位编号
	private int totalPage;// 总页数
	private List<SupplyUnit> subList;// 下级供货单位
	private static final String sql = "exec FN_TCGetBtypeTree @usertype=?,@szparid=?,@bHaveTotal=?,@OperatorID=?,@Branchid=?,@bDisplayStop=?,@where=?,@PageNo=?,@PageSize=?,@itemCount=?";

	public String getSupplyUnitInfo(DBAccess dba, int usertype, String szparid, String BranchId, String OperatorID, int bDisplayStop, int pageNum, int itemsInEachPage) {
		JSONArray result = new JSONArray();
		// FN_TCGetBtypeTree @usertype=1,@szparid=N'00000',@bHaveTotal=0,@OperatorID=N'00002 ',@Branchid=N'0000100001 ',@bDisplayStop=0,@where=N' 1=1 ',@PageNo=1,@PageSize=14,@itemCount=@p10
		if (szparid == null || "undefined".equals(szparid) || "null".equals(szparid.toLowerCase())) {
			szparid = DBConst.root_orgnization;
		}
		try {
			// 设置输入参数列表
			List<InParam> inParamList = new ArrayList<InParam>();
			inParamList.add(new InParam(1, new Integer(usertype)));// 1:为采购订单中的供货单位,2:为销售订单中的客户,30:为往来账目表的单位
			inParamList.add(new InParam(2, szparid));
			inParamList.add(new InParam(3, new Integer(0)));
			inParamList.add(new InParam(4, OperatorID));
			inParamList.add(new InParam(5, BranchId));
			inParamList.add(new InParam(6, new Integer(bDisplayStop)));// 0:为采购订单和销售订单使用 , 1:为往来账目表使用
			inParamList.add(new InParam(7, " 1=1 "));
			inParamList.add(new InParam(8, new Integer(pageNum)));
			inParamList.add(new InParam(9, new Integer(itemsInEachPage)));

			// 设置输出参数列表
			List<OutParam> outParamList = new ArrayList<OutParam>();
			outParamList.add(new OutParam(10, java.sql.Types.INTEGER));
			List<Object> resultList = dba.executeProcedure(sql, inParamList, outParamList, 10, true);
			ResultSet rs = (ResultSet) resultList.get(0);
			
			int totalPage = 0;
			if (resultList.size() > 1) {
				int itemCount = Integer.parseInt(resultList.get(1).toString());
				if(itemCount%itemsInEachPage==0){
					totalPage = itemCount/itemsInEachPage;
				}else{
					totalPage = itemCount/itemsInEachPage+1;
				}
			}

			List<SupplyUnit> supplyUnitList = resultSetToList(rs,totalPage);
			for (SupplyUnit tmp : supplyUnitList) {
				tmp.setSubList(findNext(dba, usertype, tmp.btypeid, OperatorID, BranchId, bDisplayStop, itemsInEachPage));
			}

			result = JSONArray.fromObject(supplyUnitList);
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
	private List<SupplyUnit> resultSetToList(ResultSet rs,int totalPage) throws SQLException {
		List<SupplyUnit> tmpList = new ArrayList<SupplyUnit>();
		while (rs.next()) {
			String btypeid = rs.getString("btypeid");// 供货单位标识
			String bfullname = rs.getString("bfullname");// 供货单位名称
			String busercode = rs.getString("busercode");// 供货单位编号
			SupplyUnit supplyUnit = new SupplyUnit();
			supplyUnit.setBtypeid(btypeid);
			supplyUnit.setBfullname(bfullname);
			supplyUnit.setBusercode(busercode);
			supplyUnit.setTotalPage(totalPage);
			tmpList.add(supplyUnit);
		}
		return tmpList;
	}

	private List<SupplyUnit> findNext(DBAccess dba, int useType, String szparid, String OperatorID, String BranchId, int bDisplayStop, int itemsInEachPage) throws SQLException {
		List<SupplyUnit> list = new ArrayList<SupplyUnit>();

		// 设置输入参数列表
		List<InParam> inParamList = new ArrayList<InParam>();
		inParamList.add(new InParam(1, new Integer(useType)));
		inParamList.add(new InParam(2, szparid));
		inParamList.add(new InParam(3, new Integer(0)));
		inParamList.add(new InParam(4, OperatorID));
		inParamList.add(new InParam(5, BranchId));
		inParamList.add(new InParam(6, new Integer(bDisplayStop)));
		inParamList.add(new InParam(7, " 1=1 "));
		inParamList.add(new InParam(8, new Integer(1)));
		inParamList.add(new InParam(9, new Integer(itemsInEachPage)));

		// 设置输出参数列表
		List<OutParam> outParamList = new ArrayList<OutParam>();
		outParamList.add(new OutParam(10, java.sql.Types.INTEGER));

		List<Object> resultList = dba.executeProcedure(sql, inParamList, outParamList, 10, true);
		ResultSet rs = (ResultSet) resultList.get(0);
		
		int totalPage = 0;
		if (resultList.size() > 1) {
			int itemCount = Integer.parseInt(resultList.get(1).toString());
			if(itemCount%itemsInEachPage==0){
				totalPage = itemCount/itemsInEachPage;
			}else{
				totalPage = itemCount/itemsInEachPage+1;
			}
		}

		List<SupplyUnit> supplyUnitList = resultSetToList(rs,totalPage);
		for (SupplyUnit tmp : supplyUnitList) {
			if (szparid.equals(tmp.btypeid))
				break;// 子供货单位编码等于上一级则退出循环
			tmp.setSubList(findNext(dba, useType, tmp.btypeid, OperatorID, BranchId, bDisplayStop, itemsInEachPage));
			list.add(tmp);
		}

		return list;
	}
	
	/**
	 * 
	 * @param dba
	 * @param name	模糊查询
	 * @param OperatorID
	 * @param BranchId
	 * @param pageNum
	 * @param itemsInEachPage
	 * @return
	 */
	public String getSupplyUnitInfoByName(DBAccess dba, String name,String BranchId, String OperatorID, int pageNum, int itemsInEachPage) {
		JSONArray result = new JSONArray();
		try {
			// 设置输入参数列表
			List<InParam> inParamList = new ArrayList<InParam>();
			inParamList.add(new InParam(1, new Integer(0)));//模糊查询内容
			inParamList.add(new InParam(2, new Integer(2)));
			inParamList.add(new InParam(3, "%"+name+"%"));
			inParamList.add(new InParam(4, OperatorID));
			inParamList.add(new InParam(5, BranchId));
			inParamList.add(new InParam(6, new Integer(0)));
			inParamList.add(new InParam(7, " 1=1 "));
			inParamList.add(new InParam(8, new Integer(pageNum)));
			inParamList.add(new InParam(9, new Integer(itemsInEachPage)));

			// 设置输出参数列表
			List<OutParam> outParamList = new ArrayList<OutParam>();
			outParamList.add(new OutParam(10, java.sql.Types.INTEGER));
			String curSql = "exec FN_TCGetBtypeList @nSearchType=?,@custom1=?,@custom2=?,@OperatorID=?,@Branchid=?,@bDisplayStop=?,@where=?,@PageNo=?,@PageSize=?,@itemCount=? output";
			List<Object> resultList = dba.executeProcedure(curSql, inParamList, outParamList, 10, true);
			ResultSet rs = (ResultSet) resultList.get(0);
			
			int totalPage = 0;
			if (resultList.size() > 1) {
				int itemCount = Integer.parseInt(resultList.get(1).toString());
				if(itemCount%itemsInEachPage==0){
					totalPage = itemCount/itemsInEachPage;
				}else{
					totalPage = itemCount/itemsInEachPage+1;
				}
			}

			List<SupplyUnit> supplyUnitList = resultSetToList(rs,totalPage);
			result = JSONArray.fromObject(supplyUnitList);
		} catch (Exception e) {
			e.printStackTrace();
			if (dba != null) {
				dba.close();
			}
		}
		String tmpResult = result.toString();
		return tmpResult;
	}
	

	public String getBtypeid() {
		return btypeid;
	}

	public void setBtypeid(String btypeid) {
		this.btypeid = btypeid;
	}

	public String getBfullname() {
		return bfullname;
	}

	public void setBfullname(String bfullname) {
		this.bfullname = bfullname;
	}

	public String getBusercode() {
		return busercode;
	}

	public void setBusercode(String busercode) {
		this.busercode = busercode;
	}

	public List<SupplyUnit> getSubList() {
		return subList;
	}

	public void setSubList(List<SupplyUnit> subList) {
		this.subList = subList;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public static void main(String args[]) throws Exception {
		DBAccess dba = new DBAccess(true);
		SupplyUnit supplyUnit = new SupplyUnit();
		//String result = supplyUnit.getSupplyUnitInfo(dba, 1, "0000100007", DBConst.default_orgnization, DBConst.default_OperatorID, 0, 1, 5);
		String result = supplyUnit.getSupplyUnitInfoByName(dba, "一", DBConst.default_orgnization,"00002", 1, 5);
		System.out.println("供货单位：" + result);
		dba.close();
	}

}
