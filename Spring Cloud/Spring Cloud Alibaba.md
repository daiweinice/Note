# Spring Cloud Alibaba

## 一、概述

随着 Spring Cloud Netflix 很多组件都进入停更模式, Spring Cloud Alibaba 变得越来越火。同 Netflix 版本一样, Spring Cloud Alibaba 致力于提供微服务开发的一站式解决方案。

主要功能:

+ 服务限流降级
+ 服务注册与发现
+ 分布式配置管理
+ 消息驱动能力
+ 分布式事务
+ 阿里云对象存储
+ 分布式任务调度
+ 阿里云短信服务

主要组件:

+ Sentinel
+ Nacos
+ RocketMQ
+ Dubbo
+ Seata
+ Alibaba Cloud ACM
+ Alibaba Cloud OSS
+ Alibaba Cloud SchedulerX
+ Alibaba Cloud SMS



## 二、Nacos

**Nacos:** **na**ming **co**nfig **s**ervice 

一个更易于构建云原生应用的**动态服务发现、配置管理和服务管理**平台。

### 1. 服务中心

Nacos 继承了 Ribbon, 所以天生具有负载均衡功能。

Nacos 是同时支持 AP 和 CP 的。

C 是指所有节点在同一时间看到的数据是一致的。A 是指定义的所有请求都会收到响应。

一般来说, 如果不需要存储服务级别的信息且服务实例是通过 nacos-client 注册, 并保持心跳检测, 那么可以选择 AP 模式。AP 模式减弱了一致性, 因此 AP 模式只支持注册临时实例。

如果需要在服务级别编辑或存储配置信息, 那么 CP 是必须的。K8s 和 DNS 服务就适用于 CP 模式。CP 模式下支持注册持久化实例, 此时则是以 Raft 协议为集群运行模式, 该模式下注册实例之前必须先注册服务, 如果服务不存在, 则返回错误。

Nacos 默认是 AP 模式, 如果想切换到 CP 模式. 可以向 nacos-server 端发送一个 Put 请求切换。

`curl -X PUT $NACOS_SERVER:8848/nacos/v1/ns/operator/switches?entry=serverMode&value=CP`

### 2. 配置中心

可以在 nacos UI 中新建配置并同步到各个服务节点。

Nacos 与 Spring Cloud Config 一样, 在项目初始化时, 要保证先从配置中心配置拉取, 拉取配置后, 才能保证项目正常启动。同样的 bootstrap.yml 优先级 高于 application.yml。

dataId 的命名规则为 `${prefix}-${spring.profile.active}.${file-extension}`。注意 yml 应该写成 yaml

Nacos 自带动态刷新。

在实际开发中, 通常一个项目会有多套不同环境的配置, 这时候就需要进行配置管理。

namespace 用于区分部署环境, group 和 dataId 用于逻辑上区分两个不同的目标对象。namespace 之间是相互隔离的。service 就是微服务。一个 service 可以有多个 cluster, cluster 是对指定微服务的一个虚拟划分, 比如为了容灾, 将 service 分别部署在了杭州和成都, 这时候就可以创建两个 cluster, Nacos 还可以尽量让同一个机房的微服务相互调用, 提升性能。

![](https://blog-1258617239.cos.ap-chengdu.myqcloud.com/blog_images/Nacos 命名空间.png)

默认 namespace 为 public , 默认 group 为 DEFAULT_GROUP, 默认 cluster 为 DEFAULT。

常用配置管理方案:

1. 默认空间 + 默认分组 + 不同环境对应不同 dataId 如: xxx-dev.yaml、xxx-test.yaml
2. 不同的 group 
3. 不同 namespace

### ★ 集群和持久化配置

![](https://blog-1258617239.cos.ap-chengdu.myqcloud.com/blog_images/Nacos集群.png)

Nacos 默认有嵌入式数据库 derby 实现数据存储, 但是如果启动多个 Nacos 节点, 数据存储存在一致性问题。为了解决这个问题, Nacos 采用了集中式存储的方式来支持集群化部署, 目前只支持 MySql。

## 三、熔断与限流 sentinel

Hystrix 需要手动搭建平台, 且没有一套 web 界面来提供细粒度的配置。Sentinel 在这些方面进行了改进。

Sentinel 分为两个部分, 核心库和控制台。核心库不依赖任何框架/库, 能够运行于所有的 Java 环境, 同时对 Spring Cloud 等框架有很好的支持。控制台基于 Spring Boot 开发, 打包直接运行。

Sentinel采用的是懒加载机制, 即服务要被监控至少需要处理一次请求访问。

![](https://blog-1258617239.cos.ap-chengdu.myqcloud.com/blog_images/Sentinel 功能.png)

![](https://blog-1258617239.cos.ap-chengdu.myqcloud.com/blog_images/Sentinel生态.png)

### 1. 流量控制

资源名: 唯一名称, 默认为请求路径

阈值类型:

+ QPS(没秒钟请求数): 当调用该 API 的 QPS 达到阈值后, 进行限流
+ 线程数: 当调用该 API 的线程数达到阈值后, 进行限流。

流控模式:

+ 直接: api 达到限流条件, 直接限流
+ 关联: 当关联的资源达到阈值, 就限流自己
+ 链路: 只记录指定链路上的流量(指定资源入口资源进来的流量, 如果达到阈值, 就进行限流)

流控效果:

+ 快速失败: 直接失败、抛异常
+ 预热(warm up): 根据 coldFactor (冷加载因子, 默认为3)的值, 从 阈值/coldFactor 开始, 经过预热时长, 才达到设置的 QPS 阈值。常用于日常请求几乎为0, 某个是时间点突然产生大量请求的场景, 如秒杀。
+ 排队等待: 匀速排队, 让请求以匀速的速度通过, 阈值类型必须设置为 QPS, 否则失效。剩下的请求在等待时间内排队等待。

### 2. 服务降级

Sentinel 熔断降级会在调用链路中某个资源出现不稳定状态时（例如调用超时或异常比例升高），对这个资源的调用进行限制，让请求快速失败，避免影响到其它的资源而导致级联错误。当资源被降级后，在接下来的降级时间窗口之内，对该资源的调用都自动熔断（默认行为是抛出DegradeException）。

降级策略:

+ 平均响应时间 (`DEGRADE_GRADE_RT`)：当 1s 内持续进入 N 个请求，对应时刻的平均响应时间（秒级）均超过阈值（`count`，以 ms 为单位），那么在接下的时间窗口（`DegradeRule` 中的 `timeWindow`，以 s 为单位）之内，对这个方法的调用都会自动地熔断（抛出 `DegradeException`）。注意 Sentinel 默认统计的 RT 上限是 4900 ms，**超出此阈值的都会算作 4900 ms**，若需要变更此上限可以通过启动配置项 `-Dcsp.sentinel.statistic.max.rt=xxx` 来配置。
+ 异常比例 (`DEGRADE_GRADE_EXCEPTION_RATIO`)：当资源的每秒请求量 >= N（可配置），并且每秒异常总数占通过量的比值超过阈值（`DegradeRule` 中的 `count`）之后，资源进入降级状态，即在接下的时间窗口（`DegradeRule` 中的 `timeWindow`，以 s 为单位）之内，对这个方法的调用都会自动地返回。异常比率的阈值范围是 `[0.0, 1.0]`，代表 0% - 100%。
+ 异常数 (`DEGRADE_GRADE_EXCEPTION_COUNT`)：当资源近 1 分钟的异常数目超过阈值之后会进行熔断。注意由于统计时间窗口是分钟级别的，若 `timeWindow` 小于 60s，则结束熔断状态后仍可能再进入熔断状态。

Sentinel 熔断没有半开状态。

### 3. 热点 key 限流

何为热点？热点即经常访问的数据。很多时候我们希望统计某个热点数据中访问频次最高的 Top K 数据，并对其访问进行限制。比如：

- 商品 ID 为参数，统计一段时间内最常购买的商品 ID 并进行限制
- 用户 ID 为参数，针对一段时间内频繁访问的用户 ID 进行限制

热点参数限流会统计传入参数中的热点参数，并根据配置的限流阈值与模式，对包含热点参数的资源调用进行限流。热点参数限流可以看做是一种特殊的流量控制，仅对包含热点参数的资源调用生效。

![Sentinel Parameter Flow Control](https://blog-1258617239.cos.ap-chengdu.myqcloud.com/blog_images/Sentinel热点限流)

Sentinel 利用 LRU 策略统计最近最常访问的热点参数，结合令牌桶算法来进行参数级别的流控。热点参数限流支持集群模式。

`@SentinelResource(value="", blockHandler = "")`, value 属性与热点规则配置中的资源名相对应。热点规则如果不定义 blockHandler, 则页面会显示错误界面, 对用户来说不够友好, 所以建议必须加上 blockHandler。需要注意的是, blockHandler 处理的是sentinel控制台中配置的规则的错误, 如果是其他错误如 10/0, 则不会被处理, 页面会打印错误信息, 当然也可以定义 fallback 处理。

热点规则的参数索引从0开始, 它是与代码的参数相对应。如控制层对应请求有两个参数p1和p1, 则参数索引匹配到的就是p1, 即使请求参数不包含p1只有p2, 此时索引0指的还是p1, 而不是p2。

热点规则还可以添加例外项, 如默认阈值为1, 而当参数值为100时阈值为200。但是有一个前提条件, 参数的类型必须为基本类型。

### 4. 系统自适应限流

Sentinel 系统自适应限流从整体维度对应用入口流量进行控制，结合应用的 Load、CPU 使用率、总体平均 RT、入口 QPS 和并发线程数等几个维度的监控指标，通过自适应的流控策略，让系统的入口流量和系统的负载达到一个平衡，让系统尽可能跑在最大吞吐量的同时保证系统整体的稳定性。

### 5. @SentinelResource

`@SentinelResource(value="", blockHandlerClass = xxx.class, blockHandler = "", fallback="")`

不支持注解到 private 方法。

对于 blockHandler 指定的方法, 它的参数应该与请求方法的参数相对应, 且最后再加上一个 BlockException。对于 fallback 指定的方法, 它的参数也应该对应, 且最后加上一个 Throwable

如果一个请求既符合 sentinel 控制台配置的规则, 又有其他异常出现, 则会被 blockHandler处理。

### 6. 整合 feign

+ 修改配置激活 sentinel 对 feign 的支持。

    ```yaml
    feign:
    	sentinel:
    		enable: true
    ```

### 7. 持久化

持久化思路: 将相关配置规则持久化进 Nacos。(也可以持久化到 mysql 等地方)

在微服务中导入持久化依赖, 配置文件中配置 nacos 持久化。在 Nacos 添加配置规则, sentinel 会自动同步规则。后续只要 nacos 的数据一致存在, 则 sentinel 的规则也会一直存在, 不会因为微服务重启而丢失。

## 四、分布式事务处理 seata

http://seata.io/zh-cn/docs/overview/what-is-seata.html

单体应用被拆分成微服务应用, 原来的三个模块被拆分成三个独立应用, 分别使用三个独立的数据源。

业务操作需要调用三个服务完成, 此时每个服务内部的数据一致性由本地事务保证, 但是全局的数据一致性问题没法保证。

Seata 是一款开源的分布式事务解决方案，致力于提供高性能和简单易用的分布式事务服务。Seata 将为用户提供了 AT、TCC、SAGA 和 XA 事务模式，为用户打造一站式的分布式解决方案。

![](https://blog-1258617239.cos.ap-chengdu.myqcloud.com/blog_images/seata.png)

一个典型的分布式事务过程: 一个分布式事务处理的全局唯一 ID+三个组件模型。

**TC (Transaction Coordinator) - 事务协调者**

维护全局和分支事务的状态，驱动全局事务提交或回滚。TC 可以简单理解为 seata server

**TM (Transaction Manager) - 事务管理器**

定义全局事务的范围：开始全局事务、提交或回滚全局事务。TM 可以简单理解为事务发起方, 即`@GlobalTransactional`

**RM (Resource Manager) - 资源管理器**

管理分支事务处理的资源，与TC交谈以注册分支事务和报告分支事务的状态，并驱动分支事务提交或回滚。RM可以简单理解为事务参与方, 即数据库。

![](https://blog-1258617239.cos.ap-chengdu.myqcloud.com/blog_images/seata处理流程.png)

+ TM 开启分布式事务 (TM 向 TC 注册全局事务记录)
+ 按业务场景, 编排数据库、服务等事务内资源 (RM 向 TC 汇报资源准备状态)
+ TM 结束分布式事务, 事务一阶段结束 (TM 通知 TC 提交/回滚分布式事务)
+ TC 汇总消息, 决定分布式事务是提交还是回滚
+ TC 通知所有 RM 提交/回滚 资源, 事务二阶段结束

before image | after image

使用: 本地 @Transactional + 全局 @GlobalTransactional

### seata 原理

