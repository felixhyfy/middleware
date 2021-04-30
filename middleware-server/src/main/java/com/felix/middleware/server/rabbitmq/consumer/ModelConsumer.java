package com.felix.middleware.server.rabbitmq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.felix.middleware.server.rabbitmq.entity.EventInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * @description: 消息模型-消费者
 * @author: Felix
 * @date: 2021/4/29 20:43
 */
@Component
public class ModelConsumer {

    private static final Logger log = LoggerFactory.getLogger(ModelConsumer.class);

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 监听并消费队列中的消息-fanoutExchange-one-这是第一条队列对应的消费者
     * @param msg
     */
    @RabbitListener(queues = "${mq.fanout.queue.one.name}", containerFactory = "singleListenerContainer")
    public void consumeFanoutMsgOne(@Payload EventInfo info) {
        try {
            //监听消费队列中的信息，并进行解析处理
            log.info("消息模型fanoutExchange-one-消费者-监听消费到信息：{}", info);
        } catch (Exception e) {
            log.error("消息模型-消费者-发生异常：", e.fillInStackTrace());
        }
    }

    /**
     * 监听并消费队列中的消息-fanoutExchange-two-这是第二条队列对应的消费者
     * @param msg
     */
    @RabbitListener(queues = "${mq.fanout.queue.two.name}", containerFactory = "singleListenerContainer")
    public void consumeFanoutMsgTwo(@Payload EventInfo info) {
        try {
            //监听消费队列中的信息，并进行解析处理
            log.info("消息模型fanoutExchange-one-消费者-监听消费到信息：{}", info);
        } catch (Exception e) {
            log.error("消息模型-消费者-发生异常：", e.fillInStackTrace());
        }
    }

    /**
     * 监听并消费队列中的消息-directExchange-one
     * @param info
     */
    @RabbitListener(queues = "${mq.direct.queue.one.name}", containerFactory = "singleListenerContainer")
    public void consumeDirectMsgOne(@Payload EventInfo info) {
        try {
            //监听消费队列中的信息，并进行解析处理
            log.info("消息模型directExchange-one-消费者-监听消费到信息：{}", info);
        } catch (Exception e) {
            log.error("消息模型-消费者-发生异常：", e.fillInStackTrace());
        }
    }

    /**
     * 监听并消费队列中的消息-directExchange-two
     * @param info
     */
    @RabbitListener(queues = "${mq.direct.queue.two.name}", containerFactory = "singleListenerContainer")
    public void consumeDirectMsgTwo(@Payload EventInfo info) {
        try {
            //监听消费队列中的信息，并进行解析处理
            log.info("消息模型directExchange-two-消费者-监听消费到信息：{}", info);
        } catch (Exception e) {
            log.error("消息模型-消费者-发生异常：", e.fillInStackTrace());
        }
    }

    /**
     * 监听并消费队列中的消息-topicExchange-*通配符
     * @param msg
     */
    @RabbitListener(queues = "${mq.topic.queue.one.name}", containerFactory = "singleSimpleStringListenerContainer")
    public void consumeTopicMsgOne(@Payload byte[] msg) {
        try {
            //监听消息并进行解析
            String message = new String(msg, "UTF-8");
            log.info("消息模型topicExchange-*-消费者-监听消费到消息：{}", message);
        } catch (Exception e) {
            log.error("消息模型topicExchange-*-消费者-监听消费发生异常：", e.fillInStackTrace());
        }
    }

    /**
     * 监听并消费队列中的消息-topicExchange-#通配符
     * @param msg
     */
    @RabbitListener(queues = "${mq.topic.queue.two.name}", containerFactory = "singleSimpleStringListenerContainer")
    public void consumeTopicMsgTwo(@Payload byte[] msg) {
        try {
            //监听消息并进行解析
            String message = new String(msg, "UTF-8");
            log.info("消息模型topicExchange-#-消费者-监听消费到消息：{}", message);
        } catch (Exception e) {
            log.error("消息模型topicExchange-#-消费者-监听消费发生异常：", e.fillInStackTrace());
        }
    }
}
