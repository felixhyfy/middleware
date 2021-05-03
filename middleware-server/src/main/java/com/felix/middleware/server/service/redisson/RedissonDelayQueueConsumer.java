package com.felix.middleware.server.service.redisson;

import com.felix.middleware.server.dto.DeadDto;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @description: Redisson延迟队列消息模型-消费者
 * @author: Felix
 * @date: 2021/5/3 16:26
 */
@Component
@EnableScheduling
public class RedissonDelayQueueConsumer {

    private static final Logger log = LoggerFactory.getLogger(RedissonDelayQueueConsumer.class);

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 监听消费真正队列中的消息
     * 每时每刻都在不断地监听执行
     */
    @Scheduled(cron = "*/1 * * * * ?")
    public void consumeMsg() throws Exception {
        //定义延迟队列的名称
        final String delayQueueName = "redissonDelayQueueV3";
        RBlockingQueue<DeadDto> rBlockingQueue = redissonClient.getBlockingQueue(delayQueueName);
        //从队列中取出消息
        DeadDto msg = rBlockingQueue.take();
        if (msg != null) {
            log.info("Redisson延迟队列消息模型-消费者-监听消费真正队列汇总的消息：{}", msg);

            //TODO:在这里执行相应的业务逻辑
        }
    }
}
