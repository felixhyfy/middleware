package com.felix.middleware.server.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @description: 用户账户提现申请dto
 * @author: Felix
 * @date: 2021/5/1 20:18
 */
@Data
@ToString
public class UserAccountDto implements Serializable {

    private Integer userId;

    private Double amount;
}
