package com.felix.middleware.server.service.impl;

import com.felix.middleware.model.dto.PraiseRankDto;
import com.felix.middleware.model.mapper.PraiseMapper;
import com.felix.middleware.server.service.IRedisPraiseService;
import org.assertj.core.util.Strings;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @description: 博客点赞处理服务-实现类
 * @author: Felix
 * @date: 2021/5/3 19:47
 */
@Service
public class RedisPraiseServiceImpl implements IRedisPraiseService {

    private static final Logger log = LoggerFactory.getLogger(RedisPraiseServiceImpl.class);

    private static final String KEY_BLOG = "RedisBlogPraiseMap";

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private PraiseMapper praiseMapper;


    /**
     * 缓存当前用户点赞博客的记录-包括正常点赞、取消点赞
     *
     * @param blogId
     * @param uId
     * @param status
     * @throws Exception
     */
    @Override
    public void cachePraiseBlog(Integer blogId, Integer uId, Integer status) throws Exception {
         //创建用于获取分布式锁的Key
        final String lockName = new StringBuilder("blogRedissonPraiseLock")
                .append(blogId)
                .append(uId)
                .append(status)
                .toString();
        //获取分布式锁实例
        RLock rLock = redissonClient.getLock(lockName);
        //尝试获取分布式锁（可重入锁）
        boolean res = rLock.tryLock(100L, 10L, TimeUnit.SECONDS);
        try {
            if (res) {
                //获取到了锁
                if (blogId != null && uId != null && status != null) {
                    //采用博客id+用户id的拼接作为缓存的Key
                    final String key = blogId + ":" + uId;
                    //定义Redisson的RMap映射数据结构实例
                    RMap<String, Integer> praiseMap = redissonClient.getMap(KEY_BLOG);
                    //如果status=1表示当前用户中心点赞操作;status=0表示执行取消点赞操作
                    if (status == 1) {
                        //点赞操作需要将对应的信息添加到映射中
                        praiseMap.putIfAbsent(key, uId);
                    } else if (status == 0) {
                        //取消点赞操作需要将对应的Key从映射中移除
                        praiseMap.remove(key);
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            rLock.unlock();
        }
    }

    /**
     * 获取当前博客总的点赞数
     *
     * @param blogId
     * @return
     * @throws Exception
     */
    @Override
    public Long getCacheTotalBlog(Integer blogId) throws Exception {
        Long result = 0L;
        //判断参数的合法性
        if (blogId != null) {
            RMap<String, Integer> praiseMap = redissonClient.getMap(KEY_BLOG);
            //获取RMap中所有的键值对
            Map<String, Integer> dataMap = praiseMap.readAllMap();
            if (dataMap != null) {
                //获取该map所有的键列表，每个键的取值是由“博客id:用户id”这样的格式构成
                Set<String> set = dataMap.keySet();
                Integer bId;
                for (String key : set) {
                    if (!Strings.isNullOrEmpty(key)) {
                        //通过分割字符串获得博客id和用户id
                        String[] array = key.split(":");
                        if (array != null && array.length > 0) {
                            bId = Integer.valueOf(array[0]);
                            //判断当前取出的键对应的博客id是否和当前待比较的博客id相等
                            //如果是，代表有一条点赞记录，则结果需要加1
                            if (blogId.equals(bId)) {
                                result++;
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * 触发博客点赞总数排行榜
     *
     * @throws Exception
     */
    @Override
    public void rankBlogPraise() throws Exception {
        //定义用于缓存排行榜的Key
        final String key = "praiseRankListKey";
        //根据数据库查询语句得到已经排好序的博客实体对象列表
        List<PraiseRankDto> list = praiseMapper.getPraiseRank();
        if (list != null && list.size() > 0) {
            //获取Redisson的列表组件RList实例
            RList<PraiseRankDto> rList = redissonClient.getList(key);
            //先清空缓存中的列表数据
            rList.clear();
            //将得到的最新的排行榜更新至缓存中
            rList.addAll(list);
        }
    }

    /**
     * 获取博客点赞排行榜
     *
     * @return
     * @throws Exception
     */
    @Override
    public List<PraiseRankDto> getBlogPraiseRank() throws Exception {
        //定义用于缓存排行榜的Key
        final String key = "praiseRankListKey";
        //获取Redisson的列表组件RList实例
        RList<PraiseRankDto> rList = redissonClient.getList(key);
        //获取缓存中最新的排行榜
        return rList.readAll();
    }
}
