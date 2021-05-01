package com.felix.middleware.server.service;

import com.felix.middleware.server.dto.UserLoginDto;

/**
 * @description: 系统日志服务
 * @author: Felix
 * @date: 2021/5/1 13:00
 */
public interface ISysLogService {

    /**
     * 将用户登录成功的信息记入数据库
     *
     * @param dto
     */
    void recordLog(UserLoginDto dto);
}
