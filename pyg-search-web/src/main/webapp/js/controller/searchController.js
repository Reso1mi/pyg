app.controller("searchController", function ($scope, searchService, $location) {

    //接受关键字
    $scope.loadkeywords = function () {
        $scope.searchMap.keywords = $location.search()['keywords'];
        if ($scope.searchMap.keywords != '') {
            $scope.searchItem();
        } else {
            return
        }
    }

    $scope.searchItem = function () {
        //提交到后台可能变成字符串
        $scope.searchMap.pageNo = parseInt($scope.searchMap.pageNo);
        searchService.search($scope.searchMap).success(function (resp) {
            $scope.resultMap = resp;
            //搜索完成后构建分页的标签 放在后面每次构建的时候会报错 查找不到 resuleMap.XXX
            buildPageLable();
        })
    }

    //排序方法
    $scope.sortSearch = function (sortField, sort) {
        $scope.searchMap.sortField = sortField;
        $scope.searchMap.sort = sort;
        $scope.searchItem();
    }

    //搜索对象 Angular实际上就简化了这种过程,前台后台都是面对某一个对象来进行操作(searchMap) 前后台交互都是通过一个对象
    $scope.searchMap = {
        'keywords': '',
        'category': '',
        'brand': '',
        'spec': {},
        'price': '',
        'pageNo': 1,
        'pageSize': 20,
        'sort': '',
        'sortField': ''
    };

    $scope.addSearchItem = function (searchName, searchObj) {
        //这里分页还是重新写一个方法比较好 可以做验证
        if (searchName == 'category' || searchName == 'brand' || searchName == 'price' /*||searchName=='pageNo'*/) {//如果点击的是分类或者是品牌
            $scope.searchMap[searchName] = searchObj;
        } else {
            $scope.searchMap.spec[searchName] = searchObj;
        }
        //查询
        $scope.searchItem();
    }

    $scope.removeSearchItem = function (searchName) {
        if (searchName == 'category' || searchName == 'brand' || searchName === 'price') {//如果点击的是分类或者是品牌
            $scope.searchMap[searchName] = "";
        } else {
            //删除
            delete $scope.searchMap.spec[searchName];
        }
        $scope.searchItem();
    }

    $scope.queryPage = function (page) {
        if (page < 0 || page > $scope.resultMap.totalPages) {
            return
        }
        $scope.searchMap.pageNo = page;
        $scope.searchItem();
    }

    $scope.isFirstPage = function () {
        if ($scope.searchMap.pageNo == 1) {
            return true;
        }
        return false;
    }

    $scope.isEndPage = function () {
        if ($scope.searchMap.pageNo == $scope.resultMap.totalPages) {
            return true;
        }
        return false;
    }

    $scope.isCurrentPage = function (page) {
        if (page == $scope.searchMap.pageNo) {
            return true;
        }
        return false;
    }

    //动态构建分页面板
    buildPageLable = function () {
        $scope.pageLable = [];
        //最大页码
        var maxPage = $scope.resultMap.totalPages;
        //起始页码
        var firstPage = 1;
        //截至页码
        var lastPage = maxPage;
        //小于5条前后都没有省略号
        $scope.firstDot = false;
        $scope.endDot = false;
        //如果大于5也就只显示部分 (页面上最多显示5条数据)
        if (maxPage > 5) {
            if ($scope.searchMap.pageNo <= 3) {
                //只显示前5页
                lastPage = 5;
                //后面有省略号
                $scope.endDot = true;
            } else if ($scope.searchMap.pageNo >= maxPage - 2) {
                //显示后5页
                firstPage = maxPage - 4;
                $scope.firstDot = true;
            } else {
                firstPage = $scope.searchMap.pageNo - 2;
                lastPage = $scope.searchMap.pageNo + 2;
                $scope.firstDot = true;
                $scope.endDot = true;
            }
        }
        //循环构建分页的信息
        for (var i = firstPage; i <= lastPage; i++) {
            $scope.pageLable.push(i);
        }
    }

    //判断是不是品牌
    $scope.keywordsIsBrand = function () {
        for (var i = 0; i < $scope.resultMap.brandList.length; i++) {
            if ($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text) >= 0) {//如果包含
                return true;
            }
        }
        return false;
    }
});