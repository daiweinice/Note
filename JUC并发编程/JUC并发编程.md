# JUC并发编程

## 一、基础

+ `TimeUnit.Days.sleep(1);`

+ OOP(Object Oriented Programming)解耦

+ `lock.tryLock()`

+ 线程虚假唤醒: https://www.jianshu.com/p/da312eee4ac4

+ 线程sleep不会影响其他线程的运行, 即一个线程sleep则其他线程一样可以运行。

    主线程sleep不会影响在sleep之前就创建的线程, 而sleep之后的代码还未执行, 所以后面的线程此时还不存在。
    
+ Vector是线程安全的, 它的add()方法有synchronized修饰。Vector是JDK1.0就有的, 而ArrayList是1.2才有的。

+ CurrentHashMap底层原理

+ lambda表达式是匿名内部类, 所以里面要使用外面的变量则外面的变量必须是final的。

+ 多个线程可能在不同CPU核上并行运行。

+ synchronized和lock的作用都是加锁, 线程在执行加了锁的代码块时一样因CPU调度而进入等待状态, 只不过其他线程执行时发现没锁又会进入阻塞状态, 获得锁的线程再次进入执行状态时, 因为有所又可以继续执行。

