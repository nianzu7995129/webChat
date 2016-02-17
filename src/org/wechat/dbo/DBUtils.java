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
		inParamList.add(new InParam(5, new Integer(1)));

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
			dba = new DBAccess();
			conn = dba.getConn();
			PreparedStatement ps = null;
			String bigSql = genSaveOrderBigSql(mainInfoJo,goodsInfoJa,goodsDetailInfoJa);
			//System.out.println(bigSql);
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
			//System.out.println("主表信息保存結束");
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
								//System.out.println("intValue>>"+((Integer) value).intValue());
								ps.setInt(index, ((Integer) value).intValue());
							} else if (value instanceof String) {
								ps.setString(index, (String) value);
							}
						}
					}
				}
			}
			//System.out.println("商品细单保存結束");
			// <<---------------------------------------商品细单信息-----------------------------------------
			// -----------------------------------------商品尺码信息-----------------------------------------
			start += 21 * goodsInfoJa.length();
			List<List<InParam>> goodsDetailInfoParamList = insertGoodsDetailInfo(id19, goodsDetailInfoJa.toString(), start);
			if (mainInfoParamList != null) {
				for (List<InParam> inParams : goodsDetailInfoParamList) {
					if (inParams != null) {
						for (InParam inParam : inParams) {
							int index = inParam.getIndex();
							Object value = inParam.getValue();
							if (value instanceof Integer) {
								//System.out.println("intValue>>"+((Integer) value).intValue());
								ps.setInt(index, ((Integer) value).intValue());
							} else if (value instanceof String) {
								ps.setString(index, (String) value);
							}
						}
					}
				}
			}
			//System.out.println("商品尺寸细单保存結束");
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
			}
			// <<---------------------------------------保存订单-----------------------------------------
			
			
			if("保存成功".equals(result)){
				int ddtype = mainInfoJo.optInt("ddtype");
				if(ddtype==8){//订单类型，销售订单为8，采购订单为7
					
					//保存单据最大值
					String cgdddate = mainInfoJo.optString("cgdddate");
					List<InParam> inParamListForMaxSql = new ArrayList<InParam>();
					inParamListForMaxSql.add(new InParam(1, cgdddate));
					String maxSql = "exec FZModifyVchnumber @nVchtype=8,@Date=?";
					ps = conn.prepareStatement(maxSql);
					if (inParamListForMaxSql != null) {
						for (InParam inParam : inParamListForMaxSql) {
							int index = inParam.getIndex();
							Object value = inParam.getValue();
							if (value instanceof Integer) {
								ps.setInt(index, ((Integer) value).intValue());
							} else if (value instanceof String) {
								ps.setString(index, (String) value);
							}
						}
					}
					ps.execute();
					
					//判断是否需要审核
					List<InParam> inParamListForCheck = new ArrayList<InParam>();
					inParamListForCheck.add(new InParam(1, id19));
					inParamListForCheck.add(new InParam(2, DBConst.default_OperatorID));
					String checkSql = "exec FN_BillAudtingCheckReason_Order @vchcode=? ,@BillIndexType=0,@OperatorID=?";
					ps = conn.prepareStatement(checkSql);
					if (inParamListForCheck != null) {
						for (InParam inParam : inParamListForCheck) {
							int index = inParam.getIndex();
							Object value = inParam.getValue();
							if (value instanceof Integer) {
								ps.setInt(index, ((Integer) value).intValue());
							} else if (value instanceof String) {
								ps.setString(index, (String) value);
							}
						}
					}
					rs = ps.executeQuery();
					if(rs.next()){
						String reason = "单据金额高于等于100元审核";
						if(reason.equals(rs.getString("Reason"))){
							// 形成待审单
							List<InParam> inParamListForAdd= new ArrayList<InParam>();
							inParamListForAdd.add(new InParam(1, id19));
							inParamListForAdd.add(new InParam(2, DBConst.default_OperatorID));
							String addSql = "exec FN_AddDataToAuditingBill @VchCode=? ,@BillIndexType=0,@OperatorETypeID=?,@tbName=N'DlyNdxOrder'";
							ps = conn.prepareStatement(addSql);
							if (inParamListForCheck != null) {
								for (InParam inParam : inParamListForAdd) {
									int index = inParam.getIndex();
									Object value = inParam.getValue();
									if (value instanceof Integer) {
										ps.setInt(index, ((Integer) value).intValue());
									} else if (value instanceof String) {
										ps.setString(index, (String) value);
									}
								}
							}
							ps.execute();
						}
					}
				}
			}
			ps.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
			if (conn != null) {
				conn.close();
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
		String sqlInsertGoodsInfo = "";
		int ddtype = mainInfoJo.optInt("ddtype");
		if(ddtype==8){//订单类型，销售订单为8，采购订单为7
			sqlInsertGoodsInfo = " insert into @ps2 values(0,0,?,?,?,?,?,?,?,?,?,?,N'100.00',?,0,N'100',N'0',N'0.00',?,?,?,?,0,N'0.00',?,N'0.00',?,N'0',0,0,N'0',0,? ,0,N'0.00',?,N'',N'',N'0',N'0',N'',0,0,N'',N'',N'',N'',N'',N'',1,N'1',0,? ,N'0',N'0',?,N'0') ";//倒数第8个有差别，销售订单为1,
		}else{
			sqlInsertGoodsInfo = " insert into @ps2 values(0,0,?,?,?,?,?,?,?,?,?,?,N'100.00',?,0,N'100',N'0',N'0.00',?,?,?,?,0,N'0.00',?,N'0.00',?,N'0',0,0,N'0',0,? ,0,N'0.00',?,N'',N'',N'0',N'0',N'',0,0,N'',N'',N'',N'',N'',N'',4,N'1',0,? ,N'0',N'0',?,N'0') ";//倒数第8个有差别，采购订单为4,
		}
		String declareGoodsDetailInfo = " declare @ps3 dbo.tvpBakDlyOrderDetail ";
		String sqlInsertGoodsDetailInfo = " insert into @ps3 values(0,?,?,?,?,N'0',N'0.00',N'0',N'0.00',?,?,N'0.00',?,? ,N'0',N'0',N'0') ";
		String sqlDoSave = " exec SaveOrderBill @tvpDlyNdxOrder=@ps1,@tvpBakdlyOrder=@ps2,@tvpBakDlyOrderDetail=@ps3,@Operator=? ";
		
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
		inParamList.add(new InParam(12, jo.optString("cgddcloseorganization")));//采购订单有值，销售订单无值
		inParamList.add(new InParam(13, jo.optInt("cgddgoodstotalsum")));
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
			int addedIndex = start + 21 * i;// 21为当前语句需要参数个数
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
			//insert into @p2 values(0,0,?,						 ?,			  ?,			 ?,	 			?,		  ?,		?,		  ?,		?,    ?,  N'100.00', ?,0,N'100',N'0',N'0.00',         		 ?,			?,			?,  		   ?,  0,N'0.00',       ?,   N'0.00',    ?,     N'0',0,0,N'0',0,?  ,0,N'0.00',      ?,    N'',N'',N'0',N'0',N'',0,0,N'',N'',N'',N'',N'',N'',1,N'1',0,? ,N'0',N'0',    ?    ,N'0')
			//insert into @p2 values(0,0,N'1543140627735185292 ',8,N'0000100001 ',N'0000100001 ',N'0000200001 ',N'00003 ',N'00001 ',N'00002 ',N'00008 ',N'11 ',N'100.00',N'136.80 ',0,N'100',N'0',N'0.00',N'136.80 ',N'136.80 ',N'1504.80 ',N'2015-12-27 ',0,N'0.00',N'136.80 ',N'0.00',N'1504.80 ',N'0',0,0,N'0',0,15 ,0,N'0.00',N'1504.80 ',N'',N'',N'0',N'0',N'',0,0,N'',N'',N'',N'',N'',N'',1,N'1',0,1 ,N'0',N'0',N'136.80 ',N'0')
			//System.out.println("id19>>"+id19);
			inParams.add(new InParam(1 + addedIndex, id19)); // 第二轮开始为35 20+14+1
			//System.out.println("ddtype>>"+jo.optInt("ddtype"));
			inParams.add(new InParam(2 + addedIndex, jo.optInt("ddtype"))); // 订单类型，销售订单为8，采购订单为7
			//System.out.println("cgddorganization>>"+jo.optString("cgddorganization"));
			inParams.add(new InParam(3 + addedIndex, jo.optString("cgddorganization")));
			inParams.add(new InParam(4 + addedIndex, jo.optString("cgddorganization")));
			//System.out.println("cgddsupplyunit>>"+jo.optString("cgddsupplyunit"));
			inParams.add(new InParam(5 + addedIndex, jo.optString("cgddsupplyunit")));
			//System.out.println("cgddbrokerage>>"+jo.optString("cgddbrokerage"));
			inParams.add(new InParam(6 + addedIndex, jo.optString("cgddbrokerage")));
			//System.out.println("cgddreceivestorehouse>>"+jo.optString("cgddreceivestorehouse"));
			inParams.add(new InParam(7 + addedIndex, jo.optString("cgddreceivestorehouse")));
			//System.out.println("cgdddepartment>>"+jo.optString("cgdddepartment"));
			inParams.add(new InParam(8 + addedIndex, jo.optString("cgdddepartment")));
			//System.out.println("cgddgoodscode>>"+jo.optString("cgddgoodscode"));
			inParams.add(new InParam(9 + addedIndex, jo.optString("cgddgoodscode")));
			//System.out.println("cgddgoodssumStr>>"+cgddgoodssumStr);
			inParams.add(new InParam(10 + addedIndex, cgddgoodssumStr));
			inParams.add(new InParam(11 + addedIndex, jo.optString("cgddgoodsprice")));
			//System.out.println("cgddgoodsprice>>"+jo.optString("cgddgoodsprice"));
			inParams.add(new InParam(12 + addedIndex, jo.optString("cgddgoodsprice")));
			inParams.add(new InParam(13 + addedIndex, jo.optString("cgddgoodsprice")));
			//System.out.println("sumStr>>"+sumStr);
			inParams.add(new InParam(14 + addedIndex, sumStr));
			//System.out.println("cgdddate>>"+jo.optString("cgdddate"));
			inParams.add(new InParam(15 + addedIndex, jo.optString("cgdddate")));
			inParams.add(new InParam(16 + addedIndex, jo.optString("cgddgoodsprice")));
			inParams.add(new InParam(17 + addedIndex, sumStr));
			//System.out.println("cgddgoodscolor>>"+jo.optInt("cgddgoodscolor"));
			inParams.add(new InParam(18 + addedIndex, jo.optInt("cgddgoodscolor")));
			inParams.add(new InParam(19 + addedIndex, sumStr));
			//System.out.println("cgddgoodsindex>>"+jo.optInt("cgddgoodsindex"));
			inParams.add(new InParam(20 + addedIndex, jo.optInt("cgddgoodsindex")));//
			inParams.add(new InParam(21 + addedIndex, jo.optString("cgddgoodsprice")));// 14+20
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
			// insert into @p3 values(0,?					   ,? ,?  , ?    ,N'0',N'0.00',N'0',N'0.00',N'1504.80 ',N'1504.80 ',N'0.00',N'1504.80 ',1 ,N'0',N'0',N'0')
			// insert into @p3 values(0,N'1543140627735185292 ',1 ,15 ,N'11 ',N'0',N'0.00',N'0',N'0.00',N'1504.80 ',N'1504.80 ',N'0.00',N'1504.80 ',1 ,N'0',N'0',N'0')
			inParams.add(new InParam(1 + addedIndex, id19));// 51+1,59+1
			//System.out.println("size>>>>"+jo.optInt("cgddgoodssize"));
			inParams.add(new InParam(2 + addedIndex, jo.optInt("cgddgoodssize")));
			//System.out.println("color>>>>"+jo.optInt("cgddgoodscolor"));
			inParams.add(new InParam(3 + addedIndex, jo.optInt("cgddgoodscolor")));
			//System.out.println("cgddgoodssumStr>>>>"+cgddgoodssumStr);
			inParams.add(new InParam(4 + addedIndex, cgddgoodssumStr));
			//System.out.println("sumStr>>>>"+sumStr);
			inParams.add(new InParam(5 + addedIndex, sumStr));
			inParams.add(new InParam(6 + addedIndex, sumStr));
			inParams.add(new InParam(7 + addedIndex, sumStr));
			//System.out.println("index>>>>"+jo.optInt("cgddgoodsindex"));
			inParams.add(new InParam(8 + addedIndex, new Integer(jo.optInt("cgddgoodsindex"))));// 59
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
				jo.put("ArTotal00", rs.getDouble("ArTotal00"));// 期初应收
				jo.put("ArTotalAdd", rs.getDouble("ArTotalAdd"));// 应收增加
				jo.put("ArTotalDec", rs.getDouble("ArTotalDec"));// 应收减少
				jo.put("ArTotalYS", rs.getDouble("ArTotalYS"));// 预收余额
				jo.put("ArTotal", rs.getDouble("ArTotal"));// 应收余额
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
				jo.put("APTotal00", rs.getDouble("APTotal00"));// 期初应付
				jo.put("ApTotalAdd", rs.getDouble("ApTotalAdd"));// 应付增加
				jo.put("ApTotalDec", rs.getDouble("ApTotalDec"));// 应付减少
				jo.put("APTotalYF", rs.getDouble("APTotalYF"));// 预付余额
				jo.put("APTotal", rs.getDouble("APTotal"));// 应付余额
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
	
	// --------------------------------------商品销售分析表------------------------------------>>

	public static String genQueryResultForSPXSFXB(String goodsId, String beginDate,String endDate,String employeeID,String deptId,String organization,String OperatorID) throws Exception {
		DBAccess dba = null;
		JSONObject jo = new JSONObject();
		String result = jo.toString();
		try {
			dba = new DBAccess(true);
			// 查询应收信息
			String sql = "exec FZSaleAnalyse_BS @szPtypeId=?,@szKtypeid=N'',@szBtypeid=N'',@szBtypeid1=N'',@szBeginData=?,@szEndData=?,@ifstop=N'0',@Etypeid=?,@SaleTraitname=N'全部',@Dtypeid=?,@CustType=N'',@branchid=?,@vchtype=N'',@OperatorId=?,@OpID=N'0',@salepriceid=N''";
			//String sql = "exec FZSaleAnalyse_BS @szPtypeId=N'00005',@szKtypeid=N'',@szBtypeid=N'',@szBtypeid1=N'',@szBeginData=N'2016-01-01',@szEndData=N'2016-01-05',@ifstop=N'0',@Etypeid=N'00001',@SaleTraitname=N'全部',@Dtypeid=N'00001',@CustType=N'',@branchid=N'0000100001',@vchtype=N'',@OperatorId=N'00002',@OpID=N'0',@salepriceid=N''";
			// 设置输入参数列表
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(goodsId);
			paramList.add(beginDate);
			paramList.add(endDate);
			paramList.add(employeeID);
			paramList.add(deptId);
			paramList.add(organization);
			paramList.add(OperatorID);
			ResultSet rs = dba.executeQuery(sql, paramList);
			while (rs.next()) {
				String ptypeid = rs.getString("ptypeid");// 商品标识
				int Sqty = rs.getInt("Sqty");// 销售数量
				double CostTotal = rs.getDouble("CostTotal");// 成本金额
				double Profittotal = rs.getDouble("Profittotal");// 毛利额
				jo.put("ptypeid", ptypeid);// 商品标识
				jo.put("Sqty", new Integer(Sqty));// 销售数量
				jo.put("CostTotal", new Double(CostTotal));// 成本金额
				jo.put("Profittotal", new Double(Profittotal));// 毛利额
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

	// <<------------------------------------商品销售分析表----------------------------------
	public static void main(String args[]) throws Exception {
		
		 String cgddInfo= "{\"cgdddate\":\"2016-02-05\",\"cgddcode\":\"XSDD-2016-02-05-0018\",\"ddtype\":8,\"cgddsupplyunit\":\"0000200001\",\"cgddorganization\":\"0000100001\",\"cgddbrokerage\":\"00001\",\"cgddreceivestorehouse\":\"00001\",\"cgdddepartment\":\"00001\",\"cgddoperatorid\":\"00002\",\"cgddsendgoodsdate\":\"2016-02-05\",\"cgddcloseorganization\":\"\",\"cgddgoodstotalsum\":2}"; 
		 String goodsInfo = "[{\"ddtype\":8,\"cgddorganization\":\"0000100001\",\"cgddsupplyunit\":\"0000200001\",\"cgddbrokerage\":\"00001\",\"cgddreceivestorehouse\":\"00001\",\"cgdddepartment\":\"00001\",\"cgdddate\":\"2016-02-05\",\"cgddgoodscode\":\"00004\",\"cgddgoodssum\":2,\"cgddgoodsprice\":\"299.0000000000\",\"cgddgoodscolor\":2,\"cgddgoodsindex\":1}]"; 
		 String goodsDetailInfo = "[{\"cgddgoodssize\":8,\"cgddgoodssum\":2,\"cgddgoodsprice\":\"299.0000000000\",\"cgddgoodscolor\":2,\"cgddgoodsindex\":1}]"; 
		 submitCGDD(cgddInfo, goodsInfo, goodsDetailInfo);
		

	}
}
