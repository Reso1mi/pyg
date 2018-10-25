package com.pyg.content.service.impl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.mapper.TbContentMapper;
import com.pyg.pojo.TbContent;
import com.pyg.pojo.TbContentExample;
import com.pyg.pojo.TbContentExample.Criteria;
import com.pyg.content.service.ContentService;
import com.pyg.entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbContent> page=   (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {
		contentMapper.insert(content);
		//删除缓存
		redisTemplate.boundHashOps("contentList").delete(content.getCategoryId());
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){
		//要考虑是否修改了分类的id
		//修改之前的分类id
		long catId=contentMapper.selectByPrimaryKey(content.getId()).getCategoryId();
		redisTemplate.boundHashOps("contentList").delete(content.getCategoryId());
		redisTemplate.boundHashOps("contentList").delete(catId);
		contentMapper.updateByPrimaryKey(content);
		//修改了分类id就把之前的删除
		if(catId!=content.getCategoryId()){
			redisTemplate.boundHashOps("contentList").delete(content.getCategoryId());
		}
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			long catId=contentMapper.selectByPrimaryKey(id).getCategoryId();
			redisTemplate.boundHashOps("contentList").delete(catId);
			//后删除,先删除之后就查不出来了
			contentMapper.deleteByPrimaryKey(id);
		}
	}
	
	
		@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		
		if(content!=null){			
						if(content.getTitle()!=null && content.getTitle().length()>0){
				criteria.andTitleLike("%"+content.getTitle()+"%");
			}
			if(content.getUrl()!=null && content.getUrl().length()>0){
				criteria.andUrlLike("%"+content.getUrl()+"%");
			}
			if(content.getPic()!=null && content.getPic().length()>0){
				criteria.andPicLike("%"+content.getPic()+"%");
			}
			if(content.getStatus()!=null && content.getStatus().length()>0){
				criteria.andStatusLike("%"+content.getStatus()+"%");
			}
	
		}
		
		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}



	//在这里加入Redis做缓存
	@Autowired
	private  RedisTemplate redisTemplate;

	/**
	 * 某个类别的所有广告
	 * @param id
	 * @return
	 */
	@Override
	@SuppressWarnings("all")
	public List<TbContent> getContentList(long id) {
		//先再Redis里面取值
		List<TbContent> list = (List<TbContent>) redisTemplate.boundHashOps("contentList").get(id);
		if(list==null){
			System.out.println("从数据库取");
			TbContentExample example= new TbContentExample();
			example.createCriteria().andCategoryIdEqualTo(id).andStatusEqualTo("1");
			//按照优先级排列
			example.setOrderByClause("sort_order");
			list= contentMapper.selectByExample(example);
			//将值存入Redis
			redisTemplate.boundHashOps("contentList").put(id,list);
		}
		System.out.println("从Redis里面取");
		return list;
	}
}
