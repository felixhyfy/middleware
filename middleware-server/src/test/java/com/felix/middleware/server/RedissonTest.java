package com.felix.middleware.server;

import com.felix.middleware.server.dto.RMapDto;
import com.felix.middleware.server.dto.UserLoginDto;
import com.felix.middleware.server.service.redisson.UserLoginPublisher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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

    /**
     * 测试元素淘汰机制
     */
    @Test
    public void test7() {
        final String key = "myRedissonMapCache";
        //获取映射缓存RMapCache的功能组件实例-元素淘汰机制对应的实例
        RMapCache<Integer, RMapDto> rMap = redissonClient.getMapCache(key);

        RMapDto dto1 = new RMapDto(1, "map1");
        RMapDto dto2 = new RMapDto(2, "map2");
        RMapDto dto3 = new RMapDto(3, "map3");
        RMapDto dto4 = new RMapDto(4, "map4");

        //将对象元素添加进组件中
        rMap.putIfAbsent(dto1.getId(), dto1);
        //将对象元素2添加进组件中-有效时间TTL设置为10s
        rMap.putIfAbsent(dto2.getId(), dto2, 10L, TimeUnit.SECONDS);
        rMap.putIfAbsent(dto3.getId(), dto3);
        rMap.putIfAbsent(dto4.getId(), dto4, 5L, TimeUnit.SECONDS);

        //首次获取MapCache组件的所有Key
        Set<Integer> set = rMap.keySet();
        //获取MapCache组件存储的所有元素
        Map<Integer, RMapDto> resMap = rMap.getAll(set);
        log.info("元素列表：{}", resMap);

        try {
            //等待五秒钟
            Thread.sleep(5000);
            //再次获取
            resMap = rMap.getAll(rMap.keySet());
            log.info("等待5秒钟-元素列表：{}", resMap);
            //再等待10秒
            Thread.sleep(10000);
            resMap = rMap.getAll(rMap.keySet());
            log.info("再等待10秒钟-元素列表：{}", resMap);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
