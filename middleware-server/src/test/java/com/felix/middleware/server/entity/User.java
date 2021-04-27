package com.felix.middleware.server.entity;

import lombok.Data;
import lombok.ToString;

/**
 * @description:
 * @author: Felix
 * @date: 2021/4/27 16:09
 */
@Data
@ToString
public class User {
    private Integer id;
    private String userName;
    private String name;

    public User() {
    }

    public User(Integer id, String userName, String name) {
        this.id = id;
        this.userName = userName;
        this.name = name;
    }
}
