package com.felix.middleware.server;

import com.felix.middleware.server.event.Publisher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @description: 测试消息
 * @author: Felix
 * @date: 2021/4/28 18:01
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class EventTest {

    @Autowired
    private Publisher publisher;

    @Test
    public void test1() throws Exception {
        publisher.sendMsg();
    }
}
