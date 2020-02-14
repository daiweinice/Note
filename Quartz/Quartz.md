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