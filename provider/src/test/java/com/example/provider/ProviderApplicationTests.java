package com.example.provider;

import com.example.common.config.MQConfig;
import com.example.common.sender.RabbitMQSender;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.IntStream;

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
        String s = "";
        StringBuffer sb = new StringBuffer();
        IntStream.range(0, 100000).forEach(i -> {
            sb.append(i);
        });
        s = sb.toString();

        System.out.println(s.length());

        RabbitMQSender.sendMessage(MQConfig.TEST_ROUTINGKEY, s);
    }
}
