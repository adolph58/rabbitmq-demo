package com.example.consumer.listener;

import com.example.common.config.MQConfig;
import com.example.common.dto.MessageBody;
import com.example.common.enumeration.Action;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author Arte
 * @create 2020/8/4.
 */
@Slf4j
@Component
public class RabbitMQConsumer {

    private final static int EXPIRATION_TIME = 60;
    private final static int INITIAL_VALUE = 0;
    private final static long FAIL_TIMES = 3;


	///**
	// * 监听死信队列
	// * concurrency:并发处理消息数
	// */
	//@RabbitListener(queues = MQConfig.DEMO_QUEUE, concurrency = "10")
	//@RabbitHandler
	//public void dlxQueueReceiver(Message message, Channel channel) throws IOException {
	//	long deliveryTag = message.getMessageProperties().getDeliveryTag();
	//	try{
	//		MessageBody messageBody = MessageBody.getMessageBody(message);
     //       System.out.println("custom-message-id：" + messageBody.getMessageId());
     //       System.out.println("getCreateTime：" + messageBody.getCreateTime());
	//		System.out.println("死信队列接受到消息：" + messageBody.getData().toString());
     //       System.out.println("getXDeathHeader：" + message.getMessageProperties().getXDeathHeader());
     //       Map<String, ?> map = message.getMessageProperties().getXDeathHeader().get(0);
     //       System.out.println("exchange:" + map.get("exchange"));
     //       System.out.println("queue:" + map.get("queue"));
     //       System.out.println("routing-keys:" + map.get("routing-keys"));
	//	}catch (Exception e) {
	//		e.printStackTrace();
	//	} finally {
	//		channel.basicAck(deliveryTag, false);
	//	}
	//}

	/**
	 * 监听单个队列
	 * concurrency:并发处理消息数, concurrency = "10"
	 */
	@RabbitListener(queues = MQConfig.TEST_QUEUE)
	@RabbitHandler
	public void testQueueReceiver(Message message, Channel channel) throws IOException {
		log.info("接收到单个队列消息");
		messageHandler(message, channel);
	}

	/**
	 * 监听多个队列
	 */
//	@RabbitListener(queues = {MQConfig.TEST_QUEUE, MQConfig.DEMO_QUEUE}, concurrency = "10")
//	@RabbitHandler
//	public void multipleQueueReceiver(Message message, Channel channel) throws IOException {
//		log.info("接收到多个队列消息");
//		//messageHandler(message, channel);
//	}

	private void messageHandler(Message message, Channel channel) throws IOException {
		long deliveryTag = message.getMessageProperties().getDeliveryTag();
		Action action = Action.ACCEPT;
        String messageId = "";
		try {
            MessageBody messageBody = MessageBody.getMessageBody(message);
            messageId = messageBody.getMessageId();
			log.info("接收到消息Body：{}", messageBody.toString());
			log.info("消息体大小：" + messageBody.getData(String.class).length());
		} catch (Exception e) {
            action = Action.REJECT;
//            long increment = redisUtilsTwo.increment(messageId);
//            redisUtilsTwo.expire(messageId, EXPIRATION_TIME, TimeUnit.SECONDS);
//            if (increment == FAIL_TIMES) {
//                action = Action.REJECT;
//                // TODO
//            }else {
//                action = Action.RETRY;
//            }
			log.error("处理消息出错", e);
		} finally {
			// 通过 finally 块来保证 Ack/Nack 会且只会执行一次
			if (action == Action.ACCEPT) {
				// false 只确认当前 consumer 一个消息收到，true 确认所有 consumer 获得的消息。
				channel.basicAck(deliveryTag, false);
			}else if (action == Action.RETRY){
                channel.basicReject(deliveryTag, true);
            } else {
				// 第二个 boolean 为 false 表示不会重试，为 true 会重新放回队列
				channel.basicReject(deliveryTag, false);
			}
		}
	}
}