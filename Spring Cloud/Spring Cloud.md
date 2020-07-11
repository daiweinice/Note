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

同时在服务注册与发现中有一个注册中心。当服务启动时会把服务的信息如地址等以别名的方式注册到注册中心上。服务调用者以该别名从注册中心获取到实际的服务通讯地址, 然后再调用相关方法。任何 rpc 远程调用框架都会有一个注册中心。

![](https://blog-1258617239.cos.ap-chengdu.myqcloud.com/blog_images/Eureka架构.png)

### 2. 使用

1. 创建一个 module 用于启动 Eureka 服务
2. 将 service provider 和 service consumer 注册进 Eureka

## X、速记

+ spring-boot-dependencies 用于依赖版本管理。在父项目中导入该包, 则子项目的各种 spring boot 启动器都无需设置版本号。

+ 出现 can not read artifact xxx ... 的解决办法: 在本地仓库删除相关依赖的文件夹并重新下载
+ 如果前端传参是通过请求体传递, 则后端必须要使用 @RequestBody
+ 一般我们会将各个模块都需要用到的代码单独封装成一个模块
+ 如果一个模块需要依赖另一个模块, 则被依赖的模块需要先 install 安装到本地仓库才行。父工程也应该先 install。