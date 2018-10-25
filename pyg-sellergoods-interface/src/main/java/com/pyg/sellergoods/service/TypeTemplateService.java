package com.pyg.sellergoods.service;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import com.pyg.entity.PageResult;
import com.pyg.pojo.TbTypeTemplate;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface TypeTemplateService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbTypeTemplate> findAll();
	
	/**
	 * 返回分类的下拉列表
	 */
	public List<Map> listTypeOptions();	
	
	
	/**
	 * itemCat需要的两个方法
	 */
	public long getIdByTypeName(String name);

	public String getTypeNameById(long id);
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum,int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(TbTypeTemplate typeTemplate);
	
	
	/**
	 * 修改
	 */
	public void update(TbTypeTemplate typeTemplate);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbTypeTemplate findOne(Long id);


	/**
	 * 增加的获取规格和规格信息的方法
	 */

	public List<Map> getSpecList(long typeId);


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
	public PageResult findPage(TbTypeTemplate typeTemplate, int pageNum,int pageSize);
	
}
