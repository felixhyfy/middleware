package com.felix.middleware.server;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;


/**
 * @description:
 * @author: Felix
 * @date: 2021/4/26 22:21
 */
@SpringBootApplication
@MapperScan(basePackages = "com.felix.middleware.model")
public class MainApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return super.configure(builder);
    }
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

    @Autowired
    private Environment env;

    /**
     * 自定义注入Bean-Zookeeper高度封装过的客户端Curator实例
     * @return
     */
    @Bean
    public CuratorFramework curatorFramework() {
        //创建CuratorFramework实例，采用工厂模式
        //指定客户端连接到Zookeeper服务端的策略：这里采用重试的机制（5次，每次间隔1秒）
        CuratorFramework curatorFramework = CuratorFrameworkFactory
                .builder()
                .connectString(env.getProperty("zk.host"))
                .retryPolicy(new RetryNTimes(5, 1000))
                .build();
        curatorFramework.start();
        //返回实例
        return curatorFramework;
    }
}
