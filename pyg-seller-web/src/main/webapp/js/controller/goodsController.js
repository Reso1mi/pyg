//控制层
app.controller('goodsController', function ($scope, $location, $controller, goodsService, typeTemplateService, uploadService, itemCatService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    };

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
    $scope.entity = {goods: {}, goodsDesc: {itemImages: [], specificationItems: []}};

    $scope.updateSpecification = function ($event, name, value) {
        //首先查询json里面有没有这个规格名字
        var specEntity = $scope.searchObject($scope.entity.goodsDesc.specificationItems, "attributeName", name);

        //如果对应的规格选项为null就要添加整个规格进去
        if (specEntity != null) {
            //如果是选中操作
            if ($event.target.checked) {
                specEntity.attributeValue.push(value);
            } else {
                //取消勾选
                specEntity.attributeValue.splice(specEntity.attributeValue.indexOf(value), 1);
                //所有的规格都取消了就删除整个spec
                if (specEntity.attributeValue.length == 0) {
                    $scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(specEntity), 1);
                }
            }
        } else {
            //和第一步的查询对应
            $scope.entity.goodsDesc.specificationItems.push({'attributeName': name, 'attributeValue': [value]});
        }
    }

    //比较复杂的地方,生成SKU列表
    // [{"spec":{"网络":"移动3G","机身内存":"16G"},"price":0,"num":9999,"status":"0","isDefault":"0"},
    // {"spec":{"网络":"移动3G","机身内存":"32G"},"price":0,"num":9999,"status":"0","isDefault":"0"},
    // {"spec":{"网络":"移动4G","机身内存":"16G"},"price":0,"num":9999,"status":"0","isDefault":"0"},
    // {"spec":{"网络":"移动4G","机身内存":"32G"},"price":0,"num":9999,"status":"0","isDefault":"0"}]
    // 没点击一次都是重新生成sku信息并不是增加列增加行
    $scope.creatItemList = function () {
        //定义号list集合的信息，也就是之前在pojo里面写好的实体类ItemList<TbItem>
        $scope.entity.itemList = [{spec: {}, price: 0, num: 9999, status: '0', isDefault: '0'}];
        //规格列表
        var specs = $scope.entity.goodsDesc.specificationItems;
        //  [{“attributeName”:”规格名称”,”attributeValue”:[“规格选项1”,“规格选项2”.... ]  } , ....  ]
        //遍历规格列表
        for (var i = 0; i < specs.length; i++) {
            //遍历sku列表
            //这边还要定义一个空集合,循环完之后赋值给itemList
            var newList = [];
            for (var j = 0; j < $scope.entity.itemList.length; j++) {
                var oldRow = $scope.entity.itemList[j];
                for (var k = 0; k < specs[i].attributeValue.length; k++) {
                    //深克隆
                    var newRow = JSON.parse(JSON.stringify(oldRow));
                    //赋值(改值)
                    newRow.spec[specs[i].attributeName] = specs[i].attributeValue[k];
                    //添加到itemList中去  （不能直接添加，直接添加那之前的记录没增加列的row就还存在于itemList中）
                    newList.push(newRow);
                }
            }
            $scope.entity.itemList = newList;
        }
    }


    //初始化图片对象
    $scope.entity_image = {};
    //图片列表
    $scope.addImageItem = function () {
        //将刚才上传的图片添加到数组里面
        $scope.entity.goodsDesc.itemImages.push($scope.entity_image);
    };

    //前台页面传递数组的下标
    $scope.deleImageItem = function (index) {
        //删除列表的图片
        $scope.entity.goodsDesc.itemImages.splice(index, 1);
    };


    /*上传文件*/
    $scope.uploadFile = function () {
        uploadService.uploadFile().success(function (resp) {
            if (resp.success) {
                $scope.entity_image.url = resp.message;
            } else {
                alert(resp.message);
            }
        }).error(function () {
            alert("上传发生错误");
        })
    };

    // 这里是吧typeTemplate的方法考过来的 这里就不依赖brandService了 只需要其中的一个方法 感觉依赖整个不太好
    // 也降低了代码的耦合性
    // 以上说法都是扯淡   AJAX不能跨域 这个品牌是后台另一个域9100 请求不到 只能再后台的controller里面加一个新方法了
    // 初始化下拉列表的品牌
    // $scope.brandList = {
    //     data: []
    // };
    // // 查询并赋值
    // $scope.getBrandOption = function () {
    //     goodsService.getBrandOption().success(function (resp) {
    //         $scope.brandList = {
    //             data: resp
    //         };
    //     })
    // };

    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    };

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

    //保存
    $scope.save = function () {
        //手动把添加的富文本编辑器的聂荣添加到entity中
        $scope.entity.goodsDesc.introduction = editor.html();
        var serviceObject;//服务层对象
        if ($scope.entity.goods.id != null) {//如果有ID
            serviceObject = goodsService.update($scope.entity); //修改
        } else {
            serviceObject = goodsService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    //这里成功后应该提示下 然后清空实体 便于增加下一件商品
                    alert(response.message);
                    $scope.entity = {};
                    //这是一个全局变量
                    editor.html('');//清空富文本编辑器
                    //$scope.reloadList();//重新加载
                } else {
                    alert(response.message);
                }
            }
        );
    };


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    };

    $scope.isMarket=['已下架','已上架'];
    //商家下架实际上和删除比较类似
    $scope.upShelf = function () {
        //获取选中的复选框
        goodsService.upShelf($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    };

    //下架
    $scope.downShelf= function () {
        //获取选中的复选框
        goodsService.downShelf($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    };

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

    $scope.searchEntity = {};//定义搜索对象

    //定义下商品的状态数组
    $scope.status = ['未审核', '已审核', '审核未通过', '关闭'];//商品状态

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
