package com.felix.middleware.server.service;

import com.felix.middleware.server.dto.UserAccountDto;

/**
 * @description: 基于数据库级别的乐观、悲观锁服务
 * @author: Felix
 * @date: 2021/5/1 20:46
 */
public interface IDataBaseLockService {

    /**
     * 用户账户提取金额处理
     * @param dto
     */
    void takeMoney(UserAccountDto dto) throws Exception;

    /**
     * 用户账户提取金额处理-乐观锁处理方式
     * @param dto
     */
    void takeMoneyWithLock(UserAccountDto dto) throws Exception;

    /**
     * 用户账户提取金额处理-悲观锁处理方式-for update
     * @param dto
     * @throws Exception
     */
    void takeMoneyWithLockNegative(UserAccountDto dto) throws Exception;

}
