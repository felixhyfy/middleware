package com.felix.middleware.server.service.redisson;

import com.felix.middleware.server.dto.DeadDto;
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
 * @description: RabbitMQ死信队列消息模型-生产者
 * @author: Felix
 * @date: 2021/5/3 15:23
 */
@Component
public class MqDelayQueuePublisher {

    private static final Logger log = LoggerFactory.getLogger(MqDelayQueuePublisher.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Environment env;

    /**
     * 发送消息入延迟队列
     * @param msg
     * @param ttl
     */
    public void sendDelayMsg(final DeadDto msg, final Long ttl) {
        try {
            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
            rabbitTemplate.setExchange(env.getProperty("mq.redisson.dead.basic.exchange.name"));
            rabbitTemplate.setRoutingKey(env.getProperty("mq.redisson.dead.basic.routing.key.name"));
            rabbitTemplate.convertAndSend(msg, new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message message) throws AmqpException {
                    MessageProperties messageProperties = message.getMessageProperties();
                    messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    messageProperties.setHeader(AbstractJavaTypeMapper.DEFAULT_KEY_CLASSID_FIELD_NAME, DeadDto.class);
                    //设置消息的过期时间
                    messageProperties.setExpiration(ttl.toString());

                    return message;
                }
            });

            log.info("RabbitMQ死信队列消息模型-生产者-发送消息入延迟队列-消息：{}", msg);
        } catch (Exception e) {
            log.error("RabbitMQ死信队列消息模型-生产者-发送消息入延迟队列-发生异常：{}", msg, e.fillInStackTrace());
        }
    }

}
