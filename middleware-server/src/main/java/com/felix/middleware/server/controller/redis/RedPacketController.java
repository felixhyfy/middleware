package com.felix.middleware.server.controller.redis;

import com.felix.middleware.api.enums.StatusCode;
import com.felix.middleware.api.response.BaseResponse;
import com.felix.middleware.server.dto.RedPacketDto;
import com.felix.middleware.server.service.IRedPacketService;
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
 * @description: 红包处理逻辑Controller
 * @author: Felix
 * @date: 2021/4/28 13:21
 */
@RestController
public class RedPacketController {

    private static final Logger log = LoggerFactory.getLogger(RestController.class);

    private static final String PREFIX = "red/packet";

    @Autowired
    private IRedPacketService redPacketService;

    @RequestMapping(value = PREFIX + "/hand/out", method = RequestMethod.POST)
    public BaseResponse handOut(@Validated @RequestBody RedPacketDto dto, BindingResult result) {
        //参数校验
        if (result.hasErrors()) {
            return new BaseResponse(StatusCode.INVALID_PARAMS);
        }
        BaseResponse response = new BaseResponse(StatusCode.SUCCESS);
        try {
            //核心业务逻辑处理服务-最终返回红包全局唯一标识串
            String redId = redPacketService.handOut(dto);
            //将红包全局唯一标识串返回给前端
            response.setData(redId);
        } catch (Exception e) {
            log.error("发红包发生异常：dto={}", dto, e.fillInStackTrace());
            response = new BaseResponse(StatusCode.FAIL.getCode(), e.getMessage());
        }
        return response;
    }

}
