package com.pyg.seller.controller;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.entity.PageResult;
import com.pyg.entity.PygResult;
import com.pyg.pojo.TbGoods;
import com.pyg.pojogroup.Goods;
import com.pyg.sellergoods.service.GoodsService;

/**
 * controller
 *
 * @author Administrator
 */
@RestController
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/goods/findAll")
    public List<TbGoods> findAll() {
        return goodsService.findAll();
    }

//	//模板编辑时的下拉列表的数据
    //应该重新引入一个controller
//	@RequestMapping("/goods/getBrandOption")
//	@ResponseBody
//	public String listBrand(long id){
//		return typeTemplateService.findOne(id).getBrandIds();
//	}
//

    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/goods/findPage")
    public PageResult findPage(int page, int rows) {
        return goodsService.findPage(page, rows);
    }

    /**
     * 增加
     *
     * @param goods
     * @return
     */
    @RequestMapping("/goods/add")
    public PygResult add(@RequestBody Goods goods) {
        //获取登录名
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        goods.getGoods().setSellerId(sellerId);//设置商家ID
        try {
            goodsService.add(goods);
            return new PygResult(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new PygResult(false, "增加失败");
        }
    }

    /**
     * 修改
     *
     * @param goods
     * @return
     */
    @RequestMapping("/goods/update")
    public PygResult update(@RequestBody Goods goods) {
        //校验是否是当前商家的id
        Goods goods2 = goodsService.findOne(goods.getGoods().getId());
        //获取当前登录的商家ID(通过SpringSecurity)
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        //如果传递过来的商家ID并不是当前登录的用户的ID,则属于非法操作
        if (!goods2.getGoods().getSellerId().equals(sellerId) || !goods.getGoods().getSellerId().equals(sellerId)) {
            return new PygResult(false, "操作非法");
        }
        try {
            goodsService.update(goods);
            return new PygResult(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new PygResult(false, "修改失败");
        }
    }

    /**
     * 获取实体
     *
     * @param id
     * @return
     */
    @RequestMapping("/goods/findOne")
    public Goods findOne(Long id) {
        return goodsService.findOne(id);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("/goods/delete")
    public PygResult delete(Long[] ids) {
        try {
            goodsService.delete(ids);
            return new PygResult(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new PygResult(false, "删除失败");
        }
    }

    @RequestMapping("/goods/upShelf")
    public PygResult upShelf(Long[] ids) {
        try {
            goodsService.upShelf(ids);
            return new PygResult(true, "上架成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new PygResult(false, "上架失败");
        }
    }

    @RequestMapping("/goods/downShelf")
    public PygResult downShelf(Long[] ids) {
        try {
            goodsService.downShelf(ids);
            return new PygResult(true, "下架成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new PygResult(false, "下架失败");
        }
    }

    /**
     * 查询+分页
     *
     * @param brand
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/goods/search")
    public PageResult search(@RequestBody TbGoods goods, int page, int rows) {
        //在这里获取 商家的id在后台查询这个商家的商品
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        goods.setSellerId(sellerId);
        return goodsService.findPage(goods, page, rows);
    }

}
