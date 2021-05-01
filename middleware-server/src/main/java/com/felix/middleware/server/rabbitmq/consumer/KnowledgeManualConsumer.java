package com.felix.middleware.server.rabbitmq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.felix.middleware.server.rabbitmq.entity.KnowledgeInfo;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description: 确认消费模式-人为手动确认消费-监听器
 * @author: Felix
 * @date: 2021/4/30 16:09
 */
@Component("knowledgeManualConsumer")
public class KnowledgeManualConsumer implements ChannelAwareMessageListener {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeManualConsumer.class);

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 监听消费消息
     * @param message 消息实体
     * @param channel 通道实例
     * @throws Exception
     */
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        //获取消息属性
        MessageProperties messageProperties = message.getMessageProperties();
        //获取消息分发时的全局唯一标识
        long deliveryTag = messageProperties.getDeliveryTag();

        try {
            byte[] msg = message.getBody();
            //解析消息体
            KnowledgeInfo info = objectMapper.readValue(msg, KnowledgeInfo.class);
            log.info("确认消费模式-人为手动确认消费-监听器监听消费消息-内容为：{}", info);

            //执行完业务逻辑后，手动进行确认消费，其中第一个参数为：消息的分发标识（全局唯一）；第二个参数：是否允许批量确认消费（在这里设置为true）
            channel.basicAck(deliveryTag, true);
        } catch (Exception e) {
            log.error("确认消费模式-认为手动确认消费-监听器监听消费消息-发生异常：", e.fillInStackTrace());
            //如果在处理消息的过程中发生了异常，则依旧需要人为手动确认消费掉该消息，否则消息将一直留在队列汇总，导致重复消费
            channel.basicReject(deliveryTag, false);
        }
    }
}
