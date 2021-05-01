package com.felix.middleware.server.service;

import com.felix.middleware.server.dto.RedPacketDto;

import java.math.BigDecimal;
import java.util.List;

/**
 * @description: 红包业务逻辑处理过程数据记录接口-异步实现
 * @author: Felix
 * @date: 2021/4/28 12:46
 */
public interface IRedService {

    /**
     * 记录发红包时红包的全局唯一标识串、绥靖金额列表和个数等信息
     * @param dto 红包总金额+个数
     * @param redId 红包全局唯一标识串
     * @param list 红包随机金额列表
     * @throws Exception
     */
    void recordRedPacket(RedPacketDto dto, String redId, List<Integer> list) throws Exception;

    /**
     * 记录抢红包时用户抢到的红包金额等信息入数据库
     * @param userId
     * @param redId
     * @param amount
     * @throws Exception
     */
    void recordRobRedPacket(Integer userId, String redId, BigDecimal amount) throws Exception;
}
