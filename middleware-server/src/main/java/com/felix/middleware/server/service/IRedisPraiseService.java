package com.felix.middleware.server.service;

import com.felix.middleware.model.dto.PraiseRankDto;

import java.util.List;

/**
 * @description: 博客点赞Redis处理服务
 * @author: Felix
 * @date: 2021/5/3 19:43
 */
public interface IRedisPraiseService {

    /**
     * 缓存当前用户点赞博客的记录-包括正常点赞、取消点赞
     * @param blogId
     * @param uId
     * @param status
     * @throws Exception
     */
    void cachePraiseBlog(Integer blogId, Integer uId, Integer status) throws Exception;

    /**
     * 获取当前博客总的点赞数
     * @param blogId
     * @return
     * @throws Exception
     */
    Long getCacheTotalBlog(Integer blogId) throws Exception;

    /**
     * 触发博客点赞总数排行榜
     * @throws Exception
     */
    void rankBlogPraise() throws Exception;

    /**
     * 获取博客点赞排行榜
     * @return
     * @throws Exception
     */
    List<PraiseRankDto> getBlogPraiseRank() throws Exception;
}
