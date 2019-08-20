# Spring MVC 数据校验和国际化

## 一、数据校验

#### 1. JSR 303注解实现数据校验

> JSR303 是一套JavaBean参数校验的标准, 它定义了很多常用的校验注解, 我们可以直接将这些注解加在我们JavaBean的属性上面, 就可以在需要校验的时候进行校验

常用校验注解:

| 注解                       | 详细信息                                     |
| :------------------------- | :------------------------------------------- |
| @Null                      | 注解元素必须为空                             |
| @NotNull                   | 注解元素不能为空                             |
| @AssertTrue                | 注解元素必须为true                           |
| @AssertFalse               | 注解元素必须为false                          |
| @Min(value)                | 注解元素必须为一个整数, 且值最小为指定value  |
| @Max(value)                | 注解元素必须为一个整数, 且值最大为指定value  |
| @DecimalMin(value)         | 注解元素必须为一个小数, 且其最小为指定value  |
| @DecimalMax(value)         | 注解元素必须为一个小数, 且其最大为指定value  |
| @Size(max, min)            | 注解元素的大小必须在指定范围                 |
| @Digits(integer, fraction) | 注解元素必须是一个数字, 且其值在可接受范围内 |
| @Past                      | 注解元素必须是一个过去的日期                 |
| @Futrue                    | 注解元素必须是一个将来的日期                 |
| @Pattern(value)            | 注解元素必须符合指定正则表达式               |



1. 导入jar包 `classmate.jar`、`jboss-loggng.jar`、`hibernate-validator.jar`、`validation-api.jar`
2. 编写Bean

```java
public class Data{
    @Max(value=10)
    @Min(value=1)
    private int number1;
    
    @DecimalMax("99.99")
    private double number2; 
    
    @Pattern(regexp="...", message="自定义错误信息")
    private String email;
    
    @Size(min=0, max=99)  //字符串长度为0到99
    private String note;
}
```

3. 控制器验证表单

```java
@Controller
public class Controller{
    
    @RequestMapping("/test")
    public ModelAndView test(@Valid Data data, Errors errors){
        if(errors.hasErrors()){
            List<FiledError> errorList = errors.getFieldErrors();
            for(FieldError fieldError : errorList){
                System.out.println(fieldError.getField()+" "+fieldError.getDefaultMessage());
            }   
        }
        ......
    }
    
}
```

给参数`Data data`加上`@Valid`表示开启校验, 第二个参数`Errors errors`用来接收错误信息

通过`hasErrors()`方法判断是否有错误出现, 如果有可以通过`getFieldErrors()`获取错误对象, 通过其`getField()`获取校验失败的属性名、`getDefaultMessage()`获取错误信息

#### 2. 使用验证器进行校验

有时候除了对各数据格式、大小的检测, 还需要进行业务逻辑的检测. Spring提供了`Validator`接口来实现自定义业务逻辑校验.

注意: 验证器和JSR 303不能同时使用.

Validator接口:

```java
public interface Validator{
    
    /*
    	判断当前class类型是需要检验
    */
    boolean supports(Class<?> class);
    
    /*
    	进行数据检验
    */
    void validate(Object obj, Errors errors)
}
```

1. 自定义验证器

```java
public class MyValidator implements Validator{
    
    @Override
    public boolean supports(Class<?> class){
        return Data.class.equals(class);
    }
    
    @Override
    public void validate(Object obj, Errors errors){
        Data data = (Data)obj;
        
        if(data.getNumber1()-data.getNumber2() > 0){
            errors.rejectValue("Number1", null, "验证失败");
        }
        
    }
}
```

2. 使用验证器

```java
@Controller
public class Controller{

	@InitBinder
	pulic void initBinder(DataBinder binder){
		//绑定自定义验证器
		binder.setValidator(new MyValidator());
	}
	
	@RequestMapping("/test")
    public ModelAndView test(@Valid Data data, Errors errors){
        if(errors.hasErrors()){
            List<FiledError> errorList = errors.getFieldErrors();
            for(FieldError fieldError : errorList){
                System.out.println(fieldError.getField()+" "+fieldError.getDefaultMessage());
            }   
        }
        ......
    }

}
```

## 二、国际化

> Web网站根据用户区域的不同, 对页面显示语言进行相应的改变. 这就是国际化.

#### 1. Java国际化API

i18n: Internationalization的简称, 因其i和n中间有18个字母, 故简称i18n

Java API为我们提供了与国际化有关的类:

1. **Locale:** 封装了各区域的相关信息, 每一个区域用一个Locale对象表示

```java
/*
	遍历该数组, 依次打印每个对象
	输出的值为区域信息代码. 格式为: 语言代码_国家代码
	如: zh_CN、en_US
*/
Locale[] locales = Locale.getAvailableLocales();

/*
	获取默认区域信息对象, 该对象由当前操作系统决定
*/
Locale default = Locale.getDefault();

/*
	获取对应国家的区域信息代码
*/
Locale china = Locale.CHINA

/*
	使用构造方法, 构造一个Locale
*/
Locale china = new Locale("zh", "CN");
```

2. **ResourceBundle:** 绑定对应区域的资源

资源就是一个properties配置文件, 里面配置了各个Key和对应语言的Value

资源的命名规则: `基础名_区域信息代码.properties`

user_zh_CN.properties

```properties
name=张三
```

user_en_US.properties

```properties
name=zhangsan
```

通过ResourceBundle来绑定资源

```java
Locale china = Locale.CHINA; //获取中国区域信息对象
Locale Us = Locale.US; //获取美国区域信息对象

ResourceBundle bundle = ResourceBundle.getBundle("user", china);
String name1 = bundle.getString("name"); //值为张三

ResourceBundle bundle = ResourceBundle.getBundle("user", Us);
String name2 = bundle.getString("name"); //值为zhangsan
```

3. **DateFormat:** 日期格式化

```java
/*
	常用方法:
	getDateInstance() 2019-11-11
	getDateTimeInstance() 2019-11-11 11:11:11
    getTimeInstance() 11:11:11
    
    日期风格(默认MEDIUM):
    DateFormat.FULL
			  .LONG
              .MEDIUM
              .SHORT
*/
Locale china = Locale.CHINA;

DateFormat instance = DateFormat.getDateInstance(DateFormat.FULL, china);
String formatStr = instance.format(new Date());
System.out.println(formatStr);
```

4. **NumberFormat:** 数字格式化

```java
/*
	常用方法:
	getInstance 一般数字转化
	getCurrencyInstance 货币格式转化
*/
Locale china = Locale.CHINA;
NumberFormat instance = NumberFormat.getInstance(china);
String formatStr = instance.format(99999.99);
```

5. **MessageFormat:** 消息格式化

message_zh_CN.properties

```properties
msg={0} like {1}
```

```java
Locale china = Locale.CHINA;
ResourceBundle bundle = ResourceBundle.getBundle(message, china);
String msg = budnle.getString("msg");
String msgFormat = MessageFormat.format(msg, "dw", "???");
```

#### 2. Web国际化

用浏览器访问某个页面时, 请求头中就由当前的区域信息代码. 我们可以获取该区域信息代码, 实现Web页面国际化.

在Jsp页面, 我们可以通过fmt标签库来实现页面国际化

```jsp
...
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!--设置Locale对象, 这里通过${param.loc}获取请求参数loc的值, 这里的值需要区域代码-->
<fmt:setLocale value="${param.loc}" />
<!--设置Bundle绑定的资源, basename指定其基础名-->
<fmt:setBundle basename="user"/>

<fmt:message key="name" />

<fmt:formatDate value="<% new Date() %>" />

<!--如果配置的name={0} like {1}-->
<fmt:message key="name">
    <fmt:param>dw</fmt:param>
	<fmt:param>???</fmt:param>
</fmt:message>
```

#### 3. Spring MVC国际化

**一个简单的国际化:**

1. 配置好各区域资源login_zh_CN.properties、login_en_US.properties
2. 配置ResourceBundleMessageSource类

```xml
<bean id="messageSource" class="....ResourceBundleMessageSource">
    <property name="basename" value="login"></property>
</bean>
```

3. 使用fmt标签完成国际化

**Spring MVC如何获取区域信息:**

在Spring MVC中, `DispatcherServlet`会创建一个`LocaleResolver`, 它是一个接口, 它由四个实现类
`AcceptHeaderLocaleResolver`、`FixedLocaleResolver`、`CookieLocaleResolver`、`SessionLocaleResolver`. 其中默认使用`AcceptHeaderLocaleResolver`, 它会获取请求头的区域信息来作为当前设置的区域信息.

**自定义LocaleResolver**

通过自定义LocaleResolver, 实现点击链接切换区域信息, 实现国际化

1. 方法一: 自定义一个LocaleResolver

```html
<a href="...?locale=en-US>English</a>
```

```java
public class MyLocaleResolver implements LocalResolver{
    
    @Override
    public Locale resolveLocale(HttpServletRequest request){
        Locale locale = null;
        String localeStr = request.getParameter("locale")
        if(localeStr!=null || localeStr.equals("")){
            Locale locale = new Locale(localeStr.split("-")[0], localeStr.split("-")[1]);
            return locale
        }else{
            return new Locale("zh", "CN");
        }
    }
    
    @Override //该方法不用重写
    public ...
}
```

```xml
<!--id固定-->
<bean id="messageSource" class="....ResourceBundleMessageSource">
    <property name="basename" value="login"></property>
</bean>

<!--id固定-->
<!--配置自己的resolver, 用来替换默认的AcceptHeaderLocaleResolver-->
<bean id="localeResolver" class="com.dw.resolver.MyLocaleResolver"></bean>
```

**SessionLocalResolver**

从Session中获取区域信息

1. 将链接的请求参数传递到控制器参数中, 将该参数封装成Locale对象, 并将该Locale对象保存在session中, 该对象的名字是`LOCALE_SESSION_ATTRIBUTE_NAME`
2. 配置SessionLocaleResolver

```xml
<bean id="localeResolver" class="...SessionLocaleResolver">
	<property name="defaultLocale" value="zh_CN"></property>
</bean>
```

**国际化拦截器(LocaleChangeInterceptor)**

上述实现还可以通过`LocaleChangeInterceptor`结合`SessionLocaleResolver`的方式实现

1. 配置一个SessionLocaleResolver

```xml
<bean id="localeResolver" class="...SessionLocaleResolver">
	<property name="defaultLocale" value="zh_CN"></property>
</bean>
```

2. 配置拦截器

```xml
<mvc:interceptors>
	<mvc:interceptor>
        <mvc:mapping path="/login" />
        <bean class="...LocaleChangeInterceptor">
            <property name="paramName" value="language"></property>
        </bean>    
    </mvc:interceptor>
</mvc:interceptors>
```

注意: 链接的请求参数的名字必须与配置的paramName相同, 如果没有配置, 默认为locale.

