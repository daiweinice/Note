# Spring Cloud

## 一、微服务架构与Spring Cloud

### 1. 微服务

![](https://blog-1258617239.cos.ap-chengdu.myqcloud.com/blog_images/微服务.png)

微服务架构

![](https://blog-1258617239.cos.ap-chengdu.myqcloud.com/blog_images/微服务架构.png)

### 2. Spring Cloud

Spring Cloud 是分布式微服务架构的一站式解决方案, 是多种微服务架构落地技术的集合, 俗称微服务全家桶。

### 3. 版本兼容

Spring Boot 以数字为版本, Spring Cloud 以字母为版本。

Spring Boot 的版本和 Spring Cloud 的版本需要保持兼容, 具体查看官网https://start.spring.io/actuator/info。这里使用 2.2.2 + H 的版本。

![](https://blog-1258617239.cos.ap-chengdu.myqcloud.com/blog_images/版本选择.png)

### 4. 技术更迭

![](https://blog-1258617239.cos.ap-chengdu.myqcloud.com/blog_images/技术更迭.png)

## 二、基础微服务工程搭建

约定 > 配置 > 编码

1. 建 module
2. 改 pom
3. 写 yml
4. 主启动
5. 业务类



## 三、服务注册中心

### 1. Eureka

在传统的 rpc 远程调用框架中, 管理每个服务与服务之间的依赖关系比较复杂, 所以需要服务治理。

Eureka 采用了 cs 设计架构, Eureka Server 作为服务注册功能的服务器, 它是服务注册中心, 而服务调用者通过 Eureka Client 连接到 Eureka Server 并维持心跳连接。这样系统就可以监控系统中的各个微服务是否正常运行。

同时在服务注册与发现中有一个注册中心。当服务启动时会把服务的信息如地址等以别名的方式注册到注册中心上。服务调用者以该别名从注册中心获取到实际的服务通讯地址, 然后底层再通过 HttpClient 调用相关方法。任何 rpc 远程调用框架都会有一个注册中心。

![](https://blog-1258617239.cos.ap-chengdu.myqcloud.com/blog_images/Eureka架构.png)

### 2. 基本使用

1. 创建一个 module 用于启动 Eureka 服务
2. 将 service provider 和 service consumer 注册进 Eureka

### 3. 集群原理

互相注册, 相互守望

### 4. 服务发现

对于注册进 eureka 的服务, 可以通过服务发现来获得该服务的信息。

服务发现的应用: 实现负载均衡轮询算法、...

### 5. 自我保护

保护模式主要用于服务和 eureka server 之间存在网络分区场景下的保护, 一旦进入保护模式. eureka server 将会尝试保护其服务注册表的信息, 不再删除服务注册表的数据, 即不再注销任何微服务, 即使对应的微服务已经关闭。

属于 CAP 中的 AP 分支。

为什么需要自我保护? 默认情况下 server 在90秒内没有检测到 client 的心跳, 则会注销对应服务。但是一定时间内检测不到心跳并一定就是服务被关闭了, 也有可能是 server 与 client 的网络连接状况不稳定而服务本身是健康的。所以当 eureka 在短时间内丢失了过多客户端时, 就会开启自我保护, 不轻易删除服务信息, 避免误删。

### 6. Zookeeper 作注册中心

### 7. Consul 作注册中心

+ 服务发现
+ 健康监测
+ kv 存储
+ 多数据中心
+ 可视化 web

### 8. 三个注册中心的异同点

| 组件      | 语言 | CAP         | 健康检测     | 对外接口暴露 | Spring Cloud 集成 |
| --------- | ---- | ----------- | ------------ | ------------ | ----------------- |
| Eureka    | Java | **AP**      | 支持、可配置 | HTTP         | 已集成            |
| Zookeeper | Java | **CP**      | 支持         | 客户端       | 已集成            |
| Consul    | Go   | **CP**      | 支持         | HTTP/DNS     | 已集成            |
| Nacos     |      | **AP / CP** |              |              |                   |

![](https://blog-1258617239.cos.ap-chengdu.myqcloud.com/blog_images/CAP.png)

CAP 无法同时保证, 分布式组件不是 CP 就是 AP



## 四、负载均衡服务调用

### 1. 负载均衡

LB: load balance 负载均衡

Ribbon是一款软负载均衡客户端组件, 它可以和其他所需请求的客户端结合使用, 和 eureka 结合只是其中的一个实例。Ribbon 的使用简单概括就是 负载均衡(`@LoadBalanced`) + RestTemplate 调用。前面的示例中我们并没有直接引入 Ribbon 的相关依赖就实现了负载均衡, 这是因为在 spring-cloud-starter-netflix-eureka-client 中就导入了 Ribbon 的相关依赖。

Ribbon 本地负载均衡 vs Nginx 服务端负载均衡:

+ Nginx 服务端负载均衡, 客户端所有请求都会交给 Nginx, 然后由 Nginx 实现转发请求。是集中式 LB。
+ Ribbon 本地负载均衡, 在调用微服务接口时, 会在注册中心获取服务列表并缓存在 JVM 中, 从而在本地实现 RPC 远程服务调用。是进程内的 LB。

### 2. RestTemplate

getForObj() / getForEntity()

postForObj / postForEntity()

forObj: 返回对象为响应体中数据对象

forEntity: 还包括了响应中的一些重要信息如响应头、响应状态码、响应体。 `getStatusCode().is2xxSuccessful()`、`getBody()`

### 3. IRule

IRule 是一个接口, 实现该接口就可以实现一个负载均衡算法。

Ribbon 默认提供了7中负载均衡算法。

![](https://blog-1258617239.cos.ap-chengdu.myqcloud.com/blog_images/IRule负载均衡算法.png)

通过主启动类加`@RibbonClient(name='', configuration=xxx.class)`来自定义使用何种负载均衡算法。

**自定义的 IRule 不能创建在方法 Spring Boot 扫描的包及其子包下, 需要单独创建一个包!**

### 4. 轮询算法

原理: 请求数 % provider 数

源码: 自旋锁的应用

### 5. Feign / OpenFeign

Feign 是一个声明式的 web 服务客户端, 让编写 web 服务客户端变得非常容易, 只需要创建一个接口并在接口上添加注解即可。

Feign 是 Spring Cloud 组件中的一个轻量级 RESTFUL 的 HTTP 服务客户端, Feign 内置了 Ribbon 用来做客户端负载均衡。Open Feign 是 Spring Cloud 在 Feign 的基础上支持了Spring MVC的注解。

Feign自带负载均衡配置项。

OpenFiegn 默认1秒钟调用时间, 超时则会报错。由于 OpenFeign 内置了 Ribbon, 所以可以通过 Ribbon 配置来设置超时时间。

Feign 提供了日志打印功能, 我们可以通过配置来调整日志级别, 从而了解 Feign 中的 Http 请求细节。日志级别:

+ none: 不显示任何日志(默认)
+ basic: 仅记录请求方法、URL、响应状态码和执行时间
+ headers: basic信息 + 请求头和响应头信息
+ full: headers信息 + 请求和响应的正文和元数据 



## 五、服务降级

### 1. 分布式系统面临的问题

在分布式系统中, 多个微服务之间都有调用关系, 假设服务 A 调用服务 B , 服务 B 调用服务 C, 服务 C 调用服务 D..., 这就是所谓的"扇出"。如果"扇出"的链路上某个微服务的调用响应时间过长或者不可用, 对微服务 A 的调用就会占用越来越多的系统资源, 进而引起系统崩溃, 这就是**雪崩效应**。

### 2. Hystrix

Hystrix 是一个用于处理分布式系统的延迟和容错开源库。在分布式系统里, 许多依赖不可避免的会调用失败, 比如超时、异常等。Hystrix能够保证在一个依赖出问题的情况下, 不会导致整体服务失败, 避免级联故障, 以提高分布式系统的弹性。

断路器本身是一种开关装置, 当某个服务单元发生故障之后, 通过断路器的故障监控(类似保险丝熔断), 向调用方返回一个符合预期的、可处理的备选响应, 而不是长时间的等待或抛出调用方无法处理的异常。这样就保证了服务调用方的线程不会被长时间的、不必要地占用, 从而避免了故障在分布式系统中的蔓延, 乃至雪崩。

![](https://blog-1258617239.cos.ap-chengdu.myqcloud.com/blog_images/Hystrix执行原理.png)

### 3. 服务降级 fallback

服务器繁忙, 请稍后再试, 不让客户端等待并立即返回一个友好提示。

服务降级的场景:

+ 程序运行异常
+ 超时
+ 服务熔断
+ 线程池或信号量打满

### 4. 服务熔断 break

熔断机制是应对雪崩效应的一种微服务链路保护机制。当扇出链路的某个微服务出错不可用或者响应时间太长时, 会进行服务的降级, 进而熔断该节点的微服务调用, 快速返回错误的响应信息。

当检测到该节点的微服务调用响应正常后, 恢复链路的调用。

在 Spring Cloud 中, 熔断机制通过 Hystrix 实现。Hystrix 会监控微服务调用的状况, 当失败到一定的阈值(默认5秒20次调用失败), 就会启动熔断机制。熔断机制的注解是`@HystrixCommand`。

服务熔断有三个状态:

1. 熔断打开: 服务被熔断, 服务降级
2. 熔断关闭: 服务未熔断
3. 熔断半开: 部分请求根据规则调用当前服务, 逐渐关闭熔断。

![](https://blog-1258617239.cos.ap-chengdu.myqcloud.com/blog_images/服务熔断.png)

```java
@HystrixCommand(fallbackMethod = "paymentCircuitBreaker_fallback",commandProperties = {
            @HystrixProperty(name = "circuitBreaker.enabled",value = "true"),// 是否开启断路器
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold",value = "10"),// 请求次数
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds",value = "10000"), // 时间窗口期
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage",value = "60"),// 失败率达到多少后跳闸
    })
```

### 5. 服务限流 flowlimit

秒杀高并发等场景, 严禁一窝蜂过来, 而是排好队, 一秒N个, 有序进行。

### 6. 实践

当使用 JMeter 进性压测时, 本来毫秒级的响应会变慢, 这是因为 tomcat 的工作线程有限, 当很多请求到来时, 工作线程满了后就只能等待。同样消费者调用服务也会出现这样的问题。

服务降级可以用在任何微服务中, 不过一般用在消费端。

**服务端服务降级:**

服务端需要设置调用时间峰值, 超过峰值有兜底方法, 作服务降级。

```java
@EnableCircuitBreaker
主启动
    
@HystrixCommand(
	fallbackMethod = "paymentTimeOutFallbackMethod",
	commandProperties = {@HystrixProperty(
		name="execution.isolation.thread.timeoutInMilliseconds",
		value="1500")}
 )
service
```

**消费端服务降级**

消费端也可以进行服务降级, 而且服务降级一般都是在消费端。

```yml
feign:
	hystrix:
		enabled: true
```

```java
@EnableHystrix
主启动
    
@HystrixCommand(
	fallbackMethod = "paymentTimeOutFallbackMethod",
	commandProperties = {@HystrixProperty(
		name="execution.isolation.thread.timeoutInMilliseconds",
		value="1500")}
 )
controller
```

**全局服务降级**

每个业务都有一个注解配置会导致代码重复, 所以可以使用全局服务降级。

全局配置不会覆盖单独的配置

```java
@DefaultProperties(fallback="")
class
    
@HystrixCommand
method
```

进一步解耦: feign + hystrix

```java
class FeignFallbackService implements FeignService{
    // method1对应的fallback方法
    method1()
        
    method2()
}

@FeignClient(fallback=FeignFallbackService.class, value="")
interface FeignService{
	method1();
    
    method2();
}
```

### 7. dashboard

创建模块开启 dashboard

监控服务端地址: http://localhost:80/hystrix.stream



## 六、Gateway网关

### Gateway 是什么? 为什么选择 Gateway?

Spring Cloud Gateway is built on Spring Boot 2.x, Spring WebFlux, and Project Reactor. 

Spring Cloud Gateway 是基于 WebFlux 框架实现的。WebFlux 框架底层使用了高性能的 Reactor 模式通信架构 Netty(**异步非阻塞模型**)。Spring Cloud Gateway 的目标是提供统一的路由方式且基于 Filter 链的方式提供网关的基本功能, 如: **反向代理、鉴权、流量控制、熔断、日志控制等**。

![](https://blog-1258617239.cos.ap-chengdu.myqcloud.com/blog_images/微服务网关的位置.png)

### 三大概念

**路由:** 路由是网关的基本模块, 它由 ID、目标 URL、一系列的断言和过滤器组成, 如果断言为 true 则匹配该路由。

**断言:** 参考的是 JDK8 函数式编程的 Predicate。开发人员可以匹配 HTTP 请求中的所有内容, 例如请求头、请求参数, 如果请求与断言相匹配则进行路由。

**过滤:** 指的是 Spring 框架中 GatewayFilter 实例, 使用过滤器, 可以在请求被路由前或之后对请求进行修改。

### 工作流程

Gateway 的工作流程概括来说就是路由转发+执行过滤链

客户端向 Spring Cloud Gateway 发出请求。然后再 Gateway Handler Mapping 中找到与请求相匹配的路由。将其发送到 Gateway Web Handler。

Handler 再通过指定的过滤器链来将请求发送到实际的服务执行业务逻辑。过滤器有两种类型, 一种是 pre 一种是 post。在 pre 类型可以作参数校验、权限校验、流量监控、日志输出、协议转换等。在 Post 类型可以作响应内容、响应头修改、日志输出、流量监控等。

![](https://blog-1258617239.cos.ap-chengdu.myqcloud.com/blog_images/Getway执行流程.png)

### 断言 Predicate

判断 url 是否符合规则

https://cloud.spring.io/spring-cloud-gateway/2.2.x/reference/html/#gateway-request-predicates-factories

## 七、配置中心 Spring Cloud Config

Spring Cloud Config 为微服务架构中的微服务提供**集中式**的外部配置支持, 配置服务器为各个不同的微服务应用的所有环境提供一个中心化的外部部署。

Spring Cloud Config 分为**服务端和客户端**两个部分。

服务端也称为分布式配置中心, 它是一个独立的微服务应用, 用来**连接配置服务器**并为客户端提供配置信息, 加密/解密信息等访问接口。

客户端则是通过指定的配置中心来管理应用资源, 以及业务相关的配置内容, 并在启动的时候从配置i中心获取和加载配置信息。

配置服务器默认采用 git 来存储配置信息, 这样就有助于对环境配置进行版本管理, 并且可以通过 git 客户端工具来方便管理和访问配置内容。

Spring Cloud Config 有以下功能:

1. 集中管理配置文件
2. 不同环境不同配置
3. 运行期间动态调整配置, 服务无需重启即可感知并应用新变化
4. 将配置信息以 REST 接口的形式暴露 

### bootstrap.yml

Spring Cloud 会创建一个 bootstrap context 作为 Spring 应用的上下文。初始化的时候, Bootstrap Context 负责从外部源加载配置属性并解析配置。这两个上下文共享一个从外部获取的 "Environment"。

bootstrap 属性有更高优先级, 默认情况下, 它们不会被本地覆盖。

所以 spring cloud config 的配置应该写在 bootstrap.yml 中。

### 动态刷新--手动方式

github 上的配置更新后, 需要向 client 端发送 post 请求 http://localhost:3355/actuator/refresh才能动态刷新。

## 八、消息总线 spring cloud bus

上面的手动方式动态刷新还是不够方便。我们可以使用 spring cloud bus 实现全自动的动态刷新。

spring cloud bus 是用来将分布式系统的节点与轻量级消息系统链接起来的框架。bus 推送消息到一个微服务节点, 该消息会广播到其他微服务节点。

何为总线? 在微服务架构中, 通常使用轻量级的消息代理来构建一个公用的消息主题(springCloudBus), 并让系统中所有微服务实例都连接上来。由于该主题中产生的消息会被所有实例监听和消费, 所以称它为总线。在总线上的各个实例, 都可以方便地广播一些需要让其他连接在该主题上的实例都知道的消息。

spring cloud bus 支持两种消息中间件 kafka 和 rabbitMQ。

两种广播思路

1. 利用消息总线触发一个客户端 http://localhost:xxxx/acuator/bus-refresh , 而刷新所有客户端的配置
2. 利用消息总线触发一个服务端 ConfigServer 的 http://localhost:xxxx/acuator/bus-refresh 端点, 而刷新所有客户端的配置。

一般采用第二种方式, 因为第一种方式一是破坏了微服务职责的单一性, 微服务本身是业务模块, 它不应该承担配置刷新的职责。二是破坏了微服务各节点的对等性。三是有一定的局限性, 例如在微服务迁移时网络地址发生改变, 需要增加更多配置。

定点通知: http://localhost:xxxx/acuator/bus-refresh/config:-client:3355

## 九、Spring Cloud Stream

屏蔽底层消息中间件的差异, 降低切换成本, 统一消息中间件的编程模型。

应用程序通过 **input(消费)** 和 **output(生产)** 来与 spring cloud stream 中的 **binder** 对象进行交互。

Spring Cloud Stream 目前只支持 rabbitMQ 和 kafka

### 四大对象

Biner: 方便的连接各种中间件, 屏蔽差异

Channel: 通道是队列 Queue 的一种抽象, 是存储和转发的媒介

Source / Sink: 输入和输出

### 常用注解

@Input: 标识输入通道

@Output: 标识输出通道

@StreamListener: 监听队列

@EnableBinding: 将 channel 和 exchange 绑定在一起

### 重复消费问题

多个消费者可能会重复消费, 需要使用分组和持久化解决。

### 分组

在 spring cloud stream 中, 同一个 group 的多个消费者是竞争关系, 如果多个消费者在同一个组就能确保一条消息只被一个消费者消费。而不同的组是存在重复消费的。

默认情况下不同的消费者分组不同。

### 持久化

生产者在不停的生产消息, 此时消费者停机, 消费者重启后能够把前面错过的消息消费掉, 这就是持久化。

spring cloud stream 中添加了 group 则也就开启了持久化, 否则就没有开启持久化。



## 十、分布式请求链路追踪 Spring Cloud Sleuth

在微服务架构中, 服务之间的调用关系非常复杂, 这个时候就需要对服务调用链路进行追踪。

zipkin使用:

1. 下载 zipkin jar包, 使用 java -jar 命令运行
2. 访问 http://localhost:9411/zipkin/

![](https://blog-1258617239.cos.ap-chengdu.myqcloud.com/blog_images/zipkin.png)



## X、速记

+ spring-boot-dependencies 用于依赖版本管理。在父项目中导入该包, 则子项目的各种 spring boot 启动器都无需设置版本号。
+ 出现 can not read artifact xxx ... 的解决办法: 在本地仓库删除相关依赖的文件夹并重新下载
+ 如果前端传参是通过请求体传递, 则后端必须要使用 @RequestBody
+ 一般我们会将各个模块都需要用到的代码单独封装成一个模块
+ 如果一个模块需要依赖另一个模块, 则被依赖的模块需要先 install 安装到本地仓库才行。父工程也应该先 install。
+ spring.application.name 最好不要使用下划线, 否则在 eureka 负载均衡时通过该名字访问会出现 Request URI does not contain a valid hostname
+ 为什么请求变多后请求响应会变慢: 因为tomcat默认工作过线程被占满, 其他请求只能等待。