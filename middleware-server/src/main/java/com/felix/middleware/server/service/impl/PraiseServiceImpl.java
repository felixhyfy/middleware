package com.felix.middleware.server.service.impl;

import com.felix.middleware.model.dto.PraiseRankDto;
import com.felix.middleware.model.entity.Praise;
import com.felix.middleware.model.mapper.PraiseMapper;
import com.felix.middleware.server.dto.PraiseDto;
import com.felix.middleware.server.service.IPraiseService;
import com.felix.middleware.server.service.IRedisPraiseService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @description: 点赞处理接口-实现类
 * @author: Felix
 * @date: 2021/5/3 19:40
 */
@Service
public class PraiseServiceImpl implements IPraiseService {

    private static final Logger log = LoggerFactory.getLogger(PraiseServiceImpl.class);

    /**
     * 定义点赞博客时加分布式锁对应的key
     */
    private static final String KEY_ADD_BLOG_LOCK = "RedisBlogPraiseAddLock";

    @Autowired
    private Environment env;

    @Autowired
    private PraiseMapper praiseMapper;

    @Autowired
    private IRedisPraiseService redisPraiseService;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 点赞博客-无锁
     *
     * @param dto
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPraise(PraiseDto dto) throws Exception {
        //根据博客id-用户id查询当前用户的点赞记录
        Praise praise = praiseMapper.selectByBlogUserId(dto.getBlogId(), dto.getUserId());
        if (praise == null) {
            //如果没有点赞记录，则创建博客点赞实体信息
            praise = new Praise();
            BeanUtils.copyProperties(dto, praise);
            praise.setCreateTime(new Date());
            praise.setPraiseTime(new Date());
            praise.setStatus(1);

            //插入用户点赞记录
            int total = praiseMapper.insertSelective(praise);
            if (total > 0) {
                //成功插入，将用户点赞记录添加至缓存中
                log.info("--点赞博客-{}-无锁-插入点赞记录成功--", dto.getBlogId());

                redisPraiseService.cachePraiseBlog(dto.getBlogId(), dto.getUserId(), 1);

                //触发排行榜
                this.cachePraiseTotal();
            }
        }
    }

    /**
     * 点赞博客-加分布式锁
     *
     * @param dto
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPraiseLock(PraiseDto dto) throws Exception {
        //定义用于获取分布式锁的Redis的Key
        final String lockName = KEY_ADD_BLOG_LOCK + dto.getBlogId() + "-" + dto.getUserId();
        //获取一次性锁对象
        RLock lock = redissonClient.getLock(lockName);
        //上锁并在10秒钟后自动释放
        lock.tryLock(10L, TimeUnit.SECONDS);

        try {
            //TODO:
            //根据博客id-用户id查询当前用户的点赞记录
            Praise praise = praiseMapper.selectByBlogUserId(dto.getBlogId(), dto.getUserId());
            if (praise == null) {
                //如果没有点赞记录，则创建点赞实体信息
                praise = new Praise();
                BeanUtils.copyProperties(dto, praise);
                praise.setPraiseTime(new Date());
                praise.setCreateTime(new Date());
                praise.setStatus(1);

                //插入用户点赞记录
                int total = praiseMapper.insertSelective(praise);
                if (total > 0) {
                    //成功插入，将用户点赞记录添加至缓存中
                    log.info("--点赞博客-{}-加分布式锁-插入点赞记录成功--", dto.getBlogId());

                    redisPraiseService.cachePraiseBlog(dto.getBlogId(), dto.getUserId(), 1);

                    //触发排行榜
                    this.cachePraiseTotal();
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 取消点赞博客
     *
     * @param dto
     * @throws Exception
     */
    @Override
    public void cancelPraise(PraiseDto dto) throws Exception {
        //判断参数合法性
        if (dto.getBlogId() != null && dto.getUserId() != null) {
            int result = praiseMapper.cancelPraiseBlog(dto.getBlogId(), dto.getUserId());
            if (result > 0) {
                //更新记录成功，同时更新缓存中相应博客的用户点赞记录
                log.info("--取消点赞博客-{}-更新点赞记录成功--", dto.getBlogId());

                redisPraiseService.cachePraiseBlog(dto.getBlogId(), dto.getUserId(), 0);

                //触发排行榜
                this.cachePraiseTotal();
            }
        }
    }

    /**
     * 获取博客的点赞数
     *
     * @param blogId
     * @return
     * @throws Exception
     */
    @Override
    public Long getBlogPraiseTotal(Integer blogId) throws Exception {
        //直接调用Redis服务封装好的”获取博客点赞数量“的方法即可
        return redisPraiseService.getCacheTotalBlog(blogId);
    }

    /**
     * 获取博客点赞总数排行榜-不采用缓存
     *
     * @return
     * @throws Exception
     */
    @Override
    public Collection<PraiseRankDto> getRankNoRedisson() throws Exception {
        return praiseMapper.getPraiseRank();
    }

    /**
     * 获取博客点赞总数排行榜-采用缓存
     *
     * @return
     * @throws Exception
     */
    @Override
    public Collection<PraiseRankDto> getRankWithRedisson() throws Exception {
        return redisPraiseService.getBlogPraiseRank();
    }

    /**
     * 记录当前博客id-点赞总数-实体排行榜
     */
    private void cachePraiseTotal() {
        try {
            redisPraiseService.rankBlogPraise();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
