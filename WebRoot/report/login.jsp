<%@ page language="java" pageEncoding="UTF-8"%>
<%
	String contextPath = request.getContextPath();
	Object visitPageObj = session.getAttribute("visitPage");
	String visitPage = "";
	if(visitPageObj!=null){
		visitPage = visitPageObj.toString();
	}
%>
<!DOCTYPE html>
<html>
	<head>
		<link rel="stylesheet" href="http://code.jquery.com/mobile/1.3.2/jquery.mobile-1.3.2.min.css">
		<script src="http://code.jquery.com/jquery-1.8.3.min.js"></script>
		<script src="http://code.jquery.com/mobile/1.3.2/jquery.mobile-1.3.2.min.js"></script>
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<script src="js/loading.js"></script>
		<script src="js/json2.js"></script>
		<script src="js/Base64.js"></script>
		<title>用户登录</title>
	</head>

	<body>
		<div data-role="page" id="pageHome">
			<div data-role="content" data-theme="b">
				<form method="post">					
					<div class="ui-field-contain">
						<label for="username">用户名：</label>
						<input type="text" name="username" id="username"  data-clear-btn="true">
					</div>
					<div class="ui-field-contain">
						<label for="password">密码：</label>
						<input type="password" name="password" id="password" data-clear-btn="true">
					</div>
					
					<input type="button" id="pwdSetBtn" class="ui-btn" data-inline="true" value="重设密码">
					
					<input type="button" id="privSetBtn" class="ui-btn" data-inline="true" value="权限设置">
					
					<input type="button" id="goOnVisitBtn"  class="ui-btn" data-inline="true" value="继续访问">

					<input type="button" id="loginBtn" class="ui-btn" data-corners="false" data-theme="b" value="登录">
				</form>
			</div>
		</div>
		
		<!-- 弹出已有用户列表，用于设置权限 -->
		<div data-role="page" id="pageUserList">
			<div data-role="content" data-theme="b">
				<ol data-role="listview" data-inset="true" id="userList">
				</ol>
				<input type="button" id="addUserBtn" class="ui-btn"  data-inline="true"  data-theme="b" value="增加用户">
				<input type="button" id="backToPageHomeBtn" class="ui-btn"  data-inline="true" data-theme="b" value="返回">
			</div>
		</div>
		<!-- 弹出层用于设置权限 -->
		<div data-role="page" id="pageAuthSet">
			<div data-role="content" data-theme="c">
				<fieldset data-role="controlgroup">
			        <input type="checkbox" name="cgdd" id="cgdd"><label for="cgdd">采购订单</label>
			        <input type="checkbox" name="kczkb" id="kczkb"><label for="kczkb">库存状况表</label>
			        <input type="checkbox" name="xsdd" id="xsdd"><label for="xsdd">销售订单</label>
			        <input type="checkbox" name="spxsfxb" id="spxsfxb"><label for="spxsfxb">商品销售分析表</label>
			        <input type="checkbox" name="yfkcx" id="yfkcx"><label for="yfkcx">应付款查询</label>
			        <input type="checkbox" name="yskcx" id="yskcx"><label for="yskcx">应收款查询</label>
			        <input type="hidden" name="curusername" id="curusername" />
			    </fieldset>
				<input type="button" id="saveAndBackToPageUserListBtn" class="ui-btn" data-corners="false" data-theme="b" value="保存">
			</div>
		</div>
		<!-- 弹出层用于增加用户或重设管理员密码 -->
		<div data-role="page" id="pageAddUser">
			<div data-role="content" data-theme="b">
				<div id="new_username_div" class="ui-field-contain">
					<label id="new_username_label" for="new_username">用户名：</label>
					<input type="text" name="new_username" id="new_username"  data-clear-btn="true">
				</div>
				<div class="ui-field-contain">
					<label for="new_password">密码：</label>
					<input type="password" name="new_password" id="new_password" data-clear-btn="true">
				</div>
				<div class="ui-field-contain">
					<label for="new_password_again">确认密码：</label>
					<input type="password" name="new_password_again" id="new_password_again" data-clear-btn="true">
				</div>
				<input type="button" id="saveUserBtn" class="ui-btn" data-inline="true"  data-corners="true" data-theme="b" value="保存">
				<input type="button" id="saveUserBackBtn"  class="ui-btn" data-inline="true" data-corners="true" data-theme="b" value="返回">
			</div>
		</div>
	</body>

	<script type="text/javascript">
		var contextPath = "<%=contextPath%>";
		var userInfoData = {"userList": []};//用户列表
		var reportData = {"reportList":[]};//报表列表
		var base64 = new Base64();
		var ajaxTimeout = 1000000;
		
		$(function(){
			//隐藏管理员的"重设密码"，"权限设置"和"继续访问"
			$('#pwdSetBtn').parent("div").css('display','none');
			$('#privSetBtn').parent("div").css('display','none');
			$('#goOnVisitBtn').parent("div").css('display','none');
			
			$("#loginBtn").bind("click",function(){
				//发送ajax请求验证登录信息
				var un = $("#username").val();
				if(un.length==0){
					showTip("用户名不能为空!",true);
					return;
				}
				var pd = $.trim($("#password").val());
				var bun = base64.encode(un);
				var bpd = base64.encode(pd);
				var requestData = "action=action_login&un="+bun+"&pd="+bpd;
				$.ajax({
					url: contextPath+"/AuthServlet",
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
						var isError = obj.isError;
						if(isError=="false"){
							//隐藏"权限设置"和"继续访问"
							if("admin" == un){
								$('#privSetBtn').parent("div").css('display','block');
								$('#privSetBtn').css('data-inline','true');
								if("" == pd){
									$('#pwdSetBtn').parent("div").css('display','block');
									$('#pwdSetBtn').css('data-inline','true');
								}
							}
							if("<%=visitPage%>"!=""){
								$('#goOnVisitBtn').parent("div").css('display','block');
								$('#goOnVisitBtn').css('data-inline','true');
							}else{
								showTip("登录成功，可访问报表",true);
								setTimeout(function(){
									if("admin" != un){
										window.close();
									}
								},1500);
							}
							$("#loginBtn").parent("div").css('display','none');
						}else{
							showTip(obj.result,true);
						}
					}
			    });
			});
			
			//设置面
			$("#pwdSetBtn").bind("click",function(){
				$.mobile.changePage($("#pageAddUser"), {
					 'allowSamePageTransition' : false,
					 'reloadPage' : false,
					 transition: 'none'
				});
				//设置用户名不可见
				$("#new_username_div").css("display","none");	
				$("#new_username_label").css("display","none");	
				$("#new_username").css("display","none");	
				$("#new_username").val("admin");
			});
			
			//权限设置
			$("#privSetBtn").bind("click",function(){
				$.mobile.changePage($("#pageUserList"), {
					 'allowSamePageTransition' : false,
					 'reloadPage' : false,
					 transition: 'none'
				});
				showLoader();
				userInfoData = {"userList": []};//重置数据
				//进入权限设置界面，即发送ajax请求已有用户列表。去掉admin
				//发送ajax请求用户列表
				var requestData = "action=action_get_userlist";
				$.ajax({
					url: contextPath+"/AuthServlet",
					data: requestData,
					type: "POST",
					dataType: 'text',
					timeout: ajaxTimeout,
					async:true,
					error: function(XMLHttpRequest, textStatus, errorThrown){
						hideLoader();
						showTip("请求服务器数据异常!",true);
						return;
					},
					success: function(data){
						hideLoader();
						var obj = JSON.parse(data);
						var isError = obj.isError;
						if(isError=="false"){
							var resultObj = obj.result;
							var usersArray = resultObj.users;
							var len = usersArray.length;
							for(var i=0;i<len;i++){
								var tmp = usersArray[i];
								if(tmp.un == "admin"){
									continue;
								}
								var userInfo = {"un": tmp.un,"auth": tmp.auth};
								userInfoData.userList.push(userInfo);
							}
							showUserList(userInfoData);
						}else{
							showTip(obj.result,true);
						}
					}
			    });
			});
			
			//继续访问
			$("#goOnVisitBtn").bind("click",function(){
				var visitPage = "<%=visitPage%>";
				window.location.href = visitPage;
			});
			
			//增加新用户
			$("#addUserBtn").bind("click",function(){
				$.mobile.changePage($("#pageAddUser"), {
					 'allowSamePageTransition' : false,
					 'reloadPage' : false,
					 transition: 'none'
				});
			});
			
			//增加新用户-保存
			$("#saveUserBtn").bind("click",function(){
				var new_un = $("#new_username").val();
				if(new_un.length==0){
					showTip("用户名不能为空!",true);
					return;
				}
				var new_pd = $("#new_password").val();
				var new_pd_again = $("#new_password_again").val();
				if(new_pd != new_pd_again){
					showTip("密码与确认密码不相同",true);
					return;
				}
				var bun = base64.encode(new_un);
				var bpd = base64.encode(new_pd);
				var requestData = "action=action_add_user&un="+bun+"&pd="+bpd;
				showLoader();
				$.ajax({
					url: contextPath+"/AuthServlet",
					data: requestData,
					type: "POST",
					dataType: 'text',
					timeout: ajaxTimeout,
					async:true,
					error: function(XMLHttpRequest, textStatus, errorThrown){
						hideLoader();
						showTip("请求服务器数据异常!",true);
						return;
					},
					success: function(data){
						hideLoader();
						var obj = JSON.parse(data);
						var isError = obj.isError;
						if(isError=="false"){
							if("admin"!=new_un){
								$.mobile.changePage($("#pageUserList"), {
									 'allowSamePageTransition' : false,
									 'reloadPage' : false,
									 transition: 'none'
								});
								var userInfo = {"un": new_un,"auth": ""};
								userInfoData.userList.push(userInfo);
								showUserList(userInfoData);
							}else{
								$.mobile.changePage($("#pageHome"), {
									 'allowSamePageTransition' : false,
									 'reloadPage' : false,
									 transition: 'none'
								});
								$('#pwdSetBtn').parent("div").css('display','none');
							}
						}else{
							showTip(obj.result,true);
						}
					}
			    });
				
				
			});
			
			//增加新用户-返回
			$("#saveUserBackBtn").bind("click",function(){
				var tmpUserName = $("#new_username").val();
				if(tmpUserName=="admin"){
					$.mobile.changePage($("#pageHome"), {
						 'allowSamePageTransition' : false,
						 'reloadPage' : false,
						 transition: 'none'
					});
				}else{
					$.mobile.changePage($("#pageUserList"), {
						 'allowSamePageTransition' : false,
						 'reloadPage' : false,
						 transition: 'none'
					});
				}
			});
			
			//用户列表界面返回到登录界面
			$("#backToPageHomeBtn").bind("click",function(){
				$.mobile.changePage($("#pageHome"), {
					 'allowSamePageTransition' : false,
					 'reloadPage' : false,
					 transition: 'none'
				});
			});
			
			//保存权限并返回到用户列表界面
			$("#saveAndBackToPageUserListBtn").bind("click",function(){
				var tmpAuth = "";
				$("input[type='checkbox']").each(function(){
				    var checked = $(this).attr("checked");
				    if(checked){
				    	tmpAuth += $(this).attr("id")+",";
				    }
				});
				var len = tmpAuth.length;
				if(len>0){
					tmpAuth = tmpAuth.substring(0,len-1);
				}
				var curUserName = $("#curusername").val();
				var requestData = "action=action_save_auth&un="+base64.encode(curUserName)+"&auth="+base64.encode(tmpAuth);
				showLoader();
				$.ajax({
					url: contextPath+"/AuthServlet",
					data: requestData,
					type: "POST",
					dataType: 'text',
					timeout: ajaxTimeout,
					async:true,
					error: function(XMLHttpRequest, textStatus, errorThrown){
						hideLoader();
						showTip("请求服务器数据异常!",true);
						return;
					},
					success: function(data){
						hideLoader();
						var obj = JSON.parse(data);
						var isError = obj.isError;
						if(isError=="false"){
							$.mobile.changePage($("#pageUserList"), {
								 'allowSamePageTransition' : false,
								 'reloadPage' : false,
								 transition: 'none'
							});
							var userInfoArray = userInfoData.userList;
							var len = userInfoArray.length;
							for(var i=0;i<len;i++){
								var userInfo = userInfoArray[i];
								var tmpUn = userInfo.un;
								if(tmpUn==curUserName){
									userInfo.auth = tmpAuth;
									break;
								}
							}
							showUserList(userInfoData);
						}else{
							showTip(obj.result,true);
						}
					}
			    });
				
			});
		});
		
		//显示商品列表
		function showUserList(data){
			var $wrapDiv = $("#userList");
			$wrapDiv.empty();
			var userListArr = data.userList;
			$.each(userListArr, function(i, value){ 
			    var $liObj = "<li><a id='hrefUserObj' onclick='setUserAuth(" + i + ")' >" + this.un + "<a data-role='button' data-icon='delete' onclick='deleteUser(" + i + ")'>删除</a></a></li>";  
			    $wrapDiv.append($liObj);
			});   
			$wrapDiv.listview("refresh");
		}
		
		function setUserAuth(i){
			var userInfoObj = userInfoData.userList[i];
			var un = userInfoObj.un;
			var auth = userInfoObj.auth;
			$.mobile.changePage($("#pageAuthSet"), {
				 'allowSamePageTransition' : false,
				 'reloadPage' : false,
				 transition: 'none'
			});
			$("#curusername").val(un);
			var authArr = auth.split(",");
			refreshCheckbox("cgdd",authArr);
			refreshCheckbox("kczkb",authArr);
			refreshCheckbox("xsdd",authArr);
			refreshCheckbox("spxsfxb",authArr);
			refreshCheckbox("yfkcx",authArr);
			refreshCheckbox("yskcx",authArr);
		}
		
		function refreshCheckbox(name,authArr){
			var len = authArr.length;
			var isIn = false;
			for(var i=0;i<len;i++){
				var tmp = authArr[i];
				if(name == tmp){
					isIn = true;
					$("#"+name).attr("checked",true).checkboxradio("refresh");
					break;
				}
			}
			if(!isIn){
				$("#"+name).attr("checked",false).checkboxradio("refresh");
			}
		}
		
		function deleteUser(i){
			showDialogue("确定要删除该用户吗?",function(){
				var userInfoObj = userInfoData.userList[i];
				var curUserName = userInfoObj.un;
				var requestData = "action=action_delete_user&un="+base64.encode(curUserName);
				showLoader();
				$.ajax({
					url: contextPath+"/AuthServlet",
					data: requestData,
					type: "POST",
					dataType: 'text',
					timeout: ajaxTimeout,
					async:true,
					error: function(XMLHttpRequest, textStatus, errorThrown){
						hideLoader();
						showTip("请求服务器数据异常!",true);
						return;
					},
					success: function(data){
						hideLoader();
						var obj = JSON.parse(data);
						var isError = obj.isError;
						if(isError=="false"){
							userInfoData.userList.splice(i,1);
							showUserList(userInfoData);
						}else{
							showTip(obj.result,true);
						}
					}
			    });
			},hideLoader);
		}
	</script>

</html>
