package com.pyg.sellergoods.service;

import java.util.List;
import java.util.Map;

import com.pyg.entity.PageResult;
import com.pyg.entity.PygResult;
import com.pyg.pojo.TbBrand;

public interface BrandService {

	PageResult listBrand(Integer currentPage, Integer pageSize);

	//重载一个条件查询的方法
	PageResult listBrand(Integer currentPage, Integer pageSize,TbBrand brand);
	
	PygResult addBrand(TbBrand brand);

	PygResult modBrand(TbBrand brand);

	//为了编辑时的回显数据
	TbBrand getBrand(long id);
	
	//批量删除，可变参数
	PygResult delete(long ...id);
	
	//返回Map形式的品牌数据
	List<Map> listBrandOption();
}
