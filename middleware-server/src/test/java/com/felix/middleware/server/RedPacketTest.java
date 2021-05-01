package com.felix.middleware.server;

import com.felix.middleware.server.utils.RedPacketUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.List;

/**
 * @description:
 * @author: Felix
 * @date: 2021/4/27 21:54
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class RedPacketTest {
    
    private static final Logger log = LoggerFactory.getLogger(RedPacketTest.class);

    /**
     * 二倍均值法自测
     */
    @Test
    public void one() {
        //总金额为分
        Integer amount = 1000;
        //红包个数
        Integer total = 10;
        List<Integer> list = RedPacketUtil.divideRedPackage(amount, total);
        log.info("总金额={}分，总个数={}个", amount, total);
        
        //用于统计生成的随机金额之和是否等于总金额
        Integer sum = 0;
        //遍历输出
        for (Integer integer : list) {
            log.info("随机金额为：{}分，即{}元", integer, new BigDecimal(integer.toString()).divide(new BigDecimal(100)));
            sum += integer;
        }
        log.info("所有随机金额叠加之和={}分", sum);
    }
}
