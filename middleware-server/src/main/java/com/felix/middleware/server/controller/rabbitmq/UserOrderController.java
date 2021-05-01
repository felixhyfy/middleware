package com.felix.middleware.server.controller.rabbitmq;

import com.felix.middleware.api.enums.StatusCode;
import com.felix.middleware.api.response.BaseResponse;
import com.felix.middleware.server.dto.UserOrderDto;
import com.felix.middleware.server.service.IDeadUserOrderService;
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
 * @description: 用户下单记录controller
 * @author: Felix
 * @date: 2021/5/1 17:25
 */
@RestController
public class UserOrderController {

    private static final Logger log = LoggerFactory.getLogger(UserOrderController.class);

    private static final String PREFIX = "user/order";

    /**
     * 用户下单处理服务实例
     */
    @Autowired
    private IDeadUserOrderService deadUserOrderService;

    @RequestMapping(value = PREFIX + "/push", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse login(@RequestBody @Validated UserOrderDto dto, BindingResult result) {
        if (result.hasErrors()) {
            return new BaseResponse(StatusCode.INVALID_PARAMS);
        }

        BaseResponse response = new BaseResponse(StatusCode.SUCCESS);
        try {
            deadUserOrderService.pushUserOrder(dto);
        } catch (Exception e) {
            response = new BaseResponse(StatusCode.FAIL.getCode(), e.getMessage());
        }

        return response;
    }

}
