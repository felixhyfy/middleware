package com.felix.middleware.server.rabbitmq.entity;

import lombok.Data;
import lombok.ToString;

/**
 * @description:
 * @author: Felix
 * @date: 2021/4/29 20:29
 */
@Data
@ToString
public class EventInfo {

    private Integer id;
    private String module;
    private String name;
    private String desc;

    public EventInfo() {
    }

    public EventInfo(Integer id, String module, String name, String desc) {
        this.id = id;
        this.module = module;
        this.name = name;
        this.desc = desc;
    }
}
