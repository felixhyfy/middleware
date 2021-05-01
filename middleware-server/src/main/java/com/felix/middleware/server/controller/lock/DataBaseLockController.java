package com.felix.middleware.server.controller.lock;

import com.felix.middleware.api.enums.StatusCode;
import com.felix.middleware.api.response.BaseResponse;
import com.felix.middleware.server.dto.UserAccountDto;
import com.felix.middleware.server.service.IDataBaseLockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 基于数据库的乐观悲观锁
 * @author: Felix
 * @date: 2021/5/1 20:44
 */
@RestController
public class DataBaseLockController {

    private static final Logger log = LoggerFactory.getLogger(DataBaseLockController.class);

    private static final String PREFIX = "db";

    @Autowired
    private IDataBaseLockService dataBaseLockService;

    /**
     * 用户账户余额提现申请
     * @param dto
     * @return
     */
    @RequestMapping(value = PREFIX + "/money/take", method = RequestMethod.GET)
    public BaseResponse takeMoney(UserAccountDto dto) {
        if (dto.getAmount() == null || dto.getUserId() == null) {
            return new BaseResponse(StatusCode.INVALID_PARAMS);
        }

        BaseResponse response = new BaseResponse(StatusCode.SUCCESS);
        try {
            //调用核心业务逻辑处理方法-不加锁
            //dataBaseLockService.takeMoney(dto);
            //调用核心业务逻辑处理方法-加锁
            dataBaseLockService.takeMoneyWithLock(dto);
        } catch (Exception e) {
            response = new BaseResponse(StatusCode.FAIL.getCode(), e.getMessage());
        }

        return response;
    }
}
