package com.example.provider;

import com.example.common.config.MQConfig;
import com.example.common.sender.RabbitMQSender;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Arte
 * @create 21-3-16
 */
@SpringBootTest
public class ProviderApplicationTests {

    @Test
    void contextLoads() {
        System.out.println("test!!!");
    }

    @Test
    void sendMsg() {
        String s = "hello,test";
        RabbitMQSender.sendMessage(MQConfig.TEST_ROUTINGKEY, s);
    }

    @Test
    void sendFanoutMsg() {
        String s = "hello,fanout";
        RabbitMQSender.sendMessage(null, s, MQConfig.FANOUT_EXCHANGE);
    }
}
