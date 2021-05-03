package com.felix.middleware.server.dto;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @description: 接收前端用户点赞博客的信息的实体对象
 * @author: Felix
 * @date: 2021/5/3 19:36
 */
@Data
@ToString
public class PraiseDto implements Serializable {

    @NotNull
    private Integer blogId;

    @NotNull
    private Integer userId;
}
