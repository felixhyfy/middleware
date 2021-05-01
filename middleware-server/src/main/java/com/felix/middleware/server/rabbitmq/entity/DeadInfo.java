package com.felix.middleware.server.rabbitmq.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @description:
 * @author: Felix
 * @date: 2021/5/1 15:06
 */
@Data
@ToString
public class DeadInfo implements Serializable {

    private Integer id;
    private String msg;

    public DeadInfo(Integer id, String msg) {
        this.id = id;
        this.msg = msg;
    }

    public DeadInfo() {
    }
}
