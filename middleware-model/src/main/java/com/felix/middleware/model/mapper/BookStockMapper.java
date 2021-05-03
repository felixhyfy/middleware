package com.felix.middleware.model.mapper;

import com.felix.middleware.model.entity.BookStock;
import org.apache.ibatis.annotations.Param;

public interface BookStockMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BookStock record);

    int insertSelective(BookStock record);

    BookStock selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BookStock record);

    int updateByPrimaryKey(BookStock record);

    /**
     * 根据书籍编号查询
     * @param bookNo
     * @return
     */
    BookStock selectByBookNo(@Param("bookNo") String bookNo);

    /**
     * 更新书籍库存-不加锁
     * @param bookNo
     * @return
     */
    int updateStock(@Param("bookNo") String bookNo);

    /**
     * 更新书籍库存-加锁
     * @param bookNo
     * @return
     */
    int updateStockWithLock(@Param("bookNo") String bookNo);
}