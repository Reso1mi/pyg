app.service('indexService',function($http){

	this.getAdminName=function(){
		return $http.get('../shoplogin/name.do');
	}
});