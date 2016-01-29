package org.wechat.dbo.vo;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;

import org.wechat.dbo.DBAccess;
import org.wechat.dbo.DBConst;

public class GoodsPrice {
	/** 采购价种类 */
	private String DisplayName;
	/** 价格 */
	private String price;
	/** 采购价种类标识 */
	private String PriceTypeID;

	public String getGoodPriceInfo(DBAccess dba, String ptypeid, String OperatorID) {
		JSONArray result = new JSONArray();
		try {
			String sql = "exec FN_FZGetPriceAndDiscount @ptypeid=?,@priceMode=N'B',@searStr=N'%%',@OperatorID=?,@shopid=N'0'";
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(ptypeid);
			paramList.add(OperatorID);
			ResultSet rs = dba.executeQuery(sql, paramList);
			List<GoodsPrice> goodPriceList = new ArrayList<GoodsPrice>();
			while (rs.next()) {
				String displayName = rs.getString("DisplayName");// 采购价种类
				String price = rs.getString("price");// 价格
				String priceTypeID = rs.getString("PriceTypeID");// 采购价种类标识
				GoodsPrice goodPrice = new GoodsPrice();
				goodPrice.setDisplayName(displayName);
				goodPrice.setPrice(price);
				goodPrice.setPriceTypeID(priceTypeID);
				goodPriceList.add(goodPrice);
			}
			result = JSONArray.fromObject(goodPriceList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.toString();
	}

	public String getDisplayName() {
		return DisplayName;
	}

	public void setDisplayName(String displayName) {
		DisplayName = displayName;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getPriceTypeID() {
		return PriceTypeID;
	}

	public void setPriceTypeID(String priceTypeID) {
		PriceTypeID = priceTypeID;
	}

	public static void main(String args[]) throws Exception {
		DBAccess dba = new DBAccess(true);
		GoodsPrice goodPrice = new GoodsPrice();
		String result = goodPrice.getGoodPriceInfo(dba, "00008", DBConst.default_OperatorID);
		System.out.println("商品单价信息：" + result);
		dba.close();
	}
}
