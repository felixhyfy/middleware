package com.felix.middleware.server.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * @description: 映射数据结构
 * @author: Felix
 * @date: 2021/5/3 14:32
 */
@Data
@ToString
@EqualsAndHashCode
public class RMapDto implements Serializable {

    private Integer id;
    private String name;

    public RMapDto(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
