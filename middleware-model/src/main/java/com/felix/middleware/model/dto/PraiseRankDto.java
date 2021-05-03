package com.felix.middleware.model.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @description: 博客点赞总数排行
 * @author: Felix
 * @date: 2021/5/3 18:05
 */
@Data
@ToString
public class PraiseRankDto implements Serializable {

    private Integer blogId;
    private Long total;

    public PraiseRankDto(Integer blogId, Long total) {
        this.blogId = blogId;
        this.total = total;
    }

    public PraiseRankDto() {
    }
}
