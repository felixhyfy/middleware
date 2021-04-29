package com.felix.middleware.server.rabbitmq.entity;

import lombok.Data;
import lombok.ToString;

/**
 * @description:
 * @author: Felix
 * @date: 2021/4/29 18:43
 */
@Data
@ToString
public class Person {

    private Integer id;
    private String name;
    private String username;

    public Person(Integer id, String name, String username) {
        this.id = id;
        this.name = name;
        this.username = username;
    }

    public Person() {
    }
}
