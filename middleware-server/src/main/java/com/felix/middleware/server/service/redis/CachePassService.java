package com.felix.middleware.server.service.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.felix.middleware.model.entity.Item;
import com.felix.middleware.model.mapper.ItemMapper;
import org.assertj.core.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @description: 缓存穿透service
 * @author: Felix
 * @date: 2021/4/27 20:23
 */
@Service
public class CachePassService {

    private static final Logger log = LoggerFactory.getLogger(CachePassService.class);

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String KEY_PREFIX = "item:";

    /**
     * 获取商品详情-如果缓存有，则从缓存中获取；如果没有，则从数据库查询，并将查询塞入
     * @param itemCode
     * @return
     */
    public Item getItemInfo(String itemCode) throws JsonProcessingException {
        //定义商品对象
        Item item = null;
        //定义缓存中真正的key:由前缀和商品组成
        final String key = KEY_PREFIX + itemCode;
        //定义Redis的操作组件
        ValueOperations valueOperations = redisTemplate.opsForValue();
        if (redisTemplate.hasKey(key)) {
            log.info("---获取商品详情-缓存中存在该商品---商品编号为：{}", itemCode);
            //从缓存中查询该商品详情
            Object res = valueOperations.get(key);
            if (res != null && !Strings.isNullOrEmpty(res.toString())) {
                //如果可以找到该商品，进行JSON反序列化解析
                item = objectMapper.readValue(res.toString(), Item.class);
            }
        } else {
            //没有找到该商品
            log.info("---获取商品详情-缓存中不存在该商品-从数据库中查询---商品编号为：{}", itemCode);
            //从数据库中获取该商品详情
            item = itemMapper.selectByCode(itemCode);
            if (item != null) {
                //如果数据库中查得到该商品，则将其序列化后写入缓存中
                valueOperations.set(key, objectMapper.writeValueAsString(item));
            } else {
                //过期失效时间TTL设置为30分钟，实际情况要根据实际业务决定
                valueOperations.set(key, "", 30L, TimeUnit.MINUTES);
            }
        }

        return item;
    }

}
