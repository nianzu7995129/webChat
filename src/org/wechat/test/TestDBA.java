package org.wechat.test;

import junit.framework.TestCase;

import org.wechat.dbo.DBAccess;
import org.wechat.dbo.DBUtils;
import org.wechat.dbo.vo.*;

public class TestDBA extends TestCase {

	private DBAccess dba = null;

	public void setUp() throws Exception {
		dba = new DBAccess(true);
	}

	public void tearDown() {
		dba.close();
	}

	// --------------------------------采购订单---------------------------------------

	// 生成采购订单以及订单是否重复判断
	public void testCreateCGDD() throws Exception {
		String cgdd = DBUtils.genCGDD(dba,"2015-12-28", "127.0.0.1",7);
		System.out.println("采购订单编号:" + cgdd);
	}

	/*
	// 生成机构
	public void testGenOrganization() throws Exception {
		Organization.main(null);
	}

	// 界面上选择机构后，自动生成经手人
	public void testGenBrokerage() throws Exception {
		Brokerage.main(null);
	}

	// 选择经手人后，自动生成所在部门
	public void testGenDepartment() throws Exception {
		Department.main(null);
	}

	// 选择供货单位
	public void testGenSupplyUnit() throws Exception {
		SupplyUnit.main(null);
	}

	// 收货仓库
	public void testGenReciveStorehouse() throws Exception {
		Storehouse.main(null);
	}

	// 收货类型
	public void testGenReceiveGoodsType() throws Exception {
		GoodsType.main(null);
	}

	// 商品信息，选择商品后自动带出货号
	public void testGenGoods() throws Exception {
		Goods.main(null);
	}

	// 商品颜色
	public void testGenGoodsColor() throws Exception {
		GoodsColor.main(null);
	}

	// 商品尺码
	public void testGenGoodsSize() throws Exception {
		GoodsSize.main(null);
	}

	// 商品单价(可录入)
	public void testGenGoodsPrice() throws Exception {
		GoodsPrice.main(null);
	}
	*/
	// 保存订单（最后处理）

	// <<--------------------------------采购订单---------------------------------------

}
