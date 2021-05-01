package com.felix.middleware.server.controller.rabbitmq;

import com.felix.middleware.api.enums.StatusCode;
import com.felix.middleware.api.response.BaseResponse;
import com.felix.middleware.server.dto.UserLoginDto;
import com.felix.middleware.server.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 用户登录controller
 * @author: Felix
 * @date: 2021/4/30 17:46
 */
@RestController
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private static final String PREFIX = "user";

    @Autowired
    private IUserService userService;

    @RequestMapping(value = PREFIX + "/login", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse login(@RequestBody @Validated UserLoginDto dto, BindingResult result) {
        //校验前端用户提交的用户登录信息的合法性
        if (result.hasErrors()) {
            return new BaseResponse(StatusCode.INVALID_PARAMS);
        }

        BaseResponse response = new BaseResponse(StatusCode.SUCCESS);
        try {
            Boolean res = userService.login(dto);
            if (res) {
                //登录成功
                response = new BaseResponse(StatusCode.SUCCESS.getCode(), "登录成功");
            } else {
                response = new BaseResponse(StatusCode.FAIL.getCode(), "登录失败-用户名密码不匹配");
            }
        } catch (Exception e) {
            response = new BaseResponse(StatusCode.FAIL.getCode(), e.getMessage());
        }

        return response;
    }

}
