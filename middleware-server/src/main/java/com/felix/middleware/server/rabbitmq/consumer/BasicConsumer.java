package com.felix.middleware.server.rabbitmq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.felix.middleware.server.rabbitmq.entity.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * @description: 基本消费类型-消费者
 * @author: Felix
 * @date: 2021/4/29 15:33
 */
@Component
public class BasicConsumer {

    private static final Logger log = LoggerFactory.getLogger(BasicConsumer.class);

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 监听并接收消费队列中的消息-在这里采用单一容器工厂实例即可
     * 由于消息本质是一串二进制数据流，因而监听接收的消息采用字节数组接收
     * @param msg
     */
    @RabbitListener(queues = "${mq.basic.info.queue.name}",containerFactory = "singleListenerContainer")
    public void consumeMsg(@Payload byte[] msg){
        try {
            String message=new String(msg,"utf-8");
            log.info("基本消息模型-消费者-监听消费到消息：{} ",message);
        }catch (Exception e){
            log.error("基本消息模型-消费者-发生异常：",e.fillInStackTrace());
        }
    }

    @RabbitListener(queues = "${mq.object.info.queue.name}", containerFactory = "singleListenerContainer")
    public void consumeObjectMsg(@Payload Person person) {
        try {
            log.info("基本消息类型-监听消费处理对象信息-消费者-监听消费到信息：{}", person);
        } catch (Exception e) {
            log.error("基本消息类型-监听消费处理对象类型-消费者-发生异常：", e.fillInStackTrace());
        }
    }
}
