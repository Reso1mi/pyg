package com.pyg.sellergoods.service;
import java.util.List;
import java.util.Map;

import com.pyg.entity.PageResult;
import com.pyg.pojo.TbSpecification;
import com.pyg.pojogroup.Specification;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SpecificationService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbSpecification> findAll();
	
	/**
	 * 返回select2的格式
	 * @return List<Map>
	 */
	
	List<Map> listSpecList();
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum,int pageSize);
	
	
	/**
	 * 增加
	 * 这里添加的是一个组合对象
	*/
	public void add(Specification specification);
	
	
	/**
	 * 修改
	 * 这里修改返回的也是Specification对象
	 */
	public void update(Specification specification);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 * 
	 * 
	 * 这个需要修改 这里做回显 要把组合对象查询出来注入到 页面的 entity 中
	 */
	public Specification findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long [] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbSpecification specification, int pageNum,int pageSize);
	
}
