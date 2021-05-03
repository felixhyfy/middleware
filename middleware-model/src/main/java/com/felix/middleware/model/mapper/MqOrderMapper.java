package com.felix.middleware.model.mapper;

import com.felix.middleware.model.entity.MqOrder;

public interface MqOrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MqOrder record);

    int insertSelective(MqOrder record);

    MqOrder selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MqOrder record);

    int updateByPrimaryKey(MqOrder record);
}