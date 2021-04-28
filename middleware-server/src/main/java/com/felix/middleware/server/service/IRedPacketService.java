package com.felix.middleware.server.service;

import com.felix.middleware.server.dto.RedPacketDto;

import java.math.BigDecimal;

/**
 * @description: 红包业务逻辑处理接口
 * @author: Felix
 * @date: 2021/4/28 12:41
 */
public interface IRedPacketService {

    /**
     * 发红包核心业务逻辑
     *
     * @param dto
     * @return
     * @throws Exception
     */
    String handOut(RedPacketDto dto) throws Exception;

    /**
     * 抢红包
     *
     * @param userId 用户id
     * @param redId  红包id
     * @return
     * @throws Exception
     */
    BigDecimal rob(Integer userId, String redId) throws Exception;

}
