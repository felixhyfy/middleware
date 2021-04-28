package com.felix.middleware.server.service.impl;

import com.felix.middleware.server.dto.RedPacketDto;
import com.felix.middleware.server.service.IRedPacketService;
import com.felix.middleware.server.service.IRedService;
import com.felix.middleware.server.utils.RedPacketUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @description: 红包业务逻辑处理接口-实现类
 * @author: Felix
 * @date: 2021/4/28 12:43
 */
@Service
public class RedPacketServiceImpl implements IRedPacketService {

    private static final Logger log = LoggerFactory.getLogger(RedPacketServiceImpl.class);

    private static final String KEY_PREFIX = "redis:red:packet:";

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 红包业务逻辑处理过程数据记录接口
     */
    @Autowired
    private IRedService redService;


    @Override
    public String handOut(RedPacketDto dto) throws Exception {
        //判断参数的合法性
        if (dto.getTotal() > 0 && dto.getAmount() > 0) {
            //采用二倍均值法生成随机金额列表
            List<Integer> list = RedPacketUtil.divideRedPackage(dto.getAmount(), dto.getTotal());
            //生成红包全局唯一标识串，根据系统时间（纳秒）
            String timestamp = String.valueOf(System.nanoTime());
            //根据缓存Key的前缀与其他信息拼接成一个新的用于存储随机金额列表的Key
            String redId = new StringBuilder(KEY_PREFIX)
                    .append(dto.getUserId())
                    .append(":")
                    .append(timestamp)
                    .toString();
            //将随机金额列表存入缓存List中
            redisTemplate.opsForList().leftPushAll(redId, list);
            //根据缓存Key的前缀与其他信息拼接成一个新的用于存储红包总数的Key
            String redTotalKey = redId + ":total";
            //将红包总数存入缓存中
            redisTemplate.opsForValue().set(redTotalKey, dto.getTotal());
            //异步记录红包的全局唯一标识串、红包个数与随机金额列表信息至数据库中
            redService.recordRedPacket(dto, redId, list);

            log.info("当前用户发了一个红包：userId={} key={} 个数={} 总金额={}", dto.getUserId(), redId, dto.getTotal(), dto.getAmount());

            //将红包的全局唯一标识串返回给前端
            return redId;

        } else {
            throw new Exception("系统异常-分发红包-参数不合法！");
        }
    }

    /**
     * 加分布式锁的情况
     * 抢红包-分“点”与“抢”逻辑处理
     *
     * @param userId 用户id
     * @param redId  红包id
     * @return
     * @throws Exception
     */
    @Override
    public BigDecimal rob(Integer userId, String redId) throws Exception {
        //定义Redis操作组件的值操作方法
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //在处理用户抢红包之前，需要先判断一下当前用户是否已经抢过该红包了
        //如果已经抢过了，则直接返回红包金额，并在前端显示出来
        Object obj = valueOperations.get(redId + userId + ":rob");
        if (obj != null) {
            return new BigDecimal(obj.toString());
        }

        //点红包业务逻辑-主要用于判断缓存系统中是否仍然有红包，即红包剩余个数是否大于0
        Boolean res = click(redId);
        if (res) {
            //上分布式锁：一个红包每个人只能抢到一次随机金额，即要永远保证一对一的关系
            //构造缓存中的Key
            final String lockKey = redId + userId + "-lock";
            //调用setIfAbsent()方法，其实就是间接实现了分布式锁
            Boolean lock = valueOperations.setIfAbsent(lockKey, redId);
            //设定该分布式锁的过期时间为24小时
            redisTemplate.expire(lockKey, 24L, TimeUnit.HOURS);
            try {
                //表示当前线程获取到了该分布式锁
                if (lock) {
                    //开始执行后续的抢红包逻辑
                    Object value = redisTemplate.opsForList().rightPop(redId);
                    if (value != null) {
                        //红包个数减1
                        String redTotalKey = redId + ":total";
                        Integer currTotal = valueOperations.get(redTotalKey) != null ?
                                (Integer) valueOperations.get(redTotalKey) : 0;
                        valueOperations.set(redTotalKey, currTotal - 1);
                        //将红包金额返回给用户的同时，将抢红包记录存入数据库和缓存中
                        BigDecimal result = new BigDecimal(value.toString()).divide(new BigDecimal(100));
                        redService.recordRobRedPacket(userId, redId, new BigDecimal(value.toString()));
                        //将当前用户抢到的红包记录存入缓存中，表示当前用户已经抢过该红包了
                        valueOperations.set(redId + userId + ":rob", result, 24L, TimeUnit.HOURS);
                        //打印抢到的红包金额信息
                        log.info("当前用户抢到红包了：userId={} key={} 金额={}", userId, redId, result);
                        return result;
                    }
                }
            } catch (Exception e) {
                throw new Exception("系统异常-抢红包-加分布式锁失败！");
            }
        }
        //null表示当前用户没有抢到红包
        return null;
    }

    /**
     * 点红包的业务处理逻辑-如果返回true，则代表缓存系统Redis还有红包，即剩余个数>0，否则意味着红包已经被抢光了
     *
     * @param redId
     * @return
     * @throws Exception
     */
    private Boolean click(String redId) throws Exception {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //定义用于查询缓存系统中红包剩余个数的Key，这个Key是在发红包逻辑中定义的
        String redTotalKey = redId + ":total";
        //获取缓存系统Redis中红包剩余个数
        Object total = valueOperations.get(redTotalKey);
        //判断红包剩余个数total是否大于0
        if (total != null && Integer.parseInt(total.toString()) > 0) {
            return true;
        }
        //已经没有红包了
        return false;
    }
}
