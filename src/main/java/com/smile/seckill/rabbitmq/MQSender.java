package com.smile.seckill.rabbitmq;

import com.smile.seckill.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 消息发送者
 */
@Service
public class MQSender {

	private static Logger log = LoggerFactory.getLogger(MQSender.class);
	
	@Autowired
	AmqpTemplate amqpTemplate;
	
	public void sendSeckillMessage(SeckillMessage seckillMessage) {
		String msg = RedisService.beanToString(seckillMessage);
		log.info("send message:" + msg);
		amqpTemplate.convertAndSend(MQConfig.SECKILL_QUEUE, msg);
	}

	/**
	 * 向 MQConfig.QUEUE 发送消息
	 * @param message
	 */
	public void send(Object message) {
		String msg = RedisService.beanToString(message);
		log.info("send message:" + msg);
		amqpTemplate.convertAndSend(MQConfig.QUEUE, msg);
	}

    /**
     * 向交换机 MQConfig.TOPIC_EXCHANGE 发送消息
     * @param message
     */
	public void sendTopic(Object message) {
		String msg = RedisService.beanToString(message);
		log.info("send topic message:" + msg);
		amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key1", msg + "1");
		amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key2", msg + "2");
	}

    /**
     * 向交换机 MQConfig.FANOUT_EXCHANGE 发送消息
     * @param message
     */
	public void sendFanout(Object message) {
		String msg = RedisService.beanToString(message);
		log.info("send fanout message:"+msg);
		amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE, "", msg);
	}

    /**
     * 向交换机 MQConfig.HEADERS_EXCHANGE 发送消息
     * @param message
     */
	public void sendHeader(Object message) {
		String msg = RedisService.beanToString(message);
		log.info("send fanout message:"+msg);
		MessageProperties properties = new MessageProperties();
		properties.setHeader("header1", "value1");
		properties.setHeader("header2", "value2");
		Message obj = new Message(msg.getBytes(), properties);
		amqpTemplate.convertAndSend(MQConfig.HEADERS_EXCHANGE, "", obj);
	}
}