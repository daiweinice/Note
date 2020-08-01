# Spring Boot入门

## 一、 Spring Boot简介

> Spring Boot是Spring团队开发的全新框架. 其设计目的是用来简化Spring应用的初始搭建以及开发过程。该框架使用了特定的方式来进行配置，从而使开发人员不再需要定义样板化的配置(约定 > 配置)。Spring Boot是整个Spring技术栈的大整合, 也是J2EE开发的一站式解决方案.

#### Spring Boot的优点:

+ 使用 Spring 项目引导页面可以在几秒构建一个项目
+ 方便对外输出各种形式的服务，如 REST API、WebSocket、Web、Streaming、Tasks
+ 非常简洁的安全策略集成
+ 支持关系数据库和非关系数据库
+ 支持运行期内嵌容器，如 Tomcat、Jetty
+ 强大的开发包，支持热启动
+ 自动管理依赖
+ 自带应用监控
+ 支持各种 IED，如 IntelliJ IDEA 、NetBeans

#### Spring Boot的缺点:

+ 集成度较高, 不易了解底层原理. 入门容易, 精通难



## 二、微服务简介

> 微服务是一种架构风格. 以前我们的应用架构都是单体风格, 一个应用就是一个整体, 里面包含了所有的功能模块, 部署在服务器上也是以整个应用为单位. 而微服务架构, 将一个应用分成一组组小型服务, 每一个服务对应一种功能, 部署在服务器以一组服务为单位, 服务与服务之间通过HTTP进行交互.

#### 微服务的优点:

+ 有效利用计算机资源. 以前的服务器配置是以应用为单位, 如果某项服务需要额外的服务器资源, 则需要把整个应用部署在新的服务器上. 而微服务架构的服务器配置以一组服务为单位, 可以根据各服务的资源需求量按需分配.
+ 易于项目维护. 开发者可以更新应用程序的单个服务组件，而不会影响其他的部分.
+ 有助于新兴的云服务, 如事件驱动计算. 类似AWS Lambda这样的功能让开发人员能够编写代码处于休眠状态, 直到应用程序事件触发. 事件处理时才需要使用计算资源, 而企业只需要为每次事件，而不是固定数目的计算实例支付.

#### 微服务的缺点:

+ 因为各服务组件部署在不同的服务器, 使得运维难度增加.



## 三、Spring Boot入门案例

> 案例说明: 创建一个Web应用, 前端发出/hello请求, 后端处理, 返回hello world

#### 1. 创建Maven工程

#### 2. 导入相关依赖

````xml
<parent>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-parent</artifactId>
	<version>1.5.9.RELEASE</version>
</parent>
<dependencies>
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-web</artifactId>
	</dependency>
</dependencies>
````

#### 3. 编写一个主程序, 启动Spring Boot应用

```java
/**
 *  @SpringBootApplication 注解在主程序类上, 说明这是一个Spring Boot应用
 */
@SpringBootApplication
public class MainApplication {

    public static void main(String[] args) {

        // Spring应用启动起来
        SpringApplication.run(MainApplication.class,args);
    }
}
```

#### 4. 编写后端Controller

````java
@Controller
public class Controller {

    @ResponseBody
    @RequestMapping("/hello")
    public String hello(){
        return "Hello World";
    }
}
````

#### 5. 运行main方法

运行结果: 发出/hello请求, 页面显示Hello World

#### 6. 部署

````xml
<build>
	<plugins>
		<plugin>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-maven-plugin</artifactId>
		</plugin>
	</plugins>
</build>
````

在Maven中导入上述插件, 通过Maven的-package命令将该项目打包成jar包. 找到该jar包, 在命令行中通过java -jar xxx.jar命令即可部署成功.



## 四、入门案例实现原理分析

#### 1. pom.xml探究

````xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>1.5.9.RELEASE</version>
</parent>

👆我们的项目依赖了一个Spring Boot为我们提供的父项目, 而该父项目又依赖于一个父项目👇

<parent>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-dependencies</artifactId>
  <version>1.5.9.RELEASE</version>
  <relativePath>../../spring-boot-dependencies</relativePath>
</parent>

👆该项目为依赖版本仲裁中心, 用来管理Spring Boot应用里面所有依赖的版本.
我们在导入相关依赖时, 无需再写版本信息(如果仲裁中心没有的依赖还是需要写版本信息)
````

````xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

spring-boot-starter: 场景启动器, Spring Boot把所有功能场景都抽取成了一个个场景启动器
-web: 表示这是一个web场景启动器
根据不同场景,我们只需要导入相关场景启动器, 该场景需要的依赖都在场景启动器中为我们导入了, 我们不需要自己再去导入相关依赖
````

#### 2. 主程序

````java
@SpringBootApplication
public class HelloWorldMainApplication
````

**@SprigBootApplication** 表示该类是一个主程序类, Spring Boot通过调用该类的main方法来启动Spring Boot项目. 该注解实际上是一个注解组合👇

````java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(excludeFilters = {
      @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
      @Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) })
public @interface SpringBootApplication {
````

**@SpringBootConfiguration** Spring Boot配置类

其内部是注解**@Configuration**, 配置类也是容器中的一个组件



**@EnableAutoConfiguration** 开启自动配置功能

````java
@AutoConfigurationPackage
@Import(EnableAutoConfigurationImportSelector.class)
public @interface EnableAutoConfiguration {
````

|__**@AutoConfigurationPackage** 自动配置包

​		|__**@Import(AutoConfigurationPackages.Registrar.class)** 导入组件

==该组件将主程序类(@SpringBootApplication)所在的包及其子包中所有的组件添加到Spring容器中==

|__**@Import(EnableAutoConfigurationImportSelector.class)** 导入组件选择器

该选择器将所有需要导入的组件(各框架的组件)以全类名的方式返回, 并注入容器中. 这些组件都是与对应场景相关的自动配置组件(xxxAutoConfiguration.class), 这些组件又会将场景所需的各种对象添加到容器中, 所以我们无需再手动添加相关对象进容器.组件选择器的实现原理是👇

`SpringFactoriesLoader.loadFactoryNames(EnableAutoConfiguration.class,classLoader)`

Spring Boot在启动的时候从各依赖的META-INF/spring.factories中获取EnableAutoConfiguration指定的值，将这些值代表的自动配置类组件导入到容器中，自动配置组件就生效，帮我们进行自动配置工作.

J2EE的整体整合解决方案和自动配置都在`spring-boot-autoconfigure.jar`中, 里面就是各种xxxAutoConfiguration.class

![image-20200722090537398](images/%E8%87%AA%E5%8A%A8%E9%85%8D%E7%BD%AE%E5%8E%9F%E7%90%86%E5%88%86%E6%9E%90)



## 五、通过IDEA快速创建Spring Boot项目

新建项目->选择Spring Initializer->选择需要的场景启动器

resources目录结构

+ static: 保存所有的静态资源, 如 js css  images
+ templates: 保存所有的模板页面(Spring Boot默认jar包使用嵌入式的Tomcat, 默认不支持JSP页面)可以使用模板引擎(freemarker、thymeleaf)
+ application.properties: Spring Boot应用的配置文件, 可以修改一些默认设置

