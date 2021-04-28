package com.felix.middleware.server.service.impl;

import com.felix.middleware.server.dto.RedPacketDto;
import com.felix.middleware.server.service.IRedPacketService;
import com.felix.middleware.server.service.IRedService;
import com.felix.middleware.server.utils.RedPacketUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

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

            //将红包的全局唯一标识串返回给前端
            return redId;

        } else {
            throw new Exception("系统异常-分发红包-参数不合法！");
        }
    }

    @Override
    public BigDecimal rob(Integer userId, String redId) throws Exception {
        return null;
    }
}
