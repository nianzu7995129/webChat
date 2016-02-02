package org.wechat.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.wechat.service.BusinessService;
import org.json.JSONObject;
import org.wechat.utils.base64.Base64Util;

public class BusinessServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String action_cgdd_code = "action_cgdd_code";// 生成采购订单编号
	private static final String action_xsdd_code = "action_xsdd_code";// 生成销售订单编号
	private static final String action_code_exist = "action_code_exist";// 订单编号是否重复
	private static final String action_organization = "action_organization";// 生成机构
	private static final String action_brokerage = "action_brokerage";// 生成经手人(采购订单，销售订单)
	private static final String action_employee = "action_employee";// 生成职员(商品销售分析表)
	private static final String action_department = "action_department";// 生成部门
	private static final String action_xsdd_customer = "action_xsdd_customer";// 销售订单-客户
	private static final String action_cgdd_supplyunit = "action_cgdd_supplyunit";// 采购订单-供货单位
	private static final String action_wlzmb_yfkcx_unit = "action_wlzmb_yfkcx_unit";// 往来账目表-应付款查询-单位
	private static final String action_wlzmb_yskcx_unit = "action_wlzmb_yskcx_unit";// 往来账目表-应收款查询-单位
	private static final String action_xsdd_storehouse = "action_xsdd_storehouse";// 销售订单-发货仓库
	private static final String action_cgdd_storehouse = "action_cgdd_storehouse";// 采购订单-收货仓库
	private static final String action_kczkb_storehouse = "action_kczkb_storehouse";// 库存状况表-仓库全名
	private static final String action_spxsfxb_storehouse = "action_spxsfxb_storehouse";// 商品销售分析表-仓库全名
	private static final String action_xsdd_goodstype = "action_xsdd_goodstype";// 销售订单-发货类型
	private static final String action_cgdd_goodstype = "action_cgdd_goodstype";// 销售订单-收货类型
	private static final String action_xsdd_goods = "action_xsdd_goods";// 销售订单-商品
	private static final String action_cgdd_goods = "action_cgdd_goods";// 采购订单-商品
	private static final String action_kczkb_goods = "action_kczkb_goods";// 库存状况表-商品
	private static final String action_goods_bynumer = "action_goods_bynumer";// 根据货号模糊查商品列表
	private static final String action_goodscolor = "action_goodscolor";// 商品颜色
	private static final String action_goodssize = "action_goodssize";// 商品尺寸
	private static final String action_goodsprice = "action_goodsprice";// 商品单价
	private static final String action_goodsInfo = "action_goodsInfo";// 商品颜色，尺寸和单价

	private static final String action_query_wlzmb = "action_query_wlzmb";// 往来明细表 查询
	private static final String action_save_cgdd = "action_save_cgdd";// 采购订单(销售订单) 保存
	private static final String action_query_kczk = "action_query_kczk";// 库存状况表 查询
	private static final String action_query_spxsfxb = "action_query_spxsfxb";// 商品销售分析表 查询

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String action = request.getParameter("action");

		BusinessService bs = new BusinessService();
		String result = "";
		Boolean isError = false;
		try {
			if (action_cgdd_code.equals(action)) {
				String date = request.getParameter("date");
				String pcName = request.getParameter("pcName");
				result = bs.getCGDDCode(date, pcName, 7);
			} else if (action_xsdd_code.equals(action)) {
				String date = request.getParameter("date");
				String pcName = request.getParameter("pcName");
				result = bs.getCGDDCode(date, pcName, 8);
			} else if (action_code_exist.equals(action)) {
				String date = request.getParameter("date");
				String pcName = request.getParameter("pcName");
				String code = request.getParameter("code");
				int useType = Integer.parseInt(request.getParameter("useType"));
				result = bs.isCodeExist(date, pcName, useType, code);
			} else if (action_organization.equals(action)) {
				String OperatorID = request.getParameter("OperatorID");
				int userType = Integer.parseInt(request.getParameter("userType"));
				result = bs.getOrganization(OperatorID, userType);
			} else if (action_brokerage.equals(action)) {
				String OperatorID = request.getParameter("OperatorID");
				String organization = request.getParameter("organization");
				result = bs.getBrokerage(OperatorID, organization, 0);
			}else if (action_employee.equals(action)) {
				String OperatorID = request.getParameter("OperatorID");
				String organization = request.getParameter("organization");
				result = bs.getBrokerage(OperatorID, organization, 1);
			} else if (action_department.equals(action)) {
				String BrokerageID = request.getParameter("BrokerageID");
				String OperatorID = request.getParameter("OperatorID");
				String organization = request.getParameter("organization");
				result = bs.getDepartment(BrokerageID, OperatorID, organization);
			} else if (action_xsdd_customer.equals(action)) {
				String OperatorID = request.getParameter("OperatorID");
				String supplyUnitCode = request.getParameter("supplyUnitCode");
				String organization = request.getParameter("organization");
				int pageNum = Integer.parseInt(request.getParameter("pageNum"));
				int itemsInEachPage = Integer.parseInt(request.getParameter("itemsInEachPage"));
				result = bs.getSupplyUnit(2, supplyUnitCode, organization, OperatorID, 0, pageNum, itemsInEachPage);
			} else if (action_cgdd_supplyunit.equals(action)) {
				String OperatorID = request.getParameter("OperatorID");
				String supplyUnitCode = request.getParameter("supplyUnitCode");
				String organization = request.getParameter("organization");
				int pageNum = Integer.parseInt(request.getParameter("pageNum"));
				int itemsInEachPage = Integer.parseInt(request.getParameter("itemsInEachPage"));
				result = bs.getSupplyUnit(1, supplyUnitCode, organization, OperatorID, 0, pageNum, itemsInEachPage);
			} else if (action_wlzmb_yfkcx_unit.equals(action)) {
				String OperatorID = request.getParameter("OperatorID");
				String supplyUnitCode = request.getParameter("supplyUnitCode");
				String organization = request.getParameter("organization");
				int pageNum = Integer.parseInt(request.getParameter("pageNum"));
				int itemsInEachPage = Integer.parseInt(request.getParameter("itemsInEachPage"));
				result = bs.getSupplyUnit(1, supplyUnitCode, organization, OperatorID, 1, pageNum, itemsInEachPage);
			} else if (action_wlzmb_yskcx_unit.equals(action)) {
				String OperatorID = request.getParameter("OperatorID");
				String supplyUnitCode = request.getParameter("supplyUnitCode");
				String organization = request.getParameter("organization");
				int pageNum = Integer.parseInt(request.getParameter("pageNum"));
				int itemsInEachPage = Integer.parseInt(request.getParameter("itemsInEachPage"));
				result = bs.getSupplyUnit(30, supplyUnitCode, organization, OperatorID, 1, pageNum, itemsInEachPage);
			} else if (action_xsdd_storehouse.equals(action)) {
				String OperatorID = request.getParameter("OperatorID");
				String organization = request.getParameter("organization");
				result = bs.getStoreHouse(organization, OperatorID, "send", 0);
			} else if (action_cgdd_storehouse.equals(action)) {
				String OperatorID = request.getParameter("OperatorID");
				String organization = request.getParameter("organization");
				result = bs.getStoreHouse(organization, OperatorID, "receive", 0);
			} else if (action_kczkb_storehouse.equals(action)) {
				String OperatorID = request.getParameter("OperatorID");
				String organization = request.getParameter("organization");
				result = bs.getStoreHouse(organization, OperatorID, null, 1);
			}else if (action_spxsfxb_storehouse.equals(action)) {
				String OperatorID = request.getParameter("OperatorID");
				String organization = request.getParameter("organization");
				result = bs.getStoreHouse(organization, OperatorID, null, 0);
			} else if (action_xsdd_goodstype.equals(action)) {
				String organization = request.getParameter("organization");
				result = bs.getGoodsType(1, organization);
			} else if (action_cgdd_goodstype.equals(action)) {
				String organization = request.getParameter("organization");
				result = bs.getGoodsType(2, organization);
			} else if (action_xsdd_goods.equals(action)) {
				String goodsCode = request.getParameter("goodsCode");
				String OperatorID = request.getParameter("OperatorID");
				String storeHouseID = request.getParameter("storeHouseID");
				int pageNum = Integer.parseInt(request.getParameter("pageNum"));
				int itemsInEachPage = Integer.parseInt(request.getParameter("itemsInEachPage"));
				result = bs.getGoods(goodsCode, storeHouseID, OperatorID, 0, 0, pageNum, itemsInEachPage);
			} else if (action_cgdd_goods.equals(action)) {
				String goodsCode = request.getParameter("goodsCode");
				String OperatorID = request.getParameter("OperatorID");
				String storeHouseID = request.getParameter("storeHouseID");
				int pageNum = Integer.parseInt(request.getParameter("pageNum"));
				int itemsInEachPage = Integer.parseInt(request.getParameter("itemsInEachPage"));
				result = bs.getGoods(goodsCode, storeHouseID, OperatorID, 0, 3, pageNum, itemsInEachPage);
			} else if (action_kczkb_goods.equals(action)) {//商品销售分析与库存状况表bDisplayStop与selecttype设置相同
				String goodsCode = request.getParameter("goodsCode");
				String OperatorID = request.getParameter("OperatorID");
				int pageNum = Integer.parseInt(request.getParameter("pageNum"));
				int itemsInEachPage = Integer.parseInt(request.getParameter("itemsInEachPage"));
				result = bs.getGoods(goodsCode, "", OperatorID, 1, 0, pageNum, itemsInEachPage);
			} else if (action_goods_bynumer.equals(action)) {
				String goodsCode = request.getParameter("goodsCode");
				String storeHouseID = request.getParameter("storeHouseID");
				String OperatorID = request.getParameter("OperatorID");
				int pageNum = Integer.parseInt(request.getParameter("pageNum"));
				int itemsInEachPage = Integer.parseInt(request.getParameter("itemsInEachPage"));
				result = bs.getGoodsInfoByNumber(goodsCode, storeHouseID, OperatorID, pageNum, itemsInEachPage);
			} else if (action_goodscolor.equals(action)) {
				String storeHouseID = request.getParameter("storeHouseID");
				String goodsID = request.getParameter("goodsID");
				result = bs.getGoodsColor(storeHouseID, goodsID);
			} else if (action_goodssize.equals(action)) {
				String goodsID = request.getParameter("goodsID");
				result = bs.getGoodsSize(goodsID);
			} else if (action_goodsprice.equals(action)) {
				String OperatorID = request.getParameter("OperatorID");
				String goodsID = request.getParameter("goodsID");
				result = bs.getGoodsPrice(goodsID, OperatorID);
			} else if (action_goodsInfo.equals(action)) {
				String storeHouseID = request.getParameter("storeHouseID");
				String goodsID = request.getParameter("goodsID");
				String OperatorID = request.getParameter("OperatorID");
				result = bs.getGoodsInfo(storeHouseID, goodsID, OperatorID);
			} else if (action_query_wlzmb.equals(action)) {
				String supplyUnit = request.getParameter("supplyUnit");
				String organization = request.getParameter("organization");
				String OperatorID = request.getParameter("OperatorID");
				int includeYSYF = Integer.parseInt(request.getParameter("includeYSYF"));
				String filter = request.getParameter("filter");
				result = bs.queryForWLZMB(supplyUnit, organization, OperatorID, includeYSYF, filter);
			} else if (action_query_kczk.equals(action)) {
				String goodsId = request.getParameter("goodsId");
				String organization = request.getParameter("organization");
				String storeHouseID = request.getParameter("storeHouseID");
				String OperatorID = request.getParameter("OperatorID");
				result = bs.queryForKCZKB(goodsId, organization, storeHouseID, OperatorID);
			} else if (action_save_cgdd.equals(action)) {
				String cgddInfo = Base64Util.Base64Decode("cgddInfo", request);
				String goodsInfo = Base64Util.Base64Decode("goodsInfo", request);
				String goodsDetailInfo = Base64Util.Base64Decode("goodsDetailInfo", request);
				result = bs.saveForCGDD(cgddInfo, goodsInfo, goodsDetailInfo);
			} else if (action_query_spxsfxb.equals(action)) {
				String goodsID = request.getParameter("goodsID");
				String beginDate = request.getParameter("beginDate");
				String endDate = request.getParameter("endDate");
				String employeeID = request.getParameter("employeeID");
				String deptId = request.getParameter("deptId");
				String organization = request.getParameter("organization");
				String OperatorID = request.getParameter("OperatorID");
				result = bs.queryForSPXSFXB(goodsID, beginDate, endDate, employeeID,deptId,organization,OperatorID);
			} else {
				result = "未知的action:" + action;
				isError = true;
			}
		} catch (Exception e) {
			result = e.getMessage();
			isError = true;
		}
		response.getWriter().print(resultJson(isError, result));
	}

	private String resultJson(boolean isError, String result) {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("\"isError\"").append(":\"").append(isError).append("\"");
		sb.append(",");
		try {
			new JSONObject(result);
			sb.append("\"result\"").append(":").append(result).append("");
		} catch (Exception e) {
			try {
				new org.json.JSONArray(result);
				sb.append("\"result\"").append(":").append(result).append("");
			} catch (Exception ee) {
				sb.append("\"result\"").append(":\"").append(result).append("\"");
			}
		}
		sb.append("}");
		return sb.toString();
	}
}
