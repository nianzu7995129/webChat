// 新增商品
function addGoodsInfo(){
	shangpincode = null;//清空缓存商品编码
	goodsData = null;//返回主页面时清空商品信息
	
	var goodsCode = $("#cgddgoodsname").data("orgCode");
	if(typeof(goodsCode)=="undefined" || goodsCode==null || goodsCode.length==0){
		showTip("商品编码不能为空",true);
		return;
	}
	
	var sumValue = $("#sum").val();
	if(typeof(sumValue)=="undefined" || sumValue==null || sumValue.length==0){
		showTip("商品数量不能为空",true);
		return;
	}
	var moneyValue = $("#money").val();
	if(typeof(moneyValue)=="undefined" || moneyValue==null || moneyValue.length==0){
		showTip("商品金额不能为空",true);
		return;
	}
	var transaction = $("#transaction").val();
	if(typeof(transaction)=="undefined" || transaction==null || transaction.length==0){
		showTip("商品成交金额不能为空",true);
		return;
	}
	
	var singlePrice = $("#price").val();
	if(!validate(singlePrice)){
		singlePrice = Number($("#money").val())/Number($("#sum").val());
	}
	
	var goodsInfo = {
		"cgddgoodsnumber": $("#cgddgoodsnumber").val(),	//货号
		"cgddgoodsname": $("#cgddgoodsname").data("orgCode"),		//商品名称（商品编码）
		"cgddgoodslabel": $("#cgddgoodsname").val()+"&nbsp;数量:"+$("#sum").val()+"&nbsp;金额:"+$("#transaction").val(),	//用于主页面显示商品信息
		"cgddgoodscolor": $("#cgddgoodscolor").val(),				//商品颜色
		"cgddgoodssize": $("#cgddgoodssize").val(),				//商品尺寸
		"cgddgoodssum": $("#sum").val(),					//商品数量
		"cgddgoodsprice": singlePrice,				//商品单价
		"cgddgoodsmoney": $("#money").val(),				//商品金额
		"cgddgoodstransaction": $("#transaction").val()	//商品成交金额
    };
	alreadyChooseGoodsData.goodsList.push(goodsInfo);
	showGoodsList(alreadyChooseGoodsData);
	$.mobile.changePage($("#pageHome"), {
		 'allowSamePageTransition' : false,
		 'reloadPage' : false,
		 transition: 'none'
	});
}

//显示商品列表
function showGoodsList(data){
	var $wrapDiv = $("#goodsList");
	$wrapDiv.empty();
	var goodsListArr = data.goodsList;
	$.each(goodsListArr, function(i, value){ 
	    var $liObj = "<li><a href='#' title='" + this.cgddgoodsnumber + "'>" + this.cgddgoodslabel + "<a href='#pageHome' data-role='button' data-icon='delete' onclick='deleteGoods(" + i + ")'>删除</a></a></li>";  
	    $wrapDiv.append($liObj);
	 });   
	$wrapDiv.listview("refresh");
}

function calcGoodsTotalSum(){
	var goodsListArr = alreadyChooseGoodsData.goodsList;
	var len = goodsListArr.length;
	var total = 0;
	for(var i=0;i<len;i++){
		var tmpObj = goodsListArr[i];
		var tmp = tmpObj.cgddgoodssum;
		total = Number(total) + Number(tmp);
	}
	return total;
}

//删除商品
function deleteGoods(i){
	showDialogue("确定要删除该商品吗?",function(){
		alreadyChooseGoodsData.goodsList.splice(i,1);
		showGoodsList(alreadyChooseGoodsData);
	},hideLoader);
}

//删除商品
function deleteAllGoods(){
	var goodsListArr = alreadyChooseGoodsData.goodsList;
	var len = goodsListArr.length;
	goodsListArr.splice(0,len);
	showGoodsList(alreadyChooseGoodsData);
}

function validate(oNum){ 
	if(!oNum) return false; 
	var strP=/^\d+(\.\d+)?$/; 
	if(!strP.test(oNum)) return false; 
	try{ 
		if(parseFloat(oNum)!=oNum) return false; 
	}catch(ex){ 
 		return false; 
	} 
	return true; 
}