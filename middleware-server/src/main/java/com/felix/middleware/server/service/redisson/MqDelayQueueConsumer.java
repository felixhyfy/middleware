package com.felix.middleware.server.service.redisson;

import com.felix.middleware.server.dto.DeadDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: Felix
 * @date: 2021/5/3 15:30
 */
@Component
public class MqDelayQueueConsumer {

    private static final Logger log = LoggerFactory.getLogger(MqDelayQueueConsumer.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Environment env;

    @RabbitListener(queues = "${mq.redisson.real.queue.name}", containerFactory = "singleListenerContainer")
    public void consumeMsg(@Payload DeadDto dto) {
        try {
            log.info("RabbitMQ死信队列消息模型-消费者-监听消费真正队列中的消息：{}", dto);
            //TODO：在这里执行真正的业务逻辑
        } catch (Exception e) {
            log.error("RabbitMQ死信队列消息模型-消费者-监听消费真正队列中的消息-发生异常：{}", dto, e.fillInStackTrace());
        }
    }
}
