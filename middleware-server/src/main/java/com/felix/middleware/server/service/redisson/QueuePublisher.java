package com.felix.middleware.server.service.redisson;

import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @description: Redisson-队列生产者
 * @author: Felix
 * @date: 2021/5/3 14:48
 */
@Component
public class QueuePublisher {

    private static final Logger log = LoggerFactory.getLogger(QueuePublisher.class);

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private ApplicationContext context;

    /**
     * 发送基本的消息
     *
     * @param msg
     */
    public void sendBasicMsg(String msg) {
        try {
            //定义基本队列的名称
            final String queueName = "redissonBasicQueue";
            //获取队列的实例
            RQueue<String> rQueue = redissonClient.getQueue(queueName);
            //向队列中发送消息
            rQueue.add(msg);

            //发布通知
            log.info("队列的生产者-发送基本的消息-消息发送成功：{}", msg);
            context.publishEvent(new MessageEvent(this));
        } catch (Exception e) {
            log.error("队列的生产者-发送基本的消息-发生异常：{}", msg, e.fillInStackTrace());
        }
    }

}
