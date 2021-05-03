package com.felix.middleware.server.controller.redisson;

import com.felix.middleware.api.enums.StatusCode;
import com.felix.middleware.api.response.BaseResponse;
import com.felix.middleware.server.dto.UserLoginDto;
import com.felix.middleware.server.service.redisson.QueuePublisher;
import com.felix.middleware.server.service.redisson.UserLoginPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @description:
 * @author: Felix
 * @date: 2021/5/3 13:42
 */
@RestController
public class RedissonController {

    private static final Logger log = LoggerFactory.getLogger(RedissonController.class);

    @Autowired
    private UserLoginPublisher userLoginPublisher;

    @Autowired
    private QueuePublisher queuePublisher;

    @RequestMapping(value = "redisson/consume", method = RequestMethod.POST)
    public BaseResponse consume(@RequestBody UserLoginDto dto) {
        BaseResponse response = new BaseResponse(StatusCode.SUCCESS);
        userLoginPublisher.sendMsg(dto);
        return response;
    }

    @RequestMapping(value = "redisson/queue", method = RequestMethod.GET)
    public BaseResponse queuePublish(@RequestParam String msg) {
        BaseResponse response = new BaseResponse(StatusCode.SUCCESS);
        queuePublisher.sendBasicMsg(msg);
        return response;
    }

}
