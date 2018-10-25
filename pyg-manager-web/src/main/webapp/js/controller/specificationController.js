//控制层 
app.controller('specificationController', function($scope, $controller,
		specificationService) {

	$controller('baseController', {
		$scope : $scope
	});// 继承

	// 读取列表数据绑定到表单中
	$scope.findAll = function() {
		specificationService.findAll().success(function(response) {
			$scope.list = response;
		});
	}

	/**
	 * 增加行
	 */
	$scope.addTableRow = function() {
		$scope.entity.specificationOptionList.push({});
	}

	/**
	 * 删除行
	 */
	$scope.delTableRow = function(index) {
		$scope.entity.specificationOptionList.splice(index, 1);
	}

	// 分页
	$scope.findPage = function(page, rows) {
		specificationService.findPage(page, rows).success(function(response) {
			$scope.list = response.rows;
			$scope.paginationConf.totalItems = response.total;// 更新总记录数
		});
	}

	// 查询实体
	$scope.findOne = function(id) {
		specificationService.findOne(id).success(function(response) {
			$scope.entity = response;
		});
	}

	// 保存
	$scope.save = function() {
		var serviceObject;// 服务层对象
		if ($scope.entity.specification.id != null) {// 如果有ID
			serviceObject = specificationService.update($scope.entity); // 修改
		} else {
			serviceObject = specificationService.add($scope.entity);// 增加
		}
		serviceObject.success(function(response) {
			if (response.success) {
				alert(response.message);
				// 重新查询
				$scope.reloadList();// 重新加载
			} else {
				alert(response.message);
			}
		});
	}

	// 批量删除
	$scope.dele = function() {
		// 获取选中的复选框
		if (confirm("确认要删除id为" + $scope.selectIds + "的规格么")) {
			specificationService.dele($scope.selectIds).success(
					function(response) {
						if (response.success) {
							alert(response.message);
							$scope.reloadList();// 刷新列表
							$scope.selectIds = [];
						}
					});
		}
	}

	/*
	 * $scope.searchEntity = {};// 定义搜索对象 在父类中定义了 主要是为了在进入方法的时候加载列表
	 * searchEntity已经初始化
	 */
	// 搜索
	$scope.search = function(page, rows) {
		specificationService.search(page, rows, $scope.searchEntity).success(
				function(response) {
					$scope.list = response.rows;
					$scope.paginationConf.totalItems = response.total;// 更新总记录数
				});
	}
});
