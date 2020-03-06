## 一、Quartz

### 1. Quartz简介

> Quartz 是一个完全由 Java 编写的开源作业调度框架，为在 Java 应用程序中进行作业调度提供了简单却强大的机制。
>
> Quartz 可以与 J2EE 与 J2SE 应用程序相结合也可以单独使用。
>
> Quartz 允许程序开发人员根据时间的间隔来调度作业。
>
> Quartz 实现了作业和触发器的多对多的关系，还能把多个作业与不同的触发器关联。

### 2. 核心组件

+ **Job:** 表示一个任务, 封装任务具体内容
+ **JobDetail:** 表示一个具体的可执行的调度程序, 封装任务信息、任务相关数据
+ **Trigger:** 表示一个触发器, 定义任务触发时间
+ **Scheduler:** 表示一个调度器, 一个调度器中可以注册多个 JobDetail 和 Trigger。当 Trigger 与 JobDetail 组合，就可以被 Scheduler 容器调度了



## 二、Quartz入门

#### (1) 导入依赖

导入`quartz.jar`

#### (2) 自定义Job

```java
/*
	Job定义了任务的具体执行内容
*/
public class MyJob implements Job {
    //实现execute方法, 方法体里定义任务具体执行内容
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDetail jobDetail=context.getJobDetail();
 		String name = jobDetail.getKey().getName();//任务名
 		String group = jobDetail.getKey().getGroup();//任务组
 		String job=jobDetail.getJobDataMap().getString("data");//JobDetail中保存的数据
 		System.out.println("job执⾏，job名："+name+" group:"+group+" data:"+job+new Date());
 	}
}
```

#### (3) 编写主程序

```java
public static void main(String[] args) {
 	try{
 		//创建Scheduler
 		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
 		
        //创建Trigger
 		Trigger trigger = TriggerBuilder.newTrigger()
            .withIdentity("trigger1", "group1") //配置JobDetail名字和组
 			.startNow() //一旦加入Scheduler,立即执行. 也可以通过startAt()设置开始时间
 			.withSchedule(SimpleScheduleBuilder.simpleSchedule()
 								.withIntervalInSeconds(1) //每隔⼀秒执⾏⼀次
 								.repeatForever()) //⼀直执⾏，直到结束时间. 也可通过withRepeatCount()指定重复次数
 			.endAt(new GregorianCalendar(2019,7,15,16,7,0).getTime()) //指定结束时间
            .build();
        
 		//创建JobDetail
 		JobDetail jobDetail = JobBuilder.newJob(MyJob.class) //将JobDetail与Job绑定
 			.withIdentity("测试任务1","test") //定义name、group
 			.usingJobData("data","") //存储数据, 传送给Job
 			.build();
 
        //Scheduler将JobDetail和Trigger绑定
 		scheduler.scheduleJob(jobDetail, trigger);
 		
        //启动任务调度
 		scheduler.start(); 
        
        //关闭任务调度, 所有定时任务停止
        Thread.sleep(5000);
        scheduler.shutdown();
 	
    }catch (Exception ex){
 		ex.printStackTrace();
 	}
}
```

#### (4) 配置文件 quartz.properties

```properties
# 可以不用编写配置文件, 没有配置文件的情况下会按默认配置启动

# 指定调度器名称，⾮实现类
org.quartz.scheduler.instanceName = DefaultQuartzScheduler01
# 指定线程池实现类
org.quartz.threadPool.class = org.quartz.simpl.SimpleThreadPool
# 线程池线程数量
org.quartz.threadPool.threadCount = 10
# 优先级，默认5
org.quartz.threadPool.threadPriority = 5
# ⾮持久化job
org.quartz.jobStore.class = org.quartz.simpl.RAMJobStore
```

#### (5) 核心类介绍

+ **Scheduler:** Quartz的大脑, 所有任务都由它调度实施。它包含两个组件:
    + **JobStore:** 存储运⾏时信息, 包括Trigger, Schduler, JobDetail, 业务锁等
    + **ThreadPool:** 线程池，Quartz有⾃⼰的线程池实现。所有任务都会由线程池执行
+ **SchedulerFactory:** 用于创建Scheduler。它有两个实现:
    + **DirectSchedulerFactory:** 在代码中设置Scheduler的参数(未设置按默认值)
    + **StdSchdulerFactory:** 读取`quartz.properties`, 完成Scheduler的相关参数配置(无配置文件按默认值)

```java
//Scheduler相关方法

scheduler.pauseTrigger(TriggerKey.triggerKey("hw_trigger", "hw_trigger_group")); //暂停触发器计时
scheduler.unscheduleJob(TriggerKey.triggerKey("hw_trigger", "hw_trigger_group")); //移除触发器任务
scheduler.deleteJob(JobKey.jobKey("job1","group1"));//移除Job

scheduler.pauseJob(new JobKey("job2","group1")); // 暂停任务
scheduler.resumeJob(new JobKey("job2","group1")); //恢复任务

GroupMatcher<JobKey> group1 = GroupMatcher.groupEquals("group1");
scheduler.pauseJobs(group1); //暂停⼯作组中所有⼯作
scheduler.resumeJobs(group1); //恢复工作组中所有工作
```



## 三、Trigger(Schedule)

### 1. SimpleSchedule

支持秒、分、时为单位的定时

```java
SimpleScheduleBuilder.simpleSchedule()
		.withIntervalInSeconds(10) //每隔10秒执⾏⼀次
//    	.withIntervalInMilliseconds(10) //每隔10毫秒执行一次
		.repeatForever() //一直执行
//    	.withRepeatCount(100) 执行100次
```

### 2. CalenderIntervalSchedule

支持秒、分、时、天、周、月、年为单位的定时

```java
CalendarIntervalScheduleBuilder.calendarIntervalSchedule()
		.withIntervalInDays(2) //每2天执⾏⼀次 
//    	.withIntervalInWeeks(1) 每周执行一次
		.build();
```

### 3. DailyTimeIntervalSchedule

```java
DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule()
		.startingDailyAt(TimeOfDay.hourAndMinuteOfDay(9, 0)) //每天9：00开始
		.endingDailyAt(TimeOfDay.hourAndMinuteOfDay(18, 0)) //每天18：00 结束
//		.endingDailyAfterCount(10) 每天执行10次    
		.onDaysOfTheWeek(MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY) //周⼀⾄周五执⾏
 		.withIntervalInHours(1) //每间隔1⼩时执⾏⼀次
 		.withRepeatCount(100) //最多重复100次(实际执行100+1次)
```

### 4. CronSchedule(重点)

#### (1) Cron表达式

Cron表达式是一个字符串，字符串以5或6个空格隔开，分为6或7个域，每一个域代表一个含义, 合在一起表示一个时间限制条件。

Cron表达式的7个域依次为 **秒、分、时、日、月、星期、年(可省略)**

| 字段               | 允许值                          | 允许的特殊字符  |
| ----------------- | ------------------------------ | --------------- |
| 秒（Seconds）      | 0~59的整数                      | , - * /         |
| 分（Minutes）      | 0~59的整数                      | , - * /         |
| 小时（Hours）      | 0~23的整数                      | , - * /         |
| 日期（DayofMonth） | 1~31的整数（需要考虑月的天数）  | ,- * ? / L W C  |
| 月份（Month）      | 1~12的整数或者 JAN-DEC          | , - * /         |
| 星期（DayofWeek）  | 1~7的整数或者 SUN-SAT （1=SUN） | , - * ? / L C # |
| 年(可选)（Year）   | 1970~2099                       | , - * /         |

特殊字符解释:

| 特殊字符 | 解释                                                         |
| -------- | ------------------------------------------------------------ |
| *        | 可⽤在所有字段中，表示对应时间域的每⼀个时刻。如: 在分钟字段时，表示“每分钟” |
| ?        | 该字符只在⽇期和星期字段中使⽤，它通常指定为“不确定值”。如: 每个月的2号不能确定是星期几, 所以星期字段应该用? |
| -        | 表达⼀个范围。如: 在⼩时字段中使⽤“10-12”，则表示从10到12点，即10,11,12 |
| ,        | 表达⼀个列表值。如: 在星期字段中使⽤“MON,WED,FRI”，则表示星期⼀，星期三和星期五 |
| /        | x/y表达⼀个等步⻓序列，x为起始值，y为增量步⻓值。如: 在分钟字段中使⽤0/15，则表示为0,15,30和45秒 |
| L        | 该字符只在⽇期和星期字段中使⽤，代表“Last”的意思。但它在两个字段中意思不同。<br />L在⽇期字段中，表示这个⽉的最后⼀天。<br />如果在星期字段中, 表示星期六(数字7), 如果L出现在星期字段且前面有数字X, 则表示这个月的最后一个星期X。如: 6L表示这个月的最后一个星期五 |
| W        | 该字符只能出现在⽇期字段⾥，是对前导⽇期的修饰，表示离该⽇期最近的⼯作⽇(不会跨月份)。如: 15W表示离该⽉15号最近的⼯作⽇ |
| LW       | 这个月的最后一个工作日, 即这个月的最后一个星期五             |
| #        | 只能出现在日字段, 表示每个月第几个星期几。如: 4#2表示这个月的第2个星期三 |

表达式示例:

| 表达式              | 说明                                                         |
| ------------------- | ------------------------------------------------------------ |
| 秒 分 时 日 月 周   |                                                              |
| 0 0 12 * * ?        | 每天12点运⾏                                                 |
| 0 15 10 * * ?       | 每天10:15运⾏                                                |
| 0 15 10 * * ? 2008  | 在2008年的每天10：15运⾏                                     |
| 0 * 14 * * ?        | 每天14点到15点之间每分钟运⾏⼀次，开始于14:00，结束于14:59   |
| 0 0/5 14 * * ?      | 每天14点到15点每5分钟运⾏⼀次，开始于14:00，结束于14:55      |
| 0 0/5 14,18 * * ?   | 每天14点到15点每5分钟运⾏⼀次，此外每天18点到19点每5钟也运⾏⼀次 |
| 0 0-5 14 * * ?      | 每天14:00点到14:05，每分钟运⾏⼀次                           |
| 0 0-5/2 14 * * ?    | 每天14:00点到14:05，每2分钟运⾏⼀次                          |
| 0 10,44 14 ? 3 WED  | 3⽉每周三的14:10分和14:44，每分钟运⾏⼀次                    |
| 0 15 10 ? * MON-FRI | 每周⼀，⼆，三，四，五的10:15分运⾏                          |
| 0 15 10 15 * ?      | 每⽉15⽇10:15分运⾏                                          |
| 0 15 10 L * ?       | 每⽉最后⼀天10:15分运⾏                                      |
| 0 15 10 ? * 6L      | 每⽉最后⼀个星期五10:15分运⾏                                |

#### (2) CronSchedule

```java
CronScheduleBuilder.cronSchedule("0 */2 10-12 * * ?") //每天10:00~12:00, 每两分钟执行一次
```



## 四、Job并发

Job是可能并发执行的, 如一个任务执行需要10秒, 而调度算法每1秒触发一次, 那么这个任务就会被并发执行。

有时候我们并不希望这个任务并发执行, 则可以在Job类上加上注解`@DisallowConcurrentExecution`, 加上该注解后, 如果前面的任务还没执行完, 后面的任务会等待前面的任务执行完后再执行。

```java
@DisallowConcurrentExecution
public class MyJob implements Job{
    ...
}
```

**注意:** `@DisallowConcurrentExecution`是对JobDetail实例⽣效，也就是说如果定义两个JobDetail，与同一个Job相关联, 那么还是会并发执行。



## 五、Spring整合Quartz

#### (1) 依赖

导入spring相关依赖和`quartz.jar`即可

#### (2) 配置

spring.xml

```xml
<bean name="jobDetail" class="org.springframework.scheduling.quartz.JobDetailFactoryBean">
	<!-- 指定job的名称 -->
	<property name="name" value="job1"/>
	<!-- 指定job的分组 -->
	<property name="group" value="group1"/>
	<!-- 指定具体的job类 -->
	<property name="jobClass" value="com.zhj.quartz0.MyJob"/>
 	<!-- 如果为false，当没有活动的触发器与之关联时会在调度器中会删除该任务 (可选) -->
 	<property name="durability" value="true"/>
</bean>

<bean id="cronTrigger" class="org.springframework.scheduling.quartz.CronTriggerFactoryBean">
	<!-- 指定Trigger的名称 -->
 	<property name="name" value="hw_trigger"/>
 	<!-- 指定Trigger的名称 -->
 	<property name="group" value="hw_trigger_group"/>
 	<!-- 指定Tirgger绑定的JobDetail -->
 	<property name="jobDetail" ref="jobDetail"/>
 	<!-- 指定Cron 的表达式 ，当前是每隔5s运⾏⼀次 -->
 	<property name="cronExpression" value="*/5 * * * * ?" />
</bean>

<bean id="scheduler" class="org.springframework.scheduling.quartz.SchedulerFactoryBean">
	<property name="triggers">
		<list>
			<ref bean="cronTrigger"/>
			<ref bean="cronTrigger2"/>
 		</list>
 	</property>
 	<!-- 添加 quartz 配置 -->
 	<property name="configLocation" value="classpath:quartz.properties"></property>
</bean>
```

#### (3) 编码

自定义Job



## 六、Quartz持久化

#### (1) 数据库

根据官方提供的脚本创建数据库, 完成Quartz持久化设置后, 所有的任务、触发器等都会入库。

有关数据库的操作都是Quartz自动完成的, 无需我们手动实现。

#### (2) 配置

```xml
<!--如果没有固定任务, 则不用再定义JobDetail和Trigger. 后续可以通过代码动态添加任务-->
<bean id="scheduler" class="org.springframework.scheduling.quartz.SchedulerFactoryBean">
	<property name="quartzProperties">
	<value>
 		# 指定调度器名称，实际类型为：QuartzScheduler
 		org.quartz.scheduler.instanceName = MyScheduler78
 		# 指定连接池
 		org.quartz.threadPool.class = org.quartz.simpl.SimpleThreadPool
 		# 连接池线程数量
 		org.quartz.threadPool.threadCount = 11
 		# 优先级
 		org.quartz.threadPool.threadPriority = 5
 		#持久化
 		org.quartz.jobStore.class = org.quartz.impl.jdbcjobstore.JobStoreTX
 		#quartz表的前缀
 		org.quartz.jobStore.tablePrefix = QRTZ_
 	</value>
 	</property>
 	<!-- 数据库连接池 -->
 	<property name="dataSource" ref="druidDataSource"></property>
</bean>
```

#### (3) 动态添加任务

前端通过表单传递动态任务信息, 后端动态添加任务, 添加后任务会自动入库并且按照设定时间规则执行。 

```java
@Controller
@RequestMapping("/quartz")
public class TestController {
	@Autowired
 	private Scheduler scheduler;
 
 	// 添加⼀个定时任务
    //JobAndTrigger封装前端表单传来的任务信息
 	@RequestMapping("add")
 	public String addJob(JobAndTrigger jt) throws ClassNotFoundException, SchedulerException {
 		
        // 创建JobDetail
 		JobDetail jobDetail=null;
 		jobDetail = JobBuilder.newJob((Class<? extends Job>)Class.forName(jt.getJobClassName()))
 			.withIdentity(jt.getJobName(), jt.getJobGroup())
            .storeDurably(true).build();
 
        CronTrigger cronTrigger = null;
 		cronTrigger = TriggerBuilder.newTrigger()
            .withIdentity(jt.getJobName(),jt.getJobGroup())
 			.withSchedule(CronScheduleBuilder.cronSchedule(jt.getCronExpression()))
 			.build();
 
        scheduler.scheduleJob(jobDetail,cronTrigger);

		return "redirect:query";
    }
}
```



