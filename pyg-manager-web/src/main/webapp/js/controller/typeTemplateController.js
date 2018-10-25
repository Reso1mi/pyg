//控制层    还要依赖下brandService和specificationService
app.controller('typeTemplateController', function($scope, $controller,
		typeTemplateService, brandService, specificationService) {

	$controller('baseController', {
		$scope : $scope
	});// 继承

	// 读取列表数据绑定到表单中
	$scope.findAll = function() {
		typeTemplateService.findAll().success(function(response) {
			$scope.list = response;
		});
	}

	// 分页
	$scope.findPage = function(page, rows) {
		typeTemplateService.findPage(page, rows).success(function(response) {
			$scope.list = response.rows;
			$scope.paginationConf.totalItems = response.total;// 更新总记录数
		});
	}

	$scope.addTableRow = function() {
		$scope.entity.customAttributeItems.push({});
	}

	/**
	 * 删除行
	 */
	$scope.delTableRow = function(index) {
		$scope.entity.customAttributeItems.splice(index, 1);
	}

	// 查询实体
	$scope.findOne = function(id) {
		typeTemplateService.findOne(id)
				.success(
						function(response) {
							$scope.entity = response;
							$scope.entity.brandIds = JSON
									.parse($scope.entity.brandIds);// 转换品牌列表
							$scope.entity.specIds = JSON
									.parse($scope.entity.specIds);// 转换规格列表
							$scope.entity.customAttributeItems = JSON
									.parse($scope.entity.customAttributeItems);
			});
	}

	// 保存
	$scope.save = function() {
		var serviceObject;// 服务层对象
		if ($scope.entity.id != null) {// 如果有ID
			serviceObject = typeTemplateService.update($scope.entity); // 修改
		} else {
			serviceObject = typeTemplateService.add($scope.entity);// 增加
		}
		serviceObject.success(function(response) {
			if (response.success) {
				// 重新查询
				alert(response.message);
				$scope.reloadList();// 重新加载
			} else {
				alert(response.message);
			}
		});
	}

	// 批量删除
	$scope.dele = function() {
		// 获取选中的复选框
		if (confirm("确认要删除id为" + $scope.selectIds + "的模板么")) {
			typeTemplateService.dele($scope.selectIds).success(
					function(response) {
						if (response.success) {
							$scope.reloadList();// 刷新列表
							$scope.selectIds = [];
						}
					})
		}
	}

	// 初始化下拉列表的品牌
	$scope.brandList = {
		data : []
	};
	// 查询并赋值
	$scope.listBrandOption = function() {
		brandService.listBrandOption().success(function(resp) {
			$scope.brandList = {
				data : resp
			};
		})
	};
	// 初始化下拉列表的规格选项
	$scope.specList = {
		data : []
	}
	$scope.listSpecOption = function() {
		specificationService.listSpecOption().success(function(resp) {
			$scope.specList = {
				data : resp
			};
		})
	};

	$scope.searchEntity = {};// 定义搜索对象
	// 搜索
	$scope.search = function(page, rows) {
		typeTemplateService.search(page, rows, $scope.searchEntity).success(
				function(response) {
					$scope.list = response.rows;
					$scope.paginationConf.totalItems = response.total;// 更新总记录数
				});
	}

});
