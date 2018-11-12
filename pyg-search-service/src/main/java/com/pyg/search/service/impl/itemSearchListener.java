package com.pyg.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.pyg.pojo.TbItem;
import com.pyg.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;
import java.util.Map;

/**
 * 导入索引库功能点对点完成
 */

@Component
public class itemSearchListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        System.out.println("itemSearchListener监听接收到消息...");
        try {
            TextMessage textMessage=(TextMessage)message;
            //取出消息
            String text = textMessage.getText();
            List<TbItem> list = JSON.parseArray(text,TbItem.class);
            for(TbItem item:list){
                System.out.println(item.getId()+" "+item.getTitle());
                Map specMap= JSON.parseObject(item.getSpec());//将spec字段中的json字符串转换为map
                item.setSpecMap(specMap);//给带注解的字段赋值
            }
            itemSearchService.import2Solr(list);//导入
            System.out.println("成功导入到索引库");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
