package com.felix.middleware.server.rabbitmq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.felix.middleware.server.rabbitmq.entity.KnowledgeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * @description: 确认消费模式-消费者
 * @author: Felix
 * @date: 2021/4/30 15:23
 */
@Component
public class KnowledgeConsumer {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeConsumer.class);

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 基于AUTO的确认消费模式-消费者
     * @param msg
     */
    @RabbitListener(queues = "${mq.auto.knowledge.queue.name}", containerFactory = "singleListenerContainerAuto")
    public void consumeAutoMsg(@Payload byte[] msg) {
        try {
            //监听消费解析消息体
            KnowledgeInfo info = objectMapper.readValue(msg, KnowledgeInfo.class);
            log.info("基于AUTO的确认消费模式-消费者监听消费消息-内容为：{}", info);
        } catch (Exception e) {
            log.error("基于AUTO的确认消费模式-消费者监听消费消息-发生异常：", e.fillInStackTrace());
        }
    }

}
