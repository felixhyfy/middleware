package com.felix.middleware.server.service.redisson;

import com.felix.middleware.server.dto.DeadDto;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @description: Redisson延迟队列消息模型-生产者
 * @author: Felix
 * @date: 2021/5/3 16:20
 */
@Component
public class RedissonDelayQueuePublisher {

    private static final Logger log = LoggerFactory.getLogger(RedissonDelayQueuePublisher.class);

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 发送消息入延迟队列
     * @param msg
     * @param ttl
     */
    public void sendDelayMsg(final DeadDto msg, final Long ttl) {
        try {
            //定义延迟队列的名称
            final String delayQueueName = "redissonDelayQueueV3";
            //定义获取阻塞式队列的实例，需要预先创建阻塞式队列的实例作为创建延迟队列时的参数
            RBlockingDeque<DeadDto> rBlockingDeque = redissonClient.getBlockingDeque(delayQueueName);
            //定义获取延迟队列的实例
            RDelayedQueue<DeadDto> rDelayedQueue = redissonClient.getDelayedQueue(rBlockingDeque);
            //往延迟队列发送消息-设置的TTL，相当于延迟了“阻塞队列”中消息的接收
            rDelayedQueue.offer(msg, ttl, TimeUnit.MILLISECONDS);

            log.info("Redisson延迟队列消息模型-生产者-发送消息入延迟队列-消息：{}", msg);
        } catch (Exception e) {
            log.error("Redisson延迟队列消息模型-生产者-发送消息入延迟队列-发生异常：{}", msg, e.fillInStackTrace());
        }
    }

}
