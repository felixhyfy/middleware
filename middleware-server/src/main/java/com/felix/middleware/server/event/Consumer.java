package com.felix.middleware.server.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

/**
 * @description: Spring的事件驱动模型-消费者
 * @author: Felix
 * @date: 2021/4/28 17:52
 */
@Component
@EnableAsync //允许异步执行
public class Consumer implements ApplicationListener<LoginEvent> {

    private static final Logger log = LoggerFactory.getLogger(Consumer.class);

    /**
     * 监听消费消息
     * @param event
     */
    @Override
    @Async
    public void onApplicationEvent(LoginEvent event) {
        //打印日志信息
        //由于采用异步线程监听，故而其监听业务逻辑是采用不同于主线程main的子线程去执行
        log.info("Spring事件驱动模型-接收信息：{}" , event);

        //TODO:后续为实现自身的业务逻辑—比如写入数据库等
    }
}
