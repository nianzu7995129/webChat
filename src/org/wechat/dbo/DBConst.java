package org.wechat.dbo;

public class DBConst {
	
	/** 测试数据库服务器地址　*/
	public static final String dburl_ceshi = "jdbc:jtds:sqlserver://115.28.43.22:1433/ceshi";
	/** 实际数据库服务器地址 */
	public static final String dburl_WewaySoftFZ2 = "jdbc:jtds:sqlserver://123.57.209.53:1433/WewaySoftFZ2";

	
	/** 根级机构 */
	public static final String root_orgnization = "00000";
	/** 默认机构 */
	public static final String default_orgnization = "0000100001";
	/** 默认结算机构 */
	public static final String default_closeorgnization = "0000100001";
	/** 默认用户编号*/
	public static final String default_OperatorID = "00002";
	/** 默认经手人编号 */
	public static final String default_BrokerageID = "00004";
	/** 默认仓库标识(收货仓库) */
	public static final String default_receivestorehouse = "00001";
	/** 默认商品编号 */
	public static final String default_goodscode = "00008";
	/** 默认采购订单编号 */
	public static final String default_cgddCode = "CGDD-2015-12-27-0001";
	/** 默认供货单位 */
	public static final String default_supplyunit = "0000100001";
	/** 默认部门 */
	public static final String default_department = "00003";
	/** 默认交货日期 */
	public static final String default_sendGoodsDate = "2016-12-27";
	/** 默认商品总数量 */
	public static final String default_goodstotalsum = "10";
	/** 默认商品数量(2件共10) */
	public static final String default_goodssum = "5";
	/** 默认商品单价 */
	public static final String default_goodsprice = "12.00";
	/** 默认商品尺寸 */
	public static final int default_goodssize = 1;
}
