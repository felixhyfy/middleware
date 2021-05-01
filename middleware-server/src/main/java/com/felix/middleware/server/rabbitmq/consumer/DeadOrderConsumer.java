package com.felix.middleware.server.rabbitmq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.felix.middleware.model.entity.UserOrder;
import com.felix.middleware.model.mapper.UserOrderMapper;
import com.felix.middleware.server.service.IDeadUserOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * @description: 死信队列-真正的队列消费者-用户下单支付超时消息模型
 * @author: Felix
 * @date: 2021/5/1 18:51
 */
@Component
public class DeadOrderConsumer {

    private static final Logger log = LoggerFactory.getLogger(DeadOrderConsumer.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserOrderMapper userOrderMapper;

    /**
     * 用户下单支付超时-处理服务实例
     */
    @Autowired
    private IDeadUserOrderService deadUserOrderService;

    /**
     * 用户下单支付超时消息模型-监听真正的队列
     * @param orderId
     */
    @RabbitListener(queues = "${mq.consumer.order.real.queue.name}", containerFactory = "singleListenerContainer")
    public void consumeMsg(@Payload Integer orderId) {
        try {
            log.info("用户下单支付超时消息模型-监听真正的队列-监听到消息内容为：orderId={}", orderId);

            //核心业务逻辑
            //查询该用户下单记录的id对应的状态是否为已保存
            UserOrder userOrder = userOrderMapper.selectByIdAndStatus(orderId, 1);
            if (userOrder != null) {
                //不为null，则代表该用户下单状态仍然为已保存状态，即该用户已经超时，没有支付，因此需要将该笔记录作废
                deadUserOrderService.updateUserOrderRecord(userOrder);
            }
        } catch (Exception e) {
            log.error("用户下单支付超时消息模型-监听真正队列-发生异常：orderId={}", orderId, e.fillInStackTrace());
        }
    }

}
