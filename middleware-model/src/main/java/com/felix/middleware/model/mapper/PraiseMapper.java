package com.felix.middleware.model.mapper;

import com.felix.middleware.model.dto.PraiseRankDto;
import com.felix.middleware.model.entity.Praise;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PraiseMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Praise record);

    int insertSelective(Praise record);

    Praise selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Praise record);

    int updateByPrimaryKey(Praise record);

    /**
     * 根据博客id和用户id查询点赞记录
     * @param blogId
     * @param uId
     * @return
     */
    Praise selectByBlogUserId(@Param("blogId") Integer blogId, @Param("uId") Integer uId);

    /**
     * 根据博客id查询总的点赞数
     * @param blogId
     * @return
     */
    int countByBlogId(@Param("blogId") Integer blogId);

    /**
     * 取消点赞博客
     * @param bolgId
     * @param uId
     * @return
     */
    int cancelPraiseBlog(@Param("blogId") Integer blogId, @Param("uId") Integer uId);

    /**
     * 获取博客点赞总数排行榜
     * @return
     */
    List<PraiseRankDto> getPraiseRank();

}