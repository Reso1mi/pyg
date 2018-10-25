package com.pyg.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pyg.pojo.TbItem;
import com.pyg.search.service.ItemSearchService;
import org.apache.solr.common.util.Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(timeout = 5000)
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {
        //处理空格
        String keywords = (String) searchMap.get("keywords");
        searchMap.put("keywords", keywords.replace(" ", ""));
        Map<String, Object> map = new HashMap<>();
        //高亮查询
        map.putAll(searchList(searchMap));
        //分类查询
        List<String> catList = searchCatList(searchMap);
        map.put("catList", catList);
        //查询规格和品牌列表
        /*
            每次点击页面上的分类都会重新刷新，这样写没有意义，默认加载第一个的就可以
            for (String s : catList) {
            Map brandAndSpec = searchBrandAndSpec(s);
            map.putAll(brandAndSpec);
        }*/
        if (!"".equals(searchMap.get("category"))) {
            map.putAll(searchBrandAndSpec((String) searchMap.get("category")));
        } else if (catList.size() > 0) {
            map.putAll(searchBrandAndSpec(catList.get(0)));
        }
        return map;
    }

    /**
     * 数据导入
     * @param itemList
     */
    @Override
    public void import2Solr(List<TbItem> itemList) {
        System.out.println("导入数据到solr库中");
        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();
    }

    /**
     * 数据删除
     * @param goodsIdList
     */
    @Override
    public void deleteByGoodsIds(List goodsIdList) {
        System.out.println("删除商品ID"+goodsIdList);
        Query query=new SimpleQuery();
        Criteria criteria=new Criteria("item_goodsid").in(goodsIdList);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    /**
     * Redis 里面取品牌和规格 （按道理品牌也可以从Solr里面取）
     *
     * @param cat
     * @return
     */
    private Map searchBrandAndSpec(String cat) {
        HashMap<String, Object> map = new HashMap<>();
        //先从redis里面取到分类模板id
        Long typeId = (Long) redisTemplate.boundHashOps("catList").get(cat);
        if (typeId != null) {
            //再根据模板id取品牌和规格信息（是他们的key）
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
            List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
            map.put("brandList", brandList);
            map.put("specList", specList);
        }
        return map;
    }

    /**
     * Solr 分组查询 查询分类的信息
     *
     * @param searchMap
     * @return
     */
    private List<String> searchCatList(Map searchMap) {
        List<String> list = new ArrayList<>();
        Query query = new SimpleQuery();
        //查询条件
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //设置分组信息
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        //得到分组页对象
        GroupPage<TbItem> tbItems = solrTemplate.queryForGroupPage(query, TbItem.class);
        //根据分组列得到分组结果集 (item_category)
        GroupResult<TbItem> item_category = tbItems.getGroupResult("item_category");
        //得到入口页
        Page<GroupEntry<TbItem>> groupEntries = item_category.getGroupEntries();
        for (GroupEntry<TbItem> groupEntry : groupEntries) {
            String groupValue = groupEntry.getGroupValue();
            groupEntry.getResult();
            list.add(groupValue);
        }
        return list;
    }


    /**
     * Solr商品关键字搜索并高亮显示
     *
     * @param searchMap
     * @return
     */
    //私有化方法避免代码过长
    private Map searchList(Map searchMap) {
        Map<String, Object> map = new HashMap<>();
        //高亮查村
        HighlightQuery query = new SimpleHighlightQuery();
        //设置高亮域
        HighlightOptions options = new HighlightOptions().addField("item_title");
        options.setSimplePrefix("<em style='color:red'>");
        options.setSimplePostfix("</em>");
        query.setHighlightOptions(options);
        //查询条件 is后面就是查询的参数
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //过滤查询
        //分类过滤
        if (!"".equals(searchMap.get("category"))) {
            FilterQuery filterQuery = new SimpleFilterQuery();
            Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
            filterQuery.addCriteria(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //品牌过滤
        if (!"".equals(searchMap.get("brand"))) {
            FilterQuery filterQuery = new SimpleFilterQuery();
            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            filterQuery.addCriteria(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //规格过滤
        if (searchMap.get("spec") != null) {
            FilterQuery filterQuery = new SimpleFilterQuery();
            Map<String, Object> specMap = (Map) searchMap.get("spec");
            for (String key : specMap.keySet()) {
                Criteria filterCriteria = new Criteria("item_spec_" + key).is(specMap.get(key));
                filterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }
        //价格过滤
        if (!"".equals(searchMap.get("price"))) {
            String pri = (String) searchMap.get("price");
            String[] prices = pri.split("-");
            //下限
            if (!"0".equals(prices[0])) {
                FilterQuery filterQuery = new SimpleFilterQuery();
                Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(prices[0]);
                filterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
            //上限
            if (!"*".equals(prices[1])) {
                FilterQuery filterQuery = new SimpleFilterQuery();
                Criteria filterCriteria = new Criteria("item_price").lessThanEqual(prices[1]);
                filterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }
        //分页
        //设置默认值
        Integer pageNo = (Integer)searchMap.get("pageNo");
        if(pageNo==null){
            pageNo=1;
        }
        Integer pageSize = (Integer) searchMap.get("pageSize");
        if(pageSize==null){
            pageSize=20;
        }
        query.setOffset((pageNo-1)*pageSize);
        query.setRows(pageSize);
        //排序
        String sortField= (String) searchMap.get("sortField");
        String sortValue =(String) searchMap.get("sort");
        if(sortValue!=null && !sortValue.equals("")){
            if(sortValue.equals("ASC")){
                Sort sort=new Sort(Sort.Direction.ASC, "item_"+sortField);
                query.addSort(sort);
            }
            if(sortValue.equals("DESC")){
                Sort sort=new Sort(Sort.Direction.DESC, "item_"+sortField);
                query.addSort(sort);
            }
        }
        //查询 获取高亮域对象
        HighlightPage<TbItem> tbItemHighlightPage = solrTemplate.queryForHighlightPage(query, TbItem.class);
        //高亮入口集合(每一个都对应的是一个TbItem实体类)
        List<HighlightEntry<TbItem>> highlighted = tbItemHighlightPage.getHighlighted();
        //System.out.println(highlighted);
        for (HighlightEntry<TbItem> tbItemHighlightEntry : highlighted) {
            //获取原来的实体类
            TbItem tbItem = tbItemHighlightEntry.getEntity();
            //获取高亮域列表（这里也只有一个）（每个Item的所有字段都有可能是高亮域）
            List<HighlightEntry.Highlight> highlights = tbItemHighlightEntry.getHighlights();
            //这里是 snipplets指的是每个域可能存的多个值
            //因为这里的这个域只有一个值不能重复所以直接get(0)就可以了
            if (highlights.size() > 0 && highlights.get(0).getSnipplets().size() > 0) {
                String s = highlights.get(0).getSnipplets().get(0);
                //将这个存到TbItem里面
                tbItem.setTitle(s);
            }
            //tbItem.setTitle(highlights.get(0).getSnipplets().get(0));
        }

        map.put("rows", tbItemHighlightPage.getContent());
        map.put("totalNums",tbItemHighlightPage.getTotalElements()); //总条数
        map.put("totalPages",tbItemHighlightPage.getTotalPages()); //总页数
        return map;
    }

   /* public static void main(String[] args) {
        //classpath* 可以从jar包中找配置文件，不加*只能在当前工程找配置文件
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        ItemSearchServiceImpl solrUtil = (ItemSearchServiceImpl) context.getBean("itemSearchServiceImpl");
        Map<String, Object> map = new HashMap<>();
        map.put("keywords", "联想");
        solrUtil.search(map);
    }*/
}
