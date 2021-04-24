# Spring Boot日志

## 一、日志框架

#### 1. 日志框架介绍

如今市面上有许多日志框架, 如JUL、JCL、Jboss-logging、logback、log4j、log4j2、slf4j....

这些日志框架又可以分为两类:

1. 日志抽象层框架: 它是由接口构成, 起到规范各种日志实现层框架的作用
2. 日志实现层框架: 它是对日志抽象层框架具体的实现

一个日志抽象层框架与一个对应的日志实现层框架结合起来, 就是一个完整的日志框架. 这种面向接口编程的思想也体现在JDBC与数据库驱动的关系上.

| 日志门面  （日志的抽象层）                                   | 日志实现                                                    |
| ------------------------------------------------------------ | ----------------------------------------------------------- |
| ~~JCL（Jakarta  Commons Logging）~~、SLF4j（Simple  Logging Facade for Java）、~~jboss-logging~~ | ~~Log4j~~、~~JUL（java.util.logging）~~ ~~Log4j2~~、Logback |

一般我们使用 SLF4J 和 Logback 的组合, Spring Boot也是使用的该组合.



## 二、SLF4J

#### 1. SLF4J的使用

在开发中, 日志记录方法的调用都是调用抽象层的方法, 而不是实现类的.

各实现层框架都有自己的配置文件, 使用SLF4J后, 日志配置文件应该是对应实现层框架的配置文件.

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloWorld {
  public static void main(String[] args) {
    Logger logger = LoggerFactory.getLogger(HelloWorld.class);
    logger.info("Hello World");
  }
}
```

实现图解:

![](https://blog-1258617239.cos.ap-chengdu.myqcloud.com/blog_images/SLF4J.png)

SLF4J作为抽象层, 它与logback可以直接连接, 而和log4j等实现层框架的连接需要一个适配器.因为这些框架出现时间较早, 在开发的时候还没有出现SLF4J.

#### 2. 遗留问题

**问题分析:** 在Spring Boot中, 集成了Spring、MyBatis等框架, 这些框架使用的日志框架并不是SLF4J+logback, 所以需要使它们都统一使用SLF4J+logback. 然而有些框架依赖其他日志框架, 如果没有对应的日志框架就无法使用.如Spring依赖于common-logging框架.

**解决办法:** 将这些日志框架的jar包替换掉, 替换的jar包既可以使框架正常使用, 又可以使它们都使用SLF4J+logback

![](https://blog-1258617239.cos.ap-chengdu.myqcloud.com/blog_images/解决各框架日志不同问题.png)

Spring Boot已经为我们解决了这个问题.

![](https://blog-1258617239.cos.ap-chengdu.myqcloud.com/blog_images/Spring Boot日志框架依赖结构.png)

👆由上图可知, Spring Boot导入了所有相关的替换包. 下图是这些替换包的结构👇

![](https://blog-1258617239.cos.ap-chengdu.myqcloud.com/blog_images/狸猫换太子.png)

可以看到这些替换包里面的包名依然是原日志框架的包名, 但是这只是为了让依赖该种日志框架的框架能够正常使用, 包里的具体实现已经变成了SLF4J+logback日志框架的实现了.

==所以在引入其他框架时, 一定要把其依赖的日志框架去掉.==



## 三、日志使用

#### 1. 日志级别与默认配置

```java
/*
	这里的参数需要一个class对象, 任意的class对象都可以作为参数。
	但是在日志打印时, 会带上对应的class, 如果准确填写class对象, 能够提供快速的日志定位。
*/
Logger logger = LoggerFactory.getLogger(getClass());

public void contextLoads() {
	logger.trace("这是trace日志...");
	logger.debug("这是debug日志...");
	logger.info("这是info日志...");
	logger.warn("这是warn日志...");
	logger.error("这是error日志...");
}
```

上面是日志记录的一些常用方法

日志被分成了不同的级别, 我们可以设置日志输出级别来过滤部分日志

日志级别: trace<debug<info<warn<error

默认日志输出级别是info及以上, 输出位置为控制台

#### 2. 日志相关配置

```properties
#设置日志输出级别, 这里加上了包名表示设置该包下的日志输出级别
logging.level.com.dw=warn
#设置全局输出级别
logging.level.root=warn

#设置日志输出文件名
#不写完整路径表示输出在当前项目下, 文件名为设置值
#也可以写上完整路径
logging.file=logging
logging.file=S:/logging

#设置日志输出文件目录
#记录日志的文件名固定为spring.log
logging.path=/spring boot/log

#设置控制台输出的日志格式
#%d: 日期时间，
#%thread: 线程名，
#%-5level：级别, 从左显示5个字符宽度
#%logger{50}: 表示logger名字最长50个字符，否则按照句点分割。 
#%msg：日志消息，
#%n: 换行符
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n

#设置输出到文件的日志格式
logging.pattern.file=%msg
```

#### 3. 使用自己的配置文件

我们可以把自己的配置文件放到resources目录下, 这时就不会在使用默认配置而是使用自定义的配置

| 日志框架                | 配置文件命名                                                 |
| ----------------------- | ------------------------------------------------------------ |
| Logback                 | `logback-spring.xml`, `logback-spring.groovy`, `logback.xml` or `logback.groovy` |
| Log4j2                  | `log4j2-spring.xml` or `log4j2.xml`                          |
| JDK (Java Util Logging) | `logging.properties`                                         |

当使用 logback 作为配置文件名时：配置文件直接被日志框架识别

当使用 logback-spring 作为配置文件名时: 配置文件被Spring Boot解析并配置, 此时可以使用Spring Boot的Profile功能

```xml
logback-spring.xml

<layout class="ch.qos.logback.classic.PatternLayout">
	<springProfile name="dev">
		<pattern>...</pattern>
    </springProfile>
    <springProfile name="!dev">
        <pattern>...</pattern>
    </springProfile>
</layout>
```

上面的配置可以根据不同环境设置不同的日志输出格式, 环境配置就是通过application.properties配置文件中的`spring.profiles.active=dev`指定

## 四、切换日志框架

切换日志框架(如: logback-->log4j)只需要根据上面的日志框架适配图将无关依赖删除将需要的依赖引入即可. IDEA可以可视化查看各jar包的依赖关系,操作更加方便.

还可以更换日志的启动器, 将spring-boot-starter-logging更换为spring-boot-starter-log4j2. 该启动器在spring-boot-starter-web中

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-web</artifactId>
	<exclusions>
		<exclusion>
			<artifactId>spring-boot-starter-logging</artifactId>
			<groupId>org.springframework.boot</groupId>
		</exclusion>
	</exclusions>
</dependency>

<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-log4j2</artifactId>
</dependency>
```



**参考:**

+ https://www.cnblogs.com/bigdataZJ/p/springboot-log.html