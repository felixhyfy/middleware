package com.felix.middleware.server.service.redisson;

import org.assertj.core.util.Strings;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @description: 队列消费者-监听MessageEvent
 * @author: Felix
 * @date: 2021/5/3 14:52
 */
@Component
public class QueueConsumer implements ApplicationListener<MessageEvent> {

    private static final Logger log = LoggerFactory.getLogger(QueueConsumer.class);

    @Autowired
    private RedissonClient redissonClient;

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(MessageEvent event) {
        this.consumeMsg();
    }

    /**
     * 消费消息
     */
    public void consumeMsg() {
        final String queueName = "redissonBasicQueue";
        RQueue<String> rQueue = redissonClient.getQueue(queueName);
        //从队列中弹出消息
        String msg = rQueue.poll();
        if (!Strings.isNullOrEmpty(msg)) {
            log.info("队列的消费者-监听消息消息：{}", msg);
        }

    }
}
