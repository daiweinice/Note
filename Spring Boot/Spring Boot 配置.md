# Spring Boot配置

## 一、全局配置文件

Spring Boot项目的resources目录下有一个`application.properties`或者`application.yml`文件, 它就是Spring Boot的全局配置文件, 用于修改自动配置的内容.



## 二、YAML

#### 简介

YAML（YAML Ain't Markup Language）是一种标记语言, 它以数据为中心, 比json、xml更适合作配置文件

#### 示例

yml文件

````yaml
server:
  port: 8081
````

xml文件

````xml
<server>
	<port>8081</port>
</server>
````

properties文件

````properties
server.port=8081

#设置项目名
server.servlet.context-path=/test 
````

#### 基本语法

1. 区分大小写
2. : 后需加上空格
3. 可嵌套 如: 对象的值也是一个对象
4. 字符串作为属性值默认可以不加" "或' '
    如果加了" ", 里面的转义字符有效 如\n会变成空格
    如果加了' ', 里面的转义字符无效 如\n不会变成空格
5. 一般值(数字、字符串、布尔)的写法. 

````yml
k: v
````

6. 对象、Map集合写法:

````yaml
user:
	name: dw
	age: 20

从第二行开始表示user对象的属性, 同一缩进代表同一层级
````

```yaml
user: {name: dw,age: 20}

单行写法
```

7. 数组、List集合、Set集合写法

```yaml
friends:
	- Tom
	- Jim
	
-表示数组中一个元素, -后也要加空格
```

```yaml
friends: [Tom,Jim]

单行写法
```

#### 配置文件占位符

YAML和properties配置文件都支持占位符的使用

1. 随机数占位符

```properties
${random.uuid}
${random.value} #类似于uuid, 只是没有-
${random.int}
${random.long}
${random.int(10)} #生成10以内的随机数
${random.int[1024,65536]}
```

2. 将之前配置的值作为占位符

```properties
person.name=dw
person.dog=${person.name:Tom}'s dog

:Tom表示如果前面没有配置person.name的值, 就使用默认值Tom
```



## 三、读取配置文件

1. 编写配置文件(yaml和properties均可)

```yaml
person:
	name: dw
	age: 20
	friends: [Tom,Jim]
	dog:
		name: 旺财
		age: 2
```

或者

```properties
person.name=dw
person.age=18
person.friends=Tom,Jim
person.dog.name=旺财
person.dog.age=2
```

2. 编写Person对象

```java
/*
	使用@ConfigurationProperties注解, 将配置文件的对应属性与该对象属性一一映射
	prefix属性指定映射配置文件中哪个数据
	使用该注解的前提是该对象必须在Spring容器中
	该注解还可以用在方法上, 将配置文件内容封装到方法返回值对象中
*/
@Conponent
@ConfigurationProperties(prefix="person")
public class Person{
    private String name;
    private int age;
    private List<String> friends;
    private Dog dog;
}
```

3. (可选)导入依赖, 该依赖可以在编写配置文件时给出属性提示

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-configuration-processor</artifactId>
	<optional>true</optional>
</dependency>
```

4. 单元测试

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class Test{
    
    @Autowired
    Person person;
    
    @Test
    public void test1(){
        ...
    }
    
}
```

#### properties配置文件中文乱码情况解决办法

IDEA Settings -> Editor -> file Encoding -> 勾选 transparent native-to-ascii conversion

#### @Value和@ConfigurationProperties比较

@Value是Spring中的一个注解, 它也可以用于属性注入, 但是两者有区别

|                      | @ConfigurationProperties | @Value     |
| -------------------- | ------------------------ | :--------- |
| 功能                 | 批量注入配置文件中的属性 | 一个个指定 |
| 松散绑定（松散语法） | 支持                     | 不支持     |
| SpEL                 | 不支持                   | 支持       |
| JSR303数据校验       | 支持                     | 不支持     |
| 复杂类型封装         | 支持                     | 不支持     |



## 四、多配置文件

在一个Spring Boot项目中, 我们可以有多个配置文件, 这些配置文件根据环境不同, 配置的值也不同.在我们运行项目时, 可以根据环境指定其中一个作为最终使用的配置文件.

配置文件的命名为 application-xxx.properties 或 application-xxx.yml, 
默认使用application.properties/yml 作为配置文件

#### 激活指定配置文件

1. 对于yml配置文件, 它可以通过---将文件分块, 每一块都相当于独立的一个配置文件

```yaml
server:
  port: 8080
spring:
  profiles:
    active: profile1
    
---

server:
  port: 8081
spring:
  profiles: profile1
  
---

server:
  port: 8082
spring:
  profiles: profile2
```

2. 如果是properties配置文件
    假入有两个配置文件 application.properties和application-profile1.properties
    在application.properties中设置

```properties
spring.profiles.active=profile1
```

3. 命令行方式: 

`java -jar xxx.jar --spring.profiles.active=profile1；`

4. 虚拟机参数(在项目设置中配置)

`-Dspring.profiles.active=profile1`



## 五、配置文件位置

springboot 启动会扫描以下位置的application.properties或者application.yml文件作为Spring boot的默认配置文件

–/config/

–/

–classpath:/config/

–classpath:/

优先级由高到底，Spring Boot会同时加载这四个位置的配置文件, 如果配置内容有重复, 高优先级会覆盖掉低优先级

#### 指定加载路径

在application.properties中配置

```properties
spring.config.location=S:/application.properties
```

或者在项目启动时通过命令行指定(实际上命令行可以修改Spring Boot的所有默认配置)

`java -jar xxx.jar --spring.config.location=S:/application.properties`

指定位置的配置文件和默认位置的配置文件会一同加载, 形成互补配置



## 六、自动配置原理

> Spring Boot的强大之处在于它可以使开发者免去各种配置文件繁琐的配置, 从而更专注于业务本身. 实现这种便利的就是Spring Boot的自动配置功能, 了解了Spring Boot的自动配置原理, 就能够更好的使用Spring Boot, 也能够在需要修改默认配置的时候更加得心应手.

#### 原理解析

1. Spring Boot启动时加载主程序类@SpringBootApplication

2. 通过@EnableAutoConfiguration开启自动配置功能

    + 通过@AutoConfigurationPackage将主程序类所在包及其子包下所有组件加入容器

    + 通过@Import(EnableAutoConfigurationImportSelector.class)导入组件选择器, 它会调用
        `SpringFactoriesLoader.loadFactoryNames(EnableAutoConfiguration.class,classLoader)`方法从各个依赖的META-INF/spring.factories中读取各种自动配置组件==(xxxAutoConfiguration.class)==的全类名, 并将这些自动配置组件加入到容器中.

    + 这些自动配置组件会进行自动配置并将一些场景需要的对象加入容器

        以HttpEncodingAutoConfiguration(Http自动编码设置)为例:

        ```java
        @Configuration
        @EnableConfigurationProperties(HttpEncodingProperties.class)  
        @ConditionalOnWebApplication
        @ConditionalOnClass(CharacterEncodingFilter.class)
        @ConditionalOnProperty(prefix = "spring.http.encoding", value = "enabled", matchIfMissing = true)  
        public class HttpEncodingAutoConfiguration {
          
          	private final HttpEncodingProperties properties;
          
            //只有一个有参构造器的情况下，参数的值就会从容器中拿
          	public HttpEncodingAutoConfiguration(HttpEncodingProperties properties) {
        		this.properties = properties;
        	}
          
            @Bean
        	@ConditionalOnMissingBean(CharacterEncodingFilter.class)
        	public CharacterEncodingFilter characterEncodingFilter() {
        		CharacterEncodingFilter filter = new OrderedCharacterEncodingFilter();
        		filter.setEncoding(this.properties.getCharset().name());		filter.setForceRequestEncoding(this.properties.shouldForce(Type.REQUEST));
        		filter.setForceResponseEncoding(this.properties.shouldForce(Type.RESPONSE));
        		return filter;
        }
        ```

        **@Configuration**表示该类是一个配置类, 可以通过@Bean添加对象进容器

        ==**@EnableConfigurationProperties(HttpEncodingProperties.class)**==  其内部是@ConfigurationProperties, 将配置文件中的配置封装成一个HttpEncodingProperties对象, 并将其加入容器

        **@ConditionalOnWebApplication** 内部使用Spring @Conditional, 判断该配置类是否生效. 如这里表示如果该应用是一个web应用, 则该配置类@Configuration生效

        **@ConditionalOnClass(CharacterEncodingFilter.class)** 判断过滤器是否存在

        **@ConditionalOnProperty(prefix = "spring.http.encoding", value = "enabled", matchIfMissing = true)**   判断配置文件中是否有该配置, 有返回true, 如果没有也返回true

        **@ConditionalOnMissingBean(CharacterEncodingFilter.class)** 判断容器中是否有该过滤器

        **@Bean** 将方法返回值对象加入容器
#### @Conditional注解

@Conditional注解是Spring的原生注解, 用于条件判断

Spring Boot提供了许多@Conditional的派生注解

| @Conditional扩展注解            | 作用（判断是否满足当前指定条件）                 |
| ------------------------------- | ------------------------------------------------ |
| @ConditionalOnJava              | 系统的java版本是否符合要求                       |
| @ConditionalOnBean              | 容器中存在指定Bean；                             |
| @ConditionalOnMissingBean       | 容器中不存在指定Bean；                           |
| @ConditionalOnExpression        | 满足SpEL表达式指定                               |
| @ConditionalOnClass             | 系统中有指定的类                                 |
| @ConditionalOnMissingClass      | 系统中没有指定的类                               |
| @ConditionalOnSingleCandidate   | 容器中只有一个指定的Bean，或者这个Bean是首选Bean |
| @ConditionalOnProperty          | 系统中指定的属性是否有指定的值                   |
| @ConditionalOnResource          | 类路径下是否存在指定资源文件                     |
| @ConditionalOnWebApplication    | 当前是web环境                                    |
| @ConditionalOnNotWebApplication | 当前不是web环境                                  |
| @ConditionalOnJndi              | JNDI存在指定项                                   |

由上可知, 自动配置类的自动配置是有条件的, 只有符合@Conditional的条件才会自动配置

我们可以在配置文件中配置`debug=true`, 项目启动后会打印自动配置报告, 列出有效的自动配置和无效的自动配置

#### 总结

+ xxxAutoConfiguration.class 都是自动配置组件, 想知道一个依赖有哪些自动配置组件, 可以在该依赖的/META-INF/spring.factories文件中查看
+ XXXProperties.class 都是对相关配置文件的封装, 它们的属性就是我们可以在application配置文件中自己配置的