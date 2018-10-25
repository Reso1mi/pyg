//定义模块 引入带分页的模块
var app = angular.module("pyg", ['pagination']);
/*$sce服务器写成过滤器
 */
app.filter('trustHtml', ['$sce', function ($sce) {
    return function (data) {
        return $sce.trustAsHtml(data);
    }
}])
