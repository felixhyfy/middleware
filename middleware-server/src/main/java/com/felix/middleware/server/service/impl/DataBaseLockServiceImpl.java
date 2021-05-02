package com.felix.middleware.server.service.impl;

import com.felix.middleware.model.entity.UserAccount;
import com.felix.middleware.model.entity.UserAccountRecord;
import com.felix.middleware.model.mapper.UserAccountMapper;
import com.felix.middleware.model.mapper.UserAccountRecordMapper;
import com.felix.middleware.server.dto.UserAccountDto;
import com.felix.middleware.server.service.IDataBaseLockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @description: 基于数据库级别的乐观、悲观锁服务-实现类
 * @author: Felix
 * @date: 2021/5/1 20:49
 */
@Service
public class DataBaseLockServiceImpl implements IDataBaseLockService {

    private static final Logger log = LoggerFactory.getLogger(DataBaseLockServiceImpl.class);

    @Autowired
    private UserAccountMapper userAccountMapper;

    @Autowired
    private UserAccountRecordMapper userAccountRecordMapper;

    /**
     * 用户账户提取金额处理
     *
     * @param dto
     */
    @Override
    public void takeMoney(UserAccountDto dto) throws Exception {
        //查询用户账户实体记录
        UserAccount userAccount = userAccountMapper.selectByUserId(dto.getUserId());
        //判断实体记录是否存在，以及账户余额是否足够被提现
        if (userAccount != null && userAccount.getAmount().doubleValue() - dto.getAmount() >= 0) {
            //更新现有的账户余额
            userAccountMapper.updateAmount(dto.getAmount(), userAccount.getId());
            //同时记录提现成功时的记录
            UserAccountRecord record = new UserAccountRecord();
            record.setCreateTime(new Date());
            //设置账户记录主键id
            record.setAccountId(userAccount.getId());
            record.setAmount(BigDecimal.valueOf(dto.getAmount()));

            //插入历史记录
            userAccountRecordMapper.insert(record);

            log.info("当前待提现的金额为：{} 用户账户余额为：{}", dto.getAmount(), userAccount.getAmount());
        } else {
            throw new Exception("账户不存在或者余额不足！");
        }
    }

    /**
     * 用户账户提取金额处理-乐观锁处理方式
     *
     * @param dto
     */
    @Override
    public void takeMoneyWithLock(UserAccountDto dto) throws Exception {
        //查询用户账户实体记录
        UserAccount userAccount = userAccountMapper.selectByUserId(dto.getUserId());
        //判断实体记录是否存在，以及账户余额是否足够被提现
        if (userAccount != null && userAccount.getAmount().doubleValue() - dto.getAmount() > 0) {
            //采用版本号机制更新现有账户余额
            int res = userAccountMapper.updateByPKVersion(dto.getAmount(), userAccount.getId(), userAccount.getVersion());
            if (res > 0) {
                //更新成功，同时记录提现成功时的记录
                UserAccountRecord record = new UserAccountRecord();
                record.setCreateTime(new Date());
                record.setAmount(BigDecimal.valueOf(dto.getAmount()));
                record.setAccountId(userAccount.getId());

                //插入数据
                userAccountRecordMapper.insert(record);

                log.info("当前待提现的金额为：{} 用户账户余额为：{}", dto.getAmount(), userAccount.getAmount());
            } else {
                throw new Exception("当前账户提现失败！");
            }
        } else {
            throw new Exception("账户不存在或者账户余额不足！");
        }
    }

    /**
     * 用户账户提取金额处理-悲观锁处理方式-for update
     *
     * @param dto
     * @throws Exception
     */
    @Override
    public void takeMoneyWithLockNegative(UserAccountDto dto) throws Exception {
        //查询用户账户实体记录-for update的方式
        UserAccount userAccount = userAccountMapper.selectByUserIdLock(dto.getUserId());

        if (userAccount != null && userAccount.getAmount().doubleValue() - dto.getAmount() >= 0) {
            //如果足够被提现，则更新现有的账户余额
            int res = userAccountMapper.updateAmountLock(dto.getAmount(), userAccount.getId());
            if (res > 0) {
                //同时记录提现成功时的记录
                UserAccountRecord record = new UserAccountRecord();
                record.setCreateTime(new Date());
                record.setAccountId(userAccount.getId());
                record.setAmount(BigDecimal.valueOf(dto.getAmount()));

                userAccountRecordMapper.insert(record);

                log.info("悲观锁处理方式-当前待提现的金额为：{} 用户账户余额为：{}", dto.getAmount(), userAccount.getAmount());
            } else {
                throw new Exception("当前账户提现失败！");
            }
        } else {
            throw new Exception("账户不存在或者账户余额不足！");
        }
    }
}
