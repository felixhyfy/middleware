package com.felix.middleware.server.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @description: 用于充当redisson死信队列中的消息
 * @author: Felix
 * @date: 2021/5/3 15:25
 */
@Data
@ToString
public class DeadDto implements Serializable {

    private Integer id;
    private String name;

    public DeadDto(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public DeadDto() {
    }
}
