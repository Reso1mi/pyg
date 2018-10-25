package com.pyg.solrUtil;

import com.alibaba.fastjson.JSON;
import com.pyg.mapper.*;
import com.pyg.pojo.TbItem;
import com.pyg.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private TbBrandMapper tbBrandMapper;

    @Autowired
    private TbContentMapper tbContentMapper;

    @Autowired
    private TbGoodsMapper goodsMapper;


    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;

    @Autowired
    private TbSellerMapper tbSellerMapper;
    private void import2Solr(){
//        tbSellerMapper.selectByPrimaryKey("imlgw");
//        specificationOptionMapper.selectByPrimaryKey(Long.valueOf("123"));
//        goodsMapper.selectByPrimaryKey(Long.valueOf("123"));
//        tbContentMapper.selectByPrimaryKey(Long.valueOf("123"));
//        tbBrandMapper.selectByPrimaryKey(Long.valueOf("123"));
        TbItemExample example=new TbItemExample();
        example.createCriteria().andStatusEqualTo("1");
        List<TbItem> itemList=itemMapper.selectByExample(example);
        for (TbItem tbItem : itemList) {
            Map specMap=JSON.parseObject(tbItem.getSpec(),Map.class);
            tbItem.setSpecMap(specMap);
        }
        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();
    }

    public static void main(String[] args) {
        //classpath* 可以从jar包中找配置文件，不加*只能在当前工程找配置文件
        ApplicationContext context=new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        SolrUtil solrUtil=(SolrUtil)context.getBean("solrUtil");
        solrUtil.import2Solr();
    }
}
