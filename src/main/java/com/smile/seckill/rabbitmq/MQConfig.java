package com.smile.seckill.rabbitmq;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MQ 的配置定义
 */
@Configuration
public class MQConfig {
	
	public static final String SECKILL_QUEUE = "seckill.queue";
	public static final String QUEUE = "queue";
	public static final String TOPIC_QUEUE1 = "topic.queue1";
	public static final String TOPIC_QUEUE2 = "topic.queue2";
	public static final String HEADER_QUEUE = "header.queue";
	public static final String TOPIC_EXCHANGE = "topicExchange";
	public static final String FANOUT_EXCHANGE = "fanoutExchange";
	public static final String HEADERS_EXCHANGE = "headersExchange";

	/**
	 * Direct 模式 交换机为默认的Exchange
	 * @return
	 */
	@Bean
	public Queue queue() {
		return new Queue(QUEUE, true); // true 表示需要做序列化
	}

	/**
	 * 消息队列一
	 * @return
	 */
	@Bean
	public Queue topicQueue1() {
		return new Queue(TOPIC_QUEUE1, true);
	}
	/**
	 * 消息队列二
	 * @return
	 */
	@Bean
	public Queue topicQueue2() {
		return new Queue(TOPIC_QUEUE2, true);
	}

	/**
	 * Topic 模式 Exchange 交换机定义
	 * @return
	 */
	@Bean
	public TopicExchange topicExchange(){
		return new TopicExchange(TOPIC_EXCHANGE);
	}
	/**
	 * Topic 模式 Exchange 交换机与消息队列一绑定
	 * @return
	 */
	@Bean
	public Binding topicBinding1() {
		return BindingBuilder.bind(topicQueue1()).to(topicExchange()).with("topic.key1");
	}
	/**
	 * Topic 模式 Exchange 交换机与消息队列二绑定
	 * @return
	 */
	@Bean
	public Binding topicBinding2() {
		// # 表示模糊匹配
		return BindingBuilder.bind(topicQueue2()).to(topicExchange()).with("topic.#");
	}

	/**
	 * Fanout 模式 Exchange 交换机定义
	 */
	@Bean
	public FanoutExchange fanoutExchange(){
		return new FanoutExchange(FANOUT_EXCHANGE);
	}
	/**
	 * Fanout 模式 Exchange 交换机与消息队列一绑定
	 * @return
	 */
	@Bean
	public Binding FanoutBinding1() {
		return BindingBuilder.bind(topicQueue1()).to(fanoutExchange());
	}
	/**
	 * Fanout 模式 Exchange 交换机与消息队列二绑定
	 * @return
	 */
	@Bean
	public Binding FanoutBinding2() {
		return BindingBuilder.bind(topicQueue2()).to(fanoutExchange());
	}

	/**
	 * Header 模式 Exchange 交换机定义
	 * @return
	 */
	@Bean
	public HeadersExchange headersExchange(){
		return new HeadersExchange(HEADERS_EXCHANGE);
	}
	/**
	 * Header 模式 消息队列定义
	 * @return
	 */
	@Bean
	public Queue headerQueue1() {
		return new Queue(HEADER_QUEUE, true);
	}
	/**
	 * Header 模式 Exchange 交换机与消息队列绑定
	 * @return
	 */
	@Bean
	public Binding headerBinding() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("header1", "value1");
		map.put("header2", "value2");
		// 消息体中的 header 必须完全匹配
		return BindingBuilder.bind(headerQueue1()).to(headersExchange()).whereAll(map).match();
	}
}