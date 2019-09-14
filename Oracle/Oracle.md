# Oracle

## 一、Oracle体系结构

#### 1. 数据库

Oracle数据库是数据的物理存储。这就包括（数据文件ORA或者DBF、控制文件、联机日志、参数文件）。其实Oracle数据库的概念和其它数据库不一样，这里的数据库是一个操作系统只有一个库。可以看作是Oracle就只有一个大数据库。

#### 2. 实例

一个Oracle实例（Oracle Instance）有一系列的后台进程（Backguound Processes)和内存结构（Memory Structures)组成。一个数据库可以有n个实例。

#### 3. 用户

用户是在实例下建立的。不同实例可以建相同名字的用户。

#### 4. 表空间

表空间是Oracle对物理数据库上相关数据文件（ORA或者DBF文件）的逻辑映射。一个数据库在逻辑上被划分成一到若干个表空间，每个表空间包含了在逻辑上相关联的一组结构。每个数据库至少有一个表空间(称之为system表空间)。 每个表空间由同一磁盘上的一个或多个文件组成，这些文件叫数据文件(datafile)。一个数据文件只能属于一个表空间。

#### 5. 数据文件(dbf、ora)

数据文件是数据库的物理存储单位。数据库的数据是存储在表空间中的，真正是在某一个或者多个数据文件中。而一个表空间可以由一个或多个数据文件组成，一个数据文件只能属于一个表空间。一旦数据文件被加入到某个表空间后，就不能删除这个文件，如果要删除某个数据文件，只能删除其所属于的表空间才行。

注： 表的数据，是有用户放入某一个表空间的，而这个表空间会随机把这些表数据放到一个或者多个数据文件中。

由于oracle的数据库不是普通的概念，oracle是有用户和表空间对数据进行管理和存放的。但是表不是有表空间去查询的，而是由用户去查的。因为不同用户可以在同一个表空间建立同一个名字的表！这里区分的关键就是用户了！

#### 6. 体系结构图

![1568426487683](images/Oracle体系结构)

## 二、Oracle基础

#### 1. 表空间与用户操作

+ 创建表空间

```sql
create tablespace dw
datafile 'c:\dw.dbf'
size 100m
autoextend on
next 10m;

--dw为表空间名称
--datafile指定表空间物理数据文件地址
--size指定表空间初始大小
--autoextend on设置表空间大小自增长
--next指定每次自增长大小
```

+ 删除表空间

```sql
drop tablespace dw;
```

+ 创建用户

```sql
create user username
identified by password
default tablespace dw

--default tablespace 指定默认用户可以使用的表空间
```

+ 给用户授权

```sql
grant dba to username;

--dba为Oracle中的一种角色权限
--Oracle三种常见角色权限
--1.connect 连接角色, 最基本的权限
--2.resource 开发者角色
--3.dba 超级管理员角色
```

#### 2. 常见数据类型

| 类型              | 描述                                                         |
| ----------------- | ------------------------------------------------------------ |
| varchar、varchar2 | 字符串, 可以通过varchar2(n)指定字符串最大长度                |
| NUMBER            | NUMBER(n)表示长度最多为n的整数、NUMBER(m,n)表示小数, 整数部分为m, 小数部分为n |
| DATA              | 日期类型                                                     |
| CLOB              | 大对象, 表示大文本数据类型. 最大存储容量为4G                 |
| BLOB              | 大对象, 表示二进制数据, 最大存储容量为4G                     |

#### 3. 表操作

+ 创建表

```sql
create table person(
	id number(10),
    name varchar2(10)
);
```

+ 删除表

```sql
drop table tableName
```

+ 修改表

```sql
--添加
alert table 表名称 add(列名1 类型, 列名2 类型);

--修改语法
alert table 表名称 modify(列名1 类型, 列名2 类型);

--修改列名
alert table 表名称 rename column 旧列名 to 新列名;

--删除一列
alert table 表名称 drop column 列名;
```

#### 4. 数据增删改

注意: 数据的增删改都需要`commit`来提交事务. `rollback`表示回滚

+ 增

```sql
insert into person (id, name) values (0001, 'dw');
commit;

--添加数据包含所有属性
insert into person values (0001, 'dw');
commit;
```

+ 改

```sql
update person set name = 'DW', id = 0000 where id = 0001;
commit;
```

+ 删

```sql
--删除表的所有数据
delete from 表名;

--删除表
drop table 表名;

--先删除表, 再创建该表
--效果等同于delete from
--但是在数据量大的情况下, 尤其是在带有索引的情况下, 该操作效率更高
--因为索引虽然会提高查询的效率, 但是会影响增删改的效率
truncate table 表名;
```

#### 5. 序列

对于主键的赋值, 我们应该采用自增的方式实现自动赋值, 而不是手动给主键赋值.这时候就需要用到序列.

+ 创建序列

```sql
--创建一个序列
--序列默认是从1开始, 依次加1
create sequence person_id;

--查询当前序列的值
--由于序列不属于任何一张表, 但是Oracle的查询语句必须要有from关键字
--所以引入dual, 它表示虚表, 只是为了补全语法, 不具备任何意义
select person_id.currval from dual;

--查询序列的下一个值
select person_id.nextval from dual;
```

+ 使用序列实现主键自增长赋值

```sql
insert into person values(person_id.nextval, 'dw');
commit;
```



