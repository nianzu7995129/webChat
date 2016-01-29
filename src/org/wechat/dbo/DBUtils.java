package org.wechat.dbo;

import java.util.*;
import org.wechat.dbo.vo.param.InParam;
import org.wechat.dbo.vo.param.OutParam;
import org.json.JSONObject;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.JSONArray;

public class DBUtils {
	// -----------------------------------采购订单部分---------------------------------->>
	/**
	 * 生成采购订单
	 * 
	 * @userType 7:采购订单，8:销售订单
	 * @throws Exception
	 */
	public static String genCGDD(DBAccess dba, String date, String pcName, int userType) throws Exception {
		if (dba == null)
			return "";
		// 所有参数用?形式传入
		// exec FN_VchNumber_Get @VchType=7,@Date=?,@BranchId=N'',@PCName=?,@IsMustNoSame=0,@Number=?
		String sql = "exec FN_VchNumber_Get @VchType=?,@Date=?,@BranchId=?,@PCName=?,@IsMustNoSame=?,@Number=?";

		// 设置输入参数列表
		List<InParam> inParamList = new ArrayList<InParam>();
		inParamList.add(new InParam(1, new Integer(userType)));
		inParamList.add(new InParam(2, date));
		inParamList.add(new InParam(3, ""));
		inParamList.add(new InParam(4, pcName));
		inParamList.add(new InParam(5, new Integer(0)));

		// 设置输出参数列表
		List<OutParam> outParamList = new ArrayList<OutParam>();
		outParamList.add(new OutParam(6, Types.VARCHAR));
		List<Object> resultList = dba.executeProcedure(sql, inParamList, outParamList, 6, false);
		String cgdd = resultList.get(0).toString();
		return cgdd;
	}

	/**
	 * 提交前判断采购编号是否重复
	 * 
	 * @throws Exception
	 */
	public static String CGDDisExist(DBAccess dba, String cgddCode) throws Exception {
		// 所有参数用?形式传入
		// "exec FZGetVchNumber @nWork=1,@nVchtype=0,@szDate=N'0',@szNumberin=N'CGDD-2015-12-27-0001 ',@szNumber=@p5 output";
		String sql = "exec FZGetVchNumber @nWork=?,@nVchtype=?,@szDate=?,@szNumberin=?,@szNumber=? output";

		// 设置输入参数列表
		List<InParam> inParamList = new ArrayList<InParam>();
		inParamList.add(new InParam(1, new Integer(1)));
		inParamList.add(new InParam(2, new Integer(0)));
		inParamList.add(new InParam(3, "0"));
		inParamList.add(new InParam(4, cgddCode));

		// 设置输出参数列表
		List<OutParam> outParamList = new ArrayList<OutParam>();
		outParamList.add(new OutParam(5, Types.VARCHAR));
		List<Object> resultList = dba.executeProcedure(sql, inParamList, outParamList, 5, false);
		String exist = resultList.get(0).toString();
		return exist;
	}

	/**
	 * 保存订单
	 * 
	 * @throws Exception
	 */
	public static String submitCGDD(String cgddInfo, String goodsInfo, String goodsDetailInfo) throws Exception {
		String result = "保存成功";

		// -----------------------------------------------数据准备部分---------------------------------------->>
		JSONObject mainInfoJo = new JSONObject(cgddInfo);
		JSONArray goodsInfoJa = new JSONArray(goodsInfo);
		JSONArray goodsDetailInfoJa = new JSONArray(goodsDetailInfo);
		String id19 = "" + Math.abs(new Random().nextLong());
		// <<---------------------------------------------数据准备部分-------------------------------------------

		DBAccess dba = null;
		Connection conn = null;
		try {
			dba = new DBAccess(true);
			conn = dba.getConn();
			PreparedStatement ps = null;
			conn.setAutoCommit(false);
			String bigSql = genSaveOrderBigSql(mainInfoJo, goodsInfoJa, goodsDetailInfoJa);
			System.out.println(bigSql);
			// -----------------------------------------主表信息-----------------------------------------
			ps = conn.prepareStatement(bigSql);
			List<List<InParam>> mainInfoParamList = insertMainInfo(id19, mainInfoJo.toString());
			if (mainInfoParamList != null) {
				for (List<InParam> inParams : mainInfoParamList) {
					if (inParams != null) {
						for (InParam inParam : inParams) {
							int index = inParam.getIndex();
							Object value = inParam.getValue();
							if (value instanceof Integer) {
								ps.setInt(index, ((Integer) value).intValue());
							} else if (value instanceof String) {
								ps.setString(index, (String) value);
							}
						}
					}
				}
			}
			System.out.println("主表信息保存結束");
			// <<---------------------------------------主表信息-----------------------------------------
			// ---------------------------------------商品细单信息-----------------------------------------

			int start = 14;
			List<List<InParam>> goodsInfoParamList = insertGoodsInfo(id19, goodsInfoJa.toString(), start);
			if (mainInfoParamList != null) {
				for (List<InParam> inParams : goodsInfoParamList) {
					if (inParams != null) {
						for (InParam inParam : inParams) {
							int index = inParam.getIndex();
							Object value = inParam.getValue();
							if (value instanceof Integer) {
								ps.setInt(index, ((Integer) value).intValue());
							} else if (value instanceof String) {
								ps.setString(index, (String) value);
							}
						}
					}
				}
			}
			System.out.println("商品细单保存結束");
			// <<---------------------------------------商品细单信息-----------------------------------------
			// -----------------------------------------商品尺码信息-----------------------------------------
			start += 20 * goodsInfoJa.length();
			List<List<InParam>> goodsDetailInfoParamList = insertGoodsDetailInfo(id19, goodsDetailInfoJa.toString(), start);
			if (mainInfoParamList != null) {
				for (List<InParam> inParams : goodsDetailInfoParamList) {
					if (inParams != null) {
						for (InParam inParam : inParams) {
							int index = inParam.getIndex();
							Object value = inParam.getValue();
							if (value instanceof Integer) {
								ps.setInt(index, ((Integer) value).intValue());
							} else if (value instanceof String) {
								ps.setString(index, (String) value);
							}
						}
					}
				}
			}
			System.out.println("商品尺寸细单保存結束");
			// <<---------------------------------------商品尺码信息-----------------------------------------

			// -----------------------------------------保存订单-----------------------------------------
			start += 8 * goodsDetailInfoJa.length();//

			// 设置输入参数列表
			List<InParam> inParamList = new ArrayList<InParam>();
			inParamList.add(new InParam(start + 1, DBConst.default_OperatorID));
			if (inParamList != null) {
				for (InParam inParam : inParamList) {
					int index = inParam.getIndex();
					Object value = inParam.getValue();
					if (value instanceof Integer) {
						ps.setInt(index, ((Integer) value).intValue());
					} else if (value instanceof String) {
						ps.setString(index, (String) value);
					}
				}
			}

			ps.executeUpdate();
			boolean hasMoreResult = ps.getMoreResults();
			ResultSet rs = ps.getResultSet();
			if (hasMoreResult && rs != null && rs.next()) {
				result = rs.getString("ErrorMessage");
				conn.rollback();
				conn.setAutoCommit(true);
			} else {
				conn.commit();
				conn.setAutoCommit(true);
				conn.close();
			}
			// <<---------------------------------------保存订单-----------------------------------------
			ps.close();
			conn.close();
		} catch (Exception e) {
			if (conn != null) {
				conn.rollback();
			}
			result = e.getMessage();
			throw new Exception(result);
		} finally {
			if (dba != null) {
				dba.close();
			}
		}
		return result;
	}

	private static String genSaveOrderBigSql(JSONObject mainInfoJo, JSONArray goodsInfoJa, JSONArray goodsDetailInfoJa) {
		String declareMainInfo = "declare @ps1 dbo.tvpdlyndxorder ";
		String sqlInsertMainInfo = " insert into @ps1 values (0,?,?,?,?,?,?,N'',?,?,N'',?,N'0',N'',0,N'',?,?,N'0',0,0,0,N'100',N'',N'0',0,N'摘要 ',N'附加说明 ',2,N'',N'',N'',N'',N'',0,N'0',?,0,?,N'0',N'0',N'0',0,0,N'',N'0',N'运输方式 ',N'送货地址 ',N'联系人 ',N'联系电话 ',?) ";
		String declareGoodsInfo = " declare @ps2 dbo.tvpBakDlyOrder ";
		String sqlInsertGoodsInfo = " insert into @ps2 values(0,0,?,?,?,?,?,?,?,?,?,?,N'100.00',?,0,N'100',N'0',N'0.00',N'136.80 ',?,?,?,0,N'0.00',?,N'0.00',?,N'0',0,0,N'0',0,? ,0,N'0.00',?,N'',N'',N'0',N'0',N'',0,0,N'',N'',N'',N'',N'',N'',4,N'1',0,? ,N'0',N'0',?,N'0') ";
		String declareGoodsDetailInfo = " declare @ps3 dbo.tvpBakDlyOrderDetail ";
		String sqlInsertGoodsDetailInfo = " insert into @ps3 values(0,?,? ,? ,?,N'0',N'0.00',N'0',N'0.00',?,?,N'0.00',?,? ,N'0',N'0',N'0') ";
		String sqlDoSave = " exec SaveOrderBill @tvpDlyNdxOrder=@ps1,@tvpBakdlyOrder=@ps2,@tvpBakDlyOrderDetail=@ps3,@Operator=?";

		StringBuffer bigSql = new StringBuffer();
		bigSql.append(declareMainInfo);// 声明主表临时表
		bigSql.append(sqlInsertMainInfo);// 主表插入语句

		bigSql.append(declareGoodsInfo);// 声明商品记录临时表
		int len = goodsInfoJa.length();
		for (int i = 0; i < len; i++) {
			bigSql.append(sqlInsertGoodsInfo);// 商品记录插入语句
		}

		bigSql.append(declareGoodsDetailInfo);// 声明商品尺寸明细临时表
		len = goodsDetailInfoJa.length();
		for (int i = 0; i < len; i++) {
			bigSql.append(sqlInsertGoodsDetailInfo);// 商品尺寸明细插入表
		}
		bigSql.append(sqlDoSave);// 保存商品存储过程

		return bigSql.toString();
	}

	private static List<List<InParam>> insertMainInfo(String id19, String cgddInfo) throws Exception {
		JSONObject jo = new JSONObject(cgddInfo);
		// String sql = "declare @pstep1 dbo.tvpdlyndxorder insert into @pstep1 values (0,?,N'2015-12-28 ',N'CGDD-2015-12-28-0002 ',7,N'0000100001 ',N'0000100001 ',N'',N'00004 ',N'00001 ',N'',N'00003 ',N'0',N'',0,N'',N'00002 ',N'2015-12-31 ',N'0',0,0,0,N'100',N'',N'0',0,N'摘要 ',N'附加说明 ',2,N'',N'',N'',N'',N'',0,N'0',N'0000100001 ',0,N'11 ',N'0',N'0',N'0',0,0,N'',N'0',N'运输方式 ',N'送货地址 ',N'联系人 ',N'联系电话 ','2015-12-28 22:05:00 ')";
		List<InParam> inParamList = new ArrayList<InParam>();
		inParamList.add(new InParam(1, id19));
		inParamList.add(new InParam(2, jo.optString("cgdddate")));
		inParamList.add(new InParam(3, jo.optString("cgddcode")));
		inParamList.add(new InParam(4, jo.optInt("ddtype"))); // 订单类型，销售订单为8，采购订单为7
		inParamList.add(new InParam(5, jo.optString("cgddsupplyunit")));
		inParamList.add(new InParam(6, jo.optString("cgddorganization")));
		inParamList.add(new InParam(7, jo.optString("cgddbrokerage")));
		inParamList.add(new InParam(8, jo.optString("cgddreceivestorehouse")));
		inParamList.add(new InParam(9, jo.optString("cgdddepartment")));
		inParamList.add(new InParam(10, jo.optString("cgddoperatorid")));
		inParamList.add(new InParam(11, jo.optString("cgddsendgoodsdate")));
		inParamList.add(new InParam(12, jo.optString("cgddcloseorganization")));
		inParamList.add(new InParam(13, jo.optString("cgddgoodstotalsum")));
		inParamList.add(new InParam(14, getCurrDate()));// 制单日期
		List<List<InParam>> list = new ArrayList<List<InParam>>();
		list.add(inParamList);
		return list;
	}

	private static List<List<InParam>> insertGoodsInfo(String id19, String goodsInfo, int start) throws Exception {// 13
		JSONArray ja = new JSONArray(goodsInfo);
		int len = ja.length();
		List<List<InParam>> inParamsList = new ArrayList<List<InParam>>();
		for (int i = 0; i < len; i++) {
			int addedIndex = start + 20 * i;// 20为当前语句需要参数个数
			JSONObject jo = ja.getJSONObject(i);
			List<InParam> inParams = new ArrayList<InParam>();
			// 商品数量
			String cgddgoodssumStr = jo.optString("cgddgoodssum");
			int cgddgoodssum = Integer.parseInt(cgddgoodssumStr);
			// 单价
			String cgddgoodspriceStr = jo.optString("cgddgoodsprice");
			double cgddgoodsprice = Double.parseDouble(cgddgoodspriceStr);
			// 金额
			double sum = cgddgoodssum * cgddgoodsprice;
			String sumStr = new Double(sum).toString();
			// 输入参数
			inParams.add(new InParam(1 + addedIndex, id19)); // 第二轮开始为35 20+14+1
			inParams.add(new InParam(2 + addedIndex, jo.optInt("ddtype"))); // 订单类型，销售订单为8，采购订单为7
			inParams.add(new InParam(3 + addedIndex, jo.optString("cgddorganization")));
			inParams.add(new InParam(4 + addedIndex, jo.optString("cgddorganization")));
			inParams.add(new InParam(5 + addedIndex, jo.optString("cgddsupplyunit")));
			inParams.add(new InParam(6 + addedIndex, jo.optString("cgddbrokerage")));
			inParams.add(new InParam(7 + addedIndex, jo.optString("cgddreceivestorehouse")));
			inParams.add(new InParam(8 + addedIndex, jo.optString("cgdddepartment")));
			inParams.add(new InParam(9 + addedIndex, jo.optString("cgddgoodscode")));
			inParams.add(new InParam(10 + addedIndex, cgddgoodssumStr));
			inParams.add(new InParam(11 + addedIndex, jo.optString("cgddgoodsprice")));
			inParams.add(new InParam(12 + addedIndex, jo.optString("cgddgoodsprice")));
			inParams.add(new InParam(13 + addedIndex, sumStr));
			inParams.add(new InParam(14 + addedIndex, jo.optString("cgdddate")));
			inParams.add(new InParam(15 + addedIndex, jo.optString("cgddgoodsprice")));
			inParams.add(new InParam(16 + addedIndex, cgddgoodssumStr));
			inParams.add(new InParam(17 + addedIndex, jo.optInt("cgddgoodscolor")));
			inParams.add(new InParam(18 + addedIndex, cgddgoodssumStr));
			inParams.add(new InParam(19 + addedIndex, jo.optInt("cgddgoodsindex")));//
			inParams.add(new InParam(20 + addedIndex, jo.optString("cgddgoodsprice")));// 14+20
			inParamsList.add(inParams);
		}
		return inParamsList;
	}

	private static List<List<InParam>> insertGoodsDetailInfo(String id19, String goodsDetailInfo, int start) throws Exception {// start 51
		JSONArray ja = new JSONArray(goodsDetailInfo);
		int len = ja.length();
		List<List<InParam>> inParamsList = new ArrayList<List<InParam>>();
		for (int i = 0; i < len; i++) {
			int addedIndex = start + 8 * i;
			JSONObject jo = ja.getJSONObject(i);
			List<InParam> inParams = new ArrayList<InParam>();
			// 商品数量
			String cgddgoodssumStr = jo.optString("cgddgoodssum");
			int cgddgoodssum = Integer.parseInt(cgddgoodssumStr);
			// 单价
			String cgddgoodspriceStr = jo.optString("cgddgoodsprice");
			double cgddgoodsprice = Double.parseDouble(cgddgoodspriceStr);
			// 金额
			double sum = cgddgoodssum * cgddgoodsprice;
			String sumStr = new Double(sum).toString();
			// 输入参数
			inParams.add(new InParam(1 + addedIndex, id19));// 51+1,59+1
			inParams.add(new InParam(2 + addedIndex, jo.optInt("cgddgoodssize")));
			inParams.add(new InParam(3 + addedIndex, jo.optInt("cgddgoodscolor")));
			inParams.add(new InParam(4 + addedIndex, cgddgoodssumStr));
			inParams.add(new InParam(5 + addedIndex, sumStr));
			inParams.add(new InParam(6 + addedIndex, sumStr));
			inParams.add(new InParam(7 + addedIndex, sumStr));
			inParams.add(new InParam(8 + addedIndex, jo.optInt("cgddgoodsindex")));// 59
			inParamsList.add(inParams);
		}
		return inParamsList;
	}

	public static String getCurrDate() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		return df.format(new Date());// new Date()为获取当前系统时间
	}

	// <<-----------------------------------采购订单部分----------------------------------

	// --------------------------------------往来账目------------------------------------>>
	/**
	 * includeYSYF：0:不包含 ,1:包含 (是否包含预收预付) Filter：0:全部显示 ,1:应收款为零 ,2:应收款不为零，则为2
	 */
	public static String genQueryResultForWLZMB(String supplyUnit, String organization, String OperatorID, int includeYSYF, String Filter) throws Exception {
		DBAccess dba = null;
		JSONObject jo = new JSONObject();
		String result = jo.toString();
		try {
			dba = new DBAccess();
			// 查询应收信息
			String sql = "exec FZDISPArApMSD @cMode=N'AR',@Btypeid=?,@BranchId=?,@OperatorId=?,@includeYSYF=? ,@Filter=?";
			// 设置输入参数列表
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(supplyUnit);
			paramList.add(organization);
			paramList.add(OperatorID);
			paramList.add(new Integer(includeYSYF));
			paramList.add(Filter);
			ResultSet rs = dba.executeQuery(sql, paramList);
			while (rs.next()) {
				jo.put("btypeid", rs.getString("btypeid"));// 单位编号，用于查单位全名
				jo.put("ArTotal00", rs.getString("ArTotal00"));// 期初应收
				jo.put("ArTotalAdd", rs.getString("ArTotalAdd"));// 应收增加
				jo.put("ArTotalDec", rs.getString("ArTotalDec"));// 应收减少
				jo.put("ArTotalYS", rs.getString("ArTotalYS"));// 预收余额
				jo.put("ArTotal", rs.getString("ArTotal"));// 应收余额
			}
			// 查询应付信息
			sql = "exec FZDISPArApMSD @cMode=N'AP',@Btypeid=?,@BranchId=?,@OperatorId=?,@includeYSYF=? ,@Filter=?";
			// 设置输入参数列表
			paramList = new ArrayList<Object>();
			paramList.add(supplyUnit);
			paramList.add(organization);
			paramList.add(OperatorID);
			paramList.add(new Integer(includeYSYF));
			paramList.add(Filter);
			rs = dba.executeQuery(sql, paramList);
			while (rs.next()) {
				jo.put("btypeid", rs.getString("btypeid"));// 单位编号，用于查单位全名
				jo.put("APTotal00", rs.getString("APTotal00"));// 期初应付
				jo.put("ApTotalAdd", rs.getString("ApTotalAdd"));// 应付增加
				jo.put("ApTotalDec", rs.getString("ApTotalDec"));// 应付减少
				jo.put("APTotalYF", rs.getString("APTotalYF"));// 预付余额
				jo.put("APTotal", rs.getString("APTotal"));// 应付余额
			}
			// 查询单位名称
			sql = "exec xw_GetBasicData @cMode='B',@nUpdateTag=0";
			rs = dba.executeQuery(sql, null);
			while (rs.next()) {
				String btypeid = rs.getString("btypeid");
				if (jo.optString("btypeid").equals(btypeid)) {
					jo.put("bfullname", rs.getString("bfullname"));
				}
			}
			result = jo.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			if (dba != null) {
				dba.close();
			}
		}
		return result;
	}

	// <<------------------------------------往来账目----------------------------------

	// --------------------------------------库存状况------------------------------------>>

	public static String genQueryResultForKCZK(String goodsId, String organization, String storeHouseID,String OperatorID) throws Exception {
		DBAccess dba = null;
		JSONObject jo = new JSONObject();
		String result = jo.toString();
		try {
			dba = new DBAccess();
			// 查询应收信息
			String sql = "exec FzDispGoodsStockDispZero @szParID=?,@szBranchId=?,@szPtypeID=?,@szktypeid=?,@attrib1=-1,@attrib2=-1,@attrib3=-1,@attrib4=-1,@Main=1,@Join=0,@Substitute=0,@Commission=0,@Order=0,@Stop=? ,@iflist=0,@PriceMode=?,@attrib5=-1,@attrib6=-1,@attrib7=-1,@attrib8=-1,@attribTypeid1=-1,@attribTypeid2=-1,@attribTypeid3=-1,@attribTypeid4=-1,@attribTypeid5=-1,@attribTypeid6=-1,@attribTypeid7=-1,@attribTypeid8=-1,@OperatorId=?,@ptypeBarcode=N'',@wayqty=1,@stocktype=? ";
			// 设置输入参数列表
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(goodsId);
			paramList.add(organization);
			paramList.add(goodsId);
			paramList.add(storeHouseID);
			paramList.add(new Integer(0));//不包含停用商品复选框 勾选：0 不勾选：1
			paramList.add("");//预设售价1-6：预设售价的id字段 库存均价：空值 最近折后进价：recpice （库存均价）
			paramList.add(OperatorID);
			paramList.add(new Integer(0));//库存类型 全部：0，普通：1 ，物料：3 （全部）
			ResultSet rs = dba.executeQuery(sql, paramList);
			while (rs.next()) {
				String tmpQry = rs.getString("Qty");
				jo.put("Qty", tmpQry==null?"0":tmpQry);// 实际数量
			}
			result = jo.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			if (dba != null) {
				dba.close();
			}
		}
		return result;
	}

	// <<------------------------------------库存状况----------------------------------
	public static void main(String args[]) throws Exception {
		DBAccess dba = new DBAccess(true);
		String cgdd = DBUtils.genCGDD(dba, "2017-12-29", "127.0.0.1", 7);
		System.out.println("采购订单编号：" + cgdd);
		String exist = DBUtils.CGDDisExist(dba, cgdd);
		System.out.println("采购订单编号是否重复：" + exist);
		dba.close();
		// 商品主信息假数据----------------------------------------------------------
		/*
		 * String id19 = "" + Math.abs(new Random().nextLong()); JSONObject mainInfoJo = new JSONObject(); mainInfoJo.put("cgddId", id19);// 1 mainInfoJo.put("cgdddate", "2016-1-25");// 2 mainInfoJo.put("cgddcode", "CGDD-2016-1-25-0002");// 3 mainInfoJo.put("ddtype", 7);// 4 //类型，销售订单值为8，采购订单值为7 mainInfoJo.put("cgddsupplyunit", DBConst.default_supplyunit);// 5 mainInfoJo.put("cgddorganization", DBConst.default_orgnization);// 6 mainInfoJo.put("cgddbrokerage", DBConst.default_BrokerageID);// 7 mainInfoJo.put("cgddreceivestorehouse", DBConst.default_receivestorehouse);// 8 mainInfoJo.put("cgdddepartment", DBConst.default_department);// 9 mainInfoJo.put("cgddoperatorid", DBConst.default_OperatorID);// 10 mainInfoJo.put("cgddsendgoodsdate", DBConst.default_sendGoodsDate);// 11 mainInfoJo.put("cgddcloseorganization", DBConst.default_closeorgnization);// 12 //如果为销售订单则不需要结算结构 mainInfoJo.put("cgddgoodstotalsum", DBConst.default_goodstotalsum);// 13 // 商品记录假数据---------------------------------------------------------- JSONObject goodsInfoJo1 = new JSONObject(); goodsInfoJo1.put("cgddId", "" + id19);// 1 goodsInfoJo1.put("ddtype", 7);// 2 //类型，销售订单值为8，采购订单值为7 goodsInfoJo1.put("cgddorganization", DBConst.default_orgnization);// 3 goodsInfoJo1.put("cgddsupplyunit", DBConst.default_supplyunit);// 4 goodsInfoJo1.put("cgddbrokerage", DBConst.default_BrokerageID);// 5 goodsInfoJo1.put("cgddreceivestorehouse", DBConst.default_receivestorehouse);// 6 goodsInfoJo1.put("cgdddepartment", DBConst.default_department);// 7 goodsInfoJo1.put("cgddgoodscode", DBConst.default_goodscode);// 8 goodsInfoJo1.put("cgddgoodssum", DBConst.default_goodssum);// 9 goodsInfoJo1.put("cgddgoodsprice", DBConst.default_goodsprice);// 10 goodsInfoJo1.put("cgdddate", "2016-1-25");// 11 goodsInfoJo1.put("cgddgoodscolor", 15);// 12 goodsInfoJo1.put("cgddgoodsindex", 1);// 13 JSONObject goodsInfoJo2 = new JSONObject(); goodsInfoJo2.put("cgddId", "" + id19);// 1 goodsInfoJo1.put("ddtype", 7);// 2
		 * //类型，销售订单值为8，采购订单值为7 goodsInfoJo2.put("cgddorganization", DBConst.default_orgnization);// 3 goodsInfoJo2.put("cgddsupplyunit", DBConst.default_supplyunit);// 4 goodsInfoJo2.put("cgddbrokerage", DBConst.default_BrokerageID);// 5 goodsInfoJo2.put("cgddreceivestorehouse", DBConst.default_receivestorehouse);// 6 goodsInfoJo2.put("cgdddepartment", DBConst.default_department);// 7 goodsInfoJo2.put("cgddgoodscode", DBConst.default_goodscode);// 8 goodsInfoJo2.put("cgddgoodssum", DBConst.default_goodssum);// 9 goodsInfoJo2.put("cgddgoodsprice", DBConst.default_goodsprice);// 10 goodsInfoJo2.put("cgdddate", "2016-1-25");// 11 goodsInfoJo2.put("cgddgoodscolor", 15);// 12 goodsInfoJo2.put("cgddgoodsindex", 2);// 13 JSONArray goodsInfoJa = new JSONArray(); goodsInfoJa.put(goodsInfoJo1); //goodsInfoJa.put(goodsInfoJo2); // 商品尺寸明细假数据---------------------------------------------------------- JSONObject goodsDetailInfoJo1 = new JSONObject(); goodsDetailInfoJo1.put("cgddId", "" + id19);// 1 goodsDetailInfoJo1.put("cgddgoodssize", DBConst.default_goodssize);// 2 goodsDetailInfoJo1.put("cgddgoodscolor", 15);// 3 goodsDetailInfoJo1.put("cgddgoodssum", DBConst.default_goodssum);// 4 goodsDetailInfoJo1.put("cgddgoodsprice", DBConst.default_goodsprice);// 5 goodsDetailInfoJo1.put("cgddgoodsindex", 1);// 6 JSONObject goodsDetailInfoJo2 = new JSONObject(); goodsDetailInfoJo2.put("cgddId", "" + id19);// 1 goodsDetailInfoJo2.put("cgddgoodssize", DBConst.default_goodssize + 1);// 2 goodsDetailInfoJo2.put("cgddgoodscolor", 15);// 3 goodsDetailInfoJo2.put("cgddgoodssum", DBConst.default_goodssum);// 4 goodsDetailInfoJo2.put("cgddgoodsprice", DBConst.default_goodsprice);// 5 goodsDetailInfoJo2.put("cgddgoodsindex", 2);// 6 JSONArray goodsDetailInfoJa = new JSONArray(); goodsDetailInfoJa.put(goodsDetailInfoJo1); //goodsDetailInfoJa.put(goodsDetailInfoJo2); System.out.println(id19);
		 */
		/*
		 * String cgddInfo= "{\"cgdddate\":\"2016-01-15\",\"cgddcode\":\"CGDD-2016-01-15-0001\",\"ddtype\":7,\"cgddsupplyunit\":\"0000100001\",\"cgddorganization\":\"0000100001\",\"cgddbrokerage\":\"00001\",\"cgddreceivestorehouse\":\"00001\",\"cgdddepartment\":\"00001\",\"cgddoperatorid\":\"00002\",\"cgddsendgoodsdate\":\"2016-01-09\",\"cgddcloseorganization\":\"0000100001\",\"cgddgoodstotalsum\":3}"; String goodsInfo = "[{\"ddtype\":7,\"cgddorganization\":\"0000100001\",\"cgddsupplyunit\":\"0000100001\",\"cgddbrokerage\":\"00001\",\"cgddreceivestorehouse\":\"00001\",\"cgdddepartment\":\"00001\",\"cgdddate\":\"2016-01-15\",\"cgddgoodscode\":\"00001\",\"cgddgoodssum\":\"3\",\"cgddgoodsprice\":\"10\",\"cgddgoodscolor\":\"\",\"cgddgoodsindex\":1}]"; String goodsDetailInfo = "[{\"cgddgoodssize\":\"1\",\"cgddgoodssum\":\"3\",\"cgddgoodsprice\":\"10\",\"cgddgoodscolor\":\"\",\"cgddgoodsindex\":1}]"; submitCGDD(cgddInfo, goodsInfo, goodsDetailInfo);
		 */

		/*
		 * String result = genQueryResultForWLZMB("00003", DBConst.default_orgnization, DBConst.default_OperatorID, 1, "0"); System.out.println("往来账目表查询结果：" + result);
		 */

	}
}
