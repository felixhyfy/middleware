package com.felix.middleware.server.service.impl;

import com.felix.middleware.model.entity.MqOrder;
import com.felix.middleware.model.entity.UserOrder;
import com.felix.middleware.model.mapper.MqOrderMapper;
import com.felix.middleware.model.mapper.UserOrderMapper;
import com.felix.middleware.server.dto.UserOrderDto;
import com.felix.middleware.server.rabbitmq.publisher.DeadOrderPublisher;
import com.felix.middleware.server.service.IDeadUserOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @description: 用户下单支付超时-服务处理-实现类
 * @author: Felix
 * @date: 2021/5/1 18:13
 */
@Service
public class DeadUserOrderServiceImpl implements IDeadUserOrderService {

    private static final Logger log = LoggerFactory.getLogger(DeadUserOrderServiceImpl.class);

    @Autowired
    private UserOrderMapper userOrderMapper;

    /**
     * 定义更新失效用户下单记录状态Mapper
     */
    @Autowired
    private MqOrderMapper mqOrderMapper;

    @Autowired
    private DeadOrderPublisher deadOrderPublisher;

    @Override
    public void pushUserOrder(UserOrderDto userOrderDto) {
        UserOrder userOrder = new UserOrder();
        //复制userOrderDto对应的字段取值
        BeanUtils.copyProperties(userOrderDto, userOrder);
        //设置支付状态为已保存
        userOrder.setStatus(1);
        userOrder.setCreateTime(new Date());

        userOrderMapper.insertSelective(userOrder);

        log.info("用户成功下单，下单信息为：{}", userOrder);

        //将用户下单产生的下单记录id压入死信队列中
        Integer orderId = userOrder.getId();
        deadOrderPublisher.sendMsg(orderId);
    }

    /**
     * 更新用户下单记录的状态
     * @param userOrder
     */
    @Override
    public void updateUserOrderRecord(UserOrder userOrder) {
        try {
            if (userOrder != null) {
                //更新失效用户下单记录
                userOrder.setIsActive(0);
                userOrder.setUpdateTime(new Date());
                userOrderMapper.updateByPrimaryKeySelective(userOrder);

                //记录“失效用户下单记录”的历史，定义啊RabbitMQ死信队列历史失效记录实例
                MqOrder mqOrder = new MqOrder();
                mqOrder.setBusinessTime(new Date());
                mqOrder.setMemo("更新失效当前用户下单记录Id, orderId=" + userOrder.getId());
                mqOrder.setOrderId(userOrder.getId());

                mqOrderMapper.insertSelective(mqOrder);

                log.info("用户下单支付超时-处理服务-更新用户下单记录={}", userOrder);
            }
        } catch (Exception e) {
            log.error("用户下单支付超时-处理服务-更新用户下单记录的状态发生异常：", e.fillInStackTrace());
        }
    }
}
