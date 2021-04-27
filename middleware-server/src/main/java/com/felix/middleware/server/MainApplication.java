package com.felix.middleware.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


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
}
