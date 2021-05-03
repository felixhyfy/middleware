package com.felix.middleware.server.controller.lock;

import com.felix.middleware.api.enums.StatusCode;
import com.felix.middleware.api.response.BaseResponse;
import com.felix.middleware.server.dto.UserRegDto;
import com.felix.middleware.server.service.IUserRegService;
import org.assertj.core.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 用户注册请求Controller
 * @author: Felix
 * @date: 2021/5/2 14:55
 */
@RestController
public class UserRegController {

    private static final Logger log = LoggerFactory.getLogger(UserRegController.class);

    private static final String PREFIX = "user/reg";

    @Autowired
    private IUserRegService userRegService;

    @RequestMapping(value = PREFIX + "/submit", method = RequestMethod.GET)
    public BaseResponse reg(UserRegDto dto) {
        //校验提交的用户名、密码等信息
        if (Strings.isNullOrEmpty(dto.getUserName()) || Strings.isNullOrEmpty(dto.getPassword())) {
            return new BaseResponse(StatusCode.INVALID_PARAMS);
        }

        BaseResponse response = new BaseResponse(StatusCode.SUCCESS);
        try {
            //处理用户提交请求-不加分布式锁
            //userRegService.userRegNoLock(dto);

            //加分布式锁
            //userRegService.userRegWithLock(dto);

            //加Zookeeper分布式锁
            //userRegService.userRegWithZkLock(dto);

            //加Redisson分布式锁
            userRegService.userRegRedisson(dto);
        } catch (Exception e) {
            response = new BaseResponse(StatusCode.FAIL.getCode(), e.getMessage());
        }

        return response;
    }

}
