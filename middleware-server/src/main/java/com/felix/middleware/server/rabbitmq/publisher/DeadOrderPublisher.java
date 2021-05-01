package com.felix.middleware.server.rabbitmq.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
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
 * @description: 死信队列-生产者-用户下单支付超时消息模型
 * @author: Felix
 * @date: 2021/5/1 18:15
 */
@Component
public class DeadOrderPublisher {

    private static final Logger log = LoggerFactory.getLogger(DeadOrderPublisher.class);

    @Autowired
    private Environment env;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 将用户下单记录id充当消息发送给死信队列
     * @param orderId
     */
    public void sendMsg(Integer orderId) {
        try {
            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
            rabbitTemplate.setExchange(env.getProperty("mq.producer.order.exchange.name"));
            rabbitTemplate.setRoutingKey(env.getProperty("mq.producer.order.routing.key.name"));
            rabbitTemplate.convertAndSend(orderId, new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message message) throws AmqpException {
                    MessageProperties messageProperties = message.getMessageProperties();
                    messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    messageProperties.setHeader(AbstractJavaTypeMapper.DEFAULT_CLASSID_FIELD_NAME, Integer.class);

                    return message;
                }
            });

            log.info("用户下单支付超时-发送用户下单记录id的消息入死信队列-内容为：orderId={}", orderId);
        } catch (Exception e) {
            log.error("用户下单支付超时-发送用户下单记录id的消息入死信队列-发生异常：orderId={}", orderId, e.fillInStackTrace());
        }
    }

}
