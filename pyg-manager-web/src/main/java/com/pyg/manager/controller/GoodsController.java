package com.pyg.manager.controller;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.pyg.pojo.TbItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.entity.PageResult;
import com.pyg.entity.PygResult;
import com.pyg.pojo.TbGoods;
import com.pyg.pojogroup.Goods;
import com.pyg.sellergoods.service.GoodsService;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;


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

   // @Reference(timeout=40000)
   // private ItemPageService itemPageService;


    //用于发送solr导入的信息
    //名字要和SpringIOC里面一样，或者加上那个Qualifie注解
    @Autowired
    private Destination queueSolrDestination;

    @Autowired
    private Destination topicPageDestination;

    @Autowired
    private Destination topicPageDeleteDestination;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Destination queueSolrDeleteDestination; //用户在索引库中删除记录

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
     *  jms发送消息
     */
    /*public void sendTextMessage(final String text){
        jmsTemplate.send(solrDestination, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage(text);
            }
        });
    }*/

  /*  public void sendTextMessage(final String text){
        //Lambda尝试
        jmsTemplate.send(solrDestination,(session)->session.createTextMessage(text));
    }*/

    /**
     * 修改
     *
     * @param
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
                    //解除耦合性
                   // itemSearchService.import2Solr(itemList);
                    System.out.println("(import)已经发送消息到消费者");
                    final String jsonItemList=JSON.toJSONString(itemList);
                    jmsTemplate.send(queueSolrDestination,(session ->session.createTextMessage(jsonItemList) ));
                    //静态页生成
                    for(final Long goodsId:ids){
                        jmsTemplate.send(topicPageDestination,(session -> session.createTextMessage(goodsId+"")));
                    }
                } else {
                    System.out.println("没有查询到item信息");
                }
            }
            //静态页生成
            // for(Long goodsId:ids){
                //解耦了
                // itemPageService.genItemHtml(goodsId);
            //}



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
    /*@RequestMapping("/genHtml")
    public void genHtml(Long goodsId){
        itemPageService.genItemHtml(goodsId);
    }*/

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
    public PygResult delete(final Long[] ids) {
        try {
            goodsService.delete(ids);
            //解除耦合性
            //  itemSearchService.deleteByGoodsIds(Arrays.asList(ids));
            System.out.println("(delete)已经发送消息到消费者");
            //删除solr库
            jmsTemplate.send(queueSolrDeleteDestination,(session -> session.createObjectMessage(ids)));
            //删除静态页面
            jmsTemplate.send(topicPageDeleteDestination,(session -> session.createObjectMessage(ids)));
            return new PygResult(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new PygResult(false, "删除失败");
        }
    }

    /**
     * 查询+分页
     *
     * @param goods
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbGoods goods, int page, int rows) {
        return goodsService.findPage(goods, page, rows);
    }

}
