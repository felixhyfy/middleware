package com.felix.middleware.model.mapper;

import com.felix.middleware.model.entity.UserAccountRecord;

public interface UserAccountRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserAccountRecord record);

    int insertSelective(UserAccountRecord record);

    UserAccountRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserAccountRecord record);

    int updateByPrimaryKey(UserAccountRecord record);
}