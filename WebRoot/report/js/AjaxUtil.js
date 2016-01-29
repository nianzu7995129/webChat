/**
 * Ajax工具类，基于jquery
 * @class 
 * @name AjaxUtil
 */
function AjaxUtil(){
	/**
	 * 发送ajax请求的方法
	 * @name sendAjax
	 * @methodOf AjaxUtil
	 * @param path 向后台请求的URL
	 * @param paramData 请求的参数，格式为key1=value1&key2=value2..
	 * @param successBackFunction 成功后的回调函数,回调函数可接收2个参数第一个为返回的数据，第二个为jquery传递的标准信息
	 * @param errorBackFunction 失败后的回调函数,回调函数可接收3个参数XMLHttpRequest, data, errorThrown，
	 * 		1、如果发生了ajax错误，例如服务器连接失败或超时
	 * 			XMLHttpRequest, errorThrown是jquery传递过来的标准信息
	 * 			data.errorMessage中含有翻译成中文的jquery ajax错误解释
	 * 			data.jsonMessage2FrontEnd没有值
	 * 		2、如果是后台发生了异常，并进行了正确处理
	 * 			XMLHttpRequest, errorThrown这两个参数是null
	 * 			data.errorMessage是后台传递过来的用户可理解的错误信息
	 * 			data.jsonMessage2FrontEnd含有帮助前台程序处理错误的信息
	 * @return 
	 */
	this.sendAjax = function(path,paramData,successBackFunction,errorBackFunction,settings){
		if(!settings){
			settings = {
				"datatype":"json",
				"timeout":1500000,
				"async":true
			};
		}
		if(!settings.datatype){
			settings.datatype = "json";
		}
		if(!settings.timeout){
			settings.timeout = 1500000;
		}
		if(settings.async!=false){
			settings.async = true;
		}
		$.ajax({
			type:"POST",
			url: path,
			cache:false,
			data:paramData,
			dataType:settings.datatype,
			timeout:settings.timeout,
			async:settings.async,
			success:function(data, textStatus){
				if(data&&data.errorMessage){
					//如果json中带有错误信息，则执行errorBackFunction进行错误提示
					errorBackFunction(null, data, null);
				}else{
					successBackFunction(data, textStatus);
				}
			},
			error:function(XMLHttpRequest, textStatus, errorThrown){
				//发生了ajax错误，此时并没有从后台得到任何帮助信息，我们只是简单把英文错误信息翻译成中文
				var data = {};
				if(textStatus == "error")
					data.errorMessage = "服务器错误";
				if(textStatus == "timeout")
					data.errorMessage = "连接超时";
				if(textStatus == "abort")
					data.errorMessage = "连接被终止";
				if(textStatus == "parseerror")
					data.errorMessage = "解析错误";
				errorBackFunction(XMLHttpRequest, data, errorThrown);
			}
		});
	};
}