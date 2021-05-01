package com.felix.middleware.server.rabbitmq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.felix.middleware.server.rabbitmq.entity.DeadInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * @description: 死信队列-真正的队列消费者
 * @author: Felix
 * @date: 2021/5/1 15:49
 */
@Component
public class DeadConsumer {

    private static final Logger log = LoggerFactory.getLogger(DeadConsumer.class);

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 监听真正的队列-消费队列中的消息-面向消费者
     * @param info
     */
    @RabbitListener(queues = "${mq.consumer.real.queue.name}", containerFactory = "singleListenerContainer")
    public void consumeMsg(@Payload DeadInfo info) {
        try {
            log.info("死信队列实战-监听真正的队列-消费队列汇总的消息，监听到消息内容为：{}", info);
            //TODO：用于执行后续的相关业务逻辑
        } catch (Exception e) {
            log.error("死信队列实战-监听真正的队列-消费队列中的消息-面向消费者-发生异常：{}", info, e.fillInStackTrace());
        }
    }

}
