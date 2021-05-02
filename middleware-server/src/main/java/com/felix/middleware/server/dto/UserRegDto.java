package com.felix.middleware.server.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @description: 用户注册请求接收的信息封装的实体
 * @author: Felix
 * @date: 2021/5/2 14:40
 */
@Data
@ToString
public class UserRegDto implements Serializable {

    private String userName;

    private String password;

}
