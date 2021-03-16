package com.example.common.sender;

import com.example.common.config.MQConfig;
import com.example.common.dto.MessageBody;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Arte
 * @create 2020/8/4.
 * 通用 RabbitMQ 发送消息方法
 */
@Slf4j
@Component
public class RabbitMQSender {

    private static RabbitTemplate rabbitTemplate;

    @Autowired
    public RabbitMQSender(RabbitTemplate rabbitTemplate) {
        RabbitMQSender.rabbitTemplate = rabbitTemplate;
    }

    private static String defaultExchange = MQConfig.TEST_DIRECT_EXCHANGE;

    public static void sendMessage(String routingKey, Object data) {
        sendMessage(routingKey, data, defaultExchange);
    }

    public static void sendMessage(String routingKey, Object data, String exchange) {
        if (StringUtils.isEmpty(exchange)) {
            exchange = defaultExchange;
        }

        MessageBody messageBody = new MessageBody(data);
        log.info("send mq-message:" + data.toString());
        log.info("messageId:" + messageBody.getMessageId());
        //将消息携带绑定键值：routingKey 发送到交换机：exchange
        rabbitTemplate.convertAndSend(exchange, routingKey, messageBody);
    }

}