package com.felix.middleware.server.service.impl;

import com.felix.middleware.model.entity.RedDetail;
import com.felix.middleware.model.entity.RedRecord;
import com.felix.middleware.model.entity.RedRobRecord;
import com.felix.middleware.model.mapper.RedDetailMapper;
import com.felix.middleware.model.mapper.RedRecordMapper;
import com.felix.middleware.model.mapper.RedRobRecordMapper;
import com.felix.middleware.server.dto.RedPacketDto;
import com.felix.middleware.server.service.IRedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @description: 红包业务逻辑处理过程数据记录接口-实现类
 * @author: Felix
 * @date: 2021/4/28 12:49
 */
@Service
@EnableAsync    //异步处理
public class RedServiceImpl implements IRedService {

    private static final Logger log = LoggerFactory.getLogger(RedServiceImpl.class);

    /**
     * 发红包时红包全局唯一标识串等信息操作接口Mapper
     */
    @Autowired
    private RedRecordMapper redRecordMapper;

    /**
     * 发红包时随机数算法生成的随机金额列表等信息操作接口Mapper
     */
    @Autowired
    private RedDetailMapper redDetailMapper;

    /**
     * 抢红包时相关数据信息操作接口Mapper
     */
    @Autowired
    private RedRobRecordMapper redRobRecordMapper;

    @Override
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void recordRedPacket(RedPacketDto dto, String redId, List<Integer> list) throws Exception {
        //定义实体类对象
        RedRecord redRecord = new RedRecord();
        //设置字段的取值信息
        redRecord.setUserId(dto.getUserId());
        redRecord.setRedPacket(redId);
        redRecord.setTotal(dto.getTotal());
        redRecord.setAmount(BigDecimal.valueOf(dto.getAmount()));
        redRecord.setCreateTime(new Date());

        //将对象插入数据库
        redRecordMapper.insertSelective(redRecord);

        //定义红包随机金额明细实体类对象
        RedDetail detail;
        //遍历随机金额列表，将金额等信息设置到相对应的字段中
        for (Integer integer : list) {
            detail = new RedDetail();
            detail.setRecordId(redRecord.getId());
            detail.setAmount(BigDecimal.valueOf(integer));
            detail.setCreateTime(new Date());
            //将对象信息插入数据库
            redDetailMapper.insertSelective(detail);
        }
    }

    @Override
    public void recordRobRedPacket(Integer userId, String redId, BigDecimal amount) throws Exception {
        //定义记录抢到红包时录入相关信息的实体对象，并设置相应字段的取值
        RedRobRecord redRobRecord = new RedRobRecord();
        redRobRecord.setUserId(userId);
        redRobRecord.setRedPacket(redId);   //设置红包全局唯一标识串
        redRobRecord.setAmount(amount);
        redRobRecord.setRobTime(new Date());
        //将实体对象信息插入数据库中
        redRobRecordMapper.insertSelective(redRobRecord);
    }
}
