package com.felix.middleware.server.config;

import com.felix.middleware.server.rabbitmq.consumer.KnowledgeConsumer;
import com.felix.middleware.server.rabbitmq.consumer.KnowledgeManualConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.support.MessagePropertiesConverter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: RabbitMQ自定义注入Bean配置
 * @author: Felix
 * @date: 2021/4/29 14:51
 */
@Configuration
public class RabbitmqConfig {

    private static final Logger log = LoggerFactory.getLogger(RabbitmqConfig.class);

    /**
     * 自动装配RabbitMQ的链接工厂实例
     */
    @Autowired
    private CachingConnectionFactory connectionFactory;

    /**
     * 自动装配消息监听器所在的容器工厂配置类实例
     */
    @Autowired
    private SimpleRabbitListenerContainerFactoryConfigurer factoryConfigurer;

    /**
     * 单一消费者实例的配置
     *
     * @return
     */
    @Bean(name = "singleListenerContainer")
    public SimpleRabbitListenerContainerFactory listenerContainer() {
        //定义消息监听器所在的容器工厂
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        //设置容器工厂所用的实例
        factory.setConnectionFactory(connectionFactory);
        //设置消息在传输中的格式，在这里采用JSON的格式进行传输
        //先测试传递简单字符串
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        //设置并发消费者实例的初始数量。在这里为1个
        factory.setConcurrentConsumers(1);
        //设置并发消费者实例的最大数量。在这里为1个
        factory.setMaxConcurrentConsumers(1);
        //设置并发消费者每个实例拉取的消息数量-在这里为1个
        factory.setPrefetchCount(1);

        return factory;
    }

    /**
     * 单一消费者实例的配置-接收字符串
     *
     * @return
     */
    @Bean(name = "singleSimpleStringListenerContainer")
    public SimpleRabbitListenerContainerFactory simpleStringListenerContainer() {
        //定义消息监听器所在的容器工厂
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        //设置容器工厂所用的实例
        factory.setConnectionFactory(connectionFactory);
        //设置消息在传输中的格式，在这里采用JSON的格式进行传输
        //先测试传递简单字符串
        factory.setMessageConverter(new SimpleMessageConverter());
        //设置并发消费者实例的初始数量。在这里为1个
        factory.setConcurrentConsumers(1);
        //设置并发消费者实例的最大数量。在这里为1个
        factory.setMaxConcurrentConsumers(1);
        //设置并发消费者每个实例拉取的消息数量-在这里为1个
        factory.setPrefetchCount(1);

        return factory;
    }

    /**
     * 多个消费之实例的配置，主要是针对高并发业务场景的配置
     *
     * @return
     */
    @Bean(name = "multiListenerContainer")
    public SimpleRabbitListenerContainerFactory multiListenerContainer() {
        //定义消息监听器所在的容器工厂
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        //设置容器工厂所用的实例
        factoryConfigurer.configure(factory, connectionFactory);
        //设置消息在传输中的格式。在这里采用JSON的格式进行传输
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        //设置消息的确认消费模式。在这里为NONE，表示不需要确认消费
        factory.setAcknowledgeMode(AcknowledgeMode.NONE);
        //设置并发消费者实例的初始数量。在这里为10个
        factory.setConcurrentConsumers(10);
        //设置并发消费者实例的最大数量。在这里为15个
        factory.setMaxConcurrentConsumers(15);
        //设置并发消费者每个实例拉取的消息数量。在这里为10个
        factory.setPrefetchCount(10);

        return factory;
    }

    /**
     * 自定义配置RabbitMQ发送消息的操作组件RabbitTemplate
     *
     * @return
     */
    @Bean
    public RabbitTemplate rabbitTemplate() {
        //设置“发送消息后进行确认”
        connectionFactory.setPublisherConfirms(true);
        //设置“发送消息后返回确认消息”
        connectionFactory.setPublisherReturns(true);
        //构造发送消息组件实例对象
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        //设置强制标志
        rabbitTemplate.setMandatory(true);
        //发送消息后，如果发送成功，则输出“消息发送成功”的反馈信息
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                log.info("消息发送成功：CorrelationData({}), ack({}), cause({})", correlationData, ack, cause);
            }
        });
        //发送消息后，如果发送失败，则输出“消息发送失败-消息丢失”的反馈信息
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                log.info("消息丢失: exchange({}), route({}), replyCode({}), replyText({}), message:{}",
                        exchange, routingKey, replyCode, replyText, message);
            }
        });
        //最终返回RabbitMQ的操作组件实例
        return rabbitTemplate;
    }

    /**
     * 定义读取配置文件的环境变量实例
     */
    @Autowired
    private Environment env;

    /**
     * 创建队列
     *
     * @return
     */
    @Bean(name = "basicQueue")
    public Queue basicQueue() {
        return new Queue(env.getProperty("mq.basic.info.queue.name"), true);
    }

    /**
     * 创建交换机：在这里以DirectExchange为例
     *
     * @return
     */
    @Bean
    public DirectExchange basicExchange() {
        return new DirectExchange(env.getProperty("mq.basic.info.exchange.name"), true, false);
    }

    /**
     * 创建绑定
     *
     * @return
     */
    @Bean
    public Binding basicBinding() {
        return BindingBuilder.bind(basicQueue()).to(basicExchange()).with(env.getProperty("mq.basic.info.routing.key.name"));
    }

    /**
     * 创建简单消息模型-对象类型：队列、交换机和路由
     */

    /**
     * 创建队列
     *
     * @return
     */
    @Bean(name = "objectQueue")
    public Queue objectQueue() {
        return new Queue(env.getProperty("mq.object.info.queue.name"), true);
    }

    /**
     * 创建交换机
     *
     * @return
     */
    @Bean
    public DirectExchange objectExchange() {
        return new DirectExchange(env.getProperty("mq.object.info.exchange.name"), true, false);
    }

    public Binding objectBinding() {
        return BindingBuilder.bind(objectQueue()).to(objectExchange()).with(env.getProperty("mq.object.info.routing.key.name"));
    }

    /**
     * 创建消息模型-fanoutExchange
     */
    /**
     * 创建队列1
     *
     * @return
     */
    @Bean(name = "fanoutQueueOne")
    public Queue fanoutQueueOne() {
        return new Queue(env.getProperty("mq.fanout.queue.one.name"), true);
    }

    /**
     * 创建队列2
     *
     * @return
     */
    @Bean(name = "fanoutTwo")
    public Queue fanoutQueueTwo() {
        return new Queue(env.getProperty("mq.fanout.queue.two.name"), true);
    }

    /**
     * 创建交换机-fanoutExchange
     *
     * @return
     */
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(env.getProperty("mq.fanout.exchange.name"), true, false);
    }

    /**
     * 创建绑定1
     *
     * @return
     */
    @Bean
    public Binding fanoutBindingOne() {
        return BindingBuilder.bind(fanoutQueueOne()).to(fanoutExchange());
    }

    /**
     * 创建绑定2
     *
     * @return
     */
    @Bean
    public Binding fanoutBindingTwo() {
        return BindingBuilder.bind(fanoutQueueTwo()).to(fanoutExchange());
    }

    /**
     * 创建消息模型-DirectExchange
     */

    /**
     * 创建交换机-DirectExchange
     *
     * @return
     */
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(env.getProperty("mq.direct.exchange.name"), true, false);
    }

    /**
     * 创建队列1
     *
     * @return
     */
    @Bean(name = "directQueueOne")
    public Queue directQueueOne() {
        return new Queue(env.getProperty("mq.direct.queue.one.name"), true);
    }

    /**
     * 创建队列2
     *
     * @return
     */
    @Bean(name = "directQueueTwo")
    public Queue directQueueTwo() {
        return new Queue(env.getProperty("mq.direct.queue.two.name"), true);
    }

    /**
     * 创建绑定1
     *
     * @return
     */
    @Bean
    public Binding directBindingOne() {
        return BindingBuilder.bind(directQueueOne()).to(directExchange()).with(env.getProperty("mq.direct.routing.key.one.name"));
    }

    /**
     * 创建绑定2
     *
     * @return
     */
    @Bean
    public Binding directBindingTwo() {
        return BindingBuilder.bind(directQueueTwo()).to(directExchange()).with(env.getProperty("mq.direct.routing.key.two.name"));
    }

    /**
     * 创建消息模型-topicExchange
     */

    /**
     * 创建交换机-topicExchange
     *
     * @return
     */
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(env.getProperty("mq.topic.exchange.name"), true, false);
    }

    @Bean(name = "topicQueueOne")
    public Queue topicQueueOne() {
        return new Queue(env.getProperty("mq.topic.queue.one.name"), true);
    }

    @Bean(name = "topicQueueTwo")
    public Queue topicQueueTwo() {
        return new Queue(env.getProperty("mq.topic.queue.two.name"), true);
    }

    @Bean
    public Binding topicBindingOne() {
        return BindingBuilder.bind(topicQueueOne()).to(topicExchange()).with(env.getProperty("mq.topic.routing.key.one.name"));
    }

    @Bean
    public Binding topicBindingTwo() {
        return BindingBuilder.bind(topicQueueTwo()).to(topicExchange()).with(env.getProperty("mq.topic.routing.key.two.name"));
    }

    /**
     * 单一消费者-确认模式为AUTO
     *
     * @return
     */
    @Bean(name = "singleListenerContainerAuto")
    public SimpleRabbitListenerContainerFactory listenerContainerAuto() {
        //创建消息监听器所在的容器工厂实例
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new SimpleMessageConverter());
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(1);
        factory.setPrefetchCount(1);
        //设置确认消费模式为自动确认消费AUTO
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);

        return factory;
    }

    @Bean(name = "autoQueue")
    public Queue autoQueue() {
        return new Queue(env.getProperty("mq.auto.knowledge.queue.name"), true);
    }

    @Bean
    public DirectExchange autoExchange() {
        return new DirectExchange(env.getProperty("mq.auto.knowledge.exchange.name"), true, false);
    }

    @Bean
    public Binding autoBinding() {
        return BindingBuilder.bind(autoQueue()).to(autoExchange()).with(env.getProperty("mq.auto.knowledge.routing.key.name"));
    }

    /**
     * 单一消费者-确认模式为MANUAL
     *
     * @return
     */
    @Bean(name = "manualQueue")
    public Queue manualQueue() {
        return new Queue(env.getProperty("mq.manual.knowledge.queue.name"), true);
    }

    @Bean
    public TopicExchange manualExchange() {
        return new TopicExchange(env.getProperty("mq.manual.knowledge.exchange.name"), true, false);
    }

    @Bean
    public Binding manualBinding() {
        return BindingBuilder.bind(manualQueue()).to(manualExchange()).with(env.getProperty("mq.manual.knowledge.routing.key.name"));
    }

    /**
     * 定义手动确认消费模式对应的消费者监听器实例
     */
    @Autowired
    private KnowledgeManualConsumer knowledgeManualConsumer;

    /**
     * 创建消费者监听者容器工厂实例-确认模式为MANUAL，并制定监听的队列和消费者
     *
     * @param manualQueue
     * @return
     */
    @Bean(name = "simpleContainerManual")
    public SimpleMessageListenerContainer simpleContainer(@Qualifier("manualQueue") Queue manualQueue) {
        //创建消息监听器容器实例
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        //设置链接工厂
        container.setConnectionFactory(connectionFactory);

        //单一消费者实例配置
        container.setConcurrentConsumers(1);
        container.setMaxConcurrentConsumers(1);
        container.setPrefetchCount(1);

        //TODO:设置消息的确认模式，采用手动确认消费机制
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        //指定该容器中监听的队列
        container.setQueues(manualQueue);
        //指定该容器中消息监听器，即消费者
        container.setMessageListener(knowledgeManualConsumer);

        return container;
    }

    /**
     * 用户登录成功写日志消息模型创建
     */
    @Bean(name = "loginQueue")
    public Queue loginQueue() {
        return new Queue(env.getProperty("mq.login.queue.name"), true);
    }

    @Bean
    public TopicExchange loginExchange() {
        return new TopicExchange(env.getProperty("mq.login.exchange.name"), true, false);
    }

    @Bean
    public Binding loginBinding() {
        return BindingBuilder.bind(loginExchange()).to(loginExchange()).with(env.getProperty("mq.login.routing.key.name"));
    }

    /**
     * 死信队列消息模型构建
     */

    /**
     * 创建死信队列
     *
     * @return
     */
    @Bean
    public Queue basicDeadQueue() {
        //创建死信队列的组成成分map，用于存放组成成分的相关成员
        Map<String, Object> args = new HashMap<>();
        //创建死信交换机
        args.put("x-dead-letter-exchange", env.getProperty("mq.dead.exchange.name"));
        //创建死信路由
        args.put("x-dead-letter-routing-key", env.getProperty("mq.dead.routing.key.name"));
        //设定TTL，单位为ms，这里是10s
        args.put("x-message-ttl", 10000);

        //创建并返回死信队列实例
        return new Queue(env.getProperty("mq.dead.queue.name"), true, false, false, args);
    }

    /**
     * 创建“基本消息模型”的基本交换机-面向生产者
     *
     * @return
     */
    @Bean
    public TopicExchange basicProducerExchange() {
        return new TopicExchange(env.getProperty("mq.producer.basic.exchange.name"), true, false);
    }

    /**
     * 创建“基本消息模型”的基本保定-基本交换机+基本路由-面向生产者
     *
     * @return
     */
    @Bean
    public Binding basicProducerBinding() {
        return BindingBuilder.bind(basicDeadQueue()).to(basicProducerExchange()).with(env.getProperty("mq.producer.basic.routing.key.name"));
    }

    /**
     * 创建真正的队列-面向消费者
     * @return
     */
    @Bean
    public Queue realConsumerQueue() {
        return new Queue(env.getProperty("mq.consumer.real.queue.name"), true);
    }

    /**
     * 创建死信交换机
     * @return
     */
    @Bean
    public TopicExchange basicDeadExchange() {
        return new TopicExchange(env.getProperty("mq.dead.exchange.name"), true, false);
    }

    /**
     * 创建死信路由及其绑定
     * @return
     */
    @Bean
    public Binding basicDeadBinding() {
        return BindingBuilder.bind(realConsumerQueue()).to(basicDeadExchange()).with(env.getProperty("mq.dead.routing.key.name"));
    }

    /**
     * 用户下单支付超时—RabbitMQ死信队列消息模型构建
     */

    /**
     * 创建死信队列
     * @return
     */
    @Bean
    public Queue orderDeadQueue() {
        //构建映射Map，用于添加死信队列的组成成分，并最终作为创建死信队列的最后一个参数
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", env.getProperty("mq.order.dead.exchange.name"));
        args.put("x-dead-letter-routing-key", env.getProperty("mq.order.dead.routing.key.name"));
        //设定TTL，单位为ms，在这里为了测试方便，设置为10s，实际业务场景可能为1小时或者更长
        args.put("x-message-ttl", 10000);

        return new Queue(env.getProperty("mq.order.dead.queue.name"), true, false, false, args);
    }

    /**
     * 创建“基本消息模型”的基本交换机-面向生产者
     * @return
     */
    @Bean
    public TopicExchange orderProducerExchange() {
        return new TopicExchange(env.getProperty("mq.producer.order.exchange.name"), true, false);
    }

    /**
     * 创建“基本消息模型”的基本绑定-基本交换机+基本路由-面向生产者
     * @return
     */
    @Bean
    public Binding orderProducerBinding() {
        return BindingBuilder.bind(orderDeadQueue()).to(orderProducerExchange()).with(env.getProperty("mq.producer.order.routing.key.name"));
    }

    /**
     * 创建真正队列-面向消费者
     * @return
     */
    @Bean
    public Queue realOrderConsumerQueue() {
        return new Queue(env.getProperty("mq.consumer.order.real.queue.name"));
    }

    /**
     * 创建死信交换机
     * @return
     */
    @Bean
    public TopicExchange basicOrderDeadExchange() {
        return new TopicExchange(env.getProperty("mq.order.dead.exchange.name"), true, false);
    }

    /**
     * 创建死信路由及其绑定
     * @return
     */
    @Bean
    public Binding basicOrderDeadBinding() {
        return BindingBuilder.bind(realOrderConsumerQueue()).to(basicOrderDeadExchange()).with(env.getProperty("mq.order.dead.routing.key.name"));
    }

    /**Redisson篇章-RabbitMQ死信队列的缺陷**/

    //创建死信队列-由死信交换机+死信路由组成
    @Bean
    public Queue redissonBasicDeadQueue(){
        Map<String,Object> argsMap=new HashMap<>();
        argsMap.put("x-dead-letter-exchange", env.getProperty("mq.redisson.dead.exchange.name"));
        argsMap.put("x-dead-letter-routing-key", env.getProperty("mq.redisson.dead.routing.key.name"));
        return new Queue(env.getProperty("mq.redisson.dead.queue.name"),true,false,false,argsMap);
    }

    //创建基本交换机
    @Bean
    public TopicExchange redissonBasicExchange() {
        return new TopicExchange(env.getProperty("mq.redisson.dead.basic.exchange.name"), true, false);
    }

    //创建基本路由及其绑定-绑定到死信队列
    @Bean
    public Binding redissonBasicBinding() {
        return BindingBuilder.bind(redissonBasicDeadQueue())
                .to(redissonBasicExchange()).with(env.getProperty("mq.redisson.dead.basic.routing.key.name"));
    }

    //创建死信交换机
    @Bean
    public TopicExchange redissonBasicDeadExchange() {
        return new TopicExchange(env.getProperty("mq.redisson.dead.exchange.name"), true, false);
    }

    //创建真正队列 - 面向消费者
    @Bean
    public Queue redissonBasicDeadRealQueue() {
        return new Queue(env.getProperty("mq.redisson.real.queue.name"), true);
    }

    //创建死信路由及其绑定-绑定到真正的队列
    @Bean
    public Binding redissonBasicDeadRealBinding() {
        return BindingBuilder.bind(redissonBasicDeadRealQueue())
                .to(redissonBasicDeadExchange()).with(env.getProperty("mq.redisson.dead.routing.key.name"));
    }
}
