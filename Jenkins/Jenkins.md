# Jenkins

## 一、概述

### 1. CI(Continue Integration)

CI, 即持续集成。指在编写代码时, 完成一个功能就将其提交到Git仓库中, 将项目重新构建并测试。

持续集成可以快速发现问题、防止代码偏离主分支。

### 2. CD(Continuous Delivery、Continuous Deployment)

Continuous Delivery: 持续交付, 将代码给测试人员测试

Continuous Deployment: 持续部署, 将代码部署到生产环境

![](https://blog-1258617239.cos.ap-chengdu.myqcloud.com/blog_images/CI与CD.png)



## 二、CI-持续集成

+ 安装gitlab, 搭建私有git仓库
+ 安装gitlab-runner
+ 本地项目提交到私有库中。项目根路径可以编写`.gitlab-ci.yml`文件指定项目push上去后自动运行的脚本指令。我们就可以通过配置`.gitlab-ci.yml`文件, 实现项目提交后的自动持续集成。



## 三、CD-持续交付、持续部署

CD需要使用Jenkins。

Jenkins的作用就是从gitlab上拉取项目, 发送到生产环境的服务器。同时我们可以编写脚本实现项目部署。

Jenkins的持续部署非常方便, 如果最新的版本部署后出现问题, 可以立即回退到以前的版本。