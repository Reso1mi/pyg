app.controller("contentController",function ($scope,contentService,$controller) {
    //伪继承，只是让scope连通
    $controller("baseController", {
        $scope : $scope
    });

    //根据id将对应分类的广告加载出来
    $scope.contentList=[];
    //调用次数取决于广告发分类的数量
    $scope.getContentList=function(id){
        contentService.getContentList(id).success(function (resp) {
            $scope.contentList[id]=resp;
        })
    }

    //搜索
    $scope.search=function(){
        location.href="http://localhost:9103/search.html#?keywords="+$scope.keywords;
    }

})