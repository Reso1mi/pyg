app.service("contentService",function ($http) {
    this.getContentList=function (id) {
        return $http.get("/content/getContentList.do?catId="+id);
    }
})