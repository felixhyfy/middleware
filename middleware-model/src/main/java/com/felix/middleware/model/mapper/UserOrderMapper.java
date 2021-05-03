package com.felix.middleware.model.mapper;

import com.felix.middleware.model.entity.UserOrder;
import org.apache.ibatis.annotations.Param;

public interface UserOrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserOrder record);

    int insertSelective(UserOrder record);

    UserOrder selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserOrder record);

    int updateByPrimaryKey(UserOrder record);

    UserOrder selectByIdAndStatus(@Param("id") Integer id, @Param("status") Integer status);
}