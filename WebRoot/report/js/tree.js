 /*
 * containerObj 容器jquery对象
 * upLevelObj 上一级菜单jquery对象
 * propId  id节点递归属性
 * propName 节点显示名称
 * showPageOper 显示上一页，下一页
 * isOnlyLeaf 是否只能选择叶节点
 */
function Tree(container,upLevel,choosedInfoValue,choosedInfoLabel,propId,propName,showPageOper,isOnlyLeaf){
	this.container = container;
	this.upLevel = upLevel;
	this.propId = propId;
	this.propName = propName;
	this.showPageOper = showPageOper;
	this.pageNum = 1;	//当前页码
	this.maxItemsInEachPage = 5;	//每页约定最大显示数目 （只读不可改，后续取数也放到该文件中，彻底封装）
	this.realItemsInEachPage = 1;	//实际每页显示数目
	this.totalPageNum = 1;
	this.isOnlyLeaf = isOnlyLeaf; //只能选择叶节点
	var that = this;
	var btnArray;
	var choosedInfoValue;
	var choosedInfoLabel;
	//按由上到下的顺序记录Id (用于客户（销售订单），供货单位（采购订单），单位（往来账目表） 有数据分页，所以需要返回上一级时动态取数)
	var fatherIdArray = new Array();
	
	if(typeof(this.showPageOper)=="undefinded" || this.showPageOper==false){
		$("#pageOper").css("display","none");
	}else{
		$("#pageOper").css("display","block");
	}
	
	if(typeof(this.isOnlyLeaf)=="undefinded"){
		this.isOnlyLeaf = true;
	}
	
	//清空
	btnArray = new Array();
	$("#"+choosedInfoLabel).attr("value","");
	$("#"+choosedInfoValue).attr("value","");
	$("#"+container).empty();
	
	this.show = function(resultArr,isRoot,value){
		$("#"+container).empty();
		btnArray.splice(0,btnArray.length);
		$("#prevBtn").data("curId",value);
		$("#nextBtn").data("curId",value);
		if(!isRoot){
			$("#"+upLevel).css("display","block");
			$("#"+upLevel).attr("disable",false);
			$("#"+upLevel).unbind();//解绑上一级，只保留当前一级
			$("#"+upLevel).bind("click",function(){
			    $("#"+container).empty();
				that.returnAndexpandParentSub($("#"+upLevel).data(propId));
			});
			$("#"+upLevel).data(propId,value);
		}else{
			fatherIdArray.push(value);
			$("#"+upLevel).css("display","none");
			$("#"+upLevel).attr("disable",true);
			$("#"+upLevel).unbind();
		}
		var divObj = $("#"+container);
		
		var tmpLen = resultArr.length;
		that.realItemsInEachPage = tmpLen;
		for(var i=0;i<tmpLen;i++){
			var tmpResult = resultArr[i];
			var tmpValue = tmpResult[propId];
			if(popType==2 || popType==4 || popType==5 || popType==6){//客户（销售订单），供货单位（采购订单），单位（往来账目）， 商品， 根据货号模糊查询的商品，根据单位编号或名称的模糊查询
				var totalPage = tmpResult["totalPage"];
				if(totalPage!=0){
					that.totalPageNum = totalPage;
					changePageOperStatus(that.totalPageNum);
				}
			}
			var hrefObj = $("<button href='#popupInfo' class='ui-btn' data-inline='true'>"+tmpResult[propName]+"</button>");
			hrefObj.attr(propId,tmpValue);
			if(popType==4 || popType==5){
				hrefObj.attr("pusercode",tmpResult["pusercode"]);
			}
			//机构，结算机构 沿用原来的方式,即请求的数据带有子节点数据
			if(popType==1 || popType==3 || popType==5 || popType==6) {
				var subArray = tmpResult.subList;
				var len = subArray.length;
				if(len>0){
					hrefObj.bind("click",function(){ 
						if(that.isOnlyLeaf==false || popType!=1 && popType!=2 && popType!=3 && popType!=4){//树形组件只能选叶节点
							$("#"+choosedInfoLabel).attr("value",$(this).text());
							$("#"+choosedInfoValue).attr("value",$(this).attr(propId));
						}
						//如果为商品树，则额外设置下选择商品的货号
						if(popType==4 || popType==5 ){
							var pusercode = $(this).attr("pusercode");
							$("#choosedInfoOtherValue").attr("value",pusercode);
						}
						that.expandSub($(this));
					});
					hrefObj.on("taphold",function(){
						that.expandSub($(this));
					});
				}else{
					hrefObj.bind("click",function(){ 
						$("#"+choosedInfoLabel).attr("value",$(this).text());
						$("#"+choosedInfoValue).attr("value",$(this).attr(propId));
						//如果为商品树，则额外设置下选择商品的货号
						if(popType==4 || popType==5){
							var pusercode = $(this).attr("pusercode");
							$("#choosedInfoOtherValue").attr("value",pusercode);
						}
					});
					hrefObj.bind("taphold",function(){ 
						return ;
					});
				}
			}else if(popType==2 || popType==4){
				hrefObj.bind("click",function(){ 
					if(that.isOnlyLeaf==false || popType!=1 && popType!=2 && popType!=3 && popType!=4){//树形组件只能选叶节点
						$("#"+choosedInfoLabel).attr("value",$(this).text());
						$("#"+choosedInfoValue).attr("value",$(this).attr(propId));
					}
					//如果为商品树，则额外设置下选择商品的货号
					if(popType==4 || popType==5 ){
						var pusercode = $(this).attr("pusercode");
						$("#choosedInfoOtherValue").attr("value",pusercode);
					}
					that.expandSub($(this));
				});
			}
			btnArray.push(hrefObj);
			divObj.append(hrefObj);
			that.doRefresh();
		}
	};
	
	this.doRefresh = function (){
		var len = btnArray.length;
		for(var i=0;i<len;i++){
			var btnObj = btnArray[i];
			btnObj.button();
		}
	};
	
	
	this.expandSub = function (inputObj){
		var sourceObjArray;
		//客户，供货单位，商品等由于数据量大，点击节点时动态判断是展开还是设置值
		var backCall = function(data){
			var obj = JSON.parse(data);
			var result = obj.result;
			var tmpData;
			if(obj.isError=="true"){
				showTip(result,true);
			}else{
				if(popType==2){
					tmpData = JSON.stringify(result);
				}else if(popType==4){
					tmpData = JSON.stringify(result);
				}else{
					showTip("未知的类型",true);
				}
				sourceObjArray = JSON.parse(tmpData);
				showSub(sourceObjArray,inputObj,that);
			}			
			hideLoader();			
		};
		
		if(popType==1 || popType==3){
			sourceObjArray = JSON.parse(treeData);
		}else if(popType==2){
			var value = inputObj.attr(propId);
			sendForSupplyUnit(value,1,backCall);
		}else if(popType==4){
			var value = inputObj.attr(propId);
			sendForGoods(value,1,backCall);
		}else{
			showTip("未知的类型",true);
			return;
		}
		if(popType==1 || popType==3){
			showSub(sourceObjArray,inputObj,that);
		}
	};
	
	function showSub(sourceObjArray,inputObj,that){
		//机构，结算结构，依靠原始数据即可展现不同等级
		if(popType==1 || popType==3){
			var len = sourceObjArray.length;
			for(var i=0;i<len;i++){
				var sourceObj = sourceObjArray[i];
				var value = inputObj.attr(propId);
				var array = that.findObj(sourceObj,value);
				if(array){
					//fatherIdArray.push(value);
					$("#prevBtn").data("curId",value);
					$("#nextBtn").data("curId",value);
					that.pageNum = 1;	
					that.show(array,false,value);
				}
			}
		}else if(popType==2 || popType==4){//客户，供货单位，商品，由于是动态取每一级的数据，所以直接展现数组
			var value = inputObj.attr(propId);
			var tmpLen = sourceObjArray.length;
			var tmpFirstObj = null;
			if(tmpLen>0){
				tmpFirstObj = sourceObjArray[0];
			}
			if(tmpLen==1 && tmpFirstObj!=null && value == tmpFirstObj[propId]){//如果只有一个值，且id与父Id相同，则表示无子级，设为选中状态
				$("#"+choosedInfoLabel).attr("value",tmpFirstObj[propName]);
				$("#"+choosedInfoValue).attr("value",tmpFirstObj[propId]);
				//如果为商品树，则额外设置下选择商品的货号
				if(popType==4 || popType==5){
					var pusercode = tmpFirstObj["pusercode"];
					$("#choosedInfoOtherValue").attr("value",pusercode);
				}
			}else{
				$("#"+container).empty();
				fatherIdArray.push(value);
				$("#prevBtn").data("curId",value);
				$("#nextBtn").data("curId",value);
				that.pageNum = 1;
				that.show(sourceObjArray,false,value);
			}
		}else{
			showTip("未知的类型",true);
			return;
		}
	}
	
	
	this.returnAndexpandParentSub = function (value){
		//机构和结算机构不分页，所以沿用原方式
		if(popType==1 || popType==3){
			var sourceObjArray ;
			if(popType==1 || popType==3){
				sourceObjArray = JSON.parse(treeData);
			}else if(popType==2){
				sourceObjArray = JSON.parse(customerData);
			}
			var parentObj = that.findParentObj(null,sourceObjArray,value);
			if(parentObj){
				that.show(parentObj.subList,false,parentObj[propId]);
			}else{
				that.show(sourceObjArray,true);
			}
		}else if(popType==2){//客户（销售订单），供货单位（采购订单），单位（往来账目表） 有数据分页，所以需要返回上一级时动态取数
			//alert(fatherIdArray.join("-_-")+"||"+value);
			var fatherId = fatherIdArray[fatherIdArray.length-2];//getFatherId(fatherIdArray,value);
			fatherIdArray.splice(fatherIdArray.length-1,1);
			getUpLevelData(fatherId);
		}else if(popType==4){//商品 有数据分页，所以需要返回上一级时动态取数
			//alert(fatherIdArray.join("-_-")+"||"+value);
			var fatherId = fatherIdArray[fatherIdArray.length-2];//getFatherId(fatherIdArray,value);
			fatherIdArray.splice(fatherIdArray.length-1,1);
			getUpLevelData(fatherId);
		}else {
			showTip("未知的类型",true);
		}
	};
	
	this.findObj = function  (sourceObj,value){
		if(sourceObj[propId] == value){
			return sourceObj.subList;
		}else{
			var array = sourceObj.subList;
			var len = array.length;
			for(var i=0;i<len;i++){
				var tmpObj = array[i];
				var resultObj = that.findObj(tmpObj,value);
				if(resultObj!=null) return resultObj;
			}
			return null;
		}
	};
	
	this.findParentObj = function (rootObj,sourceObjArray,value){
		var len = sourceObjArray.length;
		for(var i=0;i<len;i++){
			var sourceObj = sourceObjArray[i];
			if(sourceObj[propId] == value){
				return rootObj;
			}else{
				var array = sourceObj.subList;
				var resultObj =  that.findParentObj(sourceObj,array,value);
				if(resultObj!=null){
					return resultObj;
				}
			}
		}
		return null;
	};
	
}

//获得上一级的编码
/*
function getFatherId(fatherIdArray,value){
	var index = 0;
	var len = fatherIdArray.length;
	for(var i=0;i<len;i++){
		var tmpValue = fatherIdArray[i];
		if(tmpValue==value){
			index = i;
			break;
		}
	}
	return fatherIdArray[index-1];
}
*/

//根据页码取数
function getData(pageOperObj,pageNum){
	showLoader("正在加载数据，请稍候...");
	var curValue = pageOperObj.data("curId");
	var backCall = function(data){
		var obj = JSON.parse(data);
		var result = obj.result;
		if(obj.isError=="true"){
			showTip(result,true);
		}else{
			if(popType==2){
				customerData = JSON.stringify(result);
			}else if(popType==4){
				goodsData = JSON.stringify(result);
			}
			orgTreeObj.pageNum = pageNum;
			orgTreeObj.show(result,(curValue==null?true:false),curValue);
		}
		hideLoader();						
	};
	if(popType==2){
		sendForSupplyUnit(curValue,pageNum,backCall);
	}else if(popType==4){
		sendForGoods(curValue,pageNum,backCall);
	}else if(popType==5){
		sendForGoodsByNumber(pageNum,backCall);
	}else if(popType==6){
		sendForSupplyunitByName(pageNum,backCall);
	}else{
		showTip("未知的类型",true);
	}
}

//返回上一级取数
function getUpLevelData(curValue){
	showLoader("正在加载数据，请稍候...");
	var backCall = function(data){
		var obj = JSON.parse(data);
		var result = obj.result;
		if(obj.isError=="true"){
			showTip(result,true);
		}else{
			if(popType==2){
				customerData = JSON.stringify(result);
			}else if(popType==4){
				goodsData = JSON.stringify(result);
			}else{
				showTip("未知的类型",true);
			}
			orgTreeObj.pageNum = 1;
			orgTreeObj.show(result,(curValue==null?true:false),curValue);
		}			
		hideLoader();			
	};
	if(popType==2){
		sendForSupplyUnit(curValue,1,backCall);
	}else if(popType==4){
		sendForGoods(curValue,1,backCall);
	}else{
		showTip("未知的类型",true);
	}
}

function changePageOperStatus(totalPage){
	if(orgTreeObj.pageNum==1){
		//alert("上一页灰掉");
		$("#prevBtn").button("disable",true);
		if(totalPage == 1){
			//alert("下一页灰掉");
			$("#nextBtn").button("disable",true);
		}else{
			//alert("下一页可用");
			$("#nextBtn").button("enable",true);
		}
	}else{
		//alert("上一页可用");
		$("#prevBtn").button("enable",true);
		if(totalPage>orgTreeObj.pageNum){
			//alert("下一页可用");
			$("#nextBtn").button("enable",true);
		}else if(totalPage==orgTreeObj.pageNum){
			//alert("下一页灰掉");
			$("#nextBtn").button("disable",true);
		}
	}
}