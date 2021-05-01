package com.felix.middleware.server.service;

import com.felix.middleware.model.entity.UserOrder;
import com.felix.middleware.server.dto.UserOrderDto;

/**
 * @description: 用户下单支付超时-服务处理
 * @author: Felix
 * @date: 2021/5/1 17:26
 */
public interface IDeadUserOrderService {

    /**
     * 用户下单-将生成的下单记录id压入死信队列中等待延迟处理
     * @param userOrderDto
     */
    void pushUserOrder(UserOrderDto userOrderDto);

    /**
     * 更新用户下单记录的状态
     * @param userOrder
     */
    void updateUserOrderRecord(UserOrder userOrder);
}
