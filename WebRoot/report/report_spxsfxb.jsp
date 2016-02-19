<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="org.wechat.utils.AuthUtil"%>
<%
	String contextPath = request.getContextPath();
	String OperatorID = (String)session.getAttribute("OperatorID");
	if(OperatorID==null){
		session.setAttribute("visitPage","report_spxsfxb.jsp");
		response.setContentType("text/html; charset=UTF-8");
		response.sendRedirect("login.jsp");
	}else{
		boolean valid = AuthUtil.checkValid(OperatorID,"spxsfxb");
		if(!valid){
			session.setAttribute("visitPage","report_spxsfxb.jsp");
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
		<style type="text/css"></style>
		<title>商品销售分析表</title>
	</head>

	<body>
		<!-- 查询条件页面 -->
		<div data-role="page" id="pageHome">
			<div data-role="navbar" id="navigationBar">
				<ul>
					<li><a id="nav_query" data-ajax="false" class="ui-btn-active">商品销售分析</a></li>
					<li><a id="nav_result" data-ajax="false">查询结果</a></li>
				</ul>
			</div>
			<!-- 查询条件页面 -->
			<div id="queryPart"  class="ui-body-d ui-content">
				<form method="post">			
					<div class="ui-field-contain">
						<label for="jigoumoshi">机构模式：</label>
						<select name="jigoumoshi" id="jigoumoshi" data-native-menu="false" data-theme="b">
							<option value="15">内部机构</option>
							<option value="16">加盟机构</option>
						</select>
					</div>		
					<div class="ui-field-contain">
						<label for="jigou">机构：</label>
						<a id="jigouHref"><input readonly="readonly" type="text" name="jigou" orgCode="" id="jigou" data-clear-btn="true"></a>
					</div>
					<div class="ui-field-contain">
						<label for="kaishiriqi">开始日期：</label>
						<input type="date" name="kaishiriqi" id="kaishiriqi"  data-clear-btn="true">
					</div>
					<div class="ui-field-contain">
						<label for="jieshuriqi">结束日期：</label>
						<input type="date" name="jieshuriqi" id="jieshuriqi"  data-clear-btn="true">
					</div>
					<div class="ui-field-contain">
						<label for="cangku">仓库全名：</label>
						<select name="cangku" id="cangku" data-native-menu="false" data-theme="b"></select>
					</div>
					<div class="ui-field-contain">
						<label for="shangpin">商品全名：</label>
						<!-- <a id="shangpinHref"> -->
						<input type="text" name="shangpin" orgCode="" id="shangpin">
						<!-- </a> -->
					</div>			
					<div class="ui-field-contain">
						<label for="zhiyuan">职员全名：</label>
						<select name="zhiyuan" id="zhiyuan" data-native-menu="false" data-theme="b"></select>
					</div>
					<div class="ui-field-contain">
						<label for="bumen">部门全名：</label>
						<select name="bumen" id="bumen" data-native-menu="false" data-theme="b"></select>
					</div>
					<br/>
					<input type="button" id="queryBtn" class="ui-btn" data-corners="false" data-theme="b" value="查询">
				</form>
			</div>
			<!-- 查询结果页面-->
			<div id="resultPart"  style="display:none">
				<form method="post">				
					<div class="ui-field-contain">
						<label for="shangpinquanming">商品全名：</label>
						<input readonly="readonly" type="text" name="shangpinquanming" id="shangpinquanming" data-clear-btn="true">
					</div>
					<div class="ui-field-contain">
						<label for="xiaoshoushuliang">销售数量(实销数量)：</label>
						<input readonly="readonly" type="text" name="xiaoshoushuliang" id="xiaoshoushuliang" data-clear-btn="true">
					</div>	
					<div class="ui-field-contain">
						<label for="chengbenjine">成本金额：</label>
						<input readonly="readonly" type="text" name="chengbenjine" id="chengbenjine" data-clear-btn="true">
					</div>
					<div class="ui-field-contain">
						<label for="maolie">毛利额：</label>
						<input readonly="readonly" type="text" name="maolie" id="maolie" data-clear-btn="true">
					</div>
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
	</body>
	
	<script type="text/javascript">
		var contextPath = "<%=contextPath%>";
		var cgddcode = null;	//机构编码 （用于判断机构编码是否改变）
		var orgTreeObj;	//树组件
		var treeData = null;//机构数据
		var popType=0;//用于区分当前弹出树组件的使用对象，0：默认未使用，1：机构
		var storeHouseList = null;  //仓库列表
		
		var shangpincode = null;//商品编码 （用于判断商品编码是否改变）
		var goodsData = null;//商品信息
		
		var ajaxTimeout = 1000000;
		
		$(function(){
			
			//初始化时使部分下拉列表不可编辑，选择机构后才可编辑。这样不需要单独处理每个下拉列表的click加载
			
			$("#cangku").selectmenu("disable",true);
			$("#zhiyuan").selectmenu("disable",true);
			$("#bumen").selectmenu("disable",true);
			$("#queryBtn").button("disable");
		
			var status = 0;// 0:显示查询，1:显示结果
			$('div[data-role="navbar"] a').on('click', function () {
				var tmpId = $(this).attr("id");
				if(tmpId=="nav_query"){
					status = 0;
					$("#queryPart").css("display","block");
					$("#resultPart").css("display","none");
					$("#nav_query").removeClass("ui-btn-active");
					$("#nav_query").addClass("ui-btn-active");
					$("#nav_result").removeClass("ui-btn-active");
				}else{
					status = 1;
					$("#resultPart").css("display","block");
					$("#queryPart").css("display","none");
					$("#nav_query").removeClass("ui-btn-active");
					$("#nav_result").addClass("ui-btn-active");
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
			
			//查询
			$("#queryBtn").bind("click",function(){
				//验证机构
				var organization = $("#jigou").data("orgCode");
				if(typeof(organization) == "undefined" || organization==null || organization.length==0){
					showTip("请选择机构",true);
					return;
				}
				
				//开始日期
				var kaishiriqi = $("#kaishiriqi").val();
				if(typeof(kaishiriqi) == "undefined" || kaishiriqi==null || kaishiriqi.length==0){
					showTip("请选择开始日期",true);
					return;
				}
				
				//结束日期
				var jieshuriqi = $("#jieshuriqi").val();
				if(typeof(jieshuriqi) == "undefined" || jieshuriqi==null || jieshuriqi.length==0){
					showTip("请选择结束日期",true);
					return;
				}
				
				//验证商品
				var shangpin =  $("#shangpin").data("orgCode");
				if(typeof(shangpin) == "undefined" || shangpin==null || shangpin.length==0){
					showTip("请选择商品",true);
					return;
				}
				
				//验证职员
				var zhiyuan =  $("#zhiyuan").val();
				if(typeof(zhiyuan) == "undefined" || zhiyuan==null || zhiyuan.length==0){
					showTip("请选择职员",true);
					return;
				}
				
				//验证部门
				var bumen =  $("#bumen").val();
				if(typeof(bumen) == "undefined" || bumen==null || bumen.length==0){
					showTip("请选择部门",true);
					return;
				}
				
				
				
				$.ajax({
					url: contextPath+"/BusinessServlet",
					data: "action=action_query_spxsfxb&OperatorID=<%=OperatorID%>&goodsID="+shangpin+"&organization="+organization+"&beginDate="+kaishiriqi+"&endDate="+jieshuriqi+"&employeeID="+zhiyuan+"&deptId="+bumen,
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
							/*
								jo.put("ptypeid", ptypeid);// 商品标识
								jo.put("Sqty", Sqty);// 销售数量
								jo.put("CostTotal", CostTotal==null?"0":CostTotal);// 成本金额
								jo.put("Profittotal", Profittotal==null?"0":Profittotal);// 毛利额
							*/
							var Sqty = result.Sqty;//销售数量
							$("#xiaoshoushuliang").val(Sqty);
							var CostTotal = result.CostTotal;//成本金额
							$("#chengbenjine").val(CostTotal);
							var Profittotal = result.Profittotal;// 毛利额	
							$("#maolie").val(Profittotal);
							$("#nav_result").trigger("click");
						}
					}
			    });
			});
			
			//生成机构
			$("#jigouHref").bind("click",function(){
				var organization =  $("#jigou").data("orgCode");
				if(typeof(organization)!="undefined" && organization!=null && organization.length>0){			
					showDialogue("修改机构会更新仓库和商品信息，确定要继续吗?",function(){
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
			
			/*
			//进入页面前已加载数据
			$("#shangpinHref").bind("click",function(){
				var organization = $("#jigou").data("orgCode");
				if(typeof(organization) == "undefined" || organization==null || organization.length==0){
					showTip("请先选择机构",true);
					return;
				}
				popType = 4;
				showLoader();
				var backCall = function(data){
					hideLoader();
				};
				sendForGoods(null,null,hideLoader);
			});
			*/
			
			$("#shangpin").bind("keydown",function(event){
				if(event.which==13){
					var tmp = $("#shangpin").val();
					if(tmp==null || tmp.length==0){
						var organization = $("#jigou").data("orgCode");
						if(typeof(organization) == "undefined" || organization==null || organization.length==0){
							showTip("请先选择机构",true);
							return;
						}
						popType = 4;
						showLoader();
						var backCall = function(data){
							hideLoader();
						};
						sendForGoods(null,null,hideLoader);
						return;
					}
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
			
			
			//返回机构选择值
			$("#confirmOrgBtn").bind("click",function(){
				var needRefresh = false;
				var customerCode = $("#choosedInfoValue").val();
				var customerName = $("#choosedInfoLabel").val();
				if(customerName==null || typeof(customerName)=="undefined" || customerName.length==0){
					var content = "";
					if(popType==1){
						content = "请选择机构信息";
					}else if(popType == 4){
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
							//清空仓库信息
							$("#cangku").empty();
							$("#zhiyuan").empty();	
							$("#bumen").empty();
							$("#shangpin").val("");
							$("#shangpin").data("orgCode","");
						}
					}
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
						}
					}
					$("#shangpinquanming").val(customerName);
					$("#shangpin").val(customerName);
					$("#shangpin").data("orgCode",customerCode);
				}
				
				$.mobile.changePage($("#pageHome"), {
					 'allowSamePageTransition' : false,
					 'reloadPage' : false,
					 transition: 'none'
				});
				
				//加载机构数据后，需要加载经手人和仓库信息
				if(popType==1 && needRefresh){
					showLoader();
					setTimeout(function(){
						var backCall = function(){
							$("#cangku").selectmenu("enable",true);
							$("#zhiyuan").selectmenu("enable",true);
							$("#bumen").selectmenu("enable",true);
							$("#queryBtn").button("enable");
							hideLoader();
						};
						sendForBrokerage(backCall);
						sendForStoreHouse(backCall);
						sendForDept(backCall);
					},1500);
				}
				$("#choosedInfoLabel").attr("value","");
				$("#choosedInfoValue").attr("value","");
			});
			
			$("#returnPageHomeBtn").bind("click",function(){
				$.mobile.changePage($("#pageHome"), {
					 'allowSamePageTransition' : false,
					 'reloadPage' : false,
					 transition: 'none'
				});
				popType = 0;
			});
			
			//仓库名称改变查询结果中的仓库全名
			$("#cangku").change(function(){
				if(storeHouseList!=null){
					var len = storeHouseList.length;
					for(var i=0;i<len;i++){
						var tmpStore =  storeHouseList[i];
						if(tmpStore.typeID == $("#cangku").val()){
							$("#cangkuquanming").val(tmpStore.fullName);
							break;
						}
					}
				}
			});
			
		});
		
		//请求后台生成机构
		function sendForOrganization(backCall){
			var userType = $("#jigoumoshi").val();
			$.ajax({
				url: contextPath+"/BusinessServlet",
				data: "action=action_organization&OperatorID=<%=OperatorID%>&userType="+userType,
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
		
		//请求后台生成职员
		function sendForBrokerage(backCall){
			$("#zhiyuan").empty();
			var organization =  $("#jigou").data("orgCode");
			var that = this;
			$.ajax({
				url: contextPath+"/BusinessServlet",
				data: "action=action_employee&OperatorID=<%=OperatorID%>&organization="+organization,
				type: "POST",
				dataType: 'text',
				timeout: ajaxTimeout,
				async:true,
				error: function(XMLHttpRequest, textStatus, errorThrown){
					showTip("请求服务器数据异常!",true);
				},
				success: function(data){
					$("#zhiyuan").empty();
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
								$("#zhiyuan").append("<option selected value='"+currValue+"'>"+currText+"</option>");
							}else{
								$("#zhiyuan").append("<option value='"+currValue+"'>"+currText+"</option>");
							}
						}
						$("#zhiyuan").selectmenu('refresh', true);
						if(backCall){
							backCall();
						}
					}
				}
		    });
		}
	
		// 请求部门
		function sendForDept(backCall){
			$("#bumen").empty();
			var organization =  $("#jigou").data("orgCode");
			if(typeof(organization) == "undefined" || organization.length==0){
				showTip("请先选择机构",true);
				return;
			}
			$.ajax({
				url: contextPath+"/BusinessServlet",
				data: "action=action_department&OperatorID=<%=OperatorID%>&organization="+organization,
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
					if(obj.isError=="true"){
						showTip(resultObj,true);
					}else{
						var len = infoArray.length;
						for(var i=0;i<len;i++){
							var dataObj = infoArray[i];
							var currValue = dataObj.dtypeid;
							var currText = dataObj.dfullname;
							if(i==0){
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
		
		//生成仓库
		function sendForStoreHouse(backFunc){
			$("#cangku").empty();
			var organization =  $("#jigou").data("orgCode");
			if(typeof(organization) == "undefined" || organization.length==0){
				showTip("请先选择机构",true);
			}
			$.ajax({
				url: contextPath+"/BusinessServlet",
				data: "action=action_kczkb_storehouse&OperatorID=<%=OperatorID%>&organization="+organization,
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
					storeHouseList = infoArray;
					if(obj.isError=="true"){
						showTip(resultObj,true);
					}else{
						var len = infoArray.length;
						for(var i=0;i<len;i++){
							var dataObj = infoArray[i];
							var currValue = dataObj.typeID;
							var currText = dataObj.fullName;
							if(i == 0){
								$("#cangku").append("<option selected value='"+currValue+"'>"+currText+"</option>");
								$("#cangkuquanming").val(currText);
							}else{
								$("#cangku").append("<option value='"+currValue+"'>"+currText+"</option>");
							}
						}
						$("#cangku").selectmenu('refresh', true);
						if(backFunc){
							backFunc();
						}
					}
				}
		    });
		}
		
		//请求后台生成商品列表
		function sendForGoods(goodsCode,pageNum,backFunc){
			var storeHouseID = $("#cangku").val();
			var requestData = "action=action_kczkb_goods&OperatorID=<%=OperatorID%>&storeHouseID="+storeHouseID+"&pageNum=1&itemsInEachPage=14";
			if(pageNum!=null){
				requestData = "action=action_kczkb_goods&OperatorID=<%=OperatorID%>&storeHouseID="+storeHouseID+"&pageNum="+pageNum+"&itemsInEachPage=14&goodsCode="+goodsCode;
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
		
		//根据货号模糊查询，请求后台生成商品数据
		function sendForGoodsByNumber(pageNum,backFunc){
			var huohao = $("#shangpin").val();
			var storeHouseID = $("#cangku").val();
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
						goodsData = JSON.stringify(result);
					}
					if(result.length==0){
						popType = 0;
						showTip("未搜索到商品",true);
						return;
					}else if(result.length==1){
						popType = 0;
						var tmpObj = result[0];
						$("#shangpinquanming").val(tmpObj.pfullname);
						$("#shangpin").val(tmpObj.pfullname);
						$("#shangpin").data("orgCode",tmpObj.ptypeid);
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
							orgTreeObj = new Tree("treeContainer","returnParentDiv","choosedInfoValue","choosedInfoLabel","ptypeid","pfullname",true);
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
