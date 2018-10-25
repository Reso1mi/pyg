package com.pyg.portal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.content.service.ContentService;
import com.pyg.pojo.TbContent;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/content")
public class contentController {
    //dubbox分布式的注入标签
    @Reference
    private ContentService contentService;

    @RequestMapping("/getContentList")
    public List<TbContent> getContentList(long catId){
        return contentService.getContentList(catId);
    }
}
