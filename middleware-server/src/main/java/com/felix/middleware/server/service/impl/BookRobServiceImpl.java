package com.felix.middleware.server.service.impl;

import com.felix.middleware.model.entity.BookRob;
import com.felix.middleware.model.entity.BookStock;
import com.felix.middleware.model.mapper.BookRobMapper;
import com.felix.middleware.model.mapper.BookStockMapper;
import com.felix.middleware.server.dto.BookRobDto;
import com.felix.middleware.server.service.IBookRobService;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @description: 书籍抢购服务-实现类
 * @author: Felix
 * @date: 2021/5/2 17:21
 */
@Service
public class BookRobServiceImpl implements IBookRobService {

    private static final Logger log = LoggerFactory.getLogger(BookRobServiceImpl.class);

    @Autowired
    private BookStockMapper bookStockMapper;

    @Autowired
    private BookRobMapper bookRobMapper;

    @Autowired
    private CuratorFramework zkClient;

    private static final String PATH_PREFIX = "/middleware/zkLock/";

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 书籍抢购-不加分布式锁
     *
     * @param dto
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void robWithNoLock(BookRobDto dto) throws Exception {
        //根据书籍编号查询记录
        BookStock stock = bookStockMapper.selectByBookNo(dto.getBookNo());
        //统计每个用户每本书的抢购数量
        int total = bookRobMapper.countByBookNoUserId(dto.getUserId(), dto.getBookNo());
        //商品记录存在、库存充足、而且用户还没有抢购过本书，则代表当前用户可以抢购
        if (stock != null && stock.getStock() > 0 && total <= 0) {
            log.info("--处理书籍抢购逻辑-不加分布式锁--， 当前信息：{}", dto);

            //当前用户抢到书籍，库存减1
            int res = bookStockMapper.updateStock(dto.getBookNo());
            //更新库存成功后，需要添加抢购记录
            if (res > 0) {
                BookRob entity = new BookRob();
                BeanUtils.copyProperties(dto, entity);
                entity.setRobTime(new Date());

                bookRobMapper.insertSelective(entity);
            } else {
                throw new Exception("该书籍库存不足！");
            }
        } else {
            throw new Exception("该书籍记录不存在或者库存不足，或者当前用户已经抢购过该书籍！");
        }
    }


    /**
     * 书籍抢购-加Zookeeper分布式锁
     *
     * @param dto
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void robWithZkLock(BookRobDto dto) throws Exception {
        //创建Zookeeper互斥锁组件实例
        InterProcessSemaphoreMutex mutex = new InterProcessSemaphoreMutex(zkClient,
                PATH_PREFIX + dto.getBookNo() + dto.getUserId() + "-lock");

        /*try {
            //采用互斥锁组件尝试获取分布式锁，尝试最大时间设置为15s
            if (mutex.acquire(15L, TimeUnit.SECONDS)) {
                //根据书籍编号查询记录
                BookStock stock = bookStockMapper.selectByBookNo(dto.getBookNo());
                //统计每个用户每本书的抢购数量
                int total = bookRobMapper.countByBookNoUserId(dto.getUserId(), dto.getBookNo());
                //商品记录存在、库存充足，而且用户还没有抢购过该书，表示当前用户可以抢购
                if (stock != null && stock.getStock() > 0 && total <= 0) {
                    log.info("--处理书籍抢购逻辑-加Zookeeper分布式锁--，当前信息：{}", dto);

                    //书籍库存减1
                    int res = bookStockMapper.updateStock(dto.getBookNo());
                    if (res > 0) {
                        BookRob entity = new BookRob();
                        entity.setBookNo(dto.getBookNo());
                        entity.setUserId(dto.getUserId());
                        entity.setRobTime(new Date());

                        //插入用户抢购信息
                        bookRobMapper.insertSelective(entity);
                    } else {
                        throw new Exception("该书籍库存不足！");
                    }
                } else {
                    throw new Exception("该书籍记录不存在或者库存不足，或者当前用户已经抢购过该书籍！");
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            //不论如何最后都要释放分布式锁
            mutex.release();
        }*/

        try {
            //采用互斥锁组件尝试获取分布式锁-其中尝试的最大时间在这里设置为15s
            //当然,具体的情况需要根据实际的业务而定
            if (mutex.acquire(15L, TimeUnit.SECONDS)) {
                //TODO：真正的核心处理逻辑

                //根据书籍编号查询记录
                BookStock stock = bookStockMapper.selectByBookNo(dto.getBookNo());
                //统计每个用户每本书的抢购数量
                int total = bookRobMapper.countByBookNoUserId(dto.getUserId(), dto.getBookNo());

                //商品记录存在、库存充足，而且用户还没抢购过本书，则代表当前用户可以抢购
                if (stock != null && stock.getStock() > 0 && total <= 0) {
                    log.info("---处理书籍抢购逻辑-加ZooKeeper分布式锁---,当前信息：{} ", dto);

                    //当前用户抢购到书籍，库存减一
                    int res = bookStockMapper.updateStock(dto.getBookNo());
                    //更新库存成功后，需要添加抢购记录
                    if (res > 0) {
                        //创建书籍抢购记录实体信息
                        BookRob entity = new BookRob();
                        //将提交的用户抢购请求实体信息中对应的字段取值
                        //复制到新创建的书籍抢购记录实体的相应字段中
                        entity.setUserId(dto.getUserId());
                        entity.setBookNo(dto.getBookNo());
                        //设置抢购时间
                        entity.setRobTime(new Date());
                        //插入用户注册信息
                        bookRobMapper.insertSelective(entity);
                    }
                } else {
                    //如果不满足上述的任意一个if条件，则抛出异常
                    throw new Exception("该书籍库存不足!");
                }

            } else {
                throw new RuntimeException("获取ZooKeeper分布式锁失败!");
            }
        } catch (Exception e) {
            throw e;
        } finally {
            //TODO：不管发生何种情况，在处理完核心业务逻辑之后，需要释放该分布式锁
            mutex.release();
        }
    }

    /**
     * 书籍抢购-加Redisson分布式锁
     *
     * @param dto
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void robWithRedisson(BookRobDto dto) throws Exception {
        //定义分布式锁的名称
        final String lockName = "redisssonTryLock-" + dto.getBookNo() + "-" + dto.getUserId();
        //获取Redisson的分布式锁实例
        RLock lock = redissonClient.getLock(lockName);

        try {
            //尝试获取分布式锁，最多等待100s，上锁之后10s自动解锁
            boolean result = lock.tryLock(100, 10, TimeUnit.SECONDS);
            if (result) {
                //TODO：真正的核心逻辑
                //根据书籍编号查询记录
                BookStock stock = bookStockMapper.selectByBookNo(dto.getBookNo());
                //统计每个用户每本书的抢购数量
                int total = bookRobMapper.countByBookNoUserId(dto.getUserId(), dto.getBookNo());
                //商品记录存在、库存充足、而且用户还没有抢购过该书，则代表当前用户可以抢购
                if (stock != null && stock.getStock() > 0 && total <= 0) {
                    //当前用户抢购到书籍，库存减1
                    int res = bookStockMapper.updateStockWithLock(dto.getBookNo());
                    //如果允许商品超卖-达到解营销的目的，则可以调用下面的方法
                    //int res = bookStockMapper.updateStock(dto.getBookNo());
                    //更新库存成功后，需要添加抢购记录
                    if (res > 0) {
                        //创建书籍抢购记录实体信息
                        BookRob entity = new BookRob();
                        BeanUtils.copyProperties(dto, entity);
                        entity.setRobTime(new Date());

                        bookRobMapper.insertSelective(entity);

                        log.info("---处理书籍抢购逻辑-加Redisson分布式锁---， 当前线程成功抢到书籍：{}", dto);
                    } else {
                        throw new Exception("该书籍记录不存在或者库存不足，或者当前用户已经抢购过该书籍！");
                    }
                } else {
                    throw new Exception("---获取Redisson分布式锁失败！---");
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            lock.unlock();
        }
    }
}
