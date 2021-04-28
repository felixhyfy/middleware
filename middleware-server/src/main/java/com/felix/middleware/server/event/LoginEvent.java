package com.felix.middleware.server.event;

import lombok.Data;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

import java.io.Serializable;

/**
 * @description: 用户登录成功后的消息实体
 * @author: Felix
 * @date: 2021/4/28 17:48
 */
@Data
@ToString
public class LoginEvent extends ApplicationEvent implements Serializable {

    private String username;

    private String loginTime;

    private String ip;

    public LoginEvent(Object source) {
        super(source);
    }

    //继承ApplicationEvent类时需要重写的构造方法
    public LoginEvent(Object source, String username, String loginTime, String ip) {
        super(source);
        this.username = username;
        this.loginTime = loginTime;
        this.ip = ip;
    }
}
