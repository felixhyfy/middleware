package com.felix.middleware.server.dto;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

/**
 * @description: 用户登录实体信息
 * @author: Felix
 * @date: 2021/4/30 17:39
 */
@Data
@ToString
public class UserLoginDto implements Serializable {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private Integer userId;

}
