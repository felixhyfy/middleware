package com.felix.middleware.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.felix.middleware.server.rabbitmq.entity.EventInfo;
import com.felix.middleware.server.rabbitmq.entity.KnowledgeInfo;
import com.felix.middleware.server.rabbitmq.entity.Person;
import com.felix.middleware.server.rabbitmq.publisher.BasicPublisher;
import com.felix.middleware.server.rabbitmq.publisher.KnowledgePublisher;
import com.felix.middleware.server.rabbitmq.publisher.ModelPublisher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @description:
 * @author: Felix
 * @date: 2021/4/29 15:40
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class RabbitmqTest {

    private static final Logger log = LoggerFactory.getLogger(RabbitmqTest.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BasicPublisher basicPublisher;

    @Autowired
    private ModelPublisher modelPublisher;

    @Autowired
    private KnowledgePublisher knowledgePublisher;

    @Test
    public void test1() throws JsonProcessingException {
        String msg = "~~~这是一个字符串消息~~~";
        //生产者实例发送消息
        basicPublisher.sendMsg(msg);
    }

    @Test
    public void test2() {
        Person p = new Person(1, "大圣", "felix");
        basicPublisher.sendObjectMsg(p);
    }

    @Test
    public void test3() {
        EventInfo info = new EventInfo(1, "增删改查模块", "基于fanoutExchange的消息模型", "这是基于fanoutExchange的消息模型");
        modelPublisher.sendMsg(info);
    }

    @Test
    public void test4() {
        EventInfo info = new EventInfo(1, "增删改查模块-1", "基于directExchange的消息模型-1", "这是基于directExchange的消息模型-1");
        modelPublisher.sendMsgDirectOne(info);
        info = new EventInfo(2, "增删改查模块-2", "基于directExchange的消息模型-2", "这是基于directExchange的消息模型-2");
        modelPublisher.sendMsgDirectTwo(info);
    }

    @Test
    public void test5() {
        String msg = "这是TopicExchange消息模型的消息";
        //#表示任意单词
        String routingKeyOne = "local.middleware.mq.topic.routing.java.key";
        //php.python替代了#的位置
        String routingKeyTwo = "local.middleware.mq.topic.routing.php.python.key";
        //0个单词
        String routingKeyThree = "local.middleware.mq.topic.routing.key";
        //modelPublisher.sendMsgTopic(msg, routingKeyOne);
        //modelPublisher.sendMsgTopic(msg, routingKeyTwo);
        modelPublisher.sendMsgTopic(msg, routingKeyThree);
    }

    @Test
    public void test6() {
        KnowledgeInfo info = new KnowledgeInfo();
        info.setId(10010);
        info.setCode("auto");
        info.setMode("基于AUTO的消息确认消费模式");

        knowledgePublisher.sendAutoMsg(info);
    }
}
