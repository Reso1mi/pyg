app.controller('indexController', function($scope, $controller, indexService) {

	/*
	 * $controller('baseController',{$scope:$scope});//继承
	 */$scope.getAdminName = function() {
		indexService.getAdminName().success(function(resp) {
			$scope.name = resp.loginName;
		});
	}
});