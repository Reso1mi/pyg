/**
 * 品牌管理业务层
 */
//定义业务层
	app.service("brandService", function($http) {
		//分页显示品牌列表
		this.listBrand = function(currentPage, pageSize) {
			return $http.get("../brand/listBrand.do?currentPage=" + currentPage
					+ "&pageSize=" + pageSize);
		};
		//查询单个品牌（编辑时回显数据）
		this.getBrand = function(id) {
			return $http.get("../brand/getBrand.do?id=" + id);
		};
		//添加品牌
		this.addBrand = function(entity) {
			return $http.post("../brand/addBrand.do", entity);
		};
		//修改品牌
		this.modBrand = function(entity) {
			return $http.post("../brand/modBrand.do", entity);
		};
		//删除品牌
		this.delBrand = function(ids) {
			return $http.get("../brand/delBrand.do?ids=" + ids);
		};
		//高级查询
		this.searchBrand = function(currentPage, pageSize, entity) {
			return $http.post("../brand/searchBrand.do?currentPage=" + currentPage
					+ "&pageSize=" + pageSize, entity);
		};
		//模板的下拉列表数据
		this.listBrandOption =function(){
			return $http.get("../brand/listBrandOption.do");
		}
	})