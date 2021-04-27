package com.felix.middleware.server.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @description:
 * @author: Felix
 * @date: 2021/4/27 16:51
 */
@Data
@ToString
public class Person implements Serializable {

    private Integer id;
    private Integer age;
    private String name;
    private String userName;
    private String location;

    public Person() {
    }

    public Person(Integer id, Integer age, String name, String userName, String location) {
        this.id = id;
        this.age = age;
        this.name = name;
        this.userName = userName;
        this.location = location;
    }
}
