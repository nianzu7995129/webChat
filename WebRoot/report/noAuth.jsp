<%@ page language="java" pageEncoding="UTF-8"%>
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
		<title>无权访问</title>
	</head>

	<body>
		<div data-role="page" id="pageHome">
			<div data-role="content" data-theme="b">
				无权访问
				<input type="button" id="changeUserBtn" class="ui-btn" data-corners="false" data-theme="b" value="切换用户">
			</div>
		</div>
	</body>
	<script type="text/javascript">
		$(function(){
			$("#changeUserBtn").bind("click",function(){
				window.location.href = "login.jsp";
			});
		});
	</script>

</html>
