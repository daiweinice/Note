# Spring Boot 数据访问

> 对于数据访问层. 无论是SQL还是NOSQL, Spring Boot默认采用整合Spring Data的方式统一处理. 添加大量自动配置, 屏蔽了很多设置. 引入了各种xxxTemplate、xxxRepository来简化我们对数据访问层的操作. 对于我们来说, 只需要进行简单的设置即可.

## 一、JDBC方式

#### 1. 基本使用

1. 导入相关依赖

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
<dependency>
	<groupId>mysql</groupId>
	<artifactId>mysql-connector-java</artifactId>
	<scope>runtime</scope>
</dependency>
```

2. 配置数据源

```yml
spring:
  datasource:
    username: root
    password: 123456
    url: jdbc:mysql://192.168.15.22:3306/jdbc
    driver-class-name: com.mysql.jdbc.Driver
```

3. Spring Boot默认使用`org.apache.tomcat.jdbc.pool.DataSource`作为数据源
4. Spring Boot自动添加了`JdbcTemplate`进入容器, 可以直接使用

```java
public class Test{
    @Autowired
    private JdbcTemplate template;
    
    ...使用template进行数据库操作
    
}
```

#### 2. 整合Driud

1. 导入依赖
2. 编写配置文件

```yml
spring:
  datasource:
    username: root
    password: 123456
    driver-class-name: com.mysql.jdbc.Driver
    url: jdbc:mysql://localhost:3306/ssm_crud
    
    type: com.alibaba.druid.pool.DruidDataSource
    
#   druid数据源其他配置
    initialSize: 5
    minIdle: 5
    maxActive: 20
    maxWait: 60000
    timeBetweenEvictionRunsMillis: 60000
    minEvictableIdleTimeMillis: 300000
    validationQuery: SELECT 1 FROM DUAL
    testWhileIdle: true
    testOnBorrow: false
    testOnReturn: false
    poolPreparedStatements: true
#   配置监控统计拦截的filters，去掉后监控界面sql无法统计，'wall'用于防火墙  
    filters: stat,wall,log4j
    maxPoolPreparedStatementPerConnectionSize: 20
    useGlobalDataSourceStat: true  
    connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500
    
```

3. 自己编写一个配置类, 将datasource绑定上配置文件中的配置并注入容器, 同时注册servlet和listener用于Druid监控

```java
导入druid数据源
@Configuration
public class DruidConfig {

    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean
    public DataSource druid(){
       return  new DruidDataSource();
    }

    //配置Druid的监控
    //1、配置一个管理后台的Servlet
    @Bean
    public ServletRegistrationBean statViewServlet(){
        ServletRegistrationBean bean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
        Map<String,String> initParams = new HashMap<>();

        initParams.put("loginUsername","admin");
        initParams.put("loginPassword","123456");
        initParams.put("allow","");//默认就是允许所有访问
        initParams.put("deny","192.168.15.21");

        bean.setInitParameters(initParams);
        return bean;
    }


    //2、配置一个web监控的filter
    @Bean
    public FilterRegistrationBean webStatFilter(){
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(new WebStatFilter());

        Map<String,String> initParams = new HashMap<>();
        initParams.put("exclusions","*.js,*.css,/druid/*");

        bean.setInitParameters(initParams);

        bean.setUrlPatterns(Arrays.asList("/*"));

        return  bean;
    }
}

```

## 二、MyBatis整合

#### 1. 注解方式

1, 导入依赖

```xml
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>1.3.1</version>
</dependency>
```

2. 配置数据(同上)
3. 创建Dao接口, 用`@Mapper`注解标注

```java
//指定这是一个操作数据库的mapper
//也可以在主程序类上加上注解@MapperScan(value = "包路径"), 这样该包下所有@Mapper都可以省略
@Mapper
public interface DepartmentMapper {

    @Select("select * from department where id=#{id}")
    public Department getDeptById(Integer id);

    @Delete("delete from department where id=#{id}")
    public int deleteDeptById(Integer id);

    @Options(useGeneratedKeys = true,keyProperty = "id")
    @Insert("insert into department(departmentName) values(#{departmentName})")
    public int insertDept(Department department);

    @Update("update department set departmentName=#{departmentName} where id=#{id}")
    public int updateDept(Department department);
}
```

4. 添加`ConfigurationCustomizer`自定义配置

```java
@org.springframework.context.annotation.Configuration
public class MyBatisConfig {

    @Bean
    public ConfigurationCustomizer configurationCustomizer(){
        return new ConfigurationCustomizer(){

            @Override
            public void customize(Configuration configuration) {
            	//配置开启驼峰命名匹配 即userName也会匹配user_name
                configuration.setMapUnderscoreToCamelCase(true);
            }
        };
    }
}
```

#### 2. 配置文件版

```yml
mybatis:
  config-location: classpath:mybatis/mybatis-config.xml 指定全局配置文件的位置
  mapper-locations: classpath:mybatis/mapper/*.xml  指定sql映射文件的位置
```



