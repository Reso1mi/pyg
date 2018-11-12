package com.pyg.page.service;

public interface ItemPageService {
    /**
     * 生成商品详细页
     *
     * @param goodsId
     */
    boolean genItemHtml(Long goodsId);

    /**
     * 删除页面
     *
     */
    boolean deleteItemHtml(Long []goodsIds);

}
