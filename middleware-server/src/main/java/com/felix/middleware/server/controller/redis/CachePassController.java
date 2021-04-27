package com.felix.middleware.server.controller.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.felix.middleware.server.service.redis.CachePassService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: 缓存穿透实战 - 获取热销商品信息
 * @author: Felix
 * @date: 2021/4/27 20:13
 */
@RestController
public class CachePassController {

    private static final Logger log = LoggerFactory.getLogger(CachePassController.class);

    private static final String PREFIX =  "cache/pass";

    //缓存穿透处理服务类
    @Autowired
    private CachePassService cachePassService;

    @RequestMapping(value = PREFIX + "/item/info", method = RequestMethod.GET)
    public Map<String, Object> getItem(@RequestParam String itemCode) {
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("code", 0);
        resMap.put("msg", "成功");

        try {
            resMap.put("data", cachePassService.getItemInfo(itemCode));
        } catch (JsonProcessingException e) {
            resMap.put("code", -1);
            resMap.put("msg", "失败" + e.getMessage());
        }

        return resMap;
    }

}
