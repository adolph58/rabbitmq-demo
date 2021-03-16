package com.example.common.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Arte
 * @create 21-3-16
 */
@Configuration
public class MQConfig {

    public static final String TEST_DIRECT_EXCHANGE = "testDirectExchange";

    /**
     * 死信交换机
     */
    public static final String DLX_EXCHANGE = "dlx.exchange";

    /**
     * 死信队列
     */
    public static final String DLX_QUEUE = "dlx.queue";
    public static final String DLX_ROUTINGKEY = "dlx.routing.key";

    public static final String TEST_QUEUE = "test.test";
    public static final String TEST_ROUTINGKEY = "test.test.routingkey";

    public static final String DEMO_QUEUE = "test.demo";
    public static final String DEMO_ROUTINGKEY = "test.demo.routingkey";

    public static final String FANOUT_EXCHANGE = "fanoutExchange";

    public static final String FANOUT_QUEUE_A = "fanout.a";
    public static final String FANOUT_QUEUE_B = "fanout.b";
    public static final String FANOUT_QUEUE_C = "fanout.c";

    @Bean
    public RabbitTemplate createRabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }

    /**
     * Direct 交换机
     */
    @Bean
    public DirectExchange testDirectExchange() {
        return new DirectExchange(TEST_DIRECT_EXCHANGE, true, false);
    }


    @Bean
    public Queue queueA() {
        return new Queue(FANOUT_QUEUE_A,true, false, false, getDLXArgs());
    }

    @Bean
    public Queue queueB() {
        return new Queue(FANOUT_QUEUE_B,true, false, false, getDLXArgs());
    }

    @Bean
    public Queue queueC() {
        return new Queue(FANOUT_QUEUE_C,true, false, false, getDLXArgs());
    }

    @Bean
    FanoutExchange fanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE);
    }

    @Bean
    Binding bindingExchangeA() {
        return BindingBuilder.bind(queueA()).to(fanoutExchange());
    }

    @Bean
    Binding bindingExchangeB() {
        return BindingBuilder.bind(queueB()).to(fanoutExchange());
    }

    @Bean
    Binding bindingExchangeC() {
        return BindingBuilder.bind(queueC()).to(fanoutExchange());
    }

    /**
     * 队列
     */
    @Bean
    public Queue testQueue() {
        return new Queue(TEST_QUEUE,true, false, false, getDLXArgs());
    }

    /**
     * 绑定队列和交换机
     */
    @Bean
    public Binding bindingTestQueue() {
        return BindingBuilder.bind(testQueue()).to(testDirectExchange()).with(TEST_ROUTINGKEY);
    }

    /**
     * 队列-绑定死信队列
     * 没有绑定：return new Queue(DEMO_QUEUE,true)
     */
    @Bean
    public Queue demoQueue() {
        return new Queue(DEMO_QUEUE,true, false, false, getDLXArgs());
    }

    /**
     * 绑定队列和交换机
     */
    @Bean
    public Binding bindingDemoQueue() {
        return BindingBuilder.bind(demoQueue()).to(testDirectExchange()).with(DEMO_ROUTINGKEY);
    }

    /**
     * 死信交换机
     */
    @Bean("dlxExchange")
    public DirectExchange dlxExchange() {
        return new DirectExchange(DLX_EXCHANGE, true, false);
    }

    /**
     * 死信队列
     */
    @Bean
    public Queue dlxQueue() {
        return new Queue(DLX_QUEUE, true);
    }

    /**
     * 绑定死信队列和交换机
     */
    @Bean
    public Binding bindingDLXQueue() {
        return BindingBuilder.bind(dlxQueue()).to(dlxExchange()).with(DLX_ROUTINGKEY);
    }

    /**
     * 获取死信队列参数
     *
     * @return
     */
    private Map<String, Object> getDLXArgs() {
        Map<String, Object> args = new HashMap<>(2);
        args.put("x-dead-letter-exchange", DLX_EXCHANGE);
        // 也可以为这个DLX指定路由键,如果没有特殊指定,则使用原队列的路由键
        args.put("x-dead-letter-routing-key", DLX_ROUTINGKEY);
        return args;
    }
}
