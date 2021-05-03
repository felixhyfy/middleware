package com.felix.middleware.server.service.redisson;

import com.felix.middleware.server.dto.UserLoginDto;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description: 记录用户登录成功后的轨迹-生产者
 * @author: Felix
 * @date: 2021/5/2 21:29
 */
@Component
public class UserLoginPublisher {

    private static final Logger log = LoggerFactory.getLogger(UserLoginPublisher.class);

    /**
     * 构造基于发布-订阅式主题的Key
     */
    private static final String TOPIC_KEY = "redissonUserLoginTopicKey";

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 异步发送消息
     * @param dto
     */
    public void sendMsg(UserLoginDto dto) {
        try {
            if (dto != null) {
                log.info("记录用户登录成功后的轨迹-生产者-发送消息：{}", dto);

                //创建主题
                RTopic rTopic = redissonClient.getTopic(TOPIC_KEY);
                //异步发布消息
                rTopic.publishAsync(dto);
            }
        } catch (Exception e) {
            log.error("记录用户登录成功后的轨迹-生产者-发生异常：{}", dto, e.fillInStackTrace());
        }
    }

}
