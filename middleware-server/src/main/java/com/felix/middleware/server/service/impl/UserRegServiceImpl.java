package com.felix.middleware.server.service.impl;

import com.felix.middleware.model.entity.UserReg;
import com.felix.middleware.model.mapper.UserRegMapper;
import com.felix.middleware.server.dto.UserRegDto;
import com.felix.middleware.server.service.IUserRegService;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: Felix
 * @date: 2021/5/2 14:40
 */
@Service
public class UserRegServiceImpl implements IUserRegService {

    private static final Logger log = LoggerFactory.getLogger(UserRegServiceImpl.class);

    @Autowired
    private UserRegMapper userRegMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 定义Zookeeper客户端CuratorFramework实例
     */
    @Autowired
    private CuratorFramework zkClient;

    /**
     * ZNode节点的路径前缀
     */
    private static final String PATH_PREFIX = "/middleware/zkLock/";

    /**
     * 处理用户提交注册的请求-不加分布式锁
     *
     * @param dto
     * @throws Exception
     */
    @Override
    public void userRegNoLock(UserRegDto dto) throws Exception {
        //根据用户名查询实体信息
        UserReg reg = userRegMapper.selectByUserName(dto.getUserName());
        //如果当前用户名还没有被注册，则将当前用户信息注册入数据库中
        if (reg == null) {
            log.info("--不加分布式锁--，当前用户名为：{}", dto.getUserName());

            UserReg entity = new UserReg();
            BeanUtils.copyProperties(dto, entity);
            entity.setCreateTime(new Date());

            //插入用户注册信息
            userRegMapper.insertSelective(entity);
        } else {
            throw new Exception("用户信息已经存在！");
        }
    }

    /**
     * 处理用户提交注册的请求-加分布式锁
     *
     * @param dto
     * @throws Exception
     */
    @Override
    public void userRegWithLock(UserRegDto dto) throws Exception {
        //精心设计并构造SETNX操作中的Key，一定要与实际的业务或共享资源挂钩
        final String key = dto.getUserName() + "-lock";
        //设计Key对应的Value，为了具有随机性，在这里采用系统提供的纳秒级别的时间戳+UUID生成的随机数作为Value
        final String value = System.nanoTime() + "" + UUID.randomUUID();
        //获取操作Key的ValueOperations实例
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();

        //调用SETNX操作获取锁，如果返回true，则获取锁成功
        Boolean res = valueOperations.setIfAbsent(key, value);
        if (res) {
            //获取到分布式锁
            //为了防止出现死锁的情况，设置Key的过期时间，在这里为20s
            stringRedisTemplate.expire(key, 20, TimeUnit.SECONDS);

            try {
                UserReg reg = userRegMapper.selectByUserName(dto.getUserName());
                if (reg == null) {
                    log.info("--加了分布式锁--，当前用户名为：{}", dto.getUserName());

                    UserReg entity = new UserReg();
                    BeanUtils.copyProperties(dto, entity);
                    entity.setCreateTime(new Date());

                    userRegMapper.insertSelective(entity);
                } else {
                    throw new Exception("用户信息已经存在！");
                }
            } catch (Exception e) {
                throw e;
            } finally {
                //不管发生何种情况，都需要在Redis加锁成功并访问操作完共享资源后释放锁
                if (value.equals(valueOperations.get(key).toString())) {
                    stringRedisTemplate.delete(key);
                }
            }
        }
    }

    /**
     * 处理用户提交注册的请求-Zookeeper实现分布式锁
     *
     * @param dto
     * @throws Exception
     */
    @Override
    public void userRegWithZkLock(UserRegDto dto) throws Exception {
        //创建Zookeeper互斥锁组件实例，需要将监控用的客户端实例、共享资源作为构造参数
        InterProcessMutex mutex = new InterProcessMutex(zkClient, PATH_PREFIX + dto.getUserName() + "-lock");

        try {
            //采用互斥锁组件尝试获取分布式锁，其中尝试的最大时间在这里设置为10s。具体情况需要根据实际业务确定
            if (mutex.acquire(10L, TimeUnit.SECONDS)) {
                //TODO:真正的核心处理逻辑

                //根据用户名查询用户实体信息
                UserReg reg = userRegMapper.selectByUserName(dto.getUserName());
                if (reg == null) {
                    log.info("--加了Zookeeper分布式锁--，当前用户名为：{}", dto.getUserName());

                    UserReg entity = new UserReg();
                    BeanUtils.copyProperties(dto, entity);
                    entity.setCreateTime(new Date());

                    userRegMapper.insertSelective(entity);
                } else {
                    throw new Exception("用户信息已经存在！");
                }
            } else {
                throw new RuntimeException("获取Zookeeper分布式锁失败！");
            }
        } catch (Exception e) {
           throw e;
        } finally {
            //不管发生何种情况，在处理完核心业务逻辑之后，都需要释放该分布式锁
            mutex.release();
        }
    }
}
