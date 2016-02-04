package org.wechat.dbo.vo;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import net.sf.json.JSONArray;
import org.wechat.dbo.DBAccess;
import org.wechat.dbo.DBConst;

public class GoodsColor {
	/** 颜色名称 */
	private String PCFullName;
	/** 颜色条码 */
	private String PCUserCode;

	public String getGoodsColorInfo(DBAccess dba, String ktypeid, String ptypeid ) {
		JSONArray result = new JSONArray();
		try {
			String sql = "exec FN_TCGetPCtypeList @nSearchType=0,@sSearStr=N'%%',@szid=-1,@ktypeid=?,@ptypeid=?,@commtype=9,@bHaveQty=N'0'";
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(ktypeid);
			paramList.add(ptypeid);
			ResultSet rs = dba.executeQuery(sql, paramList);
			List<GoodsColor> goodsColorList = new ArrayList<GoodsColor>();
			while (rs.next()) {
				String fullName = rs.getString("PCFullName");// 颜色名称
				String userCode = rs.getString("PCREC");// 颜色条码
				GoodsColor goodsColor = new GoodsColor();
				goodsColor.setPCFullName(fullName);
				goodsColor.setPCUserCode(userCode);
				goodsColorList.add(goodsColor);
			}
			result = JSONArray.fromObject(goodsColorList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.toString();
	}

	public String getPCUserCode() {
		return PCUserCode;
	}

	public void setPCUserCode(String userCode) {
		PCUserCode = userCode;
	}

	public String getPCFullName() {
		return PCFullName;
	}

	public void setPCFullName(String fullName) {
		PCFullName = fullName;
	}

	public static void main(String args[]) throws Exception {
		DBAccess dba = new DBAccess(true);
		GoodsColor goodsColor = new GoodsColor();
		String result = goodsColor.getGoodsColorInfo(dba, DBConst.default_receivestorehouse, DBConst.default_goodscode);
		System.out.println("商品颜色：" + result);
		dba.close();

	}
}
