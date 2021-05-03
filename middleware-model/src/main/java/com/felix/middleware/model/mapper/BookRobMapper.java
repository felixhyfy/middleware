package com.felix.middleware.model.mapper;

import com.felix.middleware.model.entity.BookRob;
import org.apache.ibatis.annotations.Param;

public interface BookRobMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BookRob record);

    int insertSelective(BookRob record);

    BookRob selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BookRob record);

    int updateByPrimaryKey(BookRob record);

    /**
     * 统计每个用户每本书的抢购数量，用于判断用户是否抢购过该书籍
     * @param userId
     * @param bookNo
     * @return
     */
    int countByBookNoUserId(@Param("userId") Integer userId, @Param("bookNo") String bookNo);
}