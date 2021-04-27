package com.felix.middleware.server.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @description:
 * @author: Felix
 * @date: 2021/4/27 17:15
 */
@Data
@ToString
public class Fruit implements Serializable {
    private String name;
    private String color;

    public Fruit() {
    }

    public Fruit(String name, String color) {
        this.name = name;
        this.color = color;
    }
}
