package com.pyg.sellergoods.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pyg.entity.PageResult;
import com.pyg.entity.PygResult;
import com.pyg.mapper.TbBrandMapper;
import com.pyg.pojo.TbBrand;
import com.pyg.pojo.TbBrandExample;
import com.pyg.pojo.TbBrandExample.Criteria;
import com.pyg.sellergoods.service.BrandService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BrandServiceImpl implements BrandService {
	@Autowired
	private TbBrandMapper dao;

	@Override
	public PageResult listBrand(Integer currentPage, Integer pageSize) {
		//开始分页
		PageHelper.startPage(currentPage, pageSize);
		//查询出商品
		List<TbBrand> brands = dao.selectByExample(null);
		//封装分页结果信息
		PageInfo<TbBrand> pageInfo = new PageInfo<>(brands);
		PageResult pageResult = new PageResult(pageInfo.getTotal(), brands)	;
        return pageResult;
	}

	@Override
	public PygResult addBrand(TbBrand brand) {
		//这里做一下判断不能重复 这里就不再数据库家唯一约束了，感觉在数据库加约束很影响性能
		TbBrandExample example = new TbBrandExample();
		example.createCriteria().andNameEqualTo(brand.getName());
		//又犯了这个错。。。返回的是List为空但不为null.
		if (dao.selectByExample(example).isEmpty()) {
			try {
				dao.insert(brand);
				return new PygResult(true, "新建成功");
			} catch (Exception e) {
				return new PygResult(false, "新建失败");
			}
		} else {
			return new PygResult(false, "商品已经存在");
		}
	}

	@Override
	public PygResult modBrand(TbBrand brand) {
		try {
			System.out.println(brand.getId());
			dao.updateByPrimaryKey(brand);
			return new PygResult(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new PygResult(false, "修改失败");
		}
	}

	@Override
	public TbBrand getBrand(long id) {
		return dao.selectByPrimaryKey(id);
	}

	@Override
	public PygResult delete(long... ids) {
		try {
			for (long id : ids) {
				dao.deleteByPrimaryKey(id);
			}
			return new PygResult(true, "删除成功");
		} catch (Exception e) {
			return new PygResult(false, "删除失败");
		}
	}

	@Override
	public PageResult listBrand(Integer currentPage, Integer pageSize, TbBrand brand) {
		//开始分页
		PageHelper.startPage(currentPage, pageSize);
		//查询对应条件的商品
		TbBrandExample example = new TbBrandExample();
		Criteria criteria = example.createCriteria();
		if (brand != null) {
			if (brand.getFirstChar() != null && brand.getFirstChar().length() > 0) {
				criteria.andFirstCharEqualTo(brand.getFirstChar());
			}
			if (brand.getName() != null && brand.getName().length() > 0) {
				criteria.andNameLike("%" + brand.getName() + "%");
			}
		}
		List<TbBrand> brands = dao.selectByExample(example);
		//封装分页结果信息
		PageInfo<TbBrand> pageInfo = new PageInfo<>(brands);
		PageResult pageResult = new PageResult(pageInfo.getTotal(), brands);
		return pageResult;
	}

	@Override
	public List<Map> listBrandOption() {
		return dao.selectOptionList();
	}
}
