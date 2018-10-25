package com.pyg.sellergoods.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.entity.PageResult;
import com.pyg.mapper.TbSpecificationMapper;
import com.pyg.mapper.TbSpecificationOptionMapper;
import com.pyg.pojo.TbSpecification;
import com.pyg.pojo.TbSpecificationExample;
import com.pyg.pojo.TbSpecificationExample.Criteria;
import com.pyg.pojo.TbSpecificationOption;
import com.pyg.pojo.TbSpecificationOptionExample;
import com.pyg.pojogroup.Specification;
import com.pyg.sellergoods.service.SpecificationService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;

	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;

	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Specification specification) {
		//从组合对象中获取规格对象插入数据库 这里会把获取的主键set到对象中
		TbSpecification tbSpecification = specification.getSpecification();
		specificationMapper.insert(tbSpecification);
		List<TbSpecificationOption> optionList = specification.getSpecificationOptionList();
		for (TbSpecificationOption tbSpecificationOption : optionList) {
			//把获取的id设置为specificationOption的SpecId 相当于外键
			tbSpecificationOption.setSpecId(tbSpecification.getId());
			specificationOptionMapper.insert(tbSpecificationOption);
		}
	}

	/**
	 * 修改
	 */
	@Override
	public void update(Specification specification) {
		/*
		 * 这样做只能保证修改时增加和修改规格 不能保证删除某一个规格 这里也没办法判断了
		 * 最简单的就是把原来的全部删除再重新插入一遍
		 * TbSpecification tbSpecification = specification.getSpecification();
		specificationMapper.updateByPrimaryKey(tbSpecification);
		List<TbSpecificationOption> optionList = specification.getSpecificationOptionList();
		for (TbSpecificationOption tbSpecificationOption : optionList) {
			if(tbSpecificationOption.getId()==null) {
				tbSpecificationOption.setSpecId(tbSpecification.getId());
				specificationOptionMapper.insert(tbSpecificationOption);
				break;
			}
			specificationOptionMapper.updateByPrimaryKey(tbSpecificationOption);
		}*/
		//规格参数
		TbSpecification tbSpecification = specification.getSpecification();
		specificationMapper.updateByPrimaryKey(tbSpecification);
		//先删除当前规格下的所有规格参数
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		example.createCriteria().andSpecIdEqualTo(tbSpecification.getId());
		specificationOptionMapper.deleteByExample(example);
		//再将新的规格插入进数据库
		List<TbSpecificationOption> optionList = specification.getSpecificationOptionList();
		for (TbSpecificationOption tbSpecificationOption : optionList) {
			//与增加规格时一样
			tbSpecificationOption.setSpecId(tbSpecification.getId());
			specificationOptionMapper.insert(tbSpecificationOption);
		}
	}

	/**
	 * 根据ID获取实体
	 * @param id 这个id是规格的id要根据这个id把规格细节的表里面的数据也查询出来
	 * @return
	 */
	@Override
	public Specification findOne(Long id) {
		TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		example.createCriteria().andSpecIdEqualTo(tbSpecification.getId());
		//查询规格细节
		List<TbSpecificationOption> optionList = specificationOptionMapper.selectByExample(example);
		Specification specification = new Specification();

		specification.setSpecification(tbSpecification);
		specification.setSpecificationOptionList(optionList);
		return specification;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for (Long id : ids) {
			specificationMapper.deleteByPrimaryKey(id);
			//要连同规格参数一起删除
			TbSpecificationOptionExample example = new TbSpecificationOptionExample();
			example.createCriteria().andSpecIdEqualTo(id);
			specificationOptionMapper.deleteByExample(example);
		}
	}

	@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);

		TbSpecificationExample example = new TbSpecificationExample();
		Criteria criteria = example.createCriteria();

		if (specification != null) {
			if (specification.getSpecName() != null && specification.getSpecName().length() > 0) {
				criteria.andSpecNameLike("%" + specification.getSpecName() + "%");
			}
		}

		Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<Map> listSpecList() {
		return specificationMapper.selectSpecList();
	}

}
