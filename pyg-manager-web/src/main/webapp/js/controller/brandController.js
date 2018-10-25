/**
 * brand控制层
 */
//定义控制器
app.controller("brandController", function($scope, $http, $controller,
		brandService) {

	//伪继承，只是让scope连通
	$controller("baseController", {
		$scope : $scope
	});

	//进入后台controller (这个方法是之前写的没有高级查询 其实没用了)
	$scope.listBrand = function(currentPage, pageSize) {
		brandService.listBrand(currentPage, pageSize).success(function(resp) {
			//前端需要的是rows部分
			$scope.brandList = resp.rows;
			//分页模块需要的total
			$scope.paginationConf.totalItems = resp.total;
		})
	};

	//回显品牌
	$scope.getBrand = function(id) {
		brandService.getBrand(id).success(function(resp) {
			/* $scope.entity.name=resp.name;
			$scope.entity.firstChar=resp.firstChar; */
			$scope.entity = resp;
		});
	}

	//添加 ,修改品牌
	$scope.editBrand = function() {
		var method = brandService.addBrand($scope.entity);
		if ($scope.entity.id != null) { //在这里判断下有id就修改，没id就添加
			method = brandService.modBrand($scope.entity);
		}
		method.success(function(resp) {
			//响应的是PygResult对象
			if (resp.success) {
				//成功后刷新下列表就可以了
				alert(resp.message);
				$scope.reloadList();
			} else {
				alert(resp.message);
			}
		});
	};

	//删除品牌
	$scope.delBrand = function() {
		if (confirm("确认要删除id为" + $scope.selectIds + "的品牌么")) {
			brandService.delBrand($scope.selectIds).success(function(resp) {
				if (resp.success) {
					alert(resp.message);
					//清空复选框 会比较好 不然下次删除又会把这些id传到后台
					$scope.selectIds = [];
					$scope.reloadList();
				} else {
					alert(resp.message);
				}
			})
		}
	}

	//高级查询
	$scope.search = function(currentPage, pageSize) {
		brandService.searchBrand(currentPage, pageSize, $scope.searchEntity)
				.success(function(resp) {
					//前端需要的是rows部分
					$scope.brandList = resp.rows;
					//分页模块需要的total
					$scope.paginationConf.totalItems = resp.total;
				})
	}
});