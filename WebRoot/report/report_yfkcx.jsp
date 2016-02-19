﻿<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="org.wechat.utils.AuthUtil"%>
<%
	String contextPath = request.getContextPath();
	String OperatorID = (String)session.getAttribute("OperatorID");
	if(OperatorID==null){
		session.setAttribute("visitPage","report_yfkcx.jsp");
		response.setContentType("text/html; charset=UTF-8");
		response.sendRedirect("login.jsp");
	}else{
		boolean valid =AuthUtil.checkValid(OperatorID,"yfkcx");
		if(!valid){
			session.setAttribute("visitPage","report_yfkcx.jsp");
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
		<title>应付款查询</title>
	</head>

	<body>
		<div data-role="page" id="pageHome">
			<div data-role="navbar" id="navigationBar">
				<ul>
					<li><a id="nav_query" data-ajax="false" class="ui-btn-active">应付款</a></li>
					<li><a id="nav_result" data-ajax="false">查询结果</a></li>
				</ul>
			</div>
			<!-- 查询条件页面 -->
			<div id="queryPart" class="ui-body-d ui-content" data-theme="b">
				<form method="post">					
					<div class="ui-field-contain">
						<label for="jigou">机构：</label>
						<a id="jigouHref"><input readonly="readonly" type="text" name="jigou" orgCode="" id="jigou"></a>
					</div>
					<div class="ui-field-contain">
						<label for="danwei">单位全名：</label>
						<!-- <a id="danweiHref"> -->
							<input type="text" name="danwei" id="danwei">
						<!-- </a> -->
					</div>
					<div class="ui-field-contain">
    					<label for="allShow">显示方式：</label>
						<select name="allShow" id="allShow" data-native-menu="false" data-theme="b">
							<option value="0" selected>全部显示</option>
							<option value="1">应付款为零</option>
							<option value="2">应付款不为零</option>
						</select>
					</div>
					<div class="ui-field-contain">
						<label><input id="baohanyushouyufu" name="baohanyushouyufu" type="checkbox">包含预付</label>
					</div>
					<br/>
					<input type="button" id="queryBtn" class="ui-btn" data-corners="false" data-theme="b" value="查询">
				</form>
			</div>
			<!-- 查询结果页面-->
			<div id="resultPart" style="display:none" data-theme="b">
				<form method="post">
					<div class="ui-field-contain">
						<label for="danweiquanming">单位全名：</label>
						<input readonly="readonly" type="text" name="danweiquanming" id="danweiquanming" data-clear-btn="true">
					</div>
					<div style = "display:none">					
					<div class="ui-field-contain">
						<label for="qichuyingshou">期初应收：</label>
						<input readonly="readonly" type="text" name="qichuyingshou" id="qichuyingshou" data-clear-btn="true">
					</div>
					<div class="ui-field-contain">
						<label for="yingshouzengjia">应收增加：</label>
						<input readonly="readonly" type="text" name="yingshouzengjia" id="yingshouzengjia" data-clear-btn="true">
					</div>
					<div class="ui-field-contain">
						<label for="yingshoujianshao">应收减少：</label>
						<input readonly="readonly" type="text" name="yingshoujianshao" id="yingshoujianshao" data-clear-btn="true">
					</div>
					<div class="ui-field-contain">
						<label for="yushouyue">预收余额：</label>
						<input readonly="readonly" type="text" name="yushouyue" id="yushouyue" data-clear-btn="true">
					</div>
					<div class="ui-field-contain">
						<label for="yingshouyue">应收余额：</label>
						<input readonly="readonly" type="text" name="yingshouyue" id="yingshouyue" data-clear-btn="true">
					</div>
					</div>
					<div class="ui-field-contain">
						<label for="qichuyingfu">期初应付：</label>
						<input readonly="readonly" type="text" name="qichuyingfu" id="qichuyingfu" data-clear-btn="true">
					</div>
					<div class="ui-field-contain">
						<label for="yingfuzengjia">应付增加：</label>
						<input readonly="readonly" type="text" name="yingfuzengjia" id="yingfuzengjia" data-clear-btn="true">
					</div>
					<div class="ui-field-contain">
						<label for="yingfujianshao">应付减少：</label>
						<input readonly="readonly" type="text" name="yingfujianshao" id="yingfujianshao" data-clear-btn="true">
					</div>
					<div class="ui-field-contain">
						<label for="yufuyue">预付余额：</label>
						<input readonly="readonly" type="text" name="yufuyue" id="yufuyue" data-clear-btn="true">
					</div>
					<div class="ui-field-contain">
						<label for="yingfuyue">应付余额：</label>
						<input readonly="readonly" type="text" name="yingfuyue" id="yingfuyue" data-clear-btn="true">
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
		var popType=0;//用于区分当前弹出树组件的使用对象，0：默认未使用，1：机构，2：客户（供货单位）
		
		//如下数据，当机构改变时需要清空
		var customerData = null;//客户数据
		
		var ajaxTimeout = 1000000;
		
		$(function(){
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
				//验证单位
				var danwei =  $("#danwei").data("orgCode");
				if(typeof(danwei) == "undefined" || danwei==null || danwei.length==0){
					showTip("请选择单位",true);
					return;
				}
				
				var showAll = $("#allShow").val();
				var baohanyushouyufu = $("#baohanyushouyufu").prop("checked")==true?1:0;
				
				$.ajax({
					url: contextPath+"/BusinessServlet",
					data: "action=action_query_wlzmb&OperatorID=<%=OperatorID%>&supplyUnit="+danwei+"&organization="+organization+"&includeYSYF="+baohanyushouyufu+"&filter="+showAll,
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
							/*
								jo.put("btypeid", rs.getString("btypeid"));// 单位编号，用于查单位全名
								jo.put("ArTotal00", rs.getString("ArTotal00"));// 期初应收
								jo.put("ArTotalAdd", rs.getString("ArTotalAdd"));// 应收增加
								jo.put("ArTotalDec", rs.getString("ArTotalDec"));// 应收减少
								jo.put("ArTotalYS", rs.getString("ArTotalYS"));// 预收余额
								jo.put("ArTotal", rs.getString("ArTotal"));// 应收余额
							*/
							var bfullname = result.bfullname;//单位全名
							$("#danweiquanming").val(bfullname);
							var ArTotal00 = result.ArTotal00;//期初应收
							$("#qichuyingshou").val(ArTotal00);
							var ArTotalAdd = result.ArTotalAdd;//应收增加
							$("#yingshouzengjia").val(ArTotalAdd);
							var ArTotalDec = result.ArTotalDec;//应收减少
							$("#yingshoujianshao").val(ArTotalDec);
							var ArTotalYS = result.ArTotalYS;//预收余额
							$("#yushouyue").val(ArTotalYS);
							var ArTotal = result.ArTotal;//应收余额
							$("#yingshouyue").val(ArTotal);
							/*
								jo.put("btypeid", rs.getString("btypeid"));// 单位编号，用于查单位全名
								jo.put("APTotal00", rs.getString("APTotal00"));// 期初应付
								jo.put("ApTotalAdd", rs.getString("ApTotalAdd"));// 应付增加
								jo.put("ApTotalDec", rs.getString("ApTotalDec"));// 应付减少
								jo.put("APTotalYF", rs.getString("APTotalYF"));// 预付余额
								jo.put("APTotal", rs.getString("APTotal"));// 应付余额
							*/
							var APTotal00 = result.APTotal00;//期初应付
							$("#qichuyingfu").val(APTotal00);
							var ApTotalAdd = result.ApTotalAdd;//应付增加
							$("#yingfuzengjia").val(ApTotalAdd);
							var ApTotalDec = result.ApTotalDec;//应付减少
							$("#yingfujianshao").val(ApTotalDec);
							var APTotalYF = result.APTotalYF;//预付余额
							$("#yufuyue").val(APTotalYF);
							var APTotal = result.APTotal;//应付余额
							$("#yingfuyue").val(APTotal);
							
							$("#nav_result").trigger("click");
						}
					}
			    });
			});
			
			//生成机构
			$("#jigouHref").bind("click",function(){
				var organization =  $("#jigou").data("orgCode");
				if(typeof(organization)!="undefined" && organization!=null && organization.length>0){			
					showDialogue("修改机构会清除单位信息，确定要继续吗?",function(){
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
			
			//生成客户
			/*
			$("#danweiHref").bind("click",function(){
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
			
			$("#danwei").bind("keydown",function(event){
				if(event.which==13){
					var organization = $("#jigou").data("orgCode");
					var tmp = $("#danwei").val();
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
			
			
			
			//返回机构选择值
			$("#confirmOrgBtn").bind("click",function(){
				var needRefresh = false;
				var customerCode = $("#choosedInfoValue").val();
				var customerName = $("#choosedInfoLabel").val();
				if(customerName==null || typeof(customerName)=="undefined" || customerName.length==0){
					var content = "";
					if(popType==1 || popType==3){
						content = "请选择机构信息";
					}else if(popType == 2 || popType == 6){
						content = "请选择单位";
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
							//清空单位所选信息
							customerData = null;
							$("#danwei").val("");
							$("#danwei").data("orgCode","");
						}
					}
				}else if(popType==2 || popType==6){
					$("#danwei").val(customerName);
					$("#danwei").data("orgCode",customerCode);
				}
				$.mobile.changePage($("#pageHome"), {
					 'allowSamePageTransition' : false,
					 'reloadPage' : false,
					 transition: 'none'
				});
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
		});
		
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
		
		//生成供货单位
		function sendForSupplyUnit(supplyUnitCode,pageNum,backCall){
			var organization =  $("#jigou").data("orgCode");
			var requestData = "action=action_wlzmb_yfkcx_unit&OperatorID=<%=OperatorID%>&organization="+organization+"&pageNum=1&itemsInEachPage=14"
			if(typeof(pageNum)!="undefined" && typeof(supplyUnitCode)!="undefined"){
				requestData = "action=action_wlzmb_yfkcx_unit&OperatorID=<%=OperatorID%>&organization="+organization+"&pageNum="+pageNum+"&itemsInEachPage=14&supplyUnitCode="+supplyUnitCode
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
								showTip("无单位信息,请重新选择机构",true);
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
		
		//根据单位编号或名称模糊查询，请求后台生成单位数据
		function sendForSupplyunitByName(pageNum,backFunc){
			var danwei = $("#danwei").val();
			var organization = $("#jigou").data("orgCode");
			var sub_organization = organization.substring(0,5);
			var otherParam = "&custom1=1&bDisplayStop=0";
			if(sub_organization=="00001"){//内部机构
				otherParam = "&custom1=1&bDisplayStop=1";
			}else if(sub_organization=="00004"){//加盟机构
				otherParam = "&custom1=64&bDisplayStop=1";
			}
			var requestData = "action=action_supplyunit_byname&name="+danwei+"&OperatorID=<%=OperatorID%>&organization="+organization+"&pageNum=1&itemsInEachPage=14"+otherParam;
			if(pageNum!=null){
				requestData = "action=action_supplyunit_byname&name="+danwei+"&OperatorID=<%=OperatorID%>&organization="+organization+"&pageNum="+pageNum+"&itemsInEachPage=14"+otherParam;
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
						$("#danwei").val(tmpObj.bfullname);
						$("#danwei").data("orgCode",tmpObj.btypeid);
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
