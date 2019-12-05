## 一、消息中间件

### 1. 消息队列(MQ)

在高并发环境下，由于来不及同步处理，请求往往会发生堵塞，比如说，大量的insert，update之类的请求同时到达MySQL，直接导致无数的行锁表锁，甚至最后请求会堆积过多，从而触发连接错误。通过使用消息队列，我们可以异步处理请求，从而缓解系统的压力。

### 2. 消息中间件

消息中间件利用高效可靠的消息传递机制进行==平台无关==的数据交流，并基于数据通信来进行分布式系统的集成。通过提供消息传递和消息排队模型，它可以在分布式环境下扩展进程间的通信。对于消息中间件，常见的角色大致也就有Producer（生产者）、Consumer（消费者）

### 3. 消息中间键常见应用场景

#### (1) 异步处理

如用户注册功能, 注册后需向用户发送手机短信和电子邮件. 如果采用传统串行方式, 那么需要等待三个操作都完成后才能将结果返回给用户, 用户等待时间长.

因为发送短信和邮件并不是最核心的业务逻辑, 所以可以通过消息队列来进行异步处理, 这样用户的等待时间就只有注册信息写入数据库的时间和写入消息队列的时间(该时间极小).

#### (2) 解耦

如A系统发送数据给B、C、D三个系统, 通过接口调用发送. 后续如果需要增加更多的系统接收A系统的数据或者部分系统取消接收A的数据, 那么A系统就需要不断地修改.

这种情况下, A可以将数据发送到MQ中, 那个系统需要数据就去MQ中去取, 而不再需要A来发送. 这种消息中间件的发布订阅功能实现了系统的解耦.

#### (3) 削峰

如电商平台数据库在秒杀时期或购物高峰期每秒需要处理很多请求, 而在其他时间只有少量请求需要处理. 为了保证数据库服务器在高峰期不会挂掉, 可以使用消息中间件来存积请求, 数据库根据自身负载能力从消息中间件中获取请求并处理. 

这样虽然短时间内会有大量请求存积在消息中间件中, 但是可以保证服务器的稳定, 且在高峰期后, 请求变少, 服务器可以快速处理完存积在消息中间件中的请求.



## 二、RabbitMQ概述

### 1. RabbitMQ介绍

> RabbitMQ是实现了高级消息队列协议（AMQP）的开源消息代理软件（亦称面向消息的中间件）RabbitMQ服务器是用Erlang语言编写的，而群集和故障转移是构建在开放电信平台框架上的。所有主要的编程语言均有与代理接口通讯的客户端库。

### 2. RabbitMQ优点

+ 开源、性能优秀、稳定性保障
+ 提供可靠性消息投递模式(confirm)、返回模式(return)
+ 与SpringAMPQ完美整合, API丰富
+ 集群模式丰富、表达式配置、HA模式、镜像队列模型
+ 在保证数据不丢失的情况下做到高可靠性、高可用性

### 3. RabbitMQ高性能原因

RabbitMQ高性能的原因是因为它是用Erlang语言编写的.

Erlang语言最初在于交换机领域的架构模式, 它有着和原生Socket一样的延迟.

### 4. 高级消息队列协议(AMPQ)

#### (1) 概念

> AMPQ是具有现代特征的二进制协议. 是一个提供统一消息服务的应用层标准高级消息队列协议, 是应用层协议的一个开放标准, 为面向消息的中间件设计.

#### (2) AMPQ模型

![](images/AMPQ模型.png)

#### (3) AMPQ核心概念

+ **Server:** 又称Broker, 接收客户端的连接, 实现AMPQ实体服务
+ **Connection:** 连接, 应用程序与Broker的网络连接
+ **Channel:** 网络信道, 几乎所有的操作都在Channel中进行, Channel是进行消息读写的通道. 客户端可建立多个Channel, 每个Channel代表一个会话任务.
+ **Message:** 消息, 服务器和应用程序之间传送的数据. 由Properties和Body组成. Properties可以对消息进行修饰, 比如消息的优先级、延迟等高级特性; Body就是消息体内容.
+ **Virtual host:** 虚拟地址, 用于进行==逻辑隔离==, 最上层的消息路由. 一个Virtual host里可以由若干个Exchange和Queue, 同一个Virtual host里面不能有相同名称的Exchange或Queue
+ **Exchange:** 交换机, 接收消息, 根据路由键转发消息到绑定的队列(注意队列要与交换机绑定).
+ **Binding:** Exchange和Queue之间的虚拟连接, Binding中可以包含路由键(routing key)
+ **Routing key:** 一个路由规则, Virtual host可用它确定如何路由一个特定消息. 比如在Direct Exchange中, 当队列的Routing Key与消息的Routing Key一致时, 交换机就会将消息存入该队列.
+ **Queue:** 也成为Message Queue, 消息队列, 保存消息并将它们转发给消费者

#### (4) RabbitMQ架构图

![](images/RabbitMQ架构图.jpg)



## 三、RabbitMQ安装

1. 准备编译环境

    ```shell
    yum install 
    build-essential openssl openssl-devel unixODBC unixODBC-devel 
    make gcc gcc-c++ kernel-devel m4 ncurses-devel tk tc xz
    ```

2. 下载相关rpm包(这里使用rpm安装方式, 可以自动进行配置环境变量等操作, 更适合初学者)

    因为RabbitMQ是由Erlang语言编写的, 所以需要先安装Erlang.

    socat也是其依赖环境

    ```shell
    rpm -ivh erlang-18.3-1.el7.centos.x86_64.rpm
    rpm -ivh socat-1.7.3.2-5.el7.lux.x86_64.rpm
    rpm -ivh rabbitmq-server-3.6.5-1.noarch.rpm
    ```

3. 修改配置文件

    配置文件是以json格式编写的, 可进行相应的更改.

    修改loopback_users中的<<"guest">>替换为guest

    ```shell
    vim /usr/lib/rabbitmq/lib/rabbitmq_server-3.6.5/ebin/rabbit.app
    ```

4. 通过`rabbitmq-server start &`启动, 其中&表示后台启动

5. 通过`rabbitmq-plugins enable rabbitmq-management`安装管控台插件, 安装后可以通过`15672`端口访问图形化管理界面

6. 通过`rabbitmqctl app_stop`停止服务



## 四、RabbitMQ基础

### 1. 命令行操作

RabbitMQ中的命令有三种开头, 分别是`rabbitmq-server`、`rabbitmqctl`、`rabbitmq-plugins`. 其中`rabbitmqctl`功能最丰富, `rabbitmq-plugins`用于插件管理.

==另外, 大多数命令行能完成的操作, 在图形化管控台中也可以完成.==

+ `rabbitmqctl start_app` : 开启应用
+ `rabbitmqctl stop_app` : 关闭应用
+ `rabbitmqctl status` : 查看节点状态

### 2. RabbitMQ Exchange

#### (1) 属性

+ **Name:** 交换机名称
+ **Type:** 交换机类型direct、topic、fanout、headers
+ **Durability:** 持久化
+ **Auto delete:** 当最后绑定在Exchange上的队列删除后, 自动删除该Exchange
+ **Internal:** 是否用于RabbitMQ内部使用, 默认为false
+ **Arguments:** 扩展参数, 用于扩展AMQP协议定制化使用

#### (2) Exchange类型

+ **Direct Exchange**

    在代码创建Exchange时, 需要将队列、Exchange、Routing Key三者绑定在一起.

    所有发送到Direct Exchange的消息会被转发到消息Routing Key与队列Routing Key相同的队列.

+ **Topic Exchange**

    发送到Topic Exchange中的消息, 会被转发到与消息有相同Topic的队列中.

    可以模糊匹配Topic, 其中#表示一个或多个词, *表示一个词.

     比如消息Routing Key为user.name, 那么其可以发送到Routing Key为user.#的队列中

    ![](images/Topic Exchage.png)



+ **Fanout Exchange**

    无需Routing Key, 发送到Fanout Exchange的消息, 会转发给其所有绑定的队列.

    ![](images/Fanout Exchage.png)



### 3. RabbitMQ Java API

1. 导入相关jar包: `amqp-client`

2. 编写Producer类

    ```java
    package com.bfxy.rabbitmq.quickstart;
    
    import com.rabbitmq.client.Channel;
    import com.rabbitmq.client.Connection;
    import com.rabbitmq.client.ConnectionFactory;
    
    public class Procuder {
    
    	
    	public static void main(String[] args) throws Exception {
    		//1 创建一个ConnectionFactory, 并进行配置
    		ConnectionFactory connectionFactory = new ConnectionFactory();
    		connectionFactory.setHost("192.168.11.76");
    		connectionFactory.setPort(5672);
    		connectionFactory.setVirtualHost("/");
    		
    		//2 通过连接工厂创建连接
    		Connection connection = connectionFactory.newConnection();
    		
    		//3 通过connection创建一个Channel
    		Channel channel = connection.createChannel();
    		
    		//4 通过Channel发送数据
    		for(int i=0; i < 5; i++){
    			String msg = "Hello RabbitMQ!";
    			//参数1 exchange  参数2 routingKey  参数3 properties  参数4 body
    			channel.basicPublish("", "test001", null, msg.getBytes());
    		}
    
    		//5 记得要关闭相关的连接
    		channel.close();
    		connection.close();
    	}
    }
    ```

3. 编写Consumer类

    ```java
    package com.bfxy.rabbitmq.quickstart;
    
    import com.rabbitmq.client.Channel;
    import com.rabbitmq.client.Connection;
    import com.rabbitmq.client.ConnectionFactory;
    import com.rabbitmq.client.Envelope;
    import com.rabbitmq.client.QueueingConsumer;
    import com.rabbitmq.client.QueueingConsumer.Delivery;
    
    public class Consumer {
    
    	public static void main(String[] args) throws Exception {
    		
    		//1 创建一个ConnectionFactory, 并进行配置
    		ConnectionFactory connectionFactory = new ConnectionFactory();
    		connectionFactory.setHost("192.168.11.76");
    		connectionFactory.setPort(5672);
    		connectionFactory.setVirtualHost("/");
    		
    		//2 通过连接工厂创建连接
    		Connection connection = connectionFactory.newConnection();
    		
    		//3 通过connection创建一个Channel
    		Channel channel = connection.createChannel();
    		
    		//4 声明（创建）一个队列
    		String queueName = "test001";
            //参数2 队列持久化  参数3 独占队列  参数4 是否自动删除(最后一个监听被移除后队列会被自动删除)  参数5 其他参数(此处为一个Map<String, Object>对象)
    		channel.queueDeclare(queueName, true, false, false, null);
    		
    		//5 创建消费者
    		QueueingConsumer queueingConsumer = new QueueingConsumer(channel);
    		
    		//6 设置Channel
            //参数2 autoask 消费者收到broker的消息后, 会返回一个ask签收信息, 这里指定是否自动签收
    		channel.basicConsume(queueName, true, queueingConsumer);
    		
    		while(true){
    			//7 获取消息
    			Delivery delivery = queueingConsumer.nextDelivery();
    			String msg = new String(delivery.getBody());
    			System.err.println("消费端: " + msg);
    			//Envelope envelope = delivery.getEnvelope();
    		}
    		
    	}
    }
    ```

**注意:**

+ 我们在生产者端通过channel发送信息时并没有指定对应的exchange, 但是消费者还是收到了生产者的消息. 这是因为在不指定exchange的情况下, 默认会有一个default exchange, 此时会从所有队列中匹配与routing key相同名字的队列, 将消息保存到该队列中.
+ 当消费者接收到消息后, 该消息就会从消息队列中删除.



## 五、RabbitMQ高级

### 1. 如何保证消息100%投递成功?

#### (1) 消息落库, 对消息状态进行打标(更新消息状态)

首先除了业务入库外(BIZ DB), 消息也应该入库(MSG DB), 当消息发送到MQ Broker时, Broker返回一个确认, 然后更新数据库中消息的状态.

这个过程中会添加一个分布式定时任务, 当定时时间到时, 会检查消息数据库中消息的状态, 如果状态为0则重新给Broker发送一次消息, 当然重发也应该有次数限制.

![](images/可靠性投递 1.png)



#### (2) 消息的延迟投递, 做二次确认, 回调检查

首先只需要将业务入库(BIZ DB), 发送消息给MQ Broker, 并且在延迟一段时间后再发送一次消息.

当消费者端监听到生产者的消息后, 收到消息后会发送一个确认消息给Broker.

CallBack service负责监听这个确认消息, 当监听到确认消息后, 说明消息投递成功, 并将消息写入数据库(MSG DB).

CallBack service同时还负责监听延迟消息, 监听到延迟消息后, 会从数据库中查询是否有对应的消息, 如果没有则会让生产者再次发送该消息.

这种方法与第一种方法相比, 减少了数据库的写入次数, 提高效率.

![](images/可靠性投递 2.png)