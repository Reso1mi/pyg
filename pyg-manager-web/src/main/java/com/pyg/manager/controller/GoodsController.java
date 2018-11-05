package com.pyg.manager.controller;
import java.util.List;
import com.pyg.page.service.ItemPageService;
import com.pyg.pojo.TbItem;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.entity.PageResult;
import com.pyg.entity.PygResult;
import com.pyg.pojo.TbGoods;
import com.pyg.pojogroup.Goods;
import com.pyg.sellergoods.service.GoodsService;

/* controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    //@Reference
    //private ItemSearchService itemSearchService;

    @Reference(timeout=40000)
    private ItemPageService itemPageService;
    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbGoods> findAll() {
        return goodsService.findAll();
    }


    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(int page, int rows) {
        return goodsService.findPage(page, rows);
    }

    /**
     * 增加
     *
     * @param goods
     * @return
     */
    @RequestMapping("/add")
    public PygResult add(@RequestBody Goods goods) {
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
    @RequestMapping("/updateStatus")
    public PygResult update(Long ids[], String status) {
        try {
            goodsService.updateStatus(ids, status);
            //审核通过
            if (status.equals("1")) {
                List<TbItem> itemList = goodsService.findItemListByGoodsIdandStatus(ids, status);
                if (itemList.size() > 0) {
                   // itemSearchService.import2Solr(itemList);
                } else {
                    System.out.println("没有查询到item信息");
                }
            }
            //静态页生成
            for(Long goodsId:ids){
                itemPageService.genItemHtml(goodsId);
            }
            //审核不通过
            if (status.equals("0")) {
                //itemSearchService.deleteByGoodsIds(Arrays.asList(ids));
            }
            return new PygResult(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new PygResult(false, "修改失败");
        }
    }

    /**
     * 测试静态化接口
     * @param goodsId
     */
    @RequestMapping("/genHtml")
    public void genHtml(Long goodsId){
        itemPageService.genItemHtml(goodsId);
    }

    /**
     * 获取实体
     *
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public Goods findOne(Long id) {
        return goodsService.findOne(id);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public PygResult delete(Long[] ids) {
        try {
            goodsService.delete(ids);
          //  itemSearchService.deleteByGoodsIds(Arrays.asList(ids));
            return new PygResult(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new PygResult(false, "删除失败");
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
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbGoods goods, int page, int rows) {
        return goodsService.findPage(goods, page, rows);
    }

}
