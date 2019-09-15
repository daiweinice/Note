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
default tablespace dw;

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

#### 6. 查询

+ scott用户

    scott用户是oracle内置的一个用户, 里面有几张典型的表, 用于练习查询操作.

    默认scott用户的用于名为scott, 密码为tiger

```sql
--解锁scott用户
alter user scott account unlock;

--解锁scott用户密码, 也可用于修改密码
alter user scott identified by tiger;
```

+ 单行函数

    作用于一行, 返回一个值

```sql
--常见单行函数

--字符函数
    select upper('yes') from dual; --转换为全大写
    select lower('YES') from dual; --转换为全小写

--数值函数
    select round(56.12) from dual; --结果为56
    select round(56.16, -1) from dual; --结果为56.2
    select trunc(56.16, -1) form dual; --结果为56,1
    select mod(10, 3) from dual; --求余数

--日期函数
	--sysdate为当前系统时间, 该结果为求所有员工入职几天. e为emp的别名
	select sysdate-e.date from emp e;
	--算出明天此时的时间
    select sysdate+1 from dual;
    --查询所有员工入职到现在几个月
    select months_between(sysdate. e.date) from emp e;
    --查询所有员工入职几年
    select months_between(sysdate, e.date)/12 from emp e;
    --查询所有员工入职几周
    select (sysdate-e.date)/7 from emp e;
    --日期转字符串
    --yyyy-mm-dd hh:mi:ss 2019-09-14 1:56:59
    --fm yyyy-mm-dd hh24:mi:ss 2019-9-14 13:56:59
    select to_char(sysdate, 'yyyy-mm-dd hh:mi:ss') from dual;
    --字符串转日期
    select to_date('2019-9-14 13:56:59','fm yyyy-mm-dd hh24:mi:ss')

--通用函数
	--查询员工一年的工资和奖金总和, 如果奖金为null, 则在相加时自动转换为0
	select e.sal*12+nvl(e.com, 0) from emp e;
```

+ 多行函数(聚合函数)

```sql
select count(1) from emp; --查询结果总数 count(1)与count(*)一致
select sum(sal) from emp; --工资总数
select max(sal) from emp; --工资最大值
select min(sal) from emp; --工资最低值
select avg(sal) from emp; --平均工资
```

+ 条件表达式

```sql
--通用写法 MySql和Oracle通用
--查询员工姓名, 将它们的姓名换成英文名
select e.name,
	case e.name
		when 'Smith' then '史密斯'
		when 'Tom' then '汤姆'
		else '无名'
	end
from emp e;

--查询员工工资水平, 3000以上为高收入, 1500~3000为中等收入, 其他为低等收入
select e.sal,
	case
		when e.sal>3000 then '高等收入'
		when e.sal>1500 then '中等收入'
		else '低等收入'
	end
from emp e;

--Oracle专用写法
--别名可以直接写, 也可以用""括起来, 但是不能使用''
--在Oracle中, 除了起别名, 都用''
select e.name,
	decode(e.name, 'Smith', '史密斯', 'Tom', '汤姆', '无名') 别名
from emp e;
```

+ 分组查询

```sql
--查询每个部门的平均工资
select e.deptno, avg(e.sal)
from emp e
group by e.deptno;

--查询平均工资高于2000的部门
--所有条件判断语句都不能使用别名
select e.deptno, avg(e.sal) avgSal
from emp e
group by e.deptno
having avg(e.sal)>2000;

--计算每个部门工资高于800的员工的平均工资, 并查询平均工资高于2000的部门
--where是过滤分组前的数据, 写在group之前
--having是过滤分组后的数据, 写在group之后
select e.deptno, avg(e.sal)
from emp e
where e.sal>800
group by e.deptno
having avg(e.sal)>2000;
```

+ 多表查询

```sql
--笛卡尔积
select *
from emp, dept;

--等值连接
select *
from emp e, dept d
where e.deptno=d.deptno;

--内连接(同等值连接)
select * 
from emp e inner join dept d
on e.deptno = d.deptno;

--右外连接
--查询所有部门信息,以及部门下的员工信息
select *
from emp e right join dept d
on e.deptno = d.deptno;

--左外连接
--查询所有员工信息, 以及员工的部门信息
--注意与内连接区别, 当员工部门都有信息时与内连接相同. 当有员工的部门信息没有时, 左外连接的查询结果是员工个数, 而内连接的查询结果没有该员工的信息.
select * 
from emp e left join dept d
on e.deptno = d.deptno;

--Oracle专用外连接写法
--查询部门信息, 以及部门下的员工信息
select *
from emp e, dept d
where e.deptno(+) = d.deptno

--Oracle专用外连接写法
--查询员工信息, 以及员工部门信息
select *
from emp e, dept d
where e.deptno = d.deptno(+)

--自连接
--员工和领导都在一张表, 查询员工及其领导
select e1.name, e2.name
from emp e1, emp e2
where e1.mgr = e2.name;

--查询员工名字和部门, 以及员工领导的名字和部门
select e1.name, d1.dept.no, e2.name, d2.deptno
from emp e1, emp e2, dept d1, dept d2
where e1.mgr = e2.name
and e1.deptno = d1.deptno
and e2.deptno = d2.deptno;
```

+ 子查询

```sql
--查询工资和scott员工一样的员工信息
--如果name查询结果不唯一, 使用=就会出现错误, 所以建议把=换成in
select * from emp where sal =
(select sal from emp where name = 'scott');

--查询工资和10号部门任意一员工工资相同的员工信息
select * from emp where sal in
(select sal from emp where deptno = 10);

--查询每个部门最低工资, 和最低工资员工姓名, 和该员工的所在部门名称
select e.name, t.min(sal), d.deptno
from (select deptno, min(sal)
		from emp
		group by deptno) t, emp e, dept d
where t.min(sal) = e.sal
and t.deptno = e.deptno
and e.deptno = d.deptno;

```

+ 分页查询

```sql
--当我们在做select查询时, 没查询一条数据, 都会加上一个行号
--这个行号就是rownum, 行号从1开始, 依次递增
--我们可以使用rownum进行分页

--在使用排序操作后, rownum的顺序会被打乱
--如下查询结果rownum是乱的, 因为先查询出所有结果, 并依次添加roenum, 然后再进行排序, 所以rownum顺寻是乱的
select rownum,e.* from emp e order by e.sal desc; 

--如果涉及到排序, 还要使用roenum, 则需要进行嵌套
--此时rownum就是顺序的
select rownum, result.*
from (select * from emp order by sal desc) result;

--分页
--该情况下, 由于rownum必须从1开始, 而这里rownum>5, 则第一个rownum不能从1开始, 所以会出错, 所以rownum不能添加大于判断条件
--解决该问题还需要加上一层嵌套
select rownum, result.*
from (select * from emp order by sal desc) result
where rownum < 11 and rownum > 5;

--正确分页
select *
from (select rownum rn, result.*
     from (select * from emp order by sal desc) result
     where rownum < 11) 
where rn > 5;
```



## 三、Oracle对象

#### 1. 视图

**视图的概念:** 视图就是提供一个查询的窗口, 所有的数据都来自于原表. 对于视图的操作和对于表的操作是完全相同的, 对视图数据的修改, 也会导致原表对应数据的修改.

**视图的作用:** 

1. 视图可以屏蔽掉一些敏感数据
2. 视图可以保证总部和分部数据及时统一

```sql
--跨用户创建表
create table emp as select * from scott.emp;

--创建视图(需要有DBA权限)
create view emp_view as select name,job from emp;

--创建只读视图
create view emp_view as select name,job from emp with read only;

--对视图的增删改查操作与表完全相同
```

#### 2. 索引

**索引的概念:** 索引就是在表的列上创建一个二叉树, 达到大幅度提高查询效率的目的. 但是索引会影响增删改的效率.

+ 单列索引

```sql
--创建单列索引
create index index_name on emp(name);

--单列索引触发规则: 条件语句必须是索引列的原始值. 单行函数、模糊查询都会影响索引的触发
select * from emp where name = 'Tom'; --触发索引
select * from emp where name like 'T_m'; --不触发索引
```

+ 复合索引

```sql
--创建复合索引
create index index_name_job on emp(name, job);

--符合索引的第一列为优先检索列
--符合索引触发规则: 条件语句必须包含优先检索列的原始值
select * from emp where name='Tom' and job='software'; --触发
select * from emp where name='Tom' or job='software';  --不触发
```



## 四、Oracle编程

#### 1. PL/SQL编程语言简介

PL/SQL编程语言是对sql语言的扩展, 使得sql语言具有过程化编程的特性.

PL/SQL编程语言比一般过程化编程语言更加灵活高效, 主要用来编写存储过程和存储函数等.

#### 2. 使用PL/SQL编程语言

+ 声明方法

```sql
--基本模板
declare
	...声明变量...
begin
	...逻辑处理...
end;

--示例
declare
	number number(2) := 10;
	str varchar2(10) := 'Tom';
	emp_name emp.name%type; --引用型变量, emp_name变量的类型与emp表中name属性的类型一致
	emprow emp%rowtype; --记录行变量, 表示一行记录, 相当于一个对象
begin
	dbms_output.put_line(number); --控制台输出
	dbms_output.put_line(str);
	select name into emp_name from emp where empno=7788; --将查询结果赋值给emp_name变量
	dbms_output.put_line(emp_name);
	select * into emprow from emp where empno=7788;
	dbms_output.put_line(emprow.name || '的工作是:' || emprow.job); --字符串之间用||连接
end;
```

+ if语句

```sql
--基本模板
declare

begin
	if then
	elseif then
	else
	end if;
end;

--示例
declare
	age number(3) := &age; --&表示输入赋值
begin
	if age < 18 then dbms_output.put_line('未成年人');
	elseif age < 40 then dbms_output.put_line('中年人');
	else dbms_output.put_line('老年人');
	end if;
end;
```

+ 循环

```sql
--while循环
delcare
	i number(2) := 1;
begin
	while i<11 loop
		dbms_output.put_line(i);
		i := i+1;
	end loop;
end;

--exit循环
delcare
	i number(2) := 1;
begin
	loop 
		exit when i>10;
		dbms_output.put_line(i);
		i := i+1;
	end loop;
end;

--for循环
delcare

begin
	for i in 1..10 loop
		dbms_output.put_line(i);
		i := i+1;
	end loop;
end;
```

+ 游标

    游标类似于集合, 可以存放多个数据

```sql
declare 
	cursor c is select * from emp;
	emprow emp%rowtype;
begin
	open c;
		loop
			fetch c into emprow;
			exit when c%notfound;
			dbms_output.put_line(emprow.name);
		end loop;
	close c;
end;

--带参数游标
celcare
	cursor c(dno emp.deptno%type) is select empno from emp where deptno = dno;
	en emp.empno%type;
begin
	open c(10);
		loop
			fetch c into en;
			exit when c%notfound;
			update emp set sal=sal+100 where empno=empno;
			commit;
		end loop;
	close c;
end;
```

+ 存储过程

    存储过程就是将一段PL/SQL代码编译好, 存放在数据库端, 需要用到时可以直接调用. 存储过程一般用于一些固定步骤的业务操作.

```sql
--基本格式
create [or replace] procedure 过程名[(参数名 in/out 数据类型)]
as/is
	...定义变量...
begin
	...相关逻辑操作...
end;

--参数类型只是用来声明类型,所以不加具体长度
--or replace写上后, 当该存储过程名已存在时, 直接替换掉
--in/out为参数的两种形式, 默认不写为in.
--as或者is均可

--给指定员工涨工资
--编写存储过程
create or replace procedure p(eno emp.empno%type)
is
begin
	update emp set sal=sal+100 where empno=eno;
	commit;
end;
--调用存储过程
declare

begin
	p(7788); --给编号为7788的员工涨工资 
end;

--关于in或out
--如果参数为in形式, 那么不能进行into、:=等赋值操作, 如果是out形式, 则该参数可以进行赋值操作, 并且可以传递
create or replace procedure p(eno emp.empno%type, year_sal out number)
is
begin
	select sal*12 into year_sal where empno=eno;
	year_sal := year_sal+888;
end;

declare
	year_sal number(6);
begin
	p(7788, year_sal); --调用该存储过程后, 该方法中的变量year_sal的值就是存储过程第二个参数的值.
	dbms_output.put_line(year_sal);
end;
```

+ 存储函数

    存储函数与存储过程类似, 不过存储函数可以有返回值, 而存储过程没有返回值.

```sql
--基本模板
--注意返回数据类型不用加长度
create or replace function 函数名(Name in type, Name in type, ...) return 数据类型 
is
	...定义变量...
begin
	...逻辑操作...
	return 结果变量;
end;

--示例
create or replace function f(eno emp.empno%type) return number
is
	s number(10);
begin
	select sal*12 into s where empno=eno;
	return s;
end;

declare
	s number(10);
begin
	s := f(7788);
	dbms_output.put_line(s);
end;
```



