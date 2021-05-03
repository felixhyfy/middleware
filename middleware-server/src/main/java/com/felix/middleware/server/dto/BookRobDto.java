package com.felix.middleware.server.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @description: 书籍抢购实体dto
 * @author: Felix
 * @date: 2021/5/2 17:18
 */
@Data
@ToString
public class BookRobDto implements Serializable {

    private Integer userId;

    private String bookNo;

}
