package com.felix.middleware.server.service;

import com.felix.middleware.server.dto.UserLoginDto;

/**
 * @description: 用户服务
 * @author: Felix
 * @date: 2021/4/30 17:44
 */
public interface IUserService {

    /**
     * 用户登录
     * @param dto
     * @return
     */
    Boolean login(UserLoginDto dto);
}
