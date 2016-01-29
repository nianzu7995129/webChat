function showLoader(content,isTextOnly) {  
	//设置显示内容
	if(typeof(content)=="undefined" || content == null){
		content = "加载中...";
	}
	//设置是否只显示文字
	if(typeof(isTextOnly)=="undefined" || isTextOnly == null){
		isTextOnly = false;
	}
    //显示加载器.for jQuery Mobile 1.2.0  
    $.mobile.loading('show', {  
        text: content, //加载器中显示的文字  
        textVisible: true, //是否显示文字  
        theme: 'a',        //加载器主题样式a-e  
        textonly: isTextOnly,   //是否只显示文字  
        html: ""           //要显示的html内容，如图片等  
    });  
}  
  
//隐藏加载器.for jQuery Mobile 1.2.0  
function hideLoader(){  
    //隐藏加载器  
    $.mobile.loading('hide');  
}  

function showTip(content,isTextOnly){
	showLoader(content,isTextOnly);
	setTimeout(hideLoader,1500);
}

function showDialogue(content,confirmFunc,cancelFunc){
	//var html = "<div data-role=\"content\" data-theme=\"b\"><p>"+content+"</p><div><button id='dialogueConfirmBtn' class='ui-shadow ui-btn ui-corner-all' data-theme='b'>确定</button><button id='dialogueCancelBtn' class='ui-shadow ui-btn ui-corner-all' data-theme='b'>取消</button></div></div>";
	var html = "<p>"+content+"</p><div class='ui-grid-a'><div class='ui-block-a'><button id='dialogueConfirmBtn' class='ui-shadow ui-btn ui-corner-all' data-theme='b'>确定</button></div><div class='ui-block-b'><button id='dialogueCancelBtn' class='ui-shadow ui-btn ui-corner-all' data-theme='b'>取消</button></div></div>";
	//显示加载器.for jQuery Mobile 1.2.0  
    $.mobile.loading('show', {  
        text: content, //加载器中显示的文字  
        textVisible: true, //是否显示文字  
        theme: 'a',        //加载器主题样式a-e  
        textonly: true,   //是否只显示文字  
        html: html         //要显示的html内容，如图片等  
    });
    $("#dialogueConfirmBtn").button();
    $("#dialogueCancelBtn").button();
    
    $("#dialogueConfirmBtn").bind("click",function(){
    	$(this).unbind();
    	confirmFunc();
    	hideLoader();
    });
	$("#dialogueCancelBtn").bind("click",function(){
    	$(this).unbind();
    	cancelFunc();
    })
}


