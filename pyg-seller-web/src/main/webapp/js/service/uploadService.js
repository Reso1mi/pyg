app.service("uploadService", function ($http) {
    this.uploadFile = function () {
        var formData = new FormData();
        formData.append("file", file.files[0]);
        return $http(
            {
                method: 'POST',
                url: "../upload.do",
                data: formData,
                /*angularJs默认的post，get请求头都是application/json 设置为undefined就会设置为multipart/form-data*/
                headers: {'Content-Type': undefined},
                /*序列化表单对象*/
                transformRequest: angular.identity
            }
        );
    }
})