package com.pyg.search.service;

import com.pyg.pojo.TbItem;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {
    /**
     * 搜索
     *
     * @param searchMap
     * @return Map
     */
    Map<String, Object> search(Map searchMap);


    /**
     * 数据导入
     */
    void import2Solr(List<TbItem> itemList);

    /**
     * 数据删除
     * @param goodsIdList
     */
    void deleteByGoodsIds(List goodsIdList);
}
