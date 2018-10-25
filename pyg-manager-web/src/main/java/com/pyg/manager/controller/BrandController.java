package com.pyg.manager.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.entity.PageResult;
import com.pyg.entity.PygResult;
import com.pyg.pojo.TbBrand;
import com.pyg.sellergoods.service.BrandService;

@Controller
@RequestMapping("/brand")
public class BrandController {

	@Reference
	private BrandService service;
	
	@Deprecated
	@RequestMapping("/listBrand")
	@ResponseBody
	public PageResult listBrand(Integer currentPage,Integer pageSize){
		return service.listBrand(currentPage,pageSize);
	}
	
	//模板编辑时的下拉列表的数据
	@RequestMapping("/listBrandOption")
	@ResponseBody
	public List<Map> listBrand(){
		return service.listBrandOption();
	}
	
	
	@RequestMapping("/searchBrand")
	@ResponseBody
	public PageResult searchBrand(Integer currentPage,Integer pageSize,@RequestBody TbBrand brand){
		return service.listBrand(currentPage,pageSize,brand);
	}
	
	@RequestMapping("/addBrand")
	@ResponseBody
	public PygResult addBrand(@RequestBody/*SpringMVC post提交表单封装为Brand 不加这个有时会封装不了*/TbBrand brand) {
		return service.addBrand(brand);
	}
	
	@RequestMapping("/getBrand")
	@ResponseBody
	public TbBrand getBrand(long id) {
		return service.getBrand(id);
	}
	
	@RequestMapping("/modBrand")
	@ResponseBody
	public PygResult modBrand(@RequestBody TbBrand brand) {
		return service.modBrand(brand);
	}
	
	@RequestMapping("/delBrand")
	@ResponseBody
	public PygResult delBrand(long ...ids) {
		return service.delete(ids);
	}
}
