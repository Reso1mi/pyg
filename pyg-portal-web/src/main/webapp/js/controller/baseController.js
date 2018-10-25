/**
 * 父controller 抽取了公共的常用方法
 */
app.controller("baseController", function($scope) {
	//重新加载页面
	$scope.reloadList = function() {
		//高级查询
		$scope.search($scope.paginationConf.currentPage,
				$scope.paginationConf.itemsPerPage);
		/* $scope.listBrand($scope.paginationConf.currentPage,
				$scope.paginationConf.itemsPerPage); */
	};
	
	//json转换为普通字符串
	$scope.jsonToString=function(jsonString,key){
		//先将json字符串转化为json对象
		var json=JSON.parse(jsonString);
		var result='';
		for(var i=0;i<json.length;i++){
			if(i>0){
				result+='，';
			}
			result+=json[i][key];
		}
		return result;
	}
	
	
	//分页配置
	$scope.paginationConf = {
		currentPage : 1,
		totalItems : 10,
		itemsPerPage : 10,
		perPageOptions : [10, 20, 30, 40, 50],
		//当这个模块被加载的时候就会执行这个方法
		onChange : function() {
			$scope.reloadList();//重新加载
		}
	};
	
	$scope.selectIds = []; //复选框
	//更新复选框
	$scope.updateSelect = function($event, id) {
		if ($event.target.checked) {
			$scope.selectIds.push(id);
		} else {
			//原生js的方法
			var index = $scope.selectIds.indexOf(id);
			$scope.selectIds.splice(index, 1);
		}
	}


    //根据key的值遍历集合获取对象
    $scope.searchObject=function(list,key,keyValue){
        for (var i=0;i<list.length;i++){
            if(list[i][key]==keyValue){
                return list[i];
            }
        }
        return null;
    }

	//定义搜索对象 主要是为了在进入方法的时候加载列表 search已经初始化  其实也可以用ng-init 来初始化
 	$scope.searchEntity = {};
});