package com.felix.middleware.server.service;

import com.felix.middleware.server.dto.BookRobDto;

/**
 * @description: 书籍抢购服务
 * @author: Felix
 * @date: 2021/5/2 17:19
 */
public interface IBookRobService {

    /**
     * 书籍抢购-不加分布式锁
     * @param dto
     * @throws Exception
     */
    void robWithNoLock(BookRobDto dto) throws Exception;

    /**
     * 书籍抢购-加Zookeeper分布式锁
     * @param dto
     * @throws Exception
     */
    void robWithZkLock(BookRobDto dto) throws Exception;

    /**
     * 书籍抢购-加Redisson分布式锁
     * @param dto
     * @throws Exception
     */
    void robWithRedisson(BookRobDto dto) throws Exception;
}
