package com.felix.middleware.model.mapper;

import com.felix.middleware.model.entity.UserReg;
import org.apache.ibatis.annotations.Param;

public interface UserRegMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserReg record);

    int insertSelective(UserReg record);

    UserReg selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserReg record);

    int updateByPrimaryKey(UserReg record);

    /**
     * 根据用户名查询用户实体
     * @param userName
     * @return
     */
    UserReg selectByUserName(@Param("userName") String userName);
}