package com.felix.middleware.server.service;

import com.felix.middleware.model.dto.PraiseRankDto;
import com.felix.middleware.server.dto.PraiseDto;

import java.util.Collection;

/**
 * @description: 点赞业务处理接口
 * @author: Felix
 * @date: 2021/5/3 19:34
 */
public interface IPraiseService {

    /**
     * 点赞博客-无锁
     * @param dto
     * @throws Exception
     */
    void addPraise(PraiseDto dto) throws Exception;

    /**
     * 点赞博客-加分布式锁
     * @param dto
     * @throws Exception
     */
    void addPraiseLock(PraiseDto dto) throws Exception;

    /**
     * 取消点赞博客
     * @param dto
     * @throws Exception
     */
    void cancelPraise(PraiseDto dto) throws Exception;

    /**
     * 获取博客的点赞数
     * @param blogId
     * @return
     * @throws Exception
     */
    Long getBlogPraiseTotal(Integer blogId) throws Exception;

    /**
     * 获取博客点赞总数排行榜-不采用缓存
     * @return
     * @throws Exception
     */
    Collection<PraiseRankDto> getRankNoRedisson() throws Exception;

    /**
     * 获取博客点赞总数排行榜-采用缓存
     * @return
     * @throws Exception
     */
    Collection<PraiseRankDto> getRankWithRedisson() throws Exception;
}
