## 一、Dubbo概述

### 1. 分布式

分布式系统是若干独立的计算机集合, 但是这些计算机对于用户来说就像是一个完整的单个系统.

### 2. RPC&&Dubbo

在分布式系统中, 计算及之间需要通信, 这个时候就需要RPC框架. 

RPC（Remote Procedure Call Protocol）远程过程调用协议。一个通俗的描述是：客户端在不知道调用细节的情况下，调用存在于远程计算机上的某个对象和方法，就像调用本地应用程序中的对象和方法一样。比较正式的描述是：一种通过网络从远程计算机程序上请求服务，而不需要了解底层网络技术的协议。

Dubbo([ˈdʌbəʊ])是阿里巴巴公司开源的一个高性能优秀的服务框架，使得应用可通过高性能的 RPC 实现服务的输出和输入功能，可以和Spring框架无缝集成。它提供了三大核心能力：面向接口的远程方法调用，智能容错和负载均衡，以及服务自动注册和发现。

**问: 为什么使用RPC进行进程通信而不直接使用HTTP?**

http使用没有任何问题，首先业务简单，系统之间的交互不是特别多的情况下是可以使用http 没有任何问题，

但是如果是一个大型的网站，内部子系统较多、接口非常多的情况下，RPC框架的好处就显示出来了，

+ 传输协议
    - RPC，可以基于TCP协议，也可以基于HTTP协议
    - HTTP，基于HTTP协议
+ 传输效率
    - RPC，使用自定义的TCP协议，可以让请求报文体积更小，或者使用HTTP2协议，也可以很好的减少报文的体积，提高传输效率
    - HTTP，如果是基于HTTP1.1的协议，请求中会包含很多无用的内容，如果是基于HTTP2.0，那么简单的封装以下是可以作为一个RPC来使用的，这时标准RPC框架更多的是服务治理
+ 性能消耗，主要在于序列化和反序列化的耗时
    - RPC，可以基于thrift实现高效的二进制传输
    - HTTP，大部分是通过json来实现的，字节大小和序列化耗时都比thrift要更消耗性能
+ 负载均衡
    - RPC，基本都自带了负载均衡策略
    - HTTP，需要配置Nginx，HAProxy来实现
+ 服务治理（下游服务新增，重启，下线时如何不影响上游调用者）
    - RPC，能做到自动通知，不影响上游
    - HTTP，需要事先通知，修改Nginx/HAProxy配置

### 3. 注册中心

 将所有的服务都注册到注册中心中, 统一管理. 方便请求找到对应服务、监控服务运行状态、请求合理分配.

### 4. Dubbo的特点

+ **面向接口代理的高性能RPC调用**
    隐藏了远程调用底层细节, 只需要知道远程调用的接口即可
+ **智能负载均衡**
+ **服务自动注册与发现**
    支持多种注册中心服务
+ **高度可扩展能力**
+ **运行期流量调度**
    可配置不同路由规则, 实现灰度发布(即一部分访问新服务, 一部分访问旧服务)
+ **可视化服务治理与运维**

### 5. Dubbo设计架构

Dubbo设计架构采用了订阅发布模式.

![](https://blog-1258617239.cos.ap-chengdu.myqcloud.com/blog_images/Dubbo设计架构.png)

## 二、Dubbo快速启动

### 1. 搭建ZooKeeper注册中心



### 2. 搭建可视化监控中心(需开启ZooKppper)

1. 在github上下载dubbo-admin项目
2. 在` dubbo-admin-server/src/main/resources/application.properties `中进行个性化配置
3. 进入cmd(cmd位置为pom.xml所在位置, 这个项目是使用springboot完成的), 通过`mvn clean package`命令进行打包(先clean再package).
4. 通过` java -jar dubbo-admin-0.1.jar `运行该项目
5. 通过`http://localhost:8080`(可以在配置文件中配置)



### 3. 创建服务提供者

1. 导入相关jar包`dubbo.jar`、`curator-framwork.jar`. 其中`curator`是用于操作zookeeper的客户端
2. 定义服务接口

```java
package org.apache.dubbo.demo;

public interface DemoService {
    String sayHello(String name);
}
```

3. 服务端实现该接口

```java
package org.apache.dubbo.demo.provider;
 
import org.apache.dubbo.demo.DemoService;
 
public class DemoServiceImpl implements DemoService {
    public String sayHello(String name) {
        return "Hello " + name;
    }
}
```

4. 用spring配置暴露该服务(添加到注册中心)

```xml
<?xml version="1.0" encoding="UTF-8">
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:dubbo="http://dubbo.apache.org/schema/dubbo"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
    http://www.springframework.org/schema/beans/spring-beans-4.3.xsd
    http://dubbo.apache.org/schema/dubbo
   	http://dubbo.apache.org/schema/dubbo/dubbo.xsd">
 
    <!-- 指定当前服务/应用的名字 -->
    <dubbo:application name="hello-world-app"  />
 
    <!-- 指定注册中心的地址 -->
    <dubbo:registry address="zookeeper://127.0.0.1:2181" />
 
    <!-- 指定消费者和服务者的通信规则(协议和端口号) -->
    <dubbo:protocol name="dubbo" port="20880" />
 
    <!-- 声明需要暴露的服务接口 -->
    <dubbo:service interface="org.apache.dubbo.demo.DemoService" ref="demoService" />
 
    <!-- 和本地bean一样实现服务 -->
    <bean id="demoService" class="org.apache.dubbo.demo.provider.DemoServiceImpl" />
</beans>
```

5. 加载spring配置

```java
import org.springframework.context.support.ClassPathXmlApplicationContext;
 
public class Provider {
    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"http://10.20.160.198/wiki/display/dubbo/provider.xml"});
        context.start();
        System.in.read(); // 按任意键退出, 防止容器关闭
    }
}
```




### 4. 创建消费者

1. 进行spring配置

```xml
<?xml version="1.0" encoding="UTF-8">
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:dubbo="http://dubbo.apache.org/schema/dubbo"
    xsi:schemaLocation="http://www.springframework.org/schema/beans   
    http://www.springframework.org/schema/beans/spring-beans-4.3.xsd 
    http://dubbo.apache.org/schema/dubbo
 	http://dubbo.apache.org/schema/dubbo/dubbo.xsd">
 
    <!-- 消费方应用名，用于计算依赖关系，不是匹配条件，不要与提供方一样 -->
    <dubbo:application name="consumer-of-helloworld-app"  />
 
    <!-- 注册中心地址 -->
    <dubbo:registry address="zookeeper://127.0.0.1:2181" />
 
    <!-- 生成远程服务代理，可以和本地bean一样使用demoService -->
    <dubbo:reference id="demoService" interface="org.apache.dubbo.demo.DemoService" />
</beans>
```

2. 调用远程接口

```java
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.apache.dubbo.demo.DemoService;
 
public class Consumer {
    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"http://10.20.160.198/wiki/display/dubbo/consumer.xml"});
        context.start();
        DemoService demoService = (DemoService)context.getBean("demoService"); // 获取远程服务代理
        String hello = demoService.sayHello("world"); // 执行远程方法
        System.out.println( hello ); // 显示调用结果
    }
}
```



### 5. 注意事项

1. 服务端需要实现接口, 同时消费者端也需要有相关接口的定义, 且消费端的接口所在的包应该与服务端接口所在的包完全一致. 



## 三、Spring Boot整合Dubbo

### 1. 案例

1. 导入dubbo-spring-boot-starter

2. 分别在服务端和消费者端配置`application.properties`(配置信息与spring标签+属性一一对应)

    ```properties
    dubbo.application.name=dubbo-test
    dubbo.registry.address=zookeeper://127.0.0.1:2181
    dubbo.monitor.protocol=registry
    ```

3. 在主程序中加上注解`@EnableDubbo`开启注解支持(包扫描), 也可以通过`dubbo.scan.base-packages`指定包路径
4. 在服务端接口实现类上加上注解`@Service` (该注解是Dubbo的注解)
5. 在消费者端将`@Autowired`替换为`@Reference`



### 2. Spring Boot使用Dubbo的三种方式

1. 使用`@Service`、`@Reference`、`@EnableDubbo`等注解. 在注解中可以配置对应属性的值, 如timeout、vresion、retries等. 这些属性与xml中的属性是一一对应的.
2. 在主程序中使用`@ImportResource(locatios="classpath:dubbo.xml")`, 即可使用xml的方式进行配置
3. 通过配置类进行配置(不建议)



## 四、Dubbo配置

### 1. 配置优先级

Dubbo有三种配置方式, 其中按优先级配置应该是

(1) Java命令行配置 eg:`-Dubbo,protocol.port=20880`

(2) XML配置

(3) properties配置(在Spring Boot中在application.properties中配置, 在spring项目中在dubbo.properties中配置以替代在xml中配置, 一般公共的配置都可以在dubbo.properties中进行)



### 2. 启动时检查

在Dubbo中默认启动时检查是开启的, 如果消费者端先开启, 而服务端未开启, 则消费者端就算没调用接口, 也无法启动. 可以在消费者端通过配置关闭启动时检查

```xml
<dubbo:reference id="demoService" interface="org.apache.dubbo.demo.DemoService" check="false" />

<!--还可以对所有消费者进行全局配置-->
<dubbo:consumer中的配置就是一些dubbo:reference的默认配置>
<dubbo:consumer check="false" />
```

1. 配置注册中心未启动时, 服务端正常启动

```xml
<dubbo:registry check="false" />
```



### 3. Timeout

为了避免响应时间过长, 可以设置超时时间.(默认为dubbo:consumer的配置 1000毫秒)

```xml
<dubbo:reference timeout="1000">
```

同一配置可以出现在很多不同的地方, 但是它们遵循一定的优先级:

- 方法级优先，接口级次之，全局配置再次之。
- 如果级别一样，则消费方优先，提供方次之。

![](https://blog-1258617239.cos.ap-chengdu.myqcloud.com/blog_images/Dubbo不同粒度配置优先级.jpg)



### 4. 重试次数

有时与服务端连接超时, 可以设置重试次数来再次发送请求.

如果有多个服务端, 那么在第一个服务端请求失败后, 重试时会重新向下一个服务端请求.

```xml
<dubbo:reference retries="3"/>
```



### 5. 多版本(灰度发布)

服务端接口实现方法进行了更新, 但是还在测试阶段, 只需要一小部分消费者使用新的实现方法, 这时候就可以指定接口与接口实现类的版本来实现灰度发布.

在服务端为接口实现类设置版本

```xml
 <dubbo:service interface="org.apache.dubbo.demo.DemoService" ref="demoService" version="2.0.0"/>
```

在消费者端设置调用的版本号

```xml
 <dubbo:reference id="demoService" interface="org.apache.dubbo.demo.DemoService" version="2.0.0"/>
```



### 6. 本地存根

远程服务后，客户端通常只剩下接口，而实现全在服务器端，但提供方有些时候想在客户端也执行部分逻辑，比如：做 ThreadLocal 缓存，提前验证参数，调用失败后伪造容错数据等等，此时就需要在 API 中带上 Stub，客户端生成 Proxy 实例，会把 Proxy 通过构造函数传给 Stub，然后把 Stub 暴露给用户，Stub 可以决定要不要去调 Proxy。

![](https://blog-1258617239.cos.ap-chengdu.myqcloud.com/blog_images/本地存根.jpg)

在 spring 配置文件中按以下方式配置：

```xml
<dubbo:service interface="com.foo.BarService" stub="true" />
```

或

```xml
<dubbo:service interface="com.foo.BarService" stub="com.foo.BarServiceStub" />
```

提供 Stub 的实现：

```java
package com.foo;
public class BarServiceStub implements BarService {
    private final BarService barService;
    
    // 构造函数传入真正的远程代理对象
    public BarServiceStub(BarService barService){
        this.barService = barService;
    }
 
    public String sayHello(String name) {
        // 此代码在客户端执行, 你可以在客户端做ThreadLocal本地缓存，或预先验证参数是否合法，等等
        try {
            return barService.sayHello(name);
        } catch (Exception e) {
            // 你可以容错，可以做任何AOP拦截事项
            return "容错数据";
        }
    }
}
```



## 五、Dubbo高可用

### 1. ZooKeeper宕机与Dubbo直连

+ 监控中心宕机不会影响使用, 只是丢失部分采样数据
+ 数据库宕掉后, 注册中心任能够通过缓存提供服务列表查询, 但不能注册新服务
+ 注册中心对等集群, 任意一台宕掉后, 将自动切换到下一台
+ 注册中心全部宕掉后, 服务提供者和服务消费者任能通过本地缓存通讯
+ 服务提供者无状态, 任意一台宕掉后, 不影响使用
+ 服务提供者全部宕掉后, 服务消费者应用将无法使用. 并无限次重连等待服务提供者恢复



消费者和服务者可以通过`url`属性进行直连, 绕过注册中心

```xml
<dubbo:reference url="127.0.0.1"/>
```

```java
@Reference(url="127.0.0.1")
```



### 2. 负载均衡

在集群负载均衡时，Dubbo 提供了多种均衡策略，缺省为 `random` 随机调用。 

**集中负载均衡策略:**

+ **Random LoadBalance**

    随机，按权重设置随机概率。

    在一个截面上碰撞的概率高，但调用量越大分布越均匀，而且按概率使用权重后也比较均匀，有利于动态调整提供者权重。

+ **RoundRobin LoadBalance**

    轮询，按公约后的权重设置轮询比率。

    存在慢的提供者累积请求的问题，比如：第二台机器很慢，但没挂，当请求调到第二台时就卡在那，久而久之，所有请求都卡在调到第二台上。

+ **LeastActive LoadBalance**

    最少活跃调用数，相同活跃数的随机，活跃数指调用前后计数差。

    使慢的提供者收到更少请求，因为越慢的提供者的调用前后计数差会越大。

+ **ConsistentHash LoadBalance**

    一致性 Hash，相同参数的请求总是发到同一提供者。

可以在服务端也可以在消费端配置负载均衡策略

```xml
<dubbo:service interface="..." loadbalance="roundrobin" />
```



### 3. 服务降级

可以通过服务降级功能 [[1\]](http://dubbo.apache.org/zh-cn/docs/user/demos/service-downgrade.html#fn1) 临时屏蔽某个出错的非关键服务，并定义降级后的返回策略。

向注册中心写入动态配置覆盖规则：

```java
RegistryFactory registryFactory = ExtensionLoader.getExtensionLoader(RegistryFactory.class).getAdaptiveExtension();
Registry registry = registryFactory.getRegistry(URL.valueOf("zookeeper://10.20.153.10:2181"));
registry.register(URL.valueOf("override://0.0.0.0/com.foo.BarService?category=configurators&dynamic=false&application=foo&mock=force:return+null"));
```

其中：

- `mock=force:return+null` 表示消费方对该服务的方法调用都直接返回 null 值，不发起远程调用。用来屏蔽不重要服务不可用时对调用方的影响。
- 还可以改为 `mock=fail:return+null` 表示消费方对该服务的方法调用在失败后，再返回 null 值，不抛异常。用来容忍不重要服务不稳定时对调用方的影响。



