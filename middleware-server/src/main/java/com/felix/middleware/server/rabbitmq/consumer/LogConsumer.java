package com.felix.middleware.server.rabbitmq.consumer;

import com.felix.middleware.server.dto.UserLoginDto;
import com.felix.middleware.server.service.ISysLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * @description: 系统日志记录-消费者
 * @author: Felix
 * @date: 2021/4/30 17:42
 */
@Component
public class LogConsumer {

    private static final Logger log = LoggerFactory.getLogger(LogConsumer.class);

    @Autowired
    private ISysLogService sysLogService;

    /**
     * 监听消费并处理用户登录成功后的消息
     * @param loginDto
     */
    @RabbitListener(queues = "${mq.login.queue.name}", containerFactory = "singleListenerContainer")
    public void consumeMsg(@Payload UserLoginDto loginDto) {
        try {
            log.info("系统日志记录-消费者-监听消费用户登录成功后的消息-内容：{}", loginDto);

            //调用日志记录服务-用于记录用户登录成功后将相关登录信息记入数据库
            sysLogService.recordLog(loginDto);
        } catch (Exception e) {
            log.error("系统日志记录-消费者-监听消费用户登录成功后的信息-发生异常：{}", loginDto, e.fillInStackTrace());
        }
    }

}
