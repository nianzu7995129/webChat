<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<%
	String 

%>

<!DOCTYPE html>
<html>
<head>

<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet" href="jquery.mobile-1.4.5/jquery.mobile-1.4.5.min.css">
<script src="http://code.jquery.com/jquery-1.8.3.min.js"></script>
<script src="jquery.mobile-1.4.5/jquery.mobile-1.4.5.min.js"></script>
<style type="text/css">

</style>
<title>采购订单</title>
</head>

<body>
<div data-role="page" id="pageTree" >
	<div data-role="content" data-theme="b">
		<a href="#pageHome" data-role="button" data-icon="back">返回</a>
		<div id="orgTree" data-role="content">
			<div id="orgTreeListView" data-role="listview">
				<div id="returnParentDiv"><button class="ui-btn" data-inline="true">返回上一级</button></div>
				<div id="treeContainer"></div>
			</div>
		</div>
		<div data-role="popup" id="popupInfo" class="ui-content" data-theme="a" style="max-width:350px;">
          <p>Here is a <strong>tiny popup</strong> being used like a tooltip. The text will wrap to multiple lines as needed.</p>
		</div>
		<fieldset>
			<label for="choosedInfo">所选机构：</label>
			<input type="text" name="choosedInfo" id="choosedInfo" data-clear-btn="true">
		</fieldset>
		<br><br>
		<a href="#pageHome" data-role="button" data-icon="arrow-l">确定</a>
	</div>
</div>
</body>
</html>
