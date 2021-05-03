package com.felix.middleware.server.service;

import com.felix.middleware.server.dto.UserRegDto;

/**
 * @description: 处理用户注册信息提交服务
 * @author: Felix
 * @date: 2021/5/2 14:39
 */
public interface IUserRegService {

    /**
     * 处理用户提交注册的请求-不加分布式锁
     * @param dto
     * @throws Exception
     */
    void userRegNoLock(UserRegDto dto) throws Exception;

    /**
     * 处理用户提交注册的请求-加分布式锁
     * @param dto
     * @throws Exception
     */
    void userRegWithLock(UserRegDto dto) throws Exception;

    /**
     * 处理用户提交注册的请求-Zookeeper实现分布式锁
     * @param dto
     * @throws Exception
     */
    void userRegWithZkLock(UserRegDto dto) throws Exception;

    /**
     * 处理用户提交注册的请求-加Redisson分布式锁
     * @param dto
     * @throws Exception
     */
    void userRegRedisson(UserRegDto dto) throws Exception;
}
