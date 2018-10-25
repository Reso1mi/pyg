//服务层
app.service('goodsService',function($http){
	
	//**************************************************************    	
	// //模板的下拉列表数据
	// this.getBrandOption =function(id){
	// 	return $http.get("../goods/getBrandOption.do?id="+id);
	// }
	//**************************************************************
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../goods/findAll.do');		
	}
	//分页 
	this.findPage=function(page,rows){
		return $http.get('../goods/findPage.do?page='+page+'&rows='+rows);
	}
	//查询实体
	this.findOne=function(id){
		return $http.get('../goods/findOne.do?id='+id);
	}
	//增加 
	this.add=function(entity){
		return  $http.post('../goods/add.do',entity );
	}
	//修改 
	this.update=function(entity){
		return  $http.post('../goods/update.do',entity );
	}
	//删除
	this.dele=function(ids){
		return $http.get('../goods/delete.do?ids='+ids);
	}
	//搜索
	this.search=function(page,rows,searchEntity){
		return $http.post('../goods/search.do?page='+page+"&rows="+rows, searchEntity);
	}
	//上架
	this.upShelf=function (ids) {
		return $http.get('../goods/upShelf.do?ids='+ids);
    }
    //下架
    this.downShelf=function (ids) {
        return $http.get('../goods/downShelf.do?ids='+ids);
    }
});
