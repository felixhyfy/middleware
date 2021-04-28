package com.felix.middleware.server.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @description: Spring的时间驱动模型-生产者
 * @author: Felix
 * @date: 2021/4/28 17:54
 */
@Component
public class Publisher {

    private static final Logger log = LoggerFactory.getLogger(Publisher.class);

    /**
     * 定义发送消息的组件
     */
    @Autowired
    private ApplicationEventPublisher publisher;

    /**
     * 发送消息的方法
     *
     * @throws Exception
     */
    public void sendMsg() throws Exception {
        //构造 登录成功后用户的实体信息
        LoginEvent event = new LoginEvent(this,
                "Felix",
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
                "127.0.0.1");
        //发送消息
        publisher.publishEvent(event);
        //记录日志
        log.info("Spring事件驱动模型-发送消息：{}", event);
    }

}
