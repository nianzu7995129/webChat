package org.wechat.dbo.vo;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import net.sf.json.JSONArray;
import org.wechat.dbo.DBAccess;
import org.wechat.dbo.DBConst;

public class GoodsSize {
	/** 尺寸标识 */
	private String sizeid;
	/** 尺寸名称 */
	private String sizename;

	public String getGoodsSizeInfo(DBAccess dba, String ptypeid) {
		JSONArray result = new JSONArray();
		try {
			String sql = "Select a.sizeid,b.name as sizename from PtypeSize a inner join Size b on a.SizeId=b.SizeId where a.PtypeId=? order by b.[order]";
			List<Object> paramList = new ArrayList<Object>();
			paramList.add(ptypeid);
			ResultSet rs = dba.executeQuery(sql, paramList);
			List<GoodsSize> goodsSizeList = new ArrayList<GoodsSize>();
			while (rs.next()) {
				String sizeid = rs.getString("sizeid");// 尺寸标识
				String sizename = rs.getString("sizename");// 尺寸名称
				GoodsSize goodsSize = new GoodsSize();
				goodsSize.setSizeid(sizeid);
				goodsSize.setSizename(sizename);
				goodsSizeList.add(goodsSize);
			}
			result = JSONArray.fromObject(goodsSizeList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.toString();
	}

	public String getSizeid() {
		return sizeid;
	}

	public void setSizeid(String sizeid) {
		this.sizeid = sizeid;
	}

	public String getSizename() {
		return sizename;
	}

	public void setSizename(String sizename) {
		this.sizename = sizename;
	}

	public static void main(String args[]) throws Exception {
		DBAccess dba = new DBAccess();
		GoodsSize goodsSize = new GoodsSize();
		String result = goodsSize.getGoodsSizeInfo(dba, DBConst.default_goodscode);
		System.out.println("商品尺寸信息：" + result);
		dba.close();
	}
}
