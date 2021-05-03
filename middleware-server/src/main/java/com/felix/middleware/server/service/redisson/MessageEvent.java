package com.felix.middleware.server.service.redisson;

import org.springframework.context.ApplicationEvent;

/**
 * @description: Spring自定义事件
 * @author: Felix
 * @date: 2021/5/3 14:58
 */
public class MessageEvent extends ApplicationEvent {
    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public MessageEvent(Object source) {
        super(source);
    }


}
