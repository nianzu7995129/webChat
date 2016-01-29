package org.wechat.service;

import org.wechat.dbo.DBUtils;
import org.wechat.dbo.DBAccess;
import org.wechat.dbo.vo.Organization;
import org.wechat.dbo.vo.Brokerage;
import org.wechat.dbo.vo.Department;
import org.wechat.dbo.vo.SupplyUnit;
import org.wechat.dbo.vo.Storehouse;
import org.json.JSONObject;
import org.wechat.dbo.vo.GoodsType;
import org.wechat.dbo.vo.Goods;
import org.wechat.dbo.vo.GoodsColor;
import org.wechat.dbo.vo.GoodsSize;
import org.wechat.dbo.vo.GoodsPrice;

public class BusinessService {
	/**
	 * @param date
	 * @param pcName
	 * @param userType 7:采购订单，8:销售订单
	 * @return
	 * @throws Exception
	 */
	public String getCGDDCode(String date, String pcName, int userType) throws Exception {
		DBAccess dba = null;
		String result = "";
		try {
			dba = new DBAccess();
			result = DBUtils.genCGDD(dba, date, pcName, userType);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			dba.close();
		}
		return result;
	}

	/**
	 * @param date
	 * @param pcName
	 * @param userType 7:采购订单，8:销售订单
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public String isCodeExist(String date, String pcName, int useType, String code) throws Exception {
		DBAccess dba = null;
		JSONObject jo = new JSONObject();
		try {
			dba = new DBAccess();
			String tmpResult = DBUtils.CGDDisExist(dba, code);
			if ("1".equals(tmpResult)) {
				jo.put("isExist", "true");
				tmpResult = DBUtils.genCGDD(dba, date, pcName, useType);
				jo.put("ddcode", tmpResult);
			} else {
				jo.put("isExist", "fasle");
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			dba.close();
		}
		return jo.toString();
	}

	/**
	 * @param OperatorID
	 * @param userType // 销售订单为23,往来账目表为15，采购订单为15,库存状况表 31
	 * @return
	 * @throws Exception
	 */
	public String getOrganization(String OperatorID, int userType) throws Exception {
		Organization organization = new Organization();
		DBAccess dba = null;
		String result = "";
		try {
			dba = new DBAccess();
			result = organization.getOrganizationInfo(dba, OperatorID, userType);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			if (dba != null) {
				dba.close();
			}
		}
		return result;
	}

	/**
	 * @param OperatorID
	 * @param organization // 机构编号
	 * @return
	 * @throws Exception
	 */
	public String getBrokerage(String OperatorID, String organization) throws Exception {
		Brokerage brokerage = new Brokerage();
		DBAccess dba = null;
		String result = "";
		try {
			dba = new DBAccess();
			result = brokerage.getBrokerageInfo(dba, organization, OperatorID);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			if (dba != null) {
				dba.close();
			}
		}
		return result;
	}

	/**
	 * @param BrokerageID
	 * @return
	 * @throws Exception
	 */
	public String getDepartment(String BrokerageID, String OperatorID, String organization) throws Exception {
		Department dept = new Department();
		DBAccess dba = null;
		String defaultDepart = "";
		String result = "";
		JSONObject jo = new JSONObject();
		try {
			dba = new DBAccess();
			defaultDepart = dept.getDefaultDepartment(dba, BrokerageID);
			result = dept.getDepartmentInfo(dba, OperatorID, organization);
			jo.put("default", defaultDepart);
			jo.put("info", result);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			if (dba != null) {
				dba.close();
			}
		}
		return jo.toString();
	}

	/**
	 * @param usertype // 1:为采购订单中的供货单位,2:为销售订单中的客户,30:为往来账目表的单位
	 * @param orgCode
	 * @param OperatorId
	 * @param bDisplayStop // 0:为采购订单和销售订单使用 , 1:为往来账目表使用
	 * @return
	 * @throws Exception
	 */
	public String getSupplyUnit(int usertype, String supplyUnitCode, String orgCode, String OperatorId, int bDisplayStop, int pageNum, int itemsInEachPage) throws Exception {
		SupplyUnit supplyUnit = new SupplyUnit();
		DBAccess dba = null;
		String result = "";
		try {
			dba = new DBAccess();
			result = supplyUnit.getSupplyUnitInfo(dba, usertype, supplyUnitCode, orgCode, OperatorId, bDisplayStop, pageNum, itemsInEachPage);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			if (dba != null) {
				dba.close();
			}
		}
		return result;
	}

	/**
	 * @param orgCode
	 * @param OperatorId
	 * @param type 销售订单为："send" 采购订单为："receive";
	 * @param hastransferstock 销售订单为0，采购订单为0，库存状况表为1
	 * @return
	 * @throws Exception
	 */
	public String getStoreHouse(String orgCode, String OperatorId, String type, int hastransferstock) throws Exception {
		Storehouse storehouse = new Storehouse();
		DBAccess dba = null;
		String defaultStoreHouse = "";
		String result = "";
		JSONObject jo = new JSONObject();
		try {
			dba = new DBAccess();
			if (type != null) {
				defaultStoreHouse = storehouse.getDefaultReciveStorehouseInfo(dba, orgCode, OperatorId, type);
			}
			result = storehouse.getStorehouseInfo(dba, orgCode, OperatorId, hastransferstock);
			if (type != null) {
				jo.put("default", defaultStoreHouse);
			}
			jo.put("info", result);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			if (dba != null) {
				dba.close();
			}
		}
		return jo.toString();
	}

	/**
	 * @param opType //1:(销售订单)发货类型，2:(采购订单)收货类型
	 * @param orgCode
	 * @return
	 * @throws Exception
	 */
	public String getGoodsType(int opType, String orgCode) throws Exception {
		GoodsType goodsType = new GoodsType();
		DBAccess dba = null;
		String result = "";
		try {
			dba = new DBAccess();
			result = goodsType.getGoodsTypeInfo(dba, opType, orgCode);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			if (dba != null) {
				dba.close();
			}
		}
		return result;
	}

	/**
	 * @param szparid 上级商品编码
	 * @param szktypeid 仓库编号 // 销售订单有值，采购订单有值，库存状况无值
	 * @param orgCode
	 * @param OperatorID
	 * @param bDisplayStop 销售订单为0，采购订单为0，库存状况为1
	 * @param selectType // 销售订单为0 ， 采购订单为3 ， 库存状况为0
	 * @param pageNum
	 * @return
	 * @throws Exception
	 */
	public String getGoods(String szparid, String szktypeid, String orgCode, String OperatorID, int bDisplayStop, int selectType, int pageNum, int itemsInEachPage) throws Exception {
		Goods goods = new Goods();
		DBAccess dba = null;
		String result = "";
		try {
			dba = new DBAccess();
			result = goods.getGoodsInfo(dba, szparid, szktypeid, orgCode, OperatorID, bDisplayStop, selectType, pageNum, itemsInEachPage);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			if (dba != null) {
				dba.close();
			}
		}
		return result;
	}

	/**
	 * @param ktypeid 仓库编码
	 * @param ptypeid 商品编码
	 * @return
	 * @throws Exception
	 */
	public String getGoodsColor(String ktypeid, String ptypeid) throws Exception {
		GoodsColor goodsColor = new GoodsColor();
		DBAccess dba = null;
		String result = "";
		try {
			dba = new DBAccess();
			result = goodsColor.getGoodsColorInfo(dba, ktypeid, ptypeid);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			if (dba != null) {
				dba.close();
			}
		}
		return result;
	}

	public String getGoodsSize(String ptypeid) throws Exception {
		GoodsSize goodsSize = new GoodsSize();
		DBAccess dba = null;
		String result = "";
		try {
			dba = new DBAccess();
			result = goodsSize.getGoodsSizeInfo(dba, ptypeid);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			if (dba != null) {
				dba.close();
			}
		}
		return result;
	}

	public String getGoodsPrice(String ptypeid, String OperatorID) throws Exception {
		GoodsPrice goodsPrice = new GoodsPrice();
		DBAccess dba = null;
		String result = "";
		try {
			dba = new DBAccess();
			result = goodsPrice.getGoodPriceInfo(dba, ptypeid, OperatorID);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			if (dba != null) {
				dba.close();
			}
		}
		return result;
	}

	/**
	 * @param ktypeid
	 * @param ptypeid
	 * @param OperatorID
	 * @return
	 * @throws Exception
	 */
	public String getGoodsInfo(String ktypeid, String ptypeid, String OperatorID) throws Exception {
		GoodsColor goodsColor = new GoodsColor();
		GoodsSize goodsSize = new GoodsSize();
		GoodsPrice goodsPrice = new GoodsPrice();
		DBAccess dba = null;
		String result = "";
		JSONObject jo = null;
		try {
			jo = new JSONObject();
			dba = new DBAccess();
			String color = goodsColor.getGoodsColorInfo(dba, ktypeid, ptypeid);
			String size = goodsSize.getGoodsSizeInfo(dba, ptypeid);
			String price = goodsPrice.getGoodPriceInfo(dba, ptypeid, OperatorID);
			jo.put("color", color);
			jo.put("size", size);
			jo.put("price", price);
			result = jo.toString();
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			if (dba != null) {
				dba.close();
			}
		}
		return result;
	}

	/**
	 * @param supplyUnit 单位
	 * @param organization 机构
	 * @param OperatorID
	 * @param includeYSYF 0:不包含 ,1:包含 (是否包含预收预付)
	 * @param Filter 0:全部显示 ,1:应收款为零 ,2:应收款不为零，则为2 (显示方式)
	 * @return
	 * @throws Exception
	 */
	public String queryForWLZMB(String supplyUnit, String organization, String OperatorID, int includeYSYF, String Filter) throws Exception {
		String result = DBUtils.genQueryResultForWLZMB(supplyUnit, organization, OperatorID, includeYSYF, Filter);
		return result;
	}

	public String queryForKCZKB(String goodsId, String organization, String storeHouseID, String OperatorID) throws Exception {
		String result = DBUtils.genQueryResultForKCZK(goodsId, organization, storeHouseID, OperatorID);
		return result;
	}

	public String saveForCGDD(String cgddInfo, String goodsInfo, String goodsDetailInfo) throws Exception {
		String result = "";
		try {
			result = DBUtils.submitCGDD(cgddInfo, goodsInfo, goodsDetailInfo);
		} catch (Exception e) {
			throw new Exception("保存失败");
		}
		return result;
	}
}
