package com.felix.middleware.server.dto;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @description: 发红包请求时接收的参数对象
 * @author: Felix
 * @date: 2021/4/27 22:03
 */
@Data
@ToString
public class RedPacketDto {

    //用户账号id
    private Integer userId;

    //红包个数
    @NotNull
    private Integer total;

    @NotNull
    private Integer amount;
}
