<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="org.wechat.utils.AuthUtil"%>
<%
	String contextPath = request.getContextPath();
	String pcName = request.getServerName();
	String OperatorID = (String)session.getAttribute("OperatorID");
	if(OperatorID==null){
		session.setAttribute("visitPage","report_cgdd.jsp");
		response.setContentType("text/html; charset=UTF-8");
		response.sendRedirect("login.jsp");
	}else{
		boolean valid = AuthUtil.checkValid(OperatorID,"cgdd");
		if(!valid){
			session.setAttribute("visitPage","report_cgdd.jsp");
			response.setContentType("text/html; charset=UTF-8");
			response.sendRedirect("noAuth.jsp");
		}
	}
%>
<!DOCTYPE html>
<html>
	<head>
		<link rel="stylesheet" href="http://code.jquery.com/mobile/1.3.2/jquery.mobile-1.3.2.min.css">
		<script src="http://code.jquery.com/jquery-1.8.3.min.js"></script>
		<script src="http://code.jquery.com/mobile/1.3.2/jquery.mobile-1.3.2.min.js"></script>
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<meta http-equiv="pragma" content="no-cache"> 
		<meta http-equiv="cache-control" content="no-cache"> 
		<meta http-equiv="expires" content="0">
		<script src="js/tree.js"></script>
		<script src="js/goods.js"></script>
		<script src="js/loading.js"></script>
		<script src="js/json2.js"></script>
		<script src="js/Base64.js"></script>
		<title>采购订单</title>
	</head>

	<body>
		<div data-role="page" id="pageHome">
			<div data-role="content" data-theme="b">
				<form method="post">					
					<div class="ui-field-contain">
						<label for="date">录单日期：</label>
						<input type="date" name="date" id="date"  data-clear-btn="true">
					</div>
					<div class="ui-field-contain">
						<label for="bianhao">单据编号：</label>
						<input type="text" name="bianhao" id="bianhao" readonly="readonly"  data-clear-btn="true">
					</div>
					<div class="ui-field-contain">
						<label for="jigou">机构：</label>
						<a id="jigouHref"><input readonly="readonly" type="text" name="jigou" orgCode="" id="jigou"></a>
					</div>
					<div class="ui-field-contain">
						<label for="jiesuanjigou">结算机构：</label>
						<a id="jiesuanjigouHref" ><input readonly="readonly" type="text" name="jiesuanjigou" orgCode="" id="jiesuanjigou"></a>
					</div>
					<div class="ui-field-contain">
						<label for="jingshouren">经手人：</label>
						<select name="jingshouren" id="jingshouren" data-native-menu="false" ></select>
					</div>
					<div class="ui-field-contain">
						<label for="bumen">部门：</label>
						<select name="bumen" id="bumen" data-native-menu="false"></select>
					</div>
					<div class="ui-field-contain">
						<label for="gonghuodanwei">供货单位：</label>
						<!-- <a id="gonghuodanweiHref" data-rel="popup" data-transition="pop" > -->
						<input type="text" name="gonghuodanwei" id="gonghuodanwei">
						<!-- </a> -->
					</div>
					<div class="ui-field-contain">
						<label for="shouhuocangku">收货仓库：</label>
						<select name="shouhuocangku" id="shouhuocangku" data-native-menu="false"></select>
					</div>
					<div class="ui-field-contain">
						<label for="jiaohuoriqi">交货日期：</label>
						<input type="date" name="jiaohuoriqi" id="jiaohuoriqi" data-clear-btn="true">
					</div>
					<div class="ui-field-contain">
						<label for="shouhuoleixing">收货类型：</label>
						<select name="shouhuoleixing" id="shouhuoleixing" data-native-menu="false"></select>
					</div>
					
					<input type="button" id="addGoodsBtnHref" class="ui-btn" data-inline="true" value="增加商品">

					<ol data-role="listview" data-inset="true" id="goodsList">
					</ol>
					
					<input type="button" id="saveBtn" class="ui-btn" data-corners="false" data-theme="b" value="保存订单">
				</form>
			</div>
		</div>
		
		<!-- 弹出层用于树组件 -->
		<div data-role="page" id="pageBasic">
			<div data-role="content" data-theme="b">
				<!-- <a id="returnPageHomeBtn" href="#pageHome" data-role="button" data-icon="back">返回</a> -->
				<div id="pageOper" class='ui-grid-a'>
					<div class='ui-block-a'>
						<button id='prevBtn' class='ui-btn-icon-notext' data-theme='b' data-icon='arrow-l'>上一页</button>
					</div>
					<div class='ui-block-b'>
						<button id='nextBtn' class='ui-shadow ui-btn ui-corner-all' data-theme='b' data-icon='arrow-r'>下一页</button>
					</div>
				</div>
				<div id="orgTree" data-role="content">
					<div id="orgTreeListView" data-role="listview">
						<div id="returnParentDiv"><button class="ui-btn" data-inline="true">返回上一级</button></div>
						<div id="treeContainer"></div>
					</div>
				</div>
				<fieldset>
					<input type="text" name="choosedInfoLabel" readonly="readonly" id="choosedInfoLabel" data-clear-btn="true">
					<input type="hidden" name="choosedInfoValue" id="choosedInfoValue" data-clear-btn="true">
					<input type="hidden" name="choosedInfoOtherValue" id="choosedInfoOtherValue" data-clear-btn="true">
				</fieldset>
				<br>
				<div class='ui-grid-a'>
					<div class='ui-block-a'>
						<a id="confirmOrgBtn" data-role="button">确定</a>
					</div>
					<div class='ui-block-b'>
						<a id="returnPageHomeBtn" data-role="button">返回</a>
					</div>
				</div>
				<!--<a id="confirmOrgBtn" data-role="button" data-theme='b'>确定</a> -->
			</div>
		</div>
		<!-- 弹出层用于增加商品 -->
		<div id="pageAddGoods" data-role="page">
			<div data-role="content" data-theme="b">
				<form method="post">
					<a href="#pageHome" data-role="button" data-icon="back">返回</a>
					<div class="ui-field-contain">
						<label for="cgddgoodsname">商品名称：</label>
						<a id="cgddgoodsnameHref"><input readonly="readonly" type="text" name="cgddgoodsname" orgCode="" id="cgddgoodsname"></a>
					</div>
					
					<div class="ui-field-contain">
						<label for="cgddgoodsnumber">货号：</label>
						<input type="text" name="cgddgoodsnumber" id="cgddgoodsnumber"  placeholder="货号...">
					</div>
					
					<div class="ui-field-contain">
						<label for="cgddgoodscolor">商品颜色：</label>
						<select id="cgddgoodscolor" name="cgddgoodscolor" data-native-menu="false">
							<option>请选择商品颜色</option>
						</select>
					</div>
	
					<div class="ui-field-contain">
						<label for="cgddgoodssize">商品尺码：</label>
						<select id="cgddgoodssize" name="cgddgoodssize" data-native-menu="false">
							<option>请选择商品尺码</option>
						</select>
					</div>
	
					<div class="ui-field-contain">
						<label for="sum">商品数量：</label>
						<input name="sum" id="sum" type="text" placeholder="请输入商品数量">
					</div>
	
					<div class="ui-field-contain">
						<label for="price">商品单价：</label>
						<select id="price" name="price" data-native-menu="false">
							<option value="mingcheng1">请选择商品价格</option>
						</select>
					</div>
	
					<div class="ui-field-contain">
						<label for="money">商品金额：</label>
						<input id="money" name="money"  type="text"  placeholder="金额">
					</div>
	
					<div class="ui-field-contain">
						<label for="transaction">成交金额：</label>
						<input id="transaction" name="transaction" type="text" placeholder="成交金额...">
					</div>
					<!-- <a data-rel="back" data-role="button" data-icon="arrow-l" onclick="addGoodsInfo()">确定</a> -->
					<a data-role="button" data-icon="arrow-l" onclick="addGoodsInfo()">确定</a>
				</form>
			</div>
		</div>
	</body>

	<script type="text/javascript">
		var contextPath = "<%=contextPath%>";
		var cgddcode = null;	//机构编码 （用于判断机构编码是否改变）
		var orgTreeObj;	//树组件
		var treeData = null;//机构数据
		var popType=0;//用于区分当前弹出树组件的使用对象，0：默认未使用，1：机构，2：供货单位（客户），3：结算机构，4：商品，5：货号搜索得到的商品，6：单位名称搜到的单位
		
		//如下数据，当机构改变时需要清空
		var customerData = null;//供货单位数据
		var alreadyChooseGoodsData = {"goodsList": []};//商品列表
		
		var ajaxTimeout = 1000000;
		
		$(function(){
			//初始化时使部分下拉列表不可编辑，选择机构后才可编辑。这样不需要单独处理每个下拉列表的click加载
			$("#jingshouren").selectmenu("disable",true);
			$("#bumen").selectmenu("disable",true);
			$("#shouhuocangku").selectmenu("disable",true);
			$("#shouhuoleixing").selectmenu("disable",true);
			$("#addGoodsBtnHref").button("disable");
			$("#saveBtn").button("disable");
			
			//选择日期后触发
			$("#date").on('input',function(e){  
				var date = $("#date").val();
				if(date.length>0){
					sendForCGDD();
				}
			});
	
			//监听上一页和下一页
			$("#prevBtn").bind("click",function(){
				//取上一页缓存数据展现
				if(orgTreeObj.pageNum==1){
					showTip("当前为第一页",true);
					return;
				}
				var prevPage = parseInt(orgTreeObj.pageNum)-1;
				getData($(this),prevPage);
			});
			
			$("#nextBtn").bind("click",function(){
				//取下一页缓存数据展现
				if(orgTreeObj.realItemsInEachPage<orgTreeObj.maxItemsInEachPage){
					showTip("已是最后一页",true);
					return;
				}
				var nextPage = parseInt(orgTreeObj.pageNum)+1;
				getData($(this),nextPage);
			});
			
			//生成机构
			$("#jigouHref").bind("click",function(){
				var organization =  $("#jigou").data("orgCode");
				if(typeof(organization)!="undefined" && organization!=null && organization.length>0){			
					showDialogue("修改机构会更新部分数据以及清空商品列表，确定要继续吗?",function(){
						popType = 1;
						showLoader();
						sendForOrganization(hideLoader);
					},hideLoader);
				}else{
					popType = 1;
					showLoader();
					sendForOrganization(hideLoader);
				}
			});
			
			//生成结算机构
			$("#jiesuanjigouHref").bind("click",function(){
				popType = 3;
				showLoader();
				sendForOrganization(hideLoader);
			});
			
			//经手人改变触发部门变化
			$("#jingshouren").change(function(){
				$("#bumen").empty();
				sendForDept();
			});
			
			//生成供货单位
			/*
			$("#gonghuodanweiHref").bind("click",function(){
				var organization =  $("#jigou").data("orgCode");
				if(typeof(organization) == "undefined" || organization==null || organization.length==0){
					showTip("请先选择机构",true);
					return;
				}
				popType = 2;
				showLoader();
				sendForSupplyUnit();
			});
			*/
			
			$("#gonghuodanwei").bind("keydown",function(event){
				if(event.which==13){
					var organization = $("#jigou").data("orgCode");
					var tmp = $("#gonghuodanwei").val();
					if(tmp==null || tmp.length==0){
						if(typeof(organization) == "undefined" || organization==null || organization.length==0){
							showTip("请先选择机构",true);
							return;
						}
						popType = 2;
						sendForSupplyUnit();
						return;
					}
					if(typeof(organization) == "undefined" || organization==null || organization.length==0){
						showTip("请先选择机构",true);
						return;
					}
					popType = 6;
					showLoader();
					sendForSupplyunitByName(null,hideLoader);
				}
			});
			
			//返回机构，客户的选择值
			$("#confirmOrgBtn").bind("click",function(){
				var needRefresh = false;
				var customerCode = $("#choosedInfoValue").val();
				var customerName = $("#choosedInfoLabel").val();
				if(customerName==null || typeof(customerName)=="undefined" || customerName.length==0){
					var content = "";
					if(popType==1 || popType==3){
						content = "请选择机构信息";
					}else if(popType == 2 || popType == 6){
						content = "请选择供货单位";
					}else if(popType == 4 || popType == 5){
						content = "请选择商品";
					}else{}
					showTip(content+"或\"返回\"",true);
					return;
				}
				if(popType==1){
					$("#jigou").val(customerName);
					$("#jigou").data("orgCode",customerCode);
					if(cgddcode == null){
						needRefresh = true;
						cgddcode = customerCode;
					}else{
						if(cgddcode == customerCode){
							needRefresh = false;
						}else{
							needRefresh = true;
							cgddcode = customerCode;
							//清空供货单位信息
							customerData = null;
							$("#gonghuodanwei").val("");
							$("#gonghuodanwei").data("orgCode","");
							//清空商品列表
							deleteAllGoods();							
						}
					}
				}else if(popType==2 || popType==6){
					$("#gonghuodanwei").val(customerName);
					$("#gonghuodanwei").data("orgCode",customerCode);
				}else if(popType==3){
					$("#jiesuanjigou").val(customerName);
					$("#jiesuanjigou").data("orgCode",customerCode);
				}else if(popType==4 || popType==5){
					if(shangpincode == null){
						needRefresh = true;
						shangpincode = customerCode;
					}else{
						if(shangpincode == customerCode){
							needRefresh = false;
						}else{
							needRefresh = true;
							shangpincode = customerCode;
							//清空原商品信息
							$("#sum").val("");
							$("#money").val("");
							$("#transaction").val("");
							$("#cgddgoodscolor").empty();
							$("#cgddgoodssize").empty();
							$("#price").empty();
							$("#cgddgoodsnumber").data("orgCode","");						
						}
					}
					$("#cgddgoodsname").val(customerName);
					$("#cgddgoodsname").data("orgCode",customerCode);
				}
				if(popType==1 || popType==2 || popType==3 || popType==6){
					$.mobile.changePage($("#pageHome"), {
						 'allowSamePageTransition' : false,
						 'reloadPage' : false,
						 transition: 'none'
					});
				}else if(popType==4 || popType==5){
					//设置货号
					var choosedInfoOtherValue = $("#choosedInfoOtherValue").val();
					$("#cgddgoodsnumber").val(choosedInfoOtherValue);
					$.mobile.changePage($("#pageAddGoods"), {
						 'allowSamePageTransition' : false,
						 'reloadPage' : false,
						 transition: 'none'
					});
				}else {
					showTip("未知的类型",true);
					return;
				}
				
				//加载机构数据后，需要加载经手人和仓库信息
				if(popType==1 && needRefresh){
					showLoader();
					setTimeout(function(){
						var backCall = function(){
							$("#jingshouren").selectmenu("enable",true);
							$("#bumen").selectmenu("enable",true);
							$("#shouhuocangku").selectmenu("enable",true);
							$("#shouhuoleixing").selectmenu("enable",true);
							$("#addGoodsBtnHref").button("enable");
							$("#saveBtn").button("enable");
							hideLoader();
						};
						sendForBrokerage(backCall);
						sendForStoreHouse();
						sendForGoodsType();
					},1500);
				}else if((popType==4 ||  popType==5) && needRefresh){
					showLoader();				
					setTimeout(function(){
						var backCall = function(){	
							$("#cgddgoodscolor").selectmenu('enable', true);
							$("#cgddgoodssize").selectmenu('enable', true);
							$("#price").selectmenu('enable', true);
							hideLoader();
							$("#cgddgoodscolor").selectmenu('refresh', true);
							$("#cgddgoodssize").selectmenu('refresh', true);
							$("#price").selectmenu('refresh', true);
						};
						sendForGoodsOtherInfo(backCall);
					},1000);
				}
				$("#choosedInfoLabel").attr("value","");
				$("#choosedInfoValue").attr("value","");
				popType = 0;
			});
			
			$("#returnPageHomeBtn").bind("click",function(){
				if(popType==1 || popType==2 || popType==3 || popType==6){
					$.mobile.changePage($("#pageHome"), {
						 'allowSamePageTransition' : false,
						 'reloadPage' : false,
						 transition: 'none'
					});
				}else if(popType==4 || popType==5){
					$.mobile.changePage($("#pageAddGoods"), {
						 'allowSamePageTransition' : false,
						 'reloadPage' : false,
						 transition: 'none'
					});
				}else{
					showTip("未知的类型",true);
					return;
				}
				popType = 0;
			});
			
			$("#saveBtn").bind("click",function(){
				//验证录单日期
				var date = $("#date").val();
				if(typeof(date) == "undefined" || date==null || date.length==0){
					showTip("请设置录单日期",true);
					return;
				}
				//验证单据编号
				var bianhao =  $("#bianhao").val();
				if(typeof(bianhao) == "undefined" || bianhao==null || bianhao.length==0){
					showTip("单据编号不能为空,请设置录单日期",true);
					return;
				}
				
				//验证机构
				var organization = $("#jigou").data("orgCode");
				if(typeof(organization) == "undefined" || organization==null || organization.length==0){
					showTip("请选择机构",true);
					return;
				}
				
				//验证结算机构
				var closeOrganization = $("#jiesuanjigou").data("orgCode");
				if(typeof(closeOrganization) == "undefined" || closeOrganization==null || closeOrganization.length==0){
					showTip("请选择结算机构",true);
					return;
				}
				
				//验证经手人
				var brokerage = $("#jingshouren").val();
				if(typeof(brokerage) == "undefined" || brokerage==null || brokerage.length==0){
					showTip("请选择经手人",true);
					return;
				}
				
				//验证部门
				var bumen = $("#bumen").val();
				if(typeof(bumen) == "undefined" || bumen==null || bumen.length==0){
					showTip("请选择部门",true);
					return;
				}
				
				//验证供货单位
				var gonghuodanwei = $("#gonghuodanwei").data("orgCode");
				if(typeof(gonghuodanwei) == "undefined" || gonghuodanwei==null || gonghuodanwei.length==0){
					showTip("请选择供货单位",true);
					return;
				}
				
				//验证收货仓库
				var shouhuocangku = $("#shouhuocangku").val();
				if(typeof(shouhuocangku) == "undefined" || shouhuocangku==null || shouhuocangku.length==0){
					showTip("请选择收货仓库",true);
					return;
				}
				
				//验证交货日期
				var jiaohuoriqi = $("#jiaohuoriqi").val();
				if(typeof(jiaohuoriqi) == "undefined" || jiaohuoriqi==null || jiaohuoriqi.length==0){
					showTip("请设置交货日期",true);
					return;
				}
				
				//验证收货类型
				var shouhuoleixing = $("#shouhuoleixing").val();
				if(typeof(shouhuoleixing) == "undefined" || shouhuoleixing==null || shouhuoleixing.length==0){
					showTip("请选择收货类型",true);
					return;
				}
				
				//验证商品列表个数
				var goodsArray = alreadyChooseGoodsData.goodsList;
				if(goodsArray.length==0){
					showTip("请至少增加一件商品",true);
					return;
				}
				
				var date = $("#date").val();
				$.ajax({
					url: contextPath+"/BusinessServlet",
					data: "action=action_code_exist&date="+date+"&pcName=<%=pcName%>&code="+$("#bianhao").val()+"&useType=7",
					type: "POST",
					dataType: 'text',
					timeout: ajaxTimeout,
					async:true,
					error: function(XMLHttpRequest, textStatus, errorThrown){
						showTip("请求服务器数据异常!",true);
					},
					success: function(data){
						//confirm(data);
						var obj = JSON.parse(data);
						var result = obj.result;
						if(obj.isError=="true"){
							showTip(result,true);
						}else{
							var isExist = result.isExist;
							if(isExist=="true"){
								var newCode = result.ddcode;
								showTip("编号重复，已为您替换新的编号",true);
								$("#bianhao").attr("value",newCode);
							}else{
								/*
								//主商品信息
								mainInfoJo.put("cgdddate", "2015-12-28 ");// 2
								mainInfoJo.put("cgddcode", "CGDD-2015-12-28-0002");// 3
								mainInfoJo.put("ddtype", 7);// 4 //类型，销售订单值为8，采购订单值为7
								mainInfoJo.put("cgddsupplyunit", DBConst.default_supplyunit);// 5
								mainInfoJo.put("cgddorganization", DBConst.default_orgnization);// 6
								mainInfoJo.put("cgddbrokerage", DBConst.default_BrokerageID);// 7
								mainInfoJo.put("cgddreceivestorehouse", DBConst.default_receivestorehouse);// 8
								mainInfoJo.put("cgdddepartment", DBConst.default_department);// 9
								mainInfoJo.put("cgddoperatorid", DBConst.default_OperatorID);// 10
								mainInfoJo.put("cgddsendgoodsdate", DBConst.default_sendGoodsDate);// 11
								mainInfoJo.put("cgddcloseorganization", DBConst.default_closeorgnization);// 12 //如果为销售订单则不需要结算结构
								mainInfoJo.put("cgddgoodstotalsum", DBConst.default_goodstotalsum);// 13
								mainInfoJo.put("currdate", DBConst.default_goodstotalsum);// 14
							
								//商品记录
								goodsInfoJo1.put("ddtype", 7);// 2 //类型，销售订单值为8，采购订单值为7
								goodsInfoJo1.put("cgddorganization", DBConst.default_orgnization);// 3
								goodsInfoJo1.put("cgddsupplyunit", DBConst.default_supplyunit);// 4
								goodsInfoJo1.put("cgddbrokerage", DBConst.default_BrokerageID);// 5
								goodsInfoJo1.put("cgddreceivestorehouse", DBConst.default_receivestorehouse);// 6
								goodsInfoJo1.put("cgdddepartment", DBConst.default_department);// 7
								goodsInfoJo1.put("cgddgoodscode", DBConst.default_goodscode);// 8
								goodsInfoJo1.put("cgddgoodssum", DBConst.default_goodssum);// 9
								goodsInfoJo1.put("cgddgoodsprice", DBConst.default_goodsprice);// 10
								goodsInfoJo1.put("cgdddate", "2015-12-28 ");// 11
								goodsInfoJo1.put("cgddgoodscolor", 15);// 12
								goodsInfoJo1.put("cgddgoodsindex", 1);// 13
							
								//商品明细
								goodsDetailInfoJo1.put("cgddId", "" + id19);// 1
								goodsDetailInfoJo1.put("cgddgoodssize", DBConst.default_goodssize);// 2
								goodsDetailInfoJo1.put("cgddgoodscolor", 15);// 3
								goodsDetailInfoJo1.put("cgddgoodssum", DBConst.default_goodssum);// 4
								goodsDetailInfoJo1.put("cgddgoodsprice", DBConst.default_goodsprice);// 5
								goodsDetailInfoJo1.put("cgddgoodsindex", 1);// 6
								*/
								
								//主订单信息
								var objMain = new Object();
								objMain["cgdddate"] = date;
								objMain["cgddcode"] = bianhao;
								objMain["ddtype"] = 7;
								objMain["cgddsupplyunit"] = gonghuodanwei;
								objMain["cgddorganization"] = organization;
								objMain["cgddbrokerage"] = brokerage;
								objMain["cgddreceivestorehouse"] = shouhuocangku;
								objMain["cgdddepartment"] = bumen;
								objMain["cgddoperatorid"] = "<%=OperatorID%>";
								objMain["cgddsendgoodsdate"] = jiaohuoriqi;
								objMain["cgddcloseorganization"] = closeOrganization;
								objMain["cgddgoodstotalsum"] = calcGoodsTotalSum();
								
								var goodsListArr = alreadyChooseGoodsData.goodsList;	//已有商品数组
								var goodsListArrLen = goodsListArr.length;
								var goodsInfoArray = new Array();		//用于记录商品
								var goodsDetailInfoArray = new Array(); //用于记录商品明细
								for(var i=0;i<goodsListArrLen;i++){
									var tmpObj = goodsListArr[i];
									//alert("--->>"+JSON.stringify(tmpObj));
									//商品记录信息
									var objGoodsInfo = new Object();
									objGoodsInfo["ddtype"] = 7;
									objGoodsInfo["cgddorganization"] = organization;
									objGoodsInfo["cgddsupplyunit"] = gonghuodanwei;
									objGoodsInfo["cgddbrokerage"] = brokerage;
									objGoodsInfo["cgddreceivestorehouse"] = shouhuocangku;
									objGoodsInfo["cgdddepartment"] = bumen;
									objGoodsInfo["cgdddate"] = date;
									objGoodsInfo["cgddgoodscode"] = tmpObj.cgddgoodsname;
									objGoodsInfo["cgddgoodssum"] = tmpObj.cgddgoodssum;
									objGoodsInfo["cgddgoodsprice"] = tmpObj.cgddgoodsprice;
									objGoodsInfo["cgddgoodscolor"] = tmpObj.cgddgoodscolor;
									objGoodsInfo["cgddgoodsindex"] = i+1;
									goodsInfoArray.push(objGoodsInfo);
									
									//商品明细
									var objGoodsDetaiInfo = new Object();
									objGoodsDetaiInfo["cgddgoodssize"] = tmpObj.cgddgoodssize;
									objGoodsDetaiInfo["cgddgoodssum"] = tmpObj.cgddgoodssum;
									objGoodsDetaiInfo["cgddgoodsprice"] = tmpObj.cgddgoodsprice;
									objGoodsDetaiInfo["cgddgoodscolor"] = tmpObj.cgddgoodscolor;
									objGoodsDetaiInfo["cgddgoodsindex"] = i+1;
									goodsDetailInfoArray.push(objGoodsDetaiInfo);
								}
								
								//alert(JSON.stringify(goodsInfoArray));
								//alert(JSON.stringify(goodsDetailInfoArray));
								
								var base64 = new Base64();
								var cgddInfo = base64.encode(JSON.stringify(objMain));
								var goodsInfo = base64.encode(JSON.stringify(goodsInfoArray));
								var goodsDetailInfo = base64.encode(JSON.stringify(goodsDetailInfoArray));
								
								$.ajax({
									url: contextPath+"/BusinessServlet",
									data: "action=action_save_cgdd&cgddInfo="+cgddInfo+"&goodsInfo="+goodsInfo+"&goodsDetailInfo="+goodsDetailInfo,
									type: "POST",
									dataType: 'text',
									timeout: ajaxTimeout,
									async:true,
									error: function(XMLHttpRequest, textStatus, errorThrown){
										showTip("请求服务器数据异常!",true);
									},
									success: function(data){
										var obj = JSON.parse(data);
										var result = obj.result;
										if(obj.isError=="true"){
											showTip(result,true);
										}else{
											showTip(result,true);
										}
									}
							    });
							}
						}
					}
			    });
			});
			
			$("#cgddgoodsnumber").bind("keydown",function(event){
				if(event.which==13){
					var tmp = $("#cgddgoodsnumber").val();
					if(tmp==null || tmp.length==0){
						showTip("查询内容不能为空!",true);
						return;
					}
					showTip(tmp,true);
					var organization =  $("#jigou").data("orgCode");
					if(typeof(organization) == "undefined" || organization==null || organization.length==0){
						showTip("请先选择机构",true);
						return;
					}
					popType = 5;
					showLoader();
					sendForGoodsByNumber(null,hideLoader);
				}
			});
		});
		
		//请求后台生成订单编号
		function sendForCGDD(){
			var date = $("#date").val();
			if(date.length==0){
				return;
			}
			$.ajax({
				url: contextPath+"/BusinessServlet",
				data: "action=action_cgdd_code&date="+date+"&pcName=<%=pcName%>",
				type: "POST",
				dataType: 'text',
				timeout: ajaxTimeout,
				async:true,
				error: function(XMLHttpRequest, textStatus, errorThrown){
					/*
					alert(XMLHttpRequest.status);
					alert(XMLHttpRequest.readyState);
					alert(textStatus);
					*/
					showTip("请求服务器数据异常!",true);
				},
				success: function(data){
					var obj = JSON.parse(data);
					var result = obj.result;
					if(obj.isError=="true"){
						showTip(result,true);
					}else{
						$("#bianhao").attr("value",result);
						$("#bianhao").attr("readonly",true);
					}
				}
		    });
		}
		
		//请求后台生成机构
		function sendForOrganization(backCall){
			if(treeData!=null) {
				$("#treeContainer").empty();
				$.mobile.changePage($("#pageBasic"), {
					 'allowSamePageTransition' : false,
					 'reloadPage' : false,
					 transition: 'none'
				});
				var obj = JSON.parse(treeData);
				orgTreeObj = new Tree("treeContainer","returnParentDiv","choosedInfoValue","choosedInfoLabel","BCtypeid","BCFullName",false);
				orgTreeObj.show(obj,true,null);
				if(backCall){
					backCall();
				}
				return;
			}
			$.ajax({
				url: contextPath+"/BusinessServlet",
				data: "action=action_organization&OperatorID=<%=OperatorID%>&userType=15",
				type: "POST",
				dataType: 'text',
				timeout: ajaxTimeout,
				async:true,
				error: function(XMLHttpRequest, textStatus, errorThrown){
					showTip("请求服务器数据异常!",true);
				},
				success: function(data){
					var obj = JSON.parse(data);
					var result = obj.result;
					if(obj.isError=="true"){
						showTip(result,true);
					}else{
						$("#treeContainer").empty();
						treeData = JSON.stringify(result);
						$.mobile.changePage($("#pageBasic"), {
							 'allowSamePageTransition' : false,
							 'reloadPage' : false,
							 transition: 'none'
						});
						orgTreeObj = new Tree("treeContainer","returnParentDiv","choosedInfoValue","choosedInfoLabel","BCtypeid","BCFullName",false);
						orgTreeObj.show(result,true,null);
						if(backCall){
							backCall();
						}
					}
				}
		    });
		}
		
		//请求后台生成经手人
		function sendForBrokerage(backCall){
			$("#jingshouren").empty();
			var organization =  $("#jigou").data("orgCode");
			var that = this;
			$.ajax({
				url: contextPath+"/BusinessServlet",
				data: "action=action_brokerage&OperatorID=<%=OperatorID%>&organization="+organization,
				type: "POST",
				dataType: 'text',
				timeout: ajaxTimeout,
				async:true,
				error: function(XMLHttpRequest, textStatus, errorThrown){
					showTip("请求服务器数据异常!",true);
				},
				success: function(data){
					$("#jingshouren").empty();
					var obj = JSON.parse(data);
					var listArr = obj.result;
					if(obj.isError=="true"){
						showTip(listArr,true);
					}else{
						var len = listArr.length;
						for(var i=0;i<len;i++){
							var dataObj = listArr[i];
							var currValue = dataObj.etypeid;
							var currText = dataObj.efullname;
							if(i==0){
								$("#jingshouren").append("<option selected value='"+currValue+"'>"+currText+"</option>");
							}else{
								$("#jingshouren").append("<option value='"+currValue+"'>"+currText+"</option>");
							}
						}
						$("#jingshouren").selectmenu('refresh', true);
						sendForDept(backCall);
					}
				}
		    });
		}
	
		// 请求部门
		function sendForDept(backCall){
			$("#bumen").empty();
			var brokerage = $("#jingshouren").val();
			var organization =  $("#jigou").data("orgCode");
			if(typeof(organization) == "undefined" || organization.length==0){
				showTip("请先选择机构",true);
				return;
			}
			if(typeof(brokerage) == "undefined" || brokerage.length==0){
				showTip("请先选择经手人",true);
				return;
			}
			$.ajax({
				url: contextPath+"/BusinessServlet",
				data: "action=action_department&BrokerageID="+brokerage+"&OperatorID=<%=OperatorID%>&organization="+organization,
				type: "POST",
				dataType: 'text',
				timeout: ajaxTimeout,
				async:true,
				error: function(XMLHttpRequest, textStatus, errorThrown){
					showTip("请求服务器数据异常!",true);
				},
				success: function(data){
					var obj = JSON.parse(data);
					var resultObj = obj.result;
					var infoArray = JSON.parse(resultObj.info);
					var defaultValueObj = JSON.parse(resultObj.default);
					if(obj.isError=="true"){
						showTip(resultObj,true);
					}else{
						var len = infoArray.length;
						for(var i=0;i<len;i++){
							var dataObj = infoArray[i];
							var currValue = dataObj.dtypeid;
							var currText = dataObj.dfullname;
							if(defaultValueObj.dtypeid == currValue){
								$("#bumen").append("<option selected value='"+currValue+"'>"+currText+"</option>");
							}else{
								$("#bumen").append("<option value='"+currValue+"'>"+currText+"</option>");
							}
						}
						$("#bumen").selectmenu('refresh', true);
						if(backCall){
							backCall();
						}
					}
				}
		    });
		}
		
		//生成供货单位
		function sendForSupplyUnit(supplyUnitCode,pageNum,backCall){
			var organization =  $("#jigou").data("orgCode");
			var requestData = "action=action_cgdd_supplyunit&OperatorID=<%=OperatorID%>&organization="+organization+"&pageNum=1&itemsInEachPage=14"
			if(typeof(pageNum)!="undefined" && typeof(supplyUnitCode)!="undefined"){
				requestData = "action=action_cgdd_supplyunit&OperatorID=<%=OperatorID%>&organization="+organization+"&pageNum="+pageNum+"&itemsInEachPage=14&supplyUnitCode="+supplyUnitCode
			}
			$.ajax({
				url: contextPath+"/BusinessServlet",
				data: requestData,
				type: "POST",
				dataType: 'text',
				timeout: ajaxTimeout,
				async:true,
				error: function(XMLHttpRequest, textStatus, errorThrown){
					showTip("请求服务器数据异常!",true);
				},
				success: function(data){
					if(typeof(pageNum)=="undefined" && typeof(supplyUnitCode)=="undefined"){
						hideLoader();
						var obj = JSON.parse(data);
						var result = obj.result;
						if(obj.isError=="true"){
							showTip(result,true);
						}else{
							$("#treeContainer").empty();
							customerData = JSON.stringify(result);
							var tmpArr = JSON.parse(customerData);
							if(tmpArr.length==0){
								customerData = null;
								showTip("无供货单位信息,请重新选择机构",true);
								return;
							}
							$.mobile.changePage($("#pageBasic"), {
								 'allowSamePageTransition' : false,
								 'reloadPage' : false,
								 transition: 'none'
							});
							orgTreeObj = new Tree("treeContainer","returnParentDiv","choosedInfoValue","choosedInfoLabel","btypeid","bfullname",true);
							orgTreeObj.show(result,true,null);
						}
					}else{
						backCall(data);
					}
				}
		    });
		}
		
		//生成仓库
		function sendForStoreHouse(){
			$("#shouhuocangku").empty();
			var organization =  $("#jigou").data("orgCode");
			if(typeof(organization) == "undefined" || organization.length==0){
				showTip("请先选择机构",true);
			}
			$.ajax({
				url: contextPath+"/BusinessServlet",
				data: "action=action_cgdd_storehouse&OperatorID=<%=OperatorID%>&organization="+organization,
				type: "POST",
				dataType: 'text',
				timeout: ajaxTimeout,
				async:true,
				error: function(XMLHttpRequest, textStatus, errorThrown){
					showTip("请求服务器数据异常!",true);
				},
				success: function(data){
					hideLoader();
					var obj = JSON.parse(data);
					var resultObj = obj.result;
					var infoArray = JSON.parse(resultObj.info);
					var defaultValueObj = JSON.parse(resultObj.default);
					if(obj.isError=="true"){
						showTip(resultObj,true);
					}else{
						var len = infoArray.length;
						for(var i=0;i<len;i++){
							var dataObj = infoArray[i];
							var currValue = dataObj.typeID;
							var currText = dataObj.fullName;
							if(defaultValueObj.typeID == currValue){
								$("#shouhuocangku").append("<option selected value='"+currValue+"'>"+currText+"</option>");
							}else{
								$("#shouhuocangku").append("<option value='"+currValue+"'>"+currText+"</option>");
							}
						}
						$("#shouhuocangku").selectmenu('refresh', true);
					}
				}
		    });
		}
		
		//选择收货类型
		function sendForGoodsType(){
			$("#shouhuoleixing").empty();
			var organization =  $("#jigou").data("orgCode");
			if(typeof(organization) == "undefined" || organization.length==0){
				showTip("请先选择机构",true);
			}
			$.ajax({
				url: contextPath+"/BusinessServlet",
				data: "action=action_cgdd_goodstype&organization="+organization,
				type: "POST",
				dataType: 'text',
				timeout: ajaxTimeout,
				async:true,
				error: function(XMLHttpRequest, textStatus, errorThrown){
					showTip("请求服务器数据异常!",true);
				},
				success: function(data){
					var obj = JSON.parse(data);
					var listArr = obj.result;
					if(obj.isError=="true"){
						showTip(listArr,true);
					}else{
						var len = listArr.length;
						for(var i=0;i<len;i++){
							var dataObj = listArr[i];
							var currValue = dataObj.opID;
							var currText = dataObj.operName;
							if(i==0){
								$("#shouhuoleixing").append("<option selected value='"+currValue+"'>"+currText+"</option>");
							}else{
								$("#shouhuoleixing").append("<option value='"+currValue+"'>"+currText+"</option>");
							}
						}
						$("#shouhuoleixing").selectmenu('refresh', true);
					}
				}
		    });
		}
	</script>
	
	<script type="text/javascript">		
		
		var shangpincode = null;//商品编码 （用于判断商品编码是否改变）
		var goodsData = null;//商品信息
		
		//增加商品跳转页面
		$("#addGoodsBtnHref").bind("click",function(){
			var organization =  $("#jigou").data("orgCode");
			var storeHouseID = $("#shouhuocangku").val();
			if(typeof(organization) == "undefined" || organization.length==0){
				showTip("请先选择机构",true);
				return;
			}
			if(typeof(storeHouseID) == "undefined" || storeHouseID==null ||storeHouseID.length==0){
				showTip("请先选择收货仓库",true);
				return;
			}
			
			$.mobile.changePage($("#pageAddGoods"), {
				 'allowSamePageTransition' : false,
				 'reloadPage' : false,
				 transition: 'none'
			});
			//清空前一次的相关记录
			$("#cgddgoodsname").val('');
			$("#cgddgoodsname").data("orgCode","");
			$("#cgddgoodsnumber").val('');
			$("#cgddgoodscolor").empty();
			$("#cgddgoodssize").empty();
			$("#price").empty();
			$("#cgddgoodscolor").selectmenu('disable', true);
			$("#cgddgoodssize").selectmenu('disable', true);
			$("#price").selectmenu('disable', true);
			$("#cgddgoodscolor").selectmenu('refresh', true);
			$("#cgddgoodssize").selectmenu('refresh', true);
			$("#price").selectmenu('refresh', true);
			$("#sum").val('');
			$("#money").val('');
			$("#transaction").val('');
			
			/*
			//加载商品列表
			showLoader();
			var backFunc = function(data){
				$.mobile.changePage($("#pageAddGoods"), {
					 'allowSamePageTransition' : false,
					 'reloadPage' : false,
					 transition: 'none'
				});
				hideLoader();
				//尺寸，颜色，大小，设置为不可用
				$("#cgddgoodsnumber").val('');
				$("#cgddgoodscolor").selectmenu('disable', true);
				$("#cgddgoodssize").selectmenu('disable', true);
				$("#price").selectmenu('disable', true);
			};
			sendForGoods(null,null,backFunc);
			*/
			
			/*
			//默认商品加载颜色，尺寸和价格
			showLoader();				
			backFunc = function(){	
				$("#cgddgoodscolor").selectmenu('refresh', true);
				$("#cgddgoodssize").selectmenu('refresh', true);
				$("#price").selectmenu('refresh', true);
				hideLoader();
			};
			setTimeout(function(){
				sendForGoodsOtherInfo(backFunc);
			},1000);
			*/
					
		});
		
		//进入页面前已加载数据
		$("#cgddgoodsnameHref").bind("click",function(){
			popType = 4;
			showLoader();
			var backCall = function(data){
				hideLoader();
			};
			sendForGoods(null,null,hideLoader);
		});
		
		$("#sum").on("input",function(e){
			var sumValue = $("#sum").val();
			if(!validateWithOutPoint(sumValue)){
				showTip("不是合理数字,请重新输入",true);
				$("#sum").val("");
			}else{
				var price = $("#price").val();
				if(validate(price)){
					var money = sumValue*price;
					$("#money").val(money);
					$("#transaction").val(money);
				}else{
					$("#money").val("");
					$("#transaction").val("");
				}
			}
		});
		
		$("#price").change(function(){
			var sumValue = $("#sum").val();
			var price = $("#price").val();
			if(validate(price)){
				var money = sumValue*price;
				$("#money").val(money);
				$("#transaction").val(money);
			}else{
				$("#money").val("");
				$("#transaction").val("");
			}
		});
		
		$("#money").on("input",function(e){
			var moneyValue = $("#money").val();
			if(!validate(moneyValue)){
				showTip("不是合理数字,请重新输入",true);
				$("#money").val("");
			}
			$("#transaction").val(moneyValue);
		});
		
		$("#transaction").on("input",function(e){
			var moneyValue = $("#transaction").val();
			if(!validate(moneyValue)){
				showTip("不是合理数字,请重新输入",true);
				$("#transaction").val("");
			}
		});
		
		
		//请求后台生成商品数据，只取数据，展现留在跳转页面后的点击
		function sendForGoods(goodsCode,pageNum,backFunc){
			var storeHouseID = $("#shouhuocangku").val();
			var requestData = "action=action_cgdd_goods&OperatorID=<%=OperatorID%>&storeHouseID="+storeHouseID+"&pageNum=1&itemsInEachPage=14";
			if(pageNum!=null){
				requestData = "action=action_cgdd_goods&OperatorID=<%=OperatorID%>&storeHouseID="+storeHouseID+"&pageNum="+pageNum+"&itemsInEachPage=14&goodsCode="+goodsCode;
			}
			$.ajax({
				url: contextPath+"/BusinessServlet",
				data: requestData,
				type: "POST",
				dataType: 'text',
				timeout: ajaxTimeout,
				async:true,
				error: function(XMLHttpRequest, textStatus, errorThrown){
					showTip("请求服务器数据异常!",true);
					return;
				},
				success: function(data){
					var obj = JSON.parse(data);
					var result = obj.result;
					if(obj.isError=="true"){
						showTip(result,true);
					}else{
						goodsData = JSON.stringify(result);
					}
					$.mobile.changePage($("#pageBasic"), {
						 'allowSamePageTransition' : false,
						 'reloadPage' : false,
						 transition: 'none'
					});
					//树组件内部调用，这里不用刷新了
					if(pageNum==null){
						orgTreeObj = new Tree("treeContainer","returnParentDiv","choosedInfoValue","choosedInfoLabel","ptypeid","pfullname",true);
						orgTreeObj.show(result,true,null);
					}
					if(backFunc){
						backFunc(data);
					}
				}
		    });
		}
		
		//请求后台生成商品颜色，尺寸和价格列表
		function sendForGoodsOtherInfo(backCall){
			var goodsID = $("#cgddgoodsname").data("orgCode");
			var storeHouseID = $("#shouhuocangku").val();
			$.ajax({
				url: contextPath+"/BusinessServlet",
				data: "action=action_cgdd_goodsInfo&goodsID="+goodsID+"&storeHouseID="+storeHouseID+"&OperatorID=<%=OperatorID%>",
				type: "POST",
				dataType: 'text',
				timeout: ajaxTimeout,
				async:true,
				error: function(XMLHttpRequest, textStatus, errorThrown){
					showTip("请求服务器数据异常!",true);
				},
				success: function(data){
					$("#cgddgoodscolor").empty();
					$("#cgddgoodssize").empty();
					$("#price").empty();
					var obj = JSON.parse(data);
					var resultObj = obj.result;
					if(obj.isError=="true"){
						showTip(resultObj,true);
					}else{
						var colorArr = JSON.parse(resultObj.color);
						var sizeArr = JSON.parse(resultObj.size);
						var priceArr = JSON.parse(resultObj.price);
						var len = colorArr.length;
						//加载颜色列表
						for(var i=0;i<len;i++){
							var dataObj = colorArr[i];
							var currValue = dataObj.PCUserCode;
							var currText = dataObj.PCFullName;
							if(i==0){
								$("#cgddgoodscolor").append("<option selected value='"+currValue+"'>"+currText+"</option>");
							}else{
								$("#cgddgoodscolor").append("<option value='"+currValue+"'>"+currText+"</option>");
							}
						}
						len = sizeArr.length;
						//加载尺寸列表
						for(var i=0;i<len;i++){
							var dataObj = sizeArr[i];
							var currValue = dataObj.sizeid;
							var currText = dataObj.sizename;
							if(i==0){
								$("#cgddgoodssize").append("<option selected value='"+currValue+"'>"+currText+"</option>");
							}else{
								$("#cgddgoodssize").append("<option value='"+currValue+"'>"+currText+"</option>");
							}
						}
						//加载单价列表
						len = priceArr.length;
						for(var i=0;i<len;i++){
							var dataObj = priceArr[i];
							var currValue = dataObj.price;
							var currText = dataObj.price;//displayName
							if(i==0){
								$("#price").append("<option selected value='"+currValue+"'>"+currText+"</option>");
							}else{
								$("#price").append("<option value='"+currValue+"'>"+currText+"</option>");
							}
						}
						if(backCall){
							backCall();
						}
					}
				}
		    });
		}
		
		//根据货号模糊查询，请求后台生成商品数据
		function sendForGoodsByNumber(pageNum,backFunc){
			var huohao = $("#cgddgoodsnumber").val();
			var storeHouseID = $("#shouhuocangku").val();
			var requestData = "action=action_goods_bynumer&goodsCode="+huohao+"&OperatorID=<%=OperatorID%>&storeHouseID="+storeHouseID+"&pageNum=1&itemsInEachPage=14";
			if(pageNum!=null){
				requestData = "action=action_goods_bynumer&goodsCode="+huohao+"&OperatorID=<%=OperatorID%>&storeHouseID="+storeHouseID+"&pageNum="+pageNum+"&itemsInEachPage=14";
			}
			$.ajax({
				url: contextPath+"/BusinessServlet",
				data: requestData,
				type: "POST",
				dataType: 'text',
				timeout: ajaxTimeout,
				async:true,
				error: function(XMLHttpRequest, textStatus, errorThrown){
					showTip("请求服务器数据异常!",true);
					return;
				},
				success: function(data){
					var obj = JSON.parse(data);
					var result = obj.result;
					if(obj.isError=="true"){
						showTip(result,true);
					}else{
						$("#treeContainer").empty();
					}
					$.mobile.changePage($("#pageBasic"), {
						 'allowSamePageTransition' : false,
						 'reloadPage' : false,
						 transition: 'none'
					});
					//树组件内部调用，这里不用刷新了
					if(pageNum==null){
						orgTreeObj = new Tree("treeContainer","returnParentDiv","choosedInfoValue","choosedInfoLabel","ptypeid","pfullname",true);
						orgTreeObj.show(result,true,null);
					}
					if(backFunc){
						backFunc(data);
					}
				}
		    });
		}
		
		//根据供货单位编号或名称模糊查询，请求后台生成供货单位数据
		function sendForSupplyunitByName(pageNum,backFunc){
			var gonghuodanwei = $("#gonghuodanwei").val();
			var organization = $("#jigou").data("orgCode");
			var requestData = "action=action_supplyunit_byname&name="+gonghuodanwei+"&OperatorID=<%=OperatorID%>&organization="+organization+"&pageNum=1&itemsInEachPage=14&custom1=1&bDisplayStop=0";
			if(pageNum!=null){
				requestData = "action=action_supplyunit_byname&name="+gonghuodanwei+"&OperatorID=<%=OperatorID%>&organization="+organization+"&pageNum="+pageNum+"&itemsInEachPage=14&custom1=1&bDisplayStop=0";
			}
			$.ajax({
				url: contextPath+"/BusinessServlet",
				data: requestData,
				type: "POST",
				dataType: 'text',
				timeout: ajaxTimeout,
				async:true,
				error: function(XMLHttpRequest, textStatus, errorThrown){
					showTip("请求服务器数据异常!",true);
					return;
				},
				success: function(data){
					var obj = JSON.parse(data);
					var result = obj.result;
					if(obj.isError=="true"){
						showTip(result,true);
					}else{
						$("#treeContainer").empty();
						customerData = JSON.stringify(result);
					}
					if(result.length==0){
						popType = 0;
						showTip("未搜索到供货单位",true);
						return;
					}else if(result.length==1){
						popType = 0;
						var tmpObj = result[0];
						$("#gonghuodanwei").val(tmpObj.bfullname);
						$("#gonghuodanwei").data("orgCode",tmpObj.btypeid);
						if(backFunc){
							backFunc(data);
						}
					}else{
						$.mobile.changePage($("#pageBasic"), {
							 'allowSamePageTransition' : false,
							 'reloadPage' : false,
							 transition: 'none'
						});
						//树组件内部调用，这里不用刷新了
						if(pageNum==null){
							orgTreeObj = new Tree("treeContainer","returnParentDiv","choosedInfoValue","choosedInfoLabel","btypeid","bfullname",true);
							orgTreeObj.show(result,true,null);
						}
						if(backFunc){
							backFunc(data);
						}
					}
				}
		    });
		}
	</script>

</html>
