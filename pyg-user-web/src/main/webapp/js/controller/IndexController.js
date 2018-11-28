app.controller("indexController", function ($scope, loginService) {
    $scope.getName = function () {
        loginService.getName().success(function (resp) {
            $scope.loginName=resp.loginName;
        })
    }
})