# Nginx

## 一、Nginx入门

### 1. Nginx简介

> Nginx是一款轻量级的Web服务器/反向代理服务器及电子邮件（IMAP/POP3）代理服务器, 它的特点是占有内存小、并发能力强。

![](https://blog-1258617239.cos.ap-chengdu.myqcloud.com/blog_images/Nginx简介.png)

### 2. Docker Compose安装Nginx

```yml
version: '3.1'
services:
  nginx:
    image: daocloud.io/library/nginx:1.12.0
    container_name: nginx
    ports:
      - 80:80
    volumes:
      # 注意这里两边都要用绝对路径
      - /usr/local/docker-nginx/conf.d:/etc/nginx/conf.d 
      - /usr/local/docker-nginx/log:/var/log/nginx
```

### 3. Nginx核心配置文件

`docker exec -it ffb90f5d5c60 bash`进入Nginx容器内

`cd /etc/nginx`进入Nginx配置文件夹

`vim nginx.conf`

```shell
# 全局块
user  nginx;
worker_processes  1;  # 数值越大并发能力越强

error_log  /var/log/nginx/error.log warn; # 错误日志存放路径
pid        /var/run/nginx.pid;

# events块
events {
    worker_connections  1024; # 数值越大, 并发能力越强
}

# http块
http {
    include       /etc/nginx/mime.types;  # 引入各种mime类型 如application/json
    default_type  application/octet-stream;

    log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                      '$status $body_bytes_sent "$http_referer" '
                      '"$http_user_agent" "$http_x_forwarded_for"';

    access_log  /var/log/nginx/access.log  main;

    sendfile        on;
    #tcp_nopush     on;

    keepalive_timeout  65;

    #gzip  on;

    include /etc/nginx/conf.d/*.conf; # 引入conf.d目录下的所有配置文件
}

```

`cd /etc/nginx/conf.d/`

`cat default.conf`

```shell
listen       80; # 监听端口
    server_name  localhost; # Nginx接收请求的IP

    #charset koi8-r;
    #access_log  /var/log/nginx/log/host.access.log  main;

    location / {
        root   /usr/share/nginx/html; # 资源位置
        index  index.html index.htm;  # 默认首页
    }

    #error_page  404              /404.html;

    # redirect server error pages to the static page /50x.html
    #
    error_page   500 502 503 504  /50x.html;  # 错误页面位置
    location = /50x.html {
        root   /usr/share/nginx/html;
    }

    # proxy the PHP scripts to Apache listening on 127.0.0.1:80
    #
    #location ~ \.php$ {
    #    proxy_pass   http://127.0.0.1;
    #}

    # pass the PHP scripts to FastCGI server listening on 127.0.0.1:9000
    #
    #location ~ \.php$ {
    #    root           html;
    #    fastcgi_pass   127.0.0.1:9000;
    #    fastcgi_index  index.php;
    #    fastcgi_param  SCRIPT_FILENAME  /scripts$fastcgi_script_name;
    #    include        fastcgi_params;
    #}

    # deny access to .htaccess files, if Apache's document root
    # concurs with nginx's one
    #
    #location ~ /\.ht {
    #    deny  all;
    #}
}
```

综上, 一般我们只需要关注 conf.d 文件夹中的配置文件即可。

## 二、Nginx反向代理

### 1. 正向代理与反向代理

**正向代理:**

![](https://blog-1258617239.cos.ap-chengdu.myqcloud.com/blog_images/正向代理.png)

正向代理由客户端设置, 且客户端了解代理服务器和目标服务器都是谁。正向代理的作用是突破访问权限、提高访问速度、对目标服务器隐藏IP地址。

**反向代理:**

![](https://blog-1258617239.cos.ap-chengdu.myqcloud.com/blog_images/反向代理.png)

反向代理配置在服务端, 客户端不知道访问的到底是那个服务器。反向代理可以用于负载均衡、隐藏服务器真正的IP地址。

### 2. Nginx实现反向代理

default.conf

```shell
server{
  listen 80;
  server_name localhost;
  location / {
  	# 反向代理
    proxy_pass http://192.186.30.131:8080/;
  }
}
```

**实践中的问题:** 直接访问8080端口可以成功访问, 但是通过反向代理报502错误。通过查看error.log, 发现错误信息`nginx connect() failed (113: No route to host) while connecting to upstream`, 解决办法是关闭Linux的防火墙。https://blog.csdn.net/blueblueuueew/article/details/79325798

### 3. location匹配规则

一个server块中可以有多个location匹配规则, 不同的写法优先级不同。

https://segmentfault.com/a/1190000019138014

![](https://blog-1258617239.cos.ap-chengdu.myqcloud.com/blog_images/location匹配顺序.png)

## 三、Nginx负载均衡

### 1. 负载均衡策略

Nginx默认提供了三种负载均衡策略:

+ **轮询:** 将客户端发出的请求平均分配到每一台服务器
+ **权重:** 根据服务器的权重值不同, 分配不同数量的请求
+ **IP Hash:** 基于客户端的IP地址进行hash计算, 再分配到对应的服务器

注意: 轮询策略也并不是绝对的123123顺序, 也可能会出现1223等情况

### 2. 负载均衡配置

**轮询**

```
upstream my-server{
	server 192.168.0.1:8080;
    server 192.168.0.2:8080;
}

server{
    listen 80;
    server_name localhost;
    
    location / {
    	proxy_pass http://my-server/;
	}
}
```

**权重**

```
upstream my-server{
	server 192.168.0.1:8080 weight=10;
    server 192.168.0.2:8080 weight=5;
}

server{
    listen 80;
    server_name localhost;
    
    location / {
    	proxy_pass http://my-server/;
	}
}
```

**IP Hash**

```
upstream my-server{
	ip_hash;
	server 192.168.0.1:8080;
    server 192.168.0.2:8080;
}

server{
    listen 80;
    server_name localhost;
    
    location / {
    	proxy_pass http://my-server/;
	}
}
```

## 四、Nginx动静分离

### 1. 动静分离简介

Nginx的并发能力可以通过公式计算。具体公式为`worker_processes * worker_connections / 4或2`

为什么是4或2? 

这是因为如果客户端向服务器访问动态资源, 则需要先经过Nginx再经过服务器, 服务器的响应也会先经过Nginx再传递给客户端, 这个过程总共用到了4个连接。

如果使用了Nginx**动静分离**, 客户端请求静态资源, 就可以直接通过Nginx获取到, 而不需要再经过服务器, 此时连接数只有2。

### 2. 动静分离配置

**动态资源配置**

```
server{
  listen 80;
  server_name localhost;
  
  location / {
    proxy_pass http://192.186.30.131:8080/;
  }
}
```

**静态资源配置**

将Nginx容器的`/static/html`、`/static/image`目录与Linux的相应目录相关联, 此后把相关静态资源放入Linux对应目录, 也就相当于在Nginx容器中放入相关静态资源。

```shell
server{
  listen 80;
  server_name localhost;
  
  # 模板
  location / {
    root /static; # 静态资源路径
    index index.html; # 默认访问什么静态资源
    autoindex on; # 如果不是访问具体资源, 则以目录的方式展示所有的静态资源
  }
  
  # 示例1
  # 通过localhost/html就可以访问到/static/html目录下的index.html
  location /html {
  	root /static; # 会将/html自动拼接在一起, 变成/static/html/
  	index index.html;
  }
  
  # 示例2
  # 假设/static/image目录下有一个1.jpg
  # 通过localhost/image/1.jpg就可以访问到图片
  # 如果访问localhost/image, 则页面以目录结构展示image目录下所有静态资源
  location /image {
  	root /static; # 会将/html自动拼接在一起, 变成/static/image/
  	autoindex on;
  }
}
```

## 五、Nginx集群

![](https://blog-1258617239.cos.ap-chengdu.myqcloud.com/blog_images/Nginx集群.png)

Keepalived提供虚拟IP、监听Nginx的状态。客户端通过虚拟IP访问, Keepalived将请求转发到nginx master节点, 如果监测到master节点挂了, 则keepalived把请求转发到nginx slave节点