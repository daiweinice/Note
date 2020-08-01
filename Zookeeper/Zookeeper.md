## 一、Zookeeper概述

### 1. 简介

> Zookeeper 是一个**分布式协调服务**的开源框架。主要用来解决分布式集群中应用系统的一致性问题，例如怎样避免同时操作同一数据造成脏读的问题。
>
> ZooKeeper 本质上是一个分布式的小**文件存储系统**。提供基于类似于文件系统的**目录树**方式的数据存储，并且可以对树中的节点进行有效管理。从而用来维护和监控你存储的数据的状态变化。通过监控这些数据状态的变化，从而可以达到**基于数据的集群管理**。诸如：统一命名服务、分布式配置管理、分布式消息队列、分布式锁、分布式协调等功能。

### 2. Zookeeper特性

#### (1) 全局数据一致

集群中每个服务器保存一份相同的数据副本，client 无论连接到哪个服务器，展示的数据都是一致的，这是其最重要的特征

#### (2) 可靠性

如果消息被其中一台服务器接受，那么将被所有的服务器接受。

#### (3) 顺序性

包括全局有序和偏序两种：全局有序是指如果在一台服务器上消息 a 在消息 b 前发布，则在所有 Server 上消息 a 都将在消息 b 前被发布；偏序是指如果一个消息 b 在消息 a 后被同一个发送者发布，a 必将排在 b 前面。

#### (4) 数据更新原子性

一次数据更新要么成功（半数以上节点成功），要么失败，不存在中间状态。

#### (5) 实时性

Zookeeper 保证客户端将在一个时间间隔范围内获得服务器的更新信息，或者服务器失效的信息。

### 3. Zookeeper集群角色

#### (1) leader

Zookeeper集群工作的核心、**事务请求（写操作）的唯一调度和处理者**，保证集群事务处理的顺序性、集群内部各个服务器的调度者。

对于 create，setData，delete 等有写操作的请求，则需要统一转发给leader 处理，leader 需要决定编号、执行操作，这个过程称为一个事务。

#### (2) follower

处理客户端非事务（读操作）请求、转发事务请求给 Leader、参与集群 Leader 选举投票。

#### (3)observer

同follower, 但是没有选举权

### 4. Zookeeper数据模型

#### (1) Znode

Zookeeper的数据模型, 在结构上与标准文件系统类似, 为树形结构, 每一个节点被成为Znode. 

1. Znode**既是文件夹也是文件**, 也就是说Znode即可以拥有其子Znode, 也可以存储数据. 

2. Znode具有**原子性操作**, 每次读操作将获取节点所有数据, 每次写操作将替换掉节点的所有数据. 同时每一个节点都有其ACL(访问控制列表), 该列表规定了不同用户的权限.

3. Znode各节点的引用是通过路径方式实现的, 如`/aaa/bbb`.  **路径必须是绝对路径**(即从根节点/开始)
4. Znode**存储数据有大小限制**. 每个节点一般用于存放分布式应用中的配置文件信息、状态信息、汇集位置等, 这些数据都是很小的, 所以节点的存储大小一般以KB为单位, 最多1M.

#### (2) 节点类型

1. 临时节点. 一旦客户端会话结束便会被删除. **临时节点不能拥有子节点**.

2. 永久节点. 

3. 顺序节点. 创建的时候, Znode的名字后面会追加一个不断增加的序列号, 序列号对于其父节点是唯一的, 该**序列号可以表示子节点创建的先后顺序**.

4. 各类型节点英文名:

    PERSISTENT：永久节点
    EPHEMERAL：临时节点
    PERSISTENT_SEQUENTIAL：永久节点、顺序(序列化)
    EPHEMERAL_SEQUENTIAL：临时节点、顺序(序列化)

#### (3) 节点属性

+ **dataVersion** : 数据版本号, 每对数据进行一次修改, 版本号就会+1
+ **cversion** : 子节点版本号, 当子节点有变化时, 版本号就会+1
+ **aclVersion** : acl版本号
+ **cZxid** : Znode创建的事务的id
+ **mZxid** : Znode被修改的事务id, 即每次对znode进行修改都会更新mZxid
    对于zookeeper来说, 每次变化都会产生一个唯一的事务id--zxid, **通过zxid可以判断事务执行的先后顺序**.
+ **ctime** : 节点创建时间戳
+ **mtime** : 节点最后一次更新时的时间戳
+ **ephemeralOwner** : 如果为临时节点, 值为与节点绑定的session id. 如果时永久节点, 值为0

### 5. Zookeeper 订阅与发布

#### (1) watcher机制

ZooKeeper 提供了分布式数据发布/订阅功能，一个典型的发布/订阅模型系统定义了一种一对多的订阅关系，能让**多个订阅者同时监听某一个主题对象**，当这个主题对象自身状态变化时，会通知所有订阅者，使他们能够做出相应的处理。

ZooKeeper 中，引入了 Watcher 机制来实现这种分布式的通知功能。ZooKeeper 允许**客户端向服务端注册一个 Watcher 监听**，当服务端的一些事件触发了这个 Watcher，那么就会向指定客户端发送一个事件通知来实现分布式的通知功能。

触发事件种类很多，如：节点创建，节点删除，节点改变，子节点改变等。

#### (2) watcher机制特点

+ **一次触发** : 事件发生触发监听，一个 watcher event 就会被发送到设置监听的客户端， 这种效果是一次性的，**后续再次发生同样的事件，不会再次触发**。 
+ **事件封装** : ZooKeeper 使用 WatchedEvent 对象来封装服务端事件并传递。 WatchedEvent 包含了每一个事件的三个基本属性： 通知状态（keeperState），事件类型（EventType）和节点路径（path） 
+ **异步发送** : watcher 的通知事件从服务端发送到客户端是异步的。
+ **先注册再触发** : Zookeeper 中的 watch 机制，**必须客户端先去服务端注册监听**，这样事件发送才会触发监听，通知给客户端。

**注意:** 一些连接状态的事件无需手动订阅.



## 二、Zookeeper搭建

### 1. 单机搭建

Linux安装Zookeeper: https://www.jianshu.com/p/ed6ec88b01c3

错误解决: https://www.cnblogs.com/zhoading/p/11593972.html

端口: https://www.cnblogs.com/gudi/p/7828271.html

### 2. 集群搭建

1. 配置Java环境

2. 配置IP与主机名映射

3. 修改zookeeper配置文件(默认为`/zookeeper/conf/zoo.cfg`)

    ```properties
    tickTime=2000
    initLimit=10
    syncLimit=5
    dataDir=/usr/local/zookeeper/data    #数据保存位置
    clientPort=2181       #端口号
    server.1=192.168.2.158:2888:3888  #各节点的IP(也可以用映射的主机名):心跳检测端口:选举端口
    server.2=192.168.2.152:2888:3888
    server.3=192.168.2.150:2888:3888
    ```

4. 设置myid

    在数据保存文件夹中创建一个文件, 名字为myid, 在里面添加内容, 内容为主机对应的编号(server.x中的x为编号)

5. 将zookeeper文件拷贝到其他节点, 并进行相应的配置

6. 配置环境变量

    ```shell
    export ZK_HOME=/usr/local/zookeeper
    export PATH=$ZK_HOME/bin:$PATH
    ```

7. 通过`zkServer.sh start`启动各节点, 完成集群搭建

8. 通过`zkServer.sh status`查看节点状态



## 三、Zookeeper Shell

+ `zkCli.sh -server ip/hostname` 客户端连接
+ `create [-s] [-e] path data acl  `创建节点. -s表示序列节点(默认为非序列)、-e表示临时节点(默认为永久)
+ `ls path [watch] ` 查看节点的所有一级子节点. 加上watch字段表示订阅.
+ `get path [watch]` 查看指定节点的数据内容
+ `ls2 path [watch]` 查看指定节点属性信息
+ `set path data [version] ` 更新节点
+ `delete path [version]` 删除节点, 如果该节点有子节点, 会报错
+ `rmr path` 递归删除指定节点及其所有子节点
+ `setquota -n -b path` 对节点增加限制. n表示子节点最大个数、b表示数据值最大长度. 如果查出限制, 也会成功, 只不过日志记录会打印一个warning
+ `listquota path ` 查看指定节点的quota
+ `delquota [-n|-b] path ` 删除指定节点quota
+ `history` : 查看命令历史
+ `redo xx` : 与`history`配合使用, 执行指定的历史命令



##  四、Zookeeper Java API

相关依赖jar包: `zookeeper.jar`

```java
public static void main(String[] args) throws Exception {
	
    // 初始化 ZooKeeper 实例(zk 地址、会话超时时间，与系统默认一致、watcher)
	ZooKeeper zk = new ZooKeeper("node-1:2181,node-2:2181", 30000, new Watcher() {
        @Override
        public void process(WatchedEvent event) {
        System.out.println("事件类型为：" + event.getType());
        System.out.println("事件发生的路径：" + event.getPath());
        System.out.println("通知状态为：" +event.getState());
    	}
	});
    
    // 创建一个目录节点
	zk.create("/testRootPath", "testRootData".getBytes(), Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT); 
	// 创建一个子目录节点
	zk.create("/testRootPath/testChildPathOne", "testChildDataOne".getBytes(),Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT); 
	System.out.println(new String(zk.getData("/testRootPath",false,null))); 
	// 取出子目录节点列表
	System.out.println(zk.getChildren("/testRootPath",true)); 
	// 修改子目录节点数据
	zk.setData("/testRootPath/testChildPathOne","modifyChildDataOne".getBytes(),-1); 
	System.out.println("目录节点状态：["+zk.exists("/testRootPath",true)+"]"); 
	// 创建另外一个子目录节点
	zk.create("/testRootPath/testChildPathTwo", "testChildDataTwo".getBytes(), Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT); 
	System.out.println(new 	String(zk.getData("/testRootPath/testChildPathTwo",true,null))); 
	// 删除子目录节点
	zk.delete("/testRootPath/testChildPathTwo",-1); 
	zk.delete("/testRootPath/testChildPathOne",-1); 
	// 删除父目录节点
	zk.delete("/testRootPath",-1);
	zk.close();
}    
```



## 五、Zookeeper选举机制

### 1. 基本概念

+ **服务器 ID**
    比如有三台服务器，编号分别是 1,2,3。
    **编号越大在选择算法中的权重越大**。
+ **选举状态**
    LOOKING，竞选状态。
    FOLLOWING，随从状态，同步 leader 状态，参与投票。
    OBSERVING，观察状态,同步 leader 状态，不参与投票。
    LEADING，领导者状态。
+ **数据 ID**
    服务器中存放的最新数据 version。
    **值越大说明数据越新，在选举算法中数据越新权重越大**。
+ **逻辑时钟**
    也叫投票的次数，同一轮投票过程中的逻辑时钟值是相同的。每投完一次票
    这个数据就会增加，然后与接收到的其它服务器返回的投票信息中的数值相比，
    根据不同的值做出不同的判断。

### 2. 全新集群选举

假设目前有 5 台服务器，每台服务器均没有数据，它们的编号分别是1,2,3,4,5,按编号依次启动，它们的选择举过程如下：

+ 服务器 1 启动，**给自己投票**，然后发投票信息，由于其它机器还没有启动所以它收不到反馈信息，服务器 1 的状态一直属于 Looking。
+ 服务器 2 启动，给自己投票，同时与之前启动的服务器 1 交换结果，由于服务器 2 的编号大所以服务器 2 胜出，但**此时投票数没有大于半数(总票数5 得票2)**，所以两个服务器的状态依然是 LOOKING。 
+ 服务器 3 启动，给自己投票，同时与之前启动的服务器 1,2 交换信息，由于服务器 3 的编号最大所以服务器 3 胜出，**此时投票数正好大于半数(总票数5 得票3)**，所以服务器 3 成为领导者，服务器 1,2 成为小弟。
+ 服务器 4 启动，给自己投票，同时与之前启动的服务器 1,2,3 交换信息，尽管服务器 4 的编号大，但之前服务器 3 已经胜出，所以服务器 4 只能成为小弟。
+ 服务器 5 启动，后面的逻辑同服务器 4 成为小弟。

### 3. 非全新集群选举

对于运行正常的 zookeeper 集群，中途leader机器 down 掉，需要重新选举时，选举过程就需要加入数据 ID、服务器 ID 和逻辑时钟。

+ 数据 ID：数据新的 version 就大，数据每次更新都会更新 version。
+ 服务器 ID：就是我们配置的 myid 中的值，每个机器一个。
+ 逻辑时钟：这个值从 0 开始递增,每次选举对应一个值。 如果在同一次选举中,这个值是一致的。

这样选举的标准就变成：

1. 逻辑时钟小的选举结果被忽略，重新投票
2. 统一逻辑时钟后，数据 id 大的胜出
3. 数据 id 相同的情况下，服务器 id 大的胜出

根据上述规则, 选举出leader.

![img](images/%E9%80%89%E4%B8%BE%E6%9C%BA%E5%88%B6)



## 六、Zookeeper实现分布式锁





## X、Zookeeper 案例

### 1. 分布式配置中心

以往的配置信息是写在配置文件中的, 在分布式环境下, 要更换配置信息则需要更换所有的配置文件。如果把配置信息保存在Zookeeper中, 那么利用Zookeeper的watch机制, 每次配置信息变化所有的服务器都可以同时同步到最新的配置信息。

### 2. 分布式唯一ID

在单服务器系统中, 我们数据库表的id属性可以利用自增来实现唯一ID。但是在分布式系统中, 这样的方法会造成ID重复。我们可以利用Zookeeper来生成临时有序节点, 取节点序列号作为分布式环境下的唯一ID。

当然, 除了该方法, 还有**雪花算法**等方法也可以解决分布式唯一ID问题。

### 3. 服务注册与订阅

### 4. 服务命名

### 5. 数据订阅、发布

### 6. 分布式锁