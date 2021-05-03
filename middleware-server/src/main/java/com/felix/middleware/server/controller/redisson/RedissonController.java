package com.felix.middleware.server.controller.redisson;

import com.felix.middleware.api.enums.StatusCode;
import com.felix.middleware.api.response.BaseResponse;
import com.felix.middleware.server.dto.DeadDto;
import com.felix.middleware.server.dto.UserLoginDto;
import com.felix.middleware.server.service.redisson.MqDelayQueuePublisher;
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

    @Autowired
    private MqDelayQueuePublisher mqDelayQueuePublisher;

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

    @RequestMapping(value = "redisson/mq/msg/delay/send", method = RequestMethod.GET)
    public BaseResponse sendMqDelayMsg() {
        BaseResponse response = new BaseResponse(StatusCode.SUCCESS);
        try {
            //创建3个实体对象，代表3个不同的消息，同时，不同的消息携带不同的TTL
            DeadDto msgA = new DeadDto(1, "A");
            DeadDto msgB = new DeadDto(2, "B");
            DeadDto msgC = new DeadDto(3, "C");
            final Long ttlA = 10000L;
            final Long ttlB = 2000L;
            final Long ttlC = 4000L;

            //依次发送三条消息给RabbitMQ的死信队列
            mqDelayQueuePublisher.sendDelayMsg(msgA, ttlA);
            mqDelayQueuePublisher.sendDelayMsg(msgB, ttlB);
            mqDelayQueuePublisher.sendDelayMsg(msgC, ttlC);
        } catch (Exception e) {
            response = new BaseResponse(StatusCode.FAIL.getCode(), e.getMessage());
        }

        return response;
    }

}
