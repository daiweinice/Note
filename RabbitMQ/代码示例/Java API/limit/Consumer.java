package com.bfxy.rabbitmq.api.limit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;

public class Consumer {

	
	public static void main(String[] args) throws Exception {
		
		
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("192.168.11.76");
		connectionFactory.setPort(5672);
		connectionFactory.setVirtualHost("/");
		
		Connection connection = connectionFactory.newConnection();
		Channel channel = connection.createChannel();
		
		
		String exchangeName = "test_qos_exchange";
		String queueName = "test_qos_queue";
		String routingKey = "qos.#";
		
		channel.exchangeDeclare(exchangeName, "topic", true, false, null);
		channel.queueDeclare(queueName, true, false, false, null);
		channel.queueBind(queueName, exchangeName, routingKey);
		
		//设置消费者的最大处理数
		//第一个参数表示消息的大小限制, 0表示不限制
		//第二个参数表示同时处理的最大消息数
		//第三个参数表示限制范围是否是global. 如果是true则表示所有使用该channel的客户端都进行限制, false表示对单个消费者客户端进行限制
		channel.basicQos(0, 1, false);
		
		//如果要进行限制, 需要先将自动应答autoask设置为false
		channel.basicConsume(queueName, false, new MyConsumer(channel));
		
		
	}
}
