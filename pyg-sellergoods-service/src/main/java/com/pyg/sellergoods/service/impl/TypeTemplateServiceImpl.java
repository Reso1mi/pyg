package com.pyg.sellergoods.service.impl;

import java.util.List;
import java.util.Map;
/*这个JSON工具是dubbo里面的不是FastJson*/
//import com.alibaba.dubbo.common.json.JSON;
import com.alibaba.fastjson.JSON;
import com.pyg.mapper.TbSpecificationOptionMapper;
import com.pyg.pojo.TbSpecificationOption;
import com.pyg.pojo.TbSpecificationOptionExample;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.entity.PageResult;
import com.pyg.mapper.TbTypeTemplateMapper;
import com.pyg.pojo.TbTypeTemplate;
import com.pyg.pojo.TbTypeTemplateExample;
import com.pyg.pojo.TbTypeTemplateExample.Criteria;
import com.pyg.sellergoods.service.TypeTemplateService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
@Transactional
public class TypeTemplateServiceImpl implements TypeTemplateService {

    @Autowired
    private TbTypeTemplateMapper typeTemplateMapper;

    //依赖下对应规格详情的mapper
    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 查询全部
     */
    @Override
    public List<TbTypeTemplate> findAll() {
        return typeTemplateMapper.selectByExample(null);
    }

    /**
     * 下拉列表
     */
    @Override
    public List<Map> listTypeOptions() {
        return typeTemplateMapper.listTypeOptions();
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbTypeTemplate> page = (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbTypeTemplate typeTemplate) {
        typeTemplateMapper.insert(typeTemplate);
    }

    /**
     * 修改
     */
    @Override
    public void update(TbTypeTemplate typeTemplate) {
        typeTemplateMapper.updateByPrimaryKey(typeTemplate);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbTypeTemplate findOne(Long id) {
        return typeTemplateMapper.selectByPrimaryKey(id);
    }


    //新增加的方法,将规格的详细信息封装到要返回的规格中
    @Override
    public List<Map> getSpecList(long typeId) {

        TbTypeTemplate typeTemplate=typeTemplateMapper.selectByPrimaryKey(typeId);
        //得到对应规格的字符串
        String specIds = typeTemplate.getSpecIds();
        //转换成JSON对象
        List<Map> specList=JSON.parseArray(specIds,Map.class);

        //遍历这个List然后在每个Map的后面增加一个option的元素用来存放
        // 对应规格的详细信息
        for (Map spec:specList) {
             //得到对应的specId再根据id查询对应的规格详情表
             long specId=(Integer)spec.get("id");
            TbSpecificationOptionExample example=new TbSpecificationOptionExample();
            example.createCriteria().andSpecIdEqualTo(specId);
            List<TbSpecificationOption> specificationOptions=specificationOptionMapper.selectByExample(example);
            spec.put("option",specificationOptions);
        }
        return specList;
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            typeTemplateMapper.deleteByPrimaryKey(id);
        }
    }


    /**
     * 缓存规格和品牌
     */
    private void saveBrandAndSpec2Redis(){
        //缓存品牌和规格
        System.out.println("品牌规格已经存入Redis");
        List<TbTypeTemplate> templates = findAll();
        for (TbTypeTemplate template : templates) {
            //根据模板信息缓存品牌信息
            List<Map> brandList= JSON.parseArray(template.getBrandIds(), Map.class);
            redisTemplate.boundHashOps("brandList").put(template.getId(),brandList);
            //借用之前写的根据类型id查询 规格并加入list的方法
            List<Map> specList = getSpecList(template.getId());
            redisTemplate.boundHashOps("specList").put(template.getId(),specList);
        }
    }



    @Override
    public PageResult findPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbTypeTemplateExample example = new TbTypeTemplateExample();
        Criteria criteria = example.createCriteria();

        if (typeTemplate != null) {
            if (typeTemplate.getName() != null && typeTemplate.getName().length() > 0) {
                criteria.andNameLike("%" + typeTemplate.getName() + "%");
            }
            if (typeTemplate.getSpecIds() != null && typeTemplate.getSpecIds().length() > 0) {
                criteria.andSpecIdsLike("%" + typeTemplate.getSpecIds() + "%");
            }
            if (typeTemplate.getBrandIds() != null && typeTemplate.getBrandIds().length() > 0) {
                criteria.andBrandIdsLike("%" + typeTemplate.getBrandIds() + "%");
            }
            if (typeTemplate.getCustomAttributeItems() != null && typeTemplate.getCustomAttributeItems().length() > 0) {
                criteria.andCustomAttributeItemsLike("%" + typeTemplate.getCustomAttributeItems() + "%");
            }
        }
        //没次更改模板信息后都会调用这个方法（刷新页面）
        saveBrandAndSpec2Redis();
        Page<TbTypeTemplate> page = (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    //这两个都没用了
    @Override
    public long getIdByTypeName(String name) {
        System.out.println(name);
        TbTypeTemplateExample example = new TbTypeTemplateExample();
        example.createCriteria().andNameEqualTo(name);
        List<TbTypeTemplate> list = typeTemplateMapper.selectByExample(example);
        return list.get(0).getId();
    }

    @Override
    public String getTypeNameById(long id) {
        TbTypeTemplate typeTemplate = typeTemplateMapper.selectByPrimaryKey(id);
        return typeTemplate.getName();
    }
}
