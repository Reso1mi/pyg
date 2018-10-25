package com.pyg.sellergoods.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.entity.PageResult;
import com.pyg.mapper.TbItemCatMapper;
import com.pyg.pojo.TbItemCat;
import com.pyg.pojo.TbItemCatExample;
import com.pyg.pojo.TbItemCatExample.Criteria;
import com.pyg.sellergoods.service.ItemCatService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Transactional
@Service
public class ItemCatServiceImpl implements ItemCatService {

	@Autowired
	private TbItemCatMapper itemCatMapper;

	@Autowired
	private RedisTemplate redisTemplate;

	/**
	 * 查询全部
	 */
	@Override
	public List<TbItemCat> findAll() {
		return itemCatMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbItemCat> page = (Page<TbItemCat>) itemCatMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbItemCat itemCat) {
		itemCatMapper.insert(itemCat);
	}

	/**
	 * 修改
	 */
	@Override
	public void update(TbItemCat itemCat) {
		itemCatMapper.updateByPrimaryKey(itemCat);
	}

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbItemCat findOne(Long id) {
		return itemCatMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除   这里要用递归删除
	 */
	@Override
	public void delete(Long[] ids) {
		for (Long id : ids) {
			deleChildren(id);
		}
	}
	//递归删除
	public void deleChildren(long id) {
		TbItemCatExample example = new TbItemCatExample();
		example.createCriteria().andParentIdEqualTo(id);
		//得当当前节点的子节点
		List<TbItemCat> childrenList = itemCatMapper.selectByExample(example);
		for (TbItemCat childrenCat : childrenList) {
			if(hasChildren(childrenCat)) {
				deleChildren(childrenCat.getId());
			}
			//删除子节点
			itemCatMapper.deleteByPrimaryKey(childrenCat.getId());
		}
		//删除当前节点
		itemCatMapper.deleteByPrimaryKey(id);
	}
	
	//判断节点还有没有子节点
	public boolean hasChildren(TbItemCat cat) {
		TbItemCatExample example = new TbItemCatExample();
		example.createCriteria().andParentIdEqualTo(cat.getId());
		//得当当前节点的子节点
		List<TbItemCat> childrenList = itemCatMapper.selectByExample(example);
		return !childrenList.isEmpty();
	}
	
/*	//递归删除
	public void deleChildren(long id) {
		TbItemCatExample example = new TbItemCatExample();
		example.createCriteria().andParentIdEqualTo(id);
		List<TbItemCat> list = itemCatMapper.selectByExample(example);
		if(list.isEmpty()) {
			//删除当前节点
			itemCatMapper.deleteByPrimaryKey(id);
		}else {
			for (TbItemCat tbItemCat : list) {
				deleChildren(tbItemCat.getId());
			}
		}
	}
*/	

	@Override
	public PageResult findPage(TbItemCat itemCat, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);

		TbItemCatExample example = new TbItemCatExample();
		Criteria criteria = example.createCriteria();

		if (itemCat != null) {
			if (itemCat.getName() != null && itemCat.getName().length() > 0) {
				criteria.andNameLike("%" + itemCat.getName() + "%");
			}
		}

		Page<TbItemCat> page = (Page<TbItemCat>) itemCatMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<TbItemCat> findByParentId(long parentId) {
		TbItemCatExample example = new TbItemCatExample();
		example.createCriteria().andParentIdEqualTo(parentId);
		List<TbItemCat> list = itemCatMapper.selectByExample(example);
		//通过分类id缓存下模板id 用于前台的查询（solr）
		List<TbItemCat> all = findAll();
		for (TbItemCat tbItemCat : all) {
			//缓存模板id 分类名为id
			redisTemplate.boundHashOps("catList").put(tbItemCat.getName(),tbItemCat.getTypeId());
			System.out.println("模板id已经放入Redis");
		}
		return list;
	}
}
