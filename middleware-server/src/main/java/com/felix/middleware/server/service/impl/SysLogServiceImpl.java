package com.felix.middleware.server.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.felix.middleware.model.entity.SysLog;
import com.felix.middleware.model.mapper.SysLogMapper;
import com.felix.middleware.server.dto.UserLoginDto;
import com.felix.middleware.server.service.ISysLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @description: 系统日志服务-实现类
 * @author: Felix
 * @date: 2021/5/1 13:01
 */
@Service
@EnableAsync    //异步调用
public class SysLogServiceImpl implements ISysLogService {

    private static final Logger log = LoggerFactory.getLogger(SysLogServiceImpl.class);

    @Autowired
    private SysLogMapper sysLogMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Async
    public void recordLog(UserLoginDto dto) {
        try {
            SysLog sysLog = new SysLog();
            sysLog.setUserId(dto.getUserId());
            sysLog.setModule("用户登录模块");
            sysLog.setData(objectMapper.writeValueAsString(dto));
            sysLog.setMemo("用户登录成功记录相关登录信息");
            sysLog.setCreateTime(new Date());

            sysLogMapper.insertSelective(sysLog);
        } catch (Exception e) {
            log.error("系统日志服务-记录用户登录成功的信息入数据库-发生异常：{}", dto, e.fillInStackTrace());
        }
    }
}
