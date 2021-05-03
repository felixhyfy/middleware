package com.felix.middleware.server.service.redisson;

import com.felix.middleware.server.dto.UserLoginDto;
import com.felix.middleware.server.service.ISysLogService;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * @description: 记录用户登录成功后的轨迹-消费者
 * @author: Felix
 * @date: 2021/5/2 21:33
 */
@Component
public class UserLoginSubscriber implements ApplicationRunner, Ordered {

    private static final Logger log = LoggerFactory.getLogger(UserLoginSubscriber.class);

    private static final String TOPIC_KEY = "redissonUserLoginTopicKey";

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private ISysLogService sysLogService;

    /**
     * Callback used to run the bean.
     * 不断地监听该主题中消息的动态 - 即间接地实现自动监听消费
     *
     * @param args incoming application arguments
     * @throws Exception on error
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            RTopic rTopic = redissonClient.getTopic(TOPIC_KEY);
            rTopic.addListener(UserLoginDto.class, new MessageListener<UserLoginDto>() {
                @Override
                public void onMessage(CharSequence charSequence, UserLoginDto dto) {
                    log.info("记录用户登录成功后的轨迹-消费者-监听消费到消息：{}", dto);

                    //判断消息是否为null
                    if (dto != null) {
                        sysLogService.recordLog(dto);
                    }
                }
            });
        } catch (Exception e) {
            log.error("记录用户登录成功后的轨迹-消费者-发生异常：", e.fillInStackTrace());
        }
    }

    /**
     * Get the order value of this object.
     * <p>Higher values are interpreted as lower priority. As a consequence,
     * the object with the lowest value has the highest priority (somewhat
     * analogous to Servlet {@code load-on-startup} values).
     * <p>Same order values will result in arbitrary sort positions for the
     * affected objects.
     * 设置项目启动时也跟着启动
     *
     * @return the order value
     * @see #HIGHEST_PRECEDENCE
     * @see #LOWEST_PRECEDENCE
     */
    @Override
    public int getOrder() {
        //最高优先级
        return 0;
    }
}
