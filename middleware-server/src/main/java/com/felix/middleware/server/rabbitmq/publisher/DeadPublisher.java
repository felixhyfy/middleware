package com.felix.middleware.server.rabbitmq.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.felix.middleware.server.rabbitmq.entity.DeadInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.AbstractJavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @description: 死信队列-生产者
 * @author: Felix
 * @date: 2021/5/1 15:43
 */
@Component
public class DeadPublisher {

    private static final Logger log = LoggerFactory.getLogger(DeadPublisher.class);

    @Autowired
    private Environment env;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 发送消息给死信队列
     * @param info
     */
    public void sendMsg(DeadInfo info) {
        try {
            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
            rabbitTemplate.setExchange(env.getProperty("mq.producer.basic.exchange.name"));
            rabbitTemplate.setRoutingKey(env.getProperty("mq.producer.basic.routing.key.name"));
            rabbitTemplate.convertAndSend(info, new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message message) throws AmqpException {
                    MessageProperties messageProperties = message.getMessageProperties();
                    messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    messageProperties.setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME, DeadInfo.class);
                    //设置消息的TTL，当消息和队列同时都设置了TTL时，则选取较短时间的值
                    //messageProperties.setExpiration(String.valueOf(10000));

                    return message;
                }
            });

            log.info("死信队列实战-发送对象类型的消息入死信队列-内容为：{}", info);
        } catch (Exception e) {
            log.error("死信队列实战-发送对象类型的消息入死信队列-发生异常：{}", info, e.fillInStackTrace());
        }
    }

}
