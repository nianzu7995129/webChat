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

public class Goods {
	private String ptypeid;// 商品标识
	private String pfullname;// 商品名称
	private String pusercode;// 货号
	private int totalPage;// 总页数
	private List<Goods> subList;// 下级供货单位
	// FN_TCGetPtypeTree @szparid=N'00000',@szktypeid=N'00001 ',@bHaveQty=0,@bDisplayStop=0,@selecttype=3,@OperatorID=N'00002 ',@where=N' 1=1 ',@PageNo=1,@PageSize=14 ,@itemCount=@p10 output
	private static final String sql = "exec FN_TCGetPtypeTree @szparid=?,@szktypeid=?,@bHaveQty=?,@bDisplayStop=?,@selecttype=?,@OperatorID=?,@where=?,@PageNo=?,@PageSize=? ,@itemCount=? output ";

	public String getGoodsInfo(DBAccess dba, String szparid, String szktypeid, String OperatorID, int bDisplayStop, int selectType, int pageNum, int itemsInEachPage) {
		JSONArray result = new JSONArray();
		if (szparid == null || "undefined".equals(szparid) || "null".equals(szparid.toLowerCase())) {
			szparid = DBConst.root_orgnization;
		}
		try {
			// 设置输入参数列表
			List<InParam> inParamList = new ArrayList<InParam>();
			inParamList.add(new InParam(1, szparid));
			inParamList.add(new InParam(2, szktypeid)); // 销售订单有值，采购订单有值，库存状况无值
			inParamList.add(new InParam(3, new Integer(0)));
			inParamList.add(new InParam(4, new Integer(bDisplayStop)));// 销售订单为0，采购订单为0，库存状况为1，商品销售分析为1
			inParamList.add(new InParam(5, new Integer(selectType)));// 销售订单为0 ， 采购订单为3， 库存状况为0，商品销售分析为0
			inParamList.add(new InParam(6, OperatorID));
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

			List<Goods> goodsList = resultSetToList(rs, totalPage);
			for (Goods tmp : goodsList) {
				tmp.setSubList(findNext(dba, tmp.ptypeid, szktypeid, OperatorID, bDisplayStop, selectType, itemsInEachPage));
			}
			result = JSONArray.fromObject(goodsList);
		} catch (Exception e) {
			e.printStackTrace();
			if (dba != null) {
				dba.close();
			}
		}

		String tmpResult = result.toString();
		return tmpResult;
	}
	
	public String getGoodsInfoByGoodsNumber(DBAccess dba, String goodsCode, String storeHouseId, String OperatorID, int pageNum, int itemsInEachPage) {
		JSONArray result = new JSONArray();
		//exec FN_TCGetPtypeList @nSearchType=0,@custom1=N'%000 %',@custom2=0,@custom3=0,@szKtypeid=N'00001 ',@selecttype=0,@OperatorID=N'00000 ',@where=N' 1=1 ',@PageNo=1,@PageSize=6,@itemCount=@p11 output
		String curSql = " exec FN_TCGetPtypeList @nSearchType=?,@custom1=?,@custom2=?,@custom3=?,@szKtypeid=?,@selecttype=?,@OperatorID=?,@where=?,@PageNo=?,@PageSize=?,@itemCount=? ";
		try {
			// 设置输入参数列表
			List<InParam> inParamList = new ArrayList<InParam>();
			inParamList.add(new InParam(1, new Integer(0)));
			inParamList.add(new InParam(2, "%"+goodsCode+"%"));
			inParamList.add(new InParam(3, new Integer(0)));
			inParamList.add(new InParam(4, new Integer(0)));
			inParamList.add(new InParam(5, storeHouseId)); // 仓库ID
			inParamList.add(new InParam(6, new Integer(0)));
			inParamList.add(new InParam(7, OperatorID));
			inParamList.add(new InParam(8, " 1=1 "));
			inParamList.add(new InParam(9, new Integer(pageNum)));
			inParamList.add(new InParam(10, new Integer(itemsInEachPage)));
			// 设置输出参数列表
			List<OutParam> outParamList = new ArrayList<OutParam>();
			outParamList.add(new OutParam(11, java.sql.Types.INTEGER));
			
			List<Object> resultList = dba.executeProcedure(curSql, inParamList, outParamList, 11, true);
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
			List<Goods> goodsList = resultSetToList(rs, totalPage);
			result = JSONArray.fromObject(goodsList);
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
	private List<Goods> resultSetToList(ResultSet rs, int totalPage) throws SQLException {
		List<Goods> tmpList = new ArrayList<Goods>();
		while (rs.next()) {
			String ptypeid = rs.getString("Ptypeid");// 商品标识
			String pfullname = rs.getString("Pfullname");// 商品名称
			String pusercode = rs.getString("Pusercode");// 货号
			Goods goods = new Goods();
			goods.setPtypeid(ptypeid);
			goods.setPfullname(pfullname);
			goods.setPusercode(pusercode);
			goods.setTotalPage(totalPage);
			tmpList.add(goods);
		}
		return tmpList;
	}

	private List<Goods> findNext(DBAccess dba, String szparid, String szktypeid, String OperatorID, int bDisplayStop, int selectType, int itemsInEachPage) throws SQLException {
		List<Goods> list = new ArrayList<Goods>();

		// 设置输入参数列表
		List<InParam> inParamList = new ArrayList<InParam>();
		inParamList.add(new InParam(1, szparid));
		inParamList.add(new InParam(2, szktypeid)); // 销售订单有值，采购订单有值，库存状况无值
		inParamList.add(new InParam(3, new Integer(0)));
		inParamList.add(new InParam(4, new Integer(bDisplayStop)));// 销售订单为0，采购订单为0，库存状况为1
		inParamList.add(new InParam(5, new Integer(selectType)));// 销售订单为0 ， 采购订单为3， 库存状况为0
		inParamList.add(new InParam(6, OperatorID));
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

		List<Goods> goodsList = resultSetToList(rs, totalPage);
		for (Goods tmp : goodsList) {
			if (szparid.equals(tmp.ptypeid))
				break;// 子供货单位编码等于上一级则退出循环
			tmp.setSubList(findNext(dba, tmp.ptypeid, szktypeid, OperatorID, bDisplayStop, selectType, itemsInEachPage));
			list.add(tmp);
		}

		return list;
	}

	public String getPtypeid() {
		return ptypeid;
	}

	public void setPtypeid(String ptypeid) {
		this.ptypeid = ptypeid;
	}

	public String getPfullname() {
		return pfullname;
	}

	public void setPfullname(String pfullname) {
		this.pfullname = pfullname;
	}

	public String getPusercode() {
		return pusercode;
	}

	public void setPusercode(String pusercode) {
		this.pusercode = pusercode;
	}

	public List<Goods> getSubList() {
		return subList;
	}

	public void setSubList(List<Goods> subList) {
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
		Goods goods = new Goods();
		
		/*
		String result = goods.getGoodsInfo(dba, "00000", "00001", "00001", "00002", 0, 0, 1, 5);
		System.out.println("商品信息：" + result);
		*/
		
		
		String result = goods.getGoodsInfoByGoodsNumber(dba, "000", "00001", "00000", 1, 5);
		System.out.println("商品信息：" + result);
		
		dba.close();
	}

}
