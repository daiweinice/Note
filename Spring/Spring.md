## 一、Spring 概述

### 1. Spring简介

> Spring 是分层的 Java SE/EE 应用 full-stack 轻量级开源框架. 以 **IOC(Inverse of Control : 控制反转)** 和 **AOP(Aspect Oriented Programming : 面向切面编程)** 为核心. 提供了展示层 SpringMVC 和持久层 Spring JDBC 以及业务层事务管理等众多的企业级应用技术, 还能整合开源世界众多著名的第三方框架和类库, 逐渐成为使用最多的Java EE企业应用开源框架.

### 2. Spring的优势

+ 解耦 (IOC)

+ AOP与声明式事务
    *编程式事务: 代码级别, 通过代码(方法)进行事务回滚*
    *声明式事务: 方法级别, 本质是对方法前后进行拦截, 使用时只需要使用`@Transactional`声明*

+ 集成各种优秀框架

+ 统一规范, 简化开发

+ Java源码学习典范

### 3. Spring体系结构

![](https://blog-1258617239.cos.ap-chengdu.myqcloud.com/blog_images/Spring体系结构.png)

## 二、Spring--IOC

### 1. 耦合与解耦

> 耦合: 程序间的依赖关系. 如类之间的依赖关系、方法之间的依赖关系
>
> 解耦: 降低程序之间的依赖关系

#### 1.1 耦合和解耦案例

```java
/*
    在 JDBC 操作中, 第一步加载驱动有两种写法
    第一种存在耦合现象, 当对应 jar 包没有导入时, 程序编译都无法通过
    第二种在 jar 包没有导入时, 程序虽然无法正常使用, 但是编译可以通过, 只是在运行时会抛出异常
    在实际开发中, 我们的程序应该做到, 编译期不依赖, 运行时才依赖 
*/

//第一种
DriverManager.registerDriver(new com.mysql.jdbc.Driver());

//第二种
Class.forName("com.mysql.jdbc.Driver");
```

#### 1.2 解耦思路

从配置文件中读取要创建的对象的全类名, 通过**反射**创建对象。

#### 1.3 工厂模式解耦

```java
/*
  版本1:
  创建一个工厂, 工厂通过读取配置文件利用反射技术创建对象实现解耦
  但是, 该工厂模式创建的对象是多例的, 每次创建都是一个新的对象
  多例对象无线程问题, 但是程序效率较低
  单例对象存在线程问题, 但是程序效率较高
  在Web开发中, 通常创建单例对象, 所以该版本还有待优化
*/
public class Factory{
    private static Properties pro;

    static{
        pro = new Properties();
        InputStream in = Factory.class.getClassLoader().getResourceAsStream();
        pro.load(in);
    }

    public static Object getBeanByName(String beanName){
        String className = pro.getProperty(beanName);
        Object obj = Class.forName(className).newInstance();
        return obj;
    }
}
```

```java
/*
  版本2:
  为了解决版本1的问题, 引入容器的概念, 用一个Map集合作为容器, 存储工厂创建的对象, 需要使用对象时, 就从容器中取, 这样每一种对象都是单例的
*/
public class Factory{
    private static Properties pro;
    private Map<String, Object> container;

    static{
        pro = new Properties();
        InputStream in = Factory.class.getClassLoader().getResourceAsStream(xxx.properties);
        pro.load(in);
        
        container = new Map<String, Object>;
        Enumberation keys = pro.keys();
        while(keys.hasMoreElements()){
            String key = keys.nextElement().toString();
            String className = pro.getProperty(key);
            Object obj = Class.forName(className).newInstance();
            container.put(key, obj);
        }
    }

    public static Object getBeanByName(String beanName){
        return container.get(beanName);
    }
}
```

### 2. IOC

> IOC: Inversion of Control, 控制反转. 把创建对象的权力交给框架(工厂), 是框架的重要特征. 它包括 **DI(Dependency Injection: 依赖注入)** 和 **DL(Dependency Lookup: 依赖查找)**. IOC 的作用是减少程序之间的耦合.

#### 2.1 IOC 入门案例

##### 2.1.1 编写配置文件 bean.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       https://www.springframework.org/schema/beans/spring-beans.xsd">

    <!--把对象的创建交给 Spring 来管理-->
    <bean id="accountSerivice" class="com.dw.service.impl.AccountServiceImpl"></bean>

    <bean id="accountDao" class="com.dw.dao.impl.AccountDaoImpl"></bean>

</beans>
```

##### 2.1.2 使用容器

```java
public class Test{
    public static void main(String[] args){
        //1. 获取核心容器对象
        ApplicationContext ac = new ClassPathXmlApplicationContext("bean.xml");

        //2. 根据 id 获取 Bean 对象
        IAccountService service = (IAccountService)ac.getBean("accountService");
        IAccountDao dao = ac.getBean("accountDao", IAccountDao.class)
    }
}
```

#### 2.2 ApplicationContext 的三个实现类

+ ClassPathXmlApplicationContext: 可以加载类路径下的配置文件

+ FileSystemXmlApplicationContext: 可以加载磁盘任意路径的配置文件(有访问权限)

+ AnnotationConfigApplicationContext: 读取注解创建容器

#### 2.3 ApplicationContext 与 BeanFactory

BeanFactory 接口是 ApplicationContext 的父接口, 也是最顶层接口。

ApplicationContext 创建核心容器时, 采用**立即创建**的方式创建容器里的对象, 即配置文件一读取完就创建所有配置的对象。

BeanFactory 创建核心容器时, 采用**延迟加载**的方式创建对象, 在使用到指定对象时, 才创建该对象。

ApplicationContext适合于单例对象形式, BeanFactory适合于多例对象形式。

```java
Resource resource = new ClassPathResource("bean.xml");
BeanFactory factory = new XmlBeanFactory(resource);
IAccountService service = (IAccountService)factory.getBean("accountService");
```

#### 2.4 Bean 的细节问题

##### 2.4.1 Bean 对象三种创建方式

```xml
<!--1. 通过默认构造方法创建, 如果没有默认方法会报错-->
<bean id="accountSerivice" class="com.dw.service.impl.AccountServiceImpl"></bean>

<!--2. 通过工厂的 getXXX() 方法创建一个对象. 比如有一个 Factory 工厂类, 有一个方法getAccountService(), 它创建一个 AccountServiceImpl 对象并返回-->
<bean id="factory" class="com.dw.factory.Factory">
<bean id="accountService" factory-bean="factory" factory-method="getAccountService">

<!--3. 如果这个 getAccountService() 方法是一个静态方法-->
<bean id="accountService" class="com.dw.factory.Factory" factory-method="getAccountService">
```

##### 2.4.2 Bean的作用范围

bean的作用范围通过`<bean>`标签的`scope`属性指定

+ singleton(默认): 单例的

+ prototype: 多例的

+ request: 作用于 Web 应用的请求范围

+ session: 作用于 Web 应用的会话范围

+ global-session: 作用于集群环境的会话范围.  多个服务器共享一个session, 这个session叫全局session

##### 2.4.3 Bean的生命周期

`<bean>`标签有两个属性, `init-method`和`destroy-method`, 用于指定对象创建时执行的方法和对象销毁时执行的方法。

+ 单例对象的生命周期与容器相同

+ 多例对象的生命周期:

    + 创建: 在需要使用对象时才创建
    + 活着: 对象只要在使用过程中就一直活着
    + 销毁: 对象长时间不使用, 就会由Java垃圾回收机制销毁

    注意: 容器有一个 close 方法用以关闭容器, 但是 ApplicationContext 接口没有, 所以使用接口声明容器对象, 是无法使用 close() 方法的

#### 2.5 依赖注入

> 依赖注入: 创建对象时, 指定**成员变量**的值

##### 2.5.1 注入数据的三种类型

+ 基本类型和 String 类型
+ 复杂类型/集合类型
+ bean对象

##### 2.5.2 注入的三种方式:

1. **构造方法注入**

```java
/*
	<bean> 标签内部有一个 <constructor-arg> 标签, 有以下几个属性:
    1. type: 用于给指定参数类型的参数赋值, 当多个参数同一类型时无法使用
    2. index: 用索引的方式指定参数. 索引从0开始
    3. name: 用构造方法中的参数名称指定参数
    4. value: 用于指定参数的值, 只适用于基本类型和String. Spring会自动转换类型, 如value="10", 如果参数是int型, 则会转化成int型10
    5. ref: 如果参数是一个对象, 则使用ref代替value, 引用其<bean>的id
*/

//eg:创建 User 对象时, 设置其 age 属性值为20 (前提是有一个构造方法, 参数是一个 int age, 方法里赋值 this.age=age)
<bean id="user" class="com.dw.bean.User">
    <constructor-arg name="age" value="20"></constructor-arg>
</bean>

//缺点: 只能使用参数个数和类型与配置相吻合的构造方法, 不能再使用其他构造方法了
```

2. **set 方法注入**

```java
/*
    <bean> 标签内部有一个 <property> 标签, 其有三个属性:
    1. name: 需要调用哪个setXxx方法, name的值就是Xxx第一个字母小写后的值, 即xxx
    2. value: 设置的值
    3. ref: 如果值是一个对象, 则使用ref代替value, 引用<bean>的id
*/

//eg: User对象有一个setUserName方法
<bean id="user" class="com.dw.bean.User">
    <property name="userName" value="David"></property>
</bean> 

/*
    集合注入:
    <property> 或 <constructor-arg> 标签内部有 <list>、<array>、<set>、<map>、<props> 标签, 分别对应 List 集合、数组、set集合...
*/

//注意: <list>、<set>、<array> 的写法一样, 且相互兼容. 如用 <set> 一个 list 集合赋值, 也是可以的
<property name="myList">
    <list>
        <value>AAA</value>
        <value>BBB</value>
    </list>
</property>
<property name="myList">
	<list>
		<bean />
		<bean />
	</list>
</property>

//注意: <map> 和 <props> 也可以相互兼容, 用 <psops> 给 map 集合赋值, 也是可以的
<property name="myMap">
    <map>
        <entry key="1" value="AAA"></entry>
        <entry key="2"> <value>BBB</value> </entry>
    	<!--<entry key="3"> <Bean></Bean> </entry>-->
    </map>
</property>
<property name="myProp">
    <props name="myProps">
        <prop key="1">AAA</prop>
        <prop key="2">BBB</prop>
    <props>
</property>

//优点: 对构造函数无限制, 使用默认构造函数也可以赋值
```

3. **注解方式注入(见下)**

#### 2.6 注解开发

##### 2.6.1 创建对象

```java
/*
    用该注解修饰的类, Spring会创建其对象并加入容器中
    该注解只有一个属性value, value的值相当于<bean>的id属性, 如果不赋值, 默认为类的类名(首字母变小写)
*/
@Component(value="accountService")

/*
    以下三个注解的作用与用法和@Component一样, 只不过这三个注解可以用来标识三层架构
*/
@Controller   表现层
@Service      业务层
@Repository   持久层


//注意: 使用注解方式还是需要配置文件, 不过xml约束和配置内容有所变化
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:context="http://www.springframework.org/schema/context"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        https://www.springframework.org/schema/context/spring-context.xsd">

    <!--告诉Spring创建容器时, 需要扫描的包-->
    <context:component-scan base-package="com.dw">

</beans>
```

##### 2.6.2 依赖注入

```java
/*
    自动按照类型注入. 
    当容器中只有一个bean对象的类型和要注入的数据类型相匹配时, 注入成功
    当容器中有多个bean对象的类型(都实现同一接口)与要注入的数据类型相匹配时, 比较这些对象在容器中对应的key值, 若key值和需要注入的变量的变量名相同, 则可以注入.
    该注解可以用在成员变量上, 也可以用在方法上
    如果要使用注解依赖注入, 这个需要注入依赖的类也应交给工厂管理, 否则无法注入
*/
@AutoWired

/*
    按照名称注入, value指定bean的id
    必须和@Autowired一起使用
*/
@Qualifier(value="accountService")

/*
    直接按照bean的id注入, 通过name属性指定id
*/
@Resource(name="accountService")

//以上三种注入方法, 只能注入存在于容器中的对象类型, 基本类型和String类型无法注入
//集合类型的注入只能通过xml方式配置

/*
    实现基本类型和String类型的注入
    value属性用于指定注入数据的值, 它可以使用Spring的EL表达式 ${表达式}
*/
@Value(value="1")

/*
    使用EL表达式方式, 从外部配置文件中读取数据, 来注入值
    @PropertySource作用位置为配置类, classpath:表示在class路径下寻找配置文件
    读取了配置文件, 就可以使用EL表达式注入数据. 如下面代表注入数值为配置文件中key为name的值
*/
@PropertySource("classpath:config.properties")
public class SpringConfig{
    
    @Value("${name}")
	public String name;
}
```

##### 2.6.3 作用范围

```java
/*
    用于指定作用范围, value值指定, 默认为singleton
*/
@Scope(value="singleton")
```

##### 2.6.4 生命周期

```java
/*
    创建时执行的方法
    注解的位置: 创建时执行的方法上
*/
@PostConstruct

/*
    销毁前执行的方法
    注解作用位置: 销毁前执行的方法上
*/
@PreDestroy
```

##### 2.6.5 配置类来代替xml配置文件

```java
/*
    注解配置有一个局限性, 只能在自己写的类上加注解, 对于导入的jar包中的类无法加上注解. 这时可以在xml中配置, 也可以使用配置类来代替xml配置
    @Configuration表示该类是一个配置类, 当该类作为AnnotationConfigApplicaitonContext构造方法的参数时, 该注解可以不写. 当有多个配置类时, 没作为参数的配置类都必须写上该注解(或者@Import)
    @ComponentScan表示容器创建时扫描的包
    @Bean将加了该注解的方法的返回值对象添加到容器中, name属性指定bean的id, 默认为方法名
    @Import添加其他配置类, 这样其他配置类就不用添加@Configuration注解, 或者作为参数传递给构造方法. value是一个数组, 元素是配置类的class对象
    @Import添加的class也可以不是配置类, 此时就是把该class的实例对象添加进容器
*/

@Configuration
@ComponentScan(basePackages={"com.dw"})
@Import(value={otherConfig.class})
public class SpringConfig{

    @Bean(name="queryRunner")
    @Scope("prototype")
    public QueryRunner creatQueryRunner(DataSource dataSource){
        return new QueryRunner(dataSource);
    }

}

//此时获取容器对象的方法也发生了改变:
//该构造方法的参数是可变参数, 可以传入多个配置类的class对象
ApplicaionContext ac = AnnotationConfigApplicationContext(SpringConfig.class)
```

总结: 开发人员自己写的代码一般用注解配置比较方便, 导入的jar包中的bean配置一般使用xml配置方式比较方便.

#### 2.6 Spring与Junit整合

##### 2.6.1 问题分析

由于开发人员和测试人员是两种不同的角色, 测试人员不一定会Spring, 所以在测试类的测试方法里最好不要出现Spring的代码(如获取ApplicationContext等). 此时将AccountService对象都作为成员变量, 通过依赖注入赋值, 这样测试人员就只用关心功能的问题了. 

但是此时在测试类中使用`@AutoWired`等注解注入依赖是无效的, 因为缺失了加载配置文件的步骤(即创建ApplicationContext对象的步骤). Junit之所以不需要在main方法进行测试, 是因为其集成了一个main方法, 但是该main方法根本不知道我们是否会使用Spring, 因而无法为我们加载Spring的配置文件, 所以需要将Spring和Junit整合在一起, 使用Spring提供的main方法, 这样就可以成功加载配置文件了.

##### 2.6.2 解决方法

```java
//1. 导入Spring整合Junit的jar包 spring-test.jar

//2. 使用Junit的一个注解将原来的main方法替换掉, 换成Spring提供. 注解作用于测试类
@RunWith(SpringJunit4ClassRunner.class)

//3. 告知Spring的运行器, IOC是基于xml的还是注解的, 并说明其位置. 这一步操作就是加载配置的操作.
@ContextConfiguration(classes={SpringConfig,class})
或
@ContextConfiguration(locations={"classpath:bean.xml"})
```



## 三、Spring--AOP

### 1. 设计模式--代理模式

> 代理模式的结构类似于消费者-代理商-生产厂家的三级结构

#### 1.1 概述

##### 1.1.1 动态代理的特点

字节码随用随创建, 随用随加载

##### 1.1.2 动态代理的作用

不修改源码的情况下对方法进行增强

##### 1.1.3 使用场景

1. 连接池close方法增强, 使得close方法不再是关闭连接, 而是把连接归还到连接池
2. 通过代理对象实现数据库事务管理代码简化, 将事务管理作为增强部分, 创建DB对象的代理对象, 这样每一个方法都会有事务管理且不需要在每个方法中都写一遍事务管理的代码.

#### 1.2 基于接口的动态代理: 被代理对象必须实现接口

```java
/*
    生产厂家的接口IProducer
*/

public interface IProducer{
    //销售
    public void sell(float money);

    //售后
    public void afterService(float money);
}
```

```java
/*
    生产者, 接口实现类
*/

public class Producer implements IProducer{
    public void sell(float money){
        System.out.println("售出电脑, 获得"+money+"元");
    }

    public void afterService(float money){
        System.out.println("售后服务. 获得"+money+"元");
    }
}
```

```java
/*
    消费者, 使用动态代理
*/

public class Consumer{
    public static void main(String[] args){
        //创建一个需要被代理的对象
        //因为后面匿名内部类要使用该对象, 所以必须用final修饰. 原理-->Java/Java SE/Java 内部类
        final Producer producer = new Producer();


         /*
           获取一个动态代理对象
           Proxy是由JDK官方提供的创建动态代理的类
           第一个参数: 被代理对象的类加载器
           第二个参数: 被代理对象的接口
           第三个参数: 增强方法, 匿名内部类InvocationHandler, 重写里面的invoke方法
        */ 
        IProducer proxyProducer = (IProducer)Proxy.newProxyInstance(producer.getClass().getClassLoader(), producer.getClass().getInterfaces(),
                new InvocationHandler(){
                    /*
                        执行被代理的对象的任何方法都会经过该方法
                        第一个参数: 被代理对象的含义
                        第二个参数: 当前执行的方法
                        第三个参数: 执行方法的参数
                        返回值与执行方法的返回值类型相同
                    */
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable{
                        //提供增强的代码, 如这里是修改sell价格
                        if(method.getName().equals("sell")){
                            float money = (float)args[0];
                            method.invoke(producer, money*2);
                        }
                        //因为原sell方法无返回值, 所以返回null
                        return null;
                    }
         });

        proxyProducer.sell(10000);
        //因为在InvationHandler中没有invoke afterService方法, 所以这个方法就不会执行了
        proxyProducer.afterService(1000);
    }
}

运行结果:
售出电脑, 获得20000元
```

#### 1.3 基于子类的动态代理: 代理对象的类不能是最终类(即不能用final修饰)

```java
/*
    生产者类
*/

public class Producer{
    public void sell(float money){
        System.out.println("售出电脑, 获得"+money+"元");
    }

    public void afterService(float money){
        System.out.println("售后服务. 获得"+money+"元");
    }
}
```

```java
/*
    消费者类
*/

public class Consumer{
    public static void main(String[] args){
        //创建被代理对象        
        final Producer producer = new Producer();

        /*
            创建代理对象
            需要导入Jar包 cglib.jar
            使用Enhancer类的create方法创建代理对象
            第一个参数: 被代理对象的类加载器
            第二个参数: 增强方法 一般使用callback接口的子实现类MrthodInterCeptor
        */
        Prducer proxyProducer = (Producer)Enhancer.create(producer.getClass().getClassLoader(), new MethodInterCeptor(){
            public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throw Throwable{
                //提供增强的代码, 如这里是修改sell价格
                if(method.getName().equals("sell")){
                    float money = (float)args[0];
                    method.invoke(producer, money*2);
                }
                //因为原sell方法无返回值, 所以返回null
                return null;
            }
        });

        proxyProducer.sell(10000);
    ]
}

运行结果:
售出电脑, 获得20000元
```

### 2. AOP

#### 2.1 AOP概述

> AOP(Aspect Oriented Programming), 面向切面编程.  在单体架构下的软件开发中，一个大型项目通常是依照功能拆分成各个模块。但是如日志、安全和事务管理此类重要且繁琐的开发却没有必要参与到各个模块中，将这些功能与业务逻辑相关的模块分离就是面向切面编程所要解决的问题.  AOP采取的是横向抽取机制，取代了传统纵向继承体系重复性代码。

##### 2.1.1 横向与纵向

从纵向结构来看就是我们软件的各个模块，它所负责的是软件的核心业务（如购商品购买、添加购物车等）；从横向来看的话，软件的各个模块之间又有所关联，其中会包含一些公共模块（例如日志、权限等）；这些公共模块可以存在于各个核心业务中，而AOP的处理将两者分离，使开发人员可以专注于核心业务的开发，提高了开发效率。

##### 2.1.2 AOP的作用

在不修改源码的情况下对已有方法进行增强. (AOP的本质就是通过Spring的方式实现动态代理)

##### 2.1.3 AOP的优势

减少重复代码、提高开发效率、方便维护

##### 2.1.4 AOP术语

+ Joinpoint(连接点): 被代理对象的所有方法都是连接点
+ Pointcut(切入点): 对哪些Joinpoint进行拦截的定义, 即需要被增强的方法是切入点
+ Advice(通知): 拦截到Joinpoint后要做的事情, 即增强方法
    + 前置通知: invoke原方法之前的部分
    + 后置通知: invoke原方法之后的部分
    + 异常通知: catch块里的部分
    + 最终通知: finally块里的部分
    + 环绕通知: 整个invoke方法. 环绕通知中有明确的切入点调用
+ Target: 被代理对象
+ Weaving: 织入, 加入增强方法的过程
+ Proxy(代理): 方法增强后产生的代理类
+ Aspect(切面): 切入点和通知的结合. 即通知与切入点的执行先后关系

##### 2.1.5 AOP开发步骤

1. 编写核心代码
2. 将公用代码抽取出来, 制作成通知(一个一个的方法)
3. 在配置文件中声明切入点和通知的关系, 即切面此时, 当切入点方法被执行, Spring就会根据配置创建代理对象,执行增强后的方法.

#### 2.2 AOP实例

##### 2.2.1 配置文件方式

```java
/*
    模拟账户接口, 业务层接口
*/
public interface IAccountService{
    void saveAccount();
    
    void updateAccount(float addMoney);

    int deleteAccount();
    
}
```

```java
/*
    业务层实现类
*/
public class AccountServiceImpl implements IAccountService{
    void saveAccount(){
        System.out.println("执行了保存操作...");
    }

    void updateAccount(float addMoney){
        System.out.println("执行了更新操作...");
    }

    int deleteAccount(){
        System.out.println("执行了删除操作...");
    }
}
```

```java
/*
    日志记录, 将作为业务层的增强方法
*/
public class Log{
    public void printLog(){
        System.out.println("记录日志...")
    }
}
```

```java
<!--需要在配置文件中加入aop相关的配置约束文件-->
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:aop="http://www.springframework.org/schema/aop"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/aop
        https://www.springframework.org/schema/aop/spring-aop.xsd">

    <bean id="accountService" class="com.dw.service.AccountServiceImpl"></bean>
    <bean id="log" class="com.dw.utils.Log"></bean>

    <!--配置AOP-->
    <aop:config>
        <aop:aspect id="logAdvice" ref="log">
            <aop:before method="printLog" pointcut="execution(public void com.dw.service.AccountServiceImpl.saveAccount())"></aop:before>
        </aop:aspect>
    <aop:config>
    
</beans>


解释:
1. <aop:aspect>用于配置一个切面 id是唯一标识, ref指向通知的类的bean id
2. <aop:XXX> 配置通知和作用的切入点
   <aop:before>: 前置通知
   <aop:after-returning>: 后置通知
   <aop:after-throwing>: 异常通知
   <aop:after>: 最终通知
   <aop:around>: 环绕通知
3. method属性表示通知类中对应的方法名, pointcut属性指定切入点, 用切入点表达式表示
4. 切入点表达式格式: execution(表达式)
5. 表达式格式: 修饰符 返回值 全类名.方法名(参数列表)
6. 表达式简略写法(使用通配符*)(需要导入aspectjweaver.jar)
   --修饰符可以直接省略
   --返回值用* 表示任意类型返回值
   --包名可使用*表示任意包, 但是有几级包就需要写几个* 如*.*.*, *..表示当前包及其自包
   --类名和方法名也都可以用*通配
   --参数列表
     1)基本类型直接写名称 如:int
     2)引用类型写包名+类名 如java.lang.String
     3)*表参数可以是任何类型
     4)..表示有无参数均可, 有参数可是任意类型
   --全通配写法: * *..*.*(..)
   --实际开发常用: * com.dw.service.*.*(..)
7. <aop:pointcut id="" expression=""></aop:point>
   用于定义一个切面表达式, id为唯一标识, expression为切面表达式
   在<aop:xxx>中通过pointcut-ref属性按照id引入即可
   如果把<aop:pointcut>放在<aop:aspect>外部, 则所有切面配置都可以引用, 但是<aop:pointcut>必须放在<aop:aspect>之前(约束规定)
8. 在配置了环绕通知后, 切入点方法并没有执行, 而通知方法执行了.这是因为在手动实现动态代理代码中的环绕通知有一个明确的切入点方法执行, 而我们配置的环绕通知并没有.
   解决: Spring为我们提供了一个接口ProceedingJoinPoint. 它有一个方法, proceed(), 此方法就相当于明确调用切入点方法.
   该接口可以作为环绕通知的方法的参数,在程序执行时, Spring会为我们提供其实现类.
   <aop:around method="aroundPrintLog" pointcut="void com.dw.service.AccountServiceImpl.saveAccount()"></aop:around>

    public Object aroundPrintLog(ProceedingJoinPoint pjp){
        Object returnValue = null;
        try{
            Object[] args=pjp.getArgs();
            
            前置增强的代码...

            returnValue=pjp.proceed(args);

            后置增强的代码...
        }catch(Throwable t){
            异常增强的代码...
        }finally{
            最终增强的代码...
        }
    }
   
   所以Spring的环绕通知给我们提供了一种可以手动控制增强方法何时执行的方式
```

##### 2.2.2 注解方式

```java
@Service("accountService")
public class AccountServiceImpl implements IAccountService{
    void saveAccount(){
        System.out.println("执行了保存操作...");
    }


    void updateAccount(float addMoney){
        System.out.println("执行了更新操作...");
    }


    int deleteAccount(){
        System.out.println("执行了删除操作...");
    }
}
```

```java
@Component("log")
@Aspect  //说明该类是一个通知类
public class Log{
    
    @Pointcut("execution(* com.dw.service.*.*(..))")
    private void pointcutConfig(){}

    //@AfterReturning、@AfterThrowing
    @Before("pointcutConfig()")
    //或者直接配置切入点 @Before("execution(* com.dw.service.*.*(..))")
    public void printLog(){
        System.out.println("记录日志...")
    }

    @Around("pointcutConfig()")
    public Object aroundPrintLog(ProceedingJoinPoint pjp){
        Object returnValue = null;
        try{
            Object[] args=pjp.getArgs();
            
            前置增强的代码...


            returnValue=pjp.proceed(args);


            后置增强的代码...
        }catch(Throwable t){
            异常增强的代码...
        }finally{
            最终增强的代码...
        }
    }
}
```

```java
<!--需要在配置文件中加入aop相关的配置约束文件-->
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:aop="http://www.springframework.org/schema/aop"
    xmlns:context="http://www.springframework.org/schema/context"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/aop
        https://www.springframework.org/schema/aop/spring-aop.xsd"
        http://www.springframework.org/schema/context
        https://www.springframework.org/schema/context/spring-context.xsd">
    
    <context:component-scan base-package="com.dw"></context:annotation-config>

    <!--开启注解aop支持-->
    <aop:aspectj-autoproxy></aop:aspect-autoproxy>

</beans>

或者通过配置类:
@EnableAspectJAutoProxy
public class SpringConfig{

}
```

##### 2.2.3 JoinPoint对象

JoinPoint对象封装了切入点方法的信息, 可以将该对象作为通知方法的参数, 然后调用相关方法获取对应切入点方法信息

| 方法                     | 说明                                                         |
| :----------------------- | :----------------------------------------------------------- |
| Signature getSignature() | 通过该对象的getName()方法, 获取方法名                        |
| Object[] getArgs()       | 获取方法的所有参数, 这些方法的基本类型参数都应该修改类型声明 如int->Integer, 否则方法会报错 |
| Object getTarget()       | 获取该方法所属的对象(即被代理对象)                           |
| Object getThis()         | 获取代理对象                                                 |



## 四、Spring--JdbcTemplate

### 1. 概述

##### 1.1 简介

> Spring为开发者提供了许多模板, 它们都是对原始Jdbc API的简单封装. 这些模板有JdbcTemplate、HibernateTemplate、RedisTemplate等.

##### 1.2 依赖jar包

spring-jdbc、spring-tx(事务相关)

### 2. 入门案例

```java
public class Test{
    public static void main(String[] args){
        //1. 配置数据源(这里使用Spring内置数据源)
        DriverManagerDataSource ds = new DriverManagerDateSource();
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setUrl("jdbc:mysql://localhost:3306/dw");
        ds.setUserName("root");
        ds.setPassword("123456");
        
        //2. 创建Template模板
        JdbcTemplate template = new JdbcTemplate();
        template.setDatasource(ds);
        
        //3. 使用模板
        template.execute("insert into user(name,age)values("dw",20)");
    }
}

1. 由上面的代码可以看出, 使用IOC配置可以简化上面的代码操作
```

### 3. CRUD操作

```java
/*
    保存、更新、删除操作
*/
void update(String sql, args...arg)

/*
    查询操作
*/
List<T> query(String sql, RowMap<T> map, args...arg)

eg: 
List<User> list = template.query("select * form user where id=?", new BeanPropertyRowMapper<User>(User.class), 1001);
Systemout.out.println(list.isEmpty()?"空集合":list.get(0))

/*
    聚合函数, 查询单行单列
*/
<T> T queryForObject(String sql, Class class, args...arg)

eg
Long count = template.queryForObject("select count(*) from user where age>?", Integer.class, 20); 
```

### 4. JdbcDaoSupport类

在项目开发中, 有很多的Dao接口, 每个Dao接口的实现类都需要定义一个JdbcTemplate成员变量, 这些代码都是重复的, 我们可以让这个实现类都Extends JdbcDaoSupport类, 该类有一个JdbcTemplate成员变量, 我们的实现类就不需要在自己定义JdbcTemplate了, 直接调用getJdbcTemplate()方法, 共同使用父类的JdbcTemplate.



## 五、Spring--事务控制

### 1. 概述

##### 1.1 介绍

+ Spring内置了一套事务控制的API和配置方法.
+ 使用Spring的事务控制需要导入jar包spring-tx.
+ Spring的事务控制是基于AOP的, 它既可以使用编程的方式实现, 也可以使用配置的方式实现.
+ 在JavaEE中, 事务控制位于业务层(Service), 而不是持久层(Dao)

##### 1.2 相关API

+ PlatformTransactionManager接口 事务管理器
    + DataSourceTransactionManager实现类(使用Jdbc或MyBatis时使用)
    + HibernateTransactionManager实现类(使用Hibernate时使用)
+ TransactionDefinition 事务定义信息对象
+ TransactionStatus接口 事务运行状态

### 2. 配置文件方式

```xml
<?xml version="1.0" encoding="UTF-8"?>
  <beans xmlns="http://www.springframework.org/schema/beans"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns:aop="http://www.springframework.org/schema/aop"
         xmlns:tx="http://www.springframework.org/schema/tx"
         xsi:schemaLocation="
          http://www.springframework.org/schema/beans
          http://www.springframework.org/schema/beans/spring-beans.xsd
          http://www.springframework.org/schema/tx
          http://www.springframework.org/schema/tx/spring-tx.xsd
          http://www.springframework.org/schema/aop
          http://www.springframework.org/schema/aop/spring-aop.xsd">
  
      <bean id="accountService" class="com.itheima.service.AccountServiceImpl">
          <property name="accountDao" ref="accountDao"></property>
      </bean>

      <bean id="accountDao" class="com.itheima.dao.impl.AccountDaoImpl">
          <property name="dataSource" ref="dataSource"></property>
      </bean>
  
      <!-- 配置数据源-->
      <bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
          <property name="driverClassName" value="com.mysql.jdbc.Driver"></property>
          <property name="url" value="jdbc:mysql://localhost:3306/eesy"></property>
          <property name="username" value="root"></property>
          <property name="password" value="HotteMYSQL"></property>
      </bean>
  
      
      <!--配置事务管理器-->
      <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
          <property name="dataSource" ref="dataSource"></property>
      </bean>
  
      <!--配置事务通知-->
      <tx:advice id="txAdvice" transaction-manager="transactionManager">
          <!--配置事务属性-->
          <tx:attributes>
              <!--name表示方法名可以使用通配符* 如find*表示所有的find开头的方法-->
              <tx:method name="transfer" propagation="REQUIRED" read-only="false"/>
              <tx:method name="find*" propagation="SUPPORTS" read-only="true"></tx:method>
          </tx:attributes>
      </tx:advice>
  
      <!--配置切面-->
      <aop:config>
          <aop:pointcut id="pt1" expression="execution(* com.itheima.service.*.*(..))"></aop:pointcut>
          <aop:advisor advice-ref="txAdvice" pointcut-ref="pt1"></aop:advisor>
      </aop:config>
</beans>

事务属性:
* isolation：用于指定事务的隔离级别，默认值为 DEFAULT，表示数据库的默认隔离级别
* propagation：用于指定事务的传播行为。默认值是REQUIRED，表示一定会有事务，增删改的选择。查询方法可以选择SUPPORTS。
* read-only：用于指定事务是否只读。只有查询方法才能设置为true。默认值是false，表示读写。
* timeout：用于指定事务的超时时间，默认值是-1，表示永不超时。如果指定了数值，以秒为单位。
* rollback-for：用于指定一个异常，当产生该异常时，事务回滚，产生其他异常时，事务不回滚。没有默认值。表示任何异常都回滚。
* no-rollback-for：用于指定一个异常，当产生该异常时，事务不回滚，产生其他异常时事务回滚。没有默认值。表示任何异常都回滚。
```

### 3. 注解方式

```xml
1. 配置事务管理器Datasource和TransactionManager的<bean>
2. 开启注解事务支持 
   <tx:annotation-driven teansaction-manager="transactionManager"></tx:annotation-driven>  /  或者 在配置类上注解@EnableTransactionManagement
3. 在需要使用事务控制的方法上(应该是Service的方法而不是Dao的方法, 因为事务控制是位于业务层的)加上注解@Transactional(), 注解里可以配置相关属性. 该注解可以作用在方法上也可以作用在类上, 注解在类上, 表示对所有方法进行事务控制
```

