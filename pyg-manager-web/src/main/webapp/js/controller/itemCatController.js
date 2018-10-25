//控制层 
app.controller('itemCatController', function($scope, $controller,
		itemCatService, typeTemplateService) {

	$controller('baseController', {
		$scope : $scope
	});// 继承

	$scope.reloadList = function() {
		// alert('覆盖父类的controller'); // 高级查询

	};

	// 读取列表数据绑定到表单中
	$scope.findAll = function() {
		itemCatService.findAll().success(function(response) {
			$scope.list = response;
		});
	}

	// 分页
	$scope.findPage = function(page, rows) {
		itemCatService.findPage(page, rows).success(function(response) {
			$scope.list = response.rows;
			$scope.paginationConf.totalItems = response.total;// 更新总记录数
		});
	}

	// 查询实体
	$scope.findOne = function(id) {
		itemCatService.findOne(id).success(function(response) {
			$scope.entity = response;
		});
	}

	/**
	 * 这里需要两个方法，一个根据id差模版信息，一个根据模板名查询id 直接依赖模板的service
	 * 
	 * 没起到作用，我把问题搞复杂了
	 */

	$scope.getIdByTypeName = function(typeName) {
		typeTemplateService.getIdByTypeName(typeName).success(function(resp) {
			$scope.entity.typeId = resp;
		})
	}

	/*
	 * 浏览器卡死 // 这里改不了 $scope.getTypeNameById = function(id) {
	 * typeTemplateService.getTypeNameById(id).success(function(resp) {
	 * $scope.entity.typeId=resp; }) }
	 */

	// 保存
	$scope.save = function() {
		var serviceObject;// 服务层对象
		if ($scope.entity.id != null) {// 如果有ID
			serviceObject = itemCatService.update($scope.entity); // 修改
		} else {
			$scope.entity.parentId = $scope.parentId;
			serviceObject = itemCatService.add($scope.entity);// 增加
		}
		serviceObject.success(function(response) {
			if (response.success) {
				// 重新查询 这里直接根据上面保留的id查直接点
				$scope.findChildren($scope.parentId);
			} else {
				alert(response.message);
			}
		});
	}

	// 批量删除
	$scope.dele = function() {
		if (confirm("确认要删除id为" + $scope.selectIds + "的分类么")) {
			// 获取选中的复选框
			itemCatService.dele($scope.selectIds).success(function(response) {
				if (response.success) {
					// 同上
					$scope.findChildren($scope.parentId);
					$scope.selectIds = [];
				}
			});
		}
	}

	/*
	 * 一个失败的方法,主要是在页面上遍历时有点问题 $scope.topEntity={'name':'顶级分类列表','id':'0'};
	 * 
	 * $scope.nodeList=[$scope.topEntity];
	 * 
	 * $scope.addToNodeList =function(nodeEntity){
	 * $scope.nodeList.push(nodeEntity); alert($scope.nodeList.length); }
	 */

	$scope.grade = 0;// 默认的级别是从0开始的（顶级分类）

	// 写死的方法 low 可惜上面的方法没实现
	$scope.setGrade = function(grade) {
		$scope.grade = grade;
	}

	$scope.showBrandList = function(entity) {
		// 定义一个变量再这里保存这个节点的模板id
		$scope.typeId = entity.typeId;
		if ($scope.grade == 0) {
			$scope.entity_1 = null;
			$scope.entity_2 = null;
		}
		if ($scope.grade == 1) {
			$scope.entity_1 = entity;
			$scope.entity_2 = null;
		}
		if ($scope.grade == 2) {
			$scope.entity_2 = entity;
		}
		$scope.findChildren(entity.id);
	}

	// 读取子节点的信息
	$scope.findChildren = function(parentId) {
		// 在这里自己保存好父节点的id为了后面的保存就不用添加这个了
		$scope.parentId = parentId;
		itemCatService.findChildren(parentId).success(function(resp) {
			$scope.list = resp;
		});
	}

	// 初始化下拉列表的分类模板
	$scope.typeList = {
		data : []
	};
	// 查询并赋值
	$scope.listTypeOption = function() {
		typeTemplateService.listTypeOption().success(function(resp) {
			$scope.typeList = {
				data : resp
			};
		})
	};

	// 搜索 (分页模块删除了这里加分页就比较麻烦了而且也没必要，一级分类也没有很多数据)
	// 所以这个方法就不会执行了
	$scope.search = function(page, rows, parentId) {
		itemCatService.search(page, rows, parentId, $scope.searchEntity)
				.success(function(response) {
					$scope.list = response.rows;
					$scope.paginationConf.totalItems = response.total;// 更新总记录数
				});
	}
});
