package com.felix.middleware.server.service.impl;

import com.felix.middleware.model.entity.User;
import com.felix.middleware.model.mapper.UserMapper;
import com.felix.middleware.server.dto.UserLoginDto;
import com.felix.middleware.server.rabbitmq.publisher.LogPublisher;
import com.felix.middleware.server.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @description:
 * @author: Felix
 * @date: 2021/4/30 17:44
 */
@Service
public class UserServiceImpl implements IUserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Resource
    private UserMapper userMapper;

    @Autowired
    private LogPublisher logPublisher;

    @Override
    public Boolean login(UserLoginDto dto) {
        User user = userMapper.selectByUserNamePassword(dto.getUsername(), dto.getPassword());
        if (user != null) {
            //数据表中存在该用户，并且密码匹配
            dto.setUserId(user.getId());
            //发送登录成功日志信息
            logPublisher.sendLogMsg(dto);
            //返回登录成功
            return true;
        } else {
            return false;
        }
    }
}
