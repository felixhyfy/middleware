package com.felix.middleware.server.rabbitmq.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @description: 确认消费实体对象信息
 * @author: Felix
 * @date: 2021/4/30 15:23
 */
@Data
@ToString
public class KnowledgeInfo implements Serializable {

    private Integer id;
    private String mode;
    private String code;

    public KnowledgeInfo(Integer id, String mode, String code) {
        this.id = id;
        this.mode = mode;
        this.code = code;
    }

    public KnowledgeInfo() {
    }
}
