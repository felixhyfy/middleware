package com.felix.middleware.server.dto;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @description:
 * @author: Felix
 * @date: 2021/5/1 17:26
 */
@Data
@ToString
public class UserOrderDto implements Serializable {

    @NotBlank
    private String orderNo;

    @NotNull
    private Integer userId;
}
