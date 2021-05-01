package com.felix.middleware.server.rabbitmq.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.felix.middleware.server.dto.UserLoginDto;
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
 * @description:
 * @author: Felix
 * @date: 2021/4/30 17:54
 */
@Component
public class LogPublisher {

    private static final Logger log = LoggerFactory.getLogger(LogPublisher.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Environment env;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 将登录成功后的用户相关信息发送给队列
     * @param loginDto
     */
    public void sendLogMsg(UserLoginDto loginDto) {
        try {
            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
            rabbitTemplate.setExchange(env.getProperty("mq.login.exchange.name"));
            rabbitTemplate.setRoutingKey(env.getProperty("mq.login.routing.key.name"));
            rabbitTemplate.convertAndSend(loginDto, new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message message) throws AmqpException {
                    MessageProperties messageProperties = message.getMessageProperties();
                    messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    //设置消息头，表示传输的信息直接指定为某个类实例，消费者在监听消费时可以直接定义该类对象参数进行接收即可
                    messageProperties.setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME, UserLoginDto.class);

                    return message;
                }
            });

            log.info("系统日志记录-生产者-将登录成功后的用户相关信息发送给队列-内容：{}", loginDto);
        } catch (Exception e) {
            log.error("系统日志记录-生产者-将登录成功后的用户相关信息发送给队列-发生异常：{}", loginDto, e.fillInStackTrace());
        }
    }
}
