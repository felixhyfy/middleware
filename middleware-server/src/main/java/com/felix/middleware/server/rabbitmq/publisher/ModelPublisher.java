package com.felix.middleware.server.rabbitmq.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.felix.middleware.server.rabbitmq.entity.EventInfo;
import com.felix.middleware.server.rabbitmq.entity.Person;
import org.assertj.core.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.AbstractJavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @description: 消息模型-生产者
 * @author: Felix
 * @date: 2021/4/29 20:38
 */
@Component
public class ModelPublisher {

    private static final Logger log = LoggerFactory.getLogger(ModelPublisher.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Environment env;

    /**
     * 发送消息
     *
     * @param info
     */
    public void sendMsg(EventInfo info) {
        if (info != null) {
            try {
                //定义消息的传输格式
                rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
                //设置广播式交换机FanoutExchange
                rabbitTemplate.setExchange(env.getProperty("mq.fanout.exchange.name"));
                //创建消息实例
                //Message msg = MessageBuilder.withBody(objectMapper.writeValueAsBytes(info)).build();
                //发送消息
                rabbitTemplate.convertAndSend(info, messagePostProcessor(EventInfo.class));
                //打印日志
                log.info("消息模型fanoutExchange-生产者-发送消息：{}", info);
            } catch (Exception e) {
                log.error("消息模型fanoutExchange-生产者-发送消息发生异常：{}", info, e.fillInStackTrace());
            }
        }
    }

    /**
     * 发送消息-基于DirectExchange消息模型-one
     *
     * @param info
     */
    public void sendMsgDirectOne(EventInfo info) {
        if (info != null) {
            try {
                rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
                rabbitTemplate.setExchange(env.getProperty("mq.direct.exchange.name"));
                rabbitTemplate.setRoutingKey(env.getProperty("mq.direct.routing.key.one.name"));

                rabbitTemplate.convertAndSend(info, messagePostProcessor(EventInfo.class));

                log.info("消息模型directExchange-one-生产者-发送消息：{}", info);
            } catch (Exception e) {
                log.error("消息模型directExchange-one-生产者-发送消息发生异常：{}", info, e.fillInStackTrace());
            }
        }
    }

    /**
     * 发送消息-基于DirectExchange消息模型-two
     *
     * @param info
     */
    public void sendMsgDirectTwo(EventInfo info) {
        if (info != null) {
            try {
                rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
                rabbitTemplate.setExchange(env.getProperty("mq.direct.exchange.name"));
                rabbitTemplate.setRoutingKey(env.getProperty("mq.direct.routing.key.two.name"));

                rabbitTemplate.convertAndSend(info, messagePostProcessor(EventInfo.class));

                log.info("消息模型directExchange-two-生产者-发送消息：{}", info);
            } catch (Exception e) {
                log.error("消息模型directExchange-two-生产者-发送消息发生异常：{}", info, e.fillInStackTrace());
            }
        }
    }

    /**
     * 发送消息-基于TopicExchange消息模型
     *
     * @param msg
     * @param routingKey
     */
    public void sendMsgTopic(String msg, String routingKey) {
        if (!Strings.isNullOrEmpty(msg) && !Strings.isNullOrEmpty(routingKey)) {
            try {
                rabbitTemplate.setMessageConverter(new SimpleMessageConverter());
                rabbitTemplate.setExchange(env.getProperty("mq.topic.exchange.name"));
                //指定路由的实际取值，根据不同取值，RabbitMQ将自行进行匹配通配符，从而路由到不同的队列中
                rabbitTemplate.setRoutingKey(routingKey);
                Message message = MessageBuilder.withBody(msg.getBytes("UTF-8")).build();
                rabbitTemplate.convertAndSend(message);

                log.info("消息模型TopicExchange-生产者-发送消息：{} 路由：{}", msg, routingKey);
            } catch (Exception e) {
                log.error("消息模型TopicExchange-生产者-发送消息发生异常:{}", msg, e.fillInStackTrace());
            }
        }
    }

    private MessagePostProcessor messagePostProcessor(Class clazz) {
        return message -> {
            //获取消息的属性
            MessageProperties messageProperties = message.getMessageProperties();
            //设置消息的持久化模式
            messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            //设置消息的类型，在这里指定消息类型为EventInfo类型
            messageProperties.setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME, clazz);
            //返回消息实例
            return message;
        };
    }
}
