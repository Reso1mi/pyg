//控制层
app.controller('goodsController', function ($scope, $location,$controller, itemCatService, goodsService,typeTemplateService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //保存
    $scope.save = function () {
        var serviceObject;//服务层对象
        if ($scope.entity.id != null) {//如果有ID
            serviceObject = goodsService.update($scope.entity); //修改
        } else {
            serviceObject = goodsService.add($scope.entity); //增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    //重新查询
                    $scope.reloadList();//重新加载
                } else {
                    alert(response.message);
                }
            }
        );
    }

    $scope.updateStatus = function (status) {
        goodsService.updateStatus( $scope.selectIds, status).success(function (resp) {
                    if(resp.success){
                        $scope.reloadList();
                        //清空
                        $scope.selectIds=[];
                    }else{
                        alert(resp.message);
                    }
            }
        )
    }


    //批量删除
    $scope.dele = function () {
        if (confirm("确认要删除id为" + $scope.selectIds + "的商品么")) {
            //获取选中的复选框
            goodsService.dele($scope.selectIds).success(
                function (response) {
                    if (response.success) {
                        $scope.reloadList();//刷新列表
                        $scope.selectIds = [];
                    }
                }
            );
        }
    }
    //定义下商品的状态数组
    $scope.status = ['未审核', '已审核', '审核未通过', '关闭'];//商品状态

    //分类列表名
    $scope.itemCatName = [];

    //将所有分类查询出来 将id和name对应
    $scope.getItemCatName = function () {
        itemCatService.findAll().success(function (resp) {
            for (var i = 0; i < resp.length; i++) {
                $scope.itemCatName[resp[i].id] = resp[i].name;
            }
        })
    }

    //自己实现的，查询商品详情，先把商家后台的代码拷贝过来再改改
    //一级分类列表初始化
    $scope.selectItemList = function () {
        //查询顶级分类
        itemCatService.findChildren(0).success(function (resp) {
            $scope.itemList = resp;
        });
    };

    //二级分类初始化
    $scope.$watch('entity.goods.category1Id', function (newVal, oldVal) {
        itemCatService.findChildren(newVal).success(function (resp) {
            $scope.itemList2 = resp;
        })
    });

    //三级分类列表
    $scope.$watch('entity.goods.category2Id', function (newVal, oldVal) {
        itemCatService.findChildren(newVal).success(function (resp) {
            $scope.itemList3 = resp;
        })
    });

    //三级分类确定后添加分类模板信息
    $scope.$watch('entity.goods.category3Id', function (newVal, oldVal) {
        itemCatService.findOne(newVal).success(function (resp) {
            $scope.entity.goods.typeTemplateId = resp.typeId;
        })
    });


    //三级分类确定后要根据上面的到的分类模板信息获得对应的品牌的信息
    $scope.brandList = {};
    $scope.$watch('entity.goods.typeTemplateId', function (newVal, oldVal) {
        typeTemplateService.findOne(newVal).success(function (resp) {
            $scope.brandList = JSON.parse(resp.brandIds);
            //这里查询不通用的规格信息，就是额外增加的信息
            if ($location.search()['id'] == null) {
                //有id修改没id增加，避免重复加载不出来
                $scope.entity.goodsDesc.customAttributeItems = JSON.parse(resp.customAttributeItems);
            }
        })
        //加载通用的规格
        typeTemplateService.getSpecList(newVal).success(function (resp) {
            $scope.specList = resp;
        })
    });


    //先初始化一个实体的Goods对象（Goods和GoodsDesc的组合对像）
    $scope.entity = {goods: {}, goodsDesc: {itemImages: [], specificationItems: []},itemList:{}};

    //$scope.entity.itemList = [{spec: {}, price: 0, num: 9999, status: '0', isDefault: '0'}];
    //查询实体
    $scope.findOne = function () {
        //得到URL中传递过来的参数 取id
        var id = $location.search()['id'];
        if (id == null) {
            return;
        }
        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
                editor.html($scope.entity.goodsDesc.introduction);
                //显示图片列表 在后台查询出来的JSON串要解析还原
                $scope.entity.goodsDesc.itemImages =
                    JSON.parse($scope.entity.goodsDesc.itemImages);
                $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.entity.goodsDesc.customAttributeItems);
                $scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);
                for (var i=0;i<$scope.entity.itemList.length;i++){
                    $scope.entity.itemList[i].spec=JSON.parse($scope.entity.itemList[i].spec);
                }
            }
        );
    };

    $scope.checkIsSelect=function(specName,optionName){
        //后台查询出来的规格列表
        var specList=$scope.entity.goodsDesc.specificationItems;
        //查询是否又饿这个规格specName
        var spec=$scope.searchObject(specList,"attributeName",specName);
        if(spec==null){
            return null;
        }
        if(spec.attributeValue.indexOf(optionName)>=0){
            return true;
        }
        return false;
    }

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }
});
