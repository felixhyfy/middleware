package com.felix.middleware.server;

import com.felix.middleware.server.dto.UserLoginDto;
import com.felix.middleware.server.service.redisson.UserLoginPublisher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

/**
 * @description:
 * @author: Felix
 * @date: 2021/5/2 21:08
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class RedissonTest {

    private static final Logger log = LoggerFactory.getLogger(RedissonTest.class);

    @Autowired
    private RedissonClient redissonClient;

    @Test
    public void test1() throws IOException {
        log.info("redisson的配置信息：{}", redissonClient.getConfig().toJSON());
    }

    @Test
    public void test2() {
        final String key = "myBloomFilterDataV2";
        //初始化构造数组容量的大小
        Long total = 100000L;
        //创建布隆过滤器组件
        RBloomFilter<Integer> bloomFilter = redissonClient.getBloomFilter(key);
        //初始化布隆过滤器，预计统计元素数量为100000，期望误差率为0.01
        bloomFilter.tryInit(100000L, 0.01);
        //初始化遍历往布隆过滤器添加元素
        for (int i = 1; i <= total; i++) {
            bloomFilter.add(i);
        }
        //检查特定的元素在布隆过滤器中是否存在并打印输出
        log.info("该布隆过滤器是否包含数据1:{}", bloomFilter.contains(1));
        log.info("该布隆过滤器是否包含数据-1:{}", bloomFilter.contains(-1));
        log.info("该布隆过滤器是否包含数据10000:{}", bloomFilter.contains(10000));
        log.info("该布隆过滤器是否包含数据1000000:{}", bloomFilter.contains(1000000));
    }

    @Autowired
    private UserLoginPublisher userLoginPublisher;

    @Test
    public void test4() {
        UserLoginDto dto = new UserLoginDto();
        dto.setUserId(900001);
        dto.setUsername("felix");
        dto.setPassword("123456");
        //将消息以主题的信息发布出去
        userLoginPublisher.sendMsg(dto);
    }
}
