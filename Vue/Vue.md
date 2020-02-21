## 一、Vue基础

### 1. Vue简介

> Vue 是一套用于构建用户界面的渐进式框架。与其它大型框架不同的是，Vue 被设计为可以自底向上逐层应用。Vue 的核心库只关注视图层，不仅易于上手，还便于与第三方库或既有项目整合。另一方面，当与现代化的工具链以及各种支持类库结合使用时，Vue 也完全能够为复杂的单页应用提供驱动。

**渐进式:** 声明式渲染→组件系统→客户端路由→集中式状态管理→项目构建

### 2 .Vue特点

+ 易用：熟悉HTML、CSS、JavaScript知识后，可快速上手Vue
+ 灵活：在一个库和一套完整框架之间自如伸缩
+ 高效：20kB运行大小，超快虚拟 DOM

### 3. Vue与JQuery对比

+ 精力集中: 使用Vue可以更集中于数据本身, 而无需操作dom
+ 代码结构: Vue的代码结构更简洁、易读、易于维护
+ 模块化思想
+ 单页面应用
+ 组件复用
+ 性能

### 4. Vue核心概念

+ **模板语法:** 

    使用Vue框架完成的页面需按照其模板语法规则编写, 最后由模板引擎解析为最终的HTML页面

    Vue的工作方式就是 模板+数据-->前端渲染-->静态HTML内容

    Vue的模板与jsp、Theamleaf等模板的作用是一样的, 都是用于渲染成最终的静态HTML内容, 只不过Vue模板是前端渲染而jsp、Theamleaf等是后端渲染。

+ **插值表达式:** {{}}, 表达式内支持运算功能、调用方法

+ **指令:** ==指令本质就是一个自定义属性, 指令的值就是属性的值, 但是这个值已不再是简单的字符串, 而应该把它看成能执行一定功能的代码, 如条件判断、引用变量、对象等。==

    ```html
    <div id="content">
        <!--判断isActive变量是否为true, 如果是则class属性添加active这个值-->
        <p v-bind:class="isActive==true?'active':''">Text</p>
    </div>
    
    <script>
        var vm = new Vue({
        	el: '#content',
        	data: {
        		isActive: true
        	}
        })
    </script>
    ```

+ **数据响应式:** Vue框架是数据响应式的, 即动态改变数据的值会动态同步改变页面对应的值

+ **双向数据绑定:** 

    数据的值会随着用户输入的值动态变化。

    如将输入框的值与实例的一个数据绑定, 那么输入框的值是什么, 绑定的数据的值就是什么。

    如果将该数据的值展示再页面上, 则可以实现页面展示的数据与用户输入的数据动态同步。

+ **MVVM思想:**

    ![](images/MVVM.png)

+ **事件修饰符**: 对事件的触发条件进行限制

+ **声明式编程:** 使用Vue模板, 模板结构和最终页面显示效果基本一致, 这对于开发人员来说更加友好。

+ **实例:** `new Vue()`

+ **生命周期:** 

    ![](images/实例生命周期.png)





## 二、前端工程化

### 1. 模块化

#### (1) 模块化概述

传统开发模式中常常会遇到命名冲突、文件依赖等问题。通过模块化能够很好的解决这些问题。

模块化就是把单独的一个功能封装到一个模块（文件）中，模块之间相互隔离，但是可以通过特定的接口公开内部成员，也可以依赖别的模块。

模块化开发可以方便代码的重用，从而提升开发效率，并且方便后期的维护。

在ES6模块化规范诞生前, Javascript 社区已经尝试并提出了 AMD、CMD、CommonJS 等模块化规范。但是，这些社区提出的模块化标准，还是存在一定的差异性与局限性、并不是浏览器与服务器通用的模块化标准。因此，ES6 语法规范中，在语言层面上定义了 ES6 模块化规范，是浏览器端与服务器端通用的模块化开发规范。

ES6模块化规范中定义:

+ 每个 js 文件都是一个独立的模块
+ 导入模块成员使用 import 关键字
+ 暴露模块成员使用 export 关键字



#### (2) ES6模块化基本语法

**1. 默认导出与默认导入**

```javascript
// 当前文件模块为 m1.js

// 定义私有成员 a 和 c
let a = 10
let c = 20
// 外界访问不到变量 d ,因为它没有被暴露出去
let d = 30

function show() {}

// 将本模块中的私有成员暴露出去，供其它模块使用
// 每个模块只能使用一次export default
export default {
 a,
 c,
 show
}
```

```javascript
// 导入模块成员
import m1 from './m1.js'

// 打印输出的结果为：{ a: 10, c: 20, show: [Function: show] }
console.log(m1);
```

**2. 按需导出与按需导入**

```javascript
// 当前文件模块为 m1.js

// 向外按需导出变量 s1
export let s1 = 'aaa' 
// 向外按需导出变量 s2
export let s2 = 'ccc'
// 向外按需导出方法 say
export function say = function() {}
```

```javascript
// 导入模块成员
// 可以通过as关键字为导出变量起别名
import { s1, s2 as ss2, say } from './m1.js'

// 按需导入与默认导入可以同时进行
// import m1, { s1, s2 as ss2, say } from './m1.js'

console.log(s1) // 打印输出 aaa
console.log(ss2) // 打印输出 ccc
console.log(say) // 打印输出 [Function: say]
```

**3. 直接导入并执行模块代码**

```javascript
// 当前文件模块为 m2.js

// 在当前模块中执行一个 for 循环操作
for(let i = 0; i < 3; i++) {
 console.log(i)
}
```

```javascript
// 导入后会执行m2.js的代码
import './m2.js'
```



### 2. webpack

#### (1) 概述

webpack 是一个流行的前端项目构建工具(打包工具), 可以解决当前 web 开发中所面临的困境。

webpack 提供了友好的**模块化支持，以及代码压缩混淆、处理 js 兼容问题、性能优化**等强大的功能，从而让程序员把工作的重心放到具体的功能实现上，提高了开发效率和项目的可维护性。

目前绝大多数企业中的前端项目，都是基于 webpack 进行打包构建的。

![](images/webpack.png)



#### (2) 入门案例

**1. 安装webpack**

`npm install webpack webpack-cli -g`命令, 安装webpack

**Tips: 关于npm install**

+ **-g:** 全局安装, 将包放在 `./node_globals`(自己设置的包存储路径), 可直接通过命令行使用
+ **无选项:** 本地安装, 在执行该npm命令的路径下新建一个`node_modules`目录, 并将包放入其中, 需通过 require() 来引入本地安装的包

+ **-S:** 本地安装, 包名会被注册在package.json的dependencies里面，包在开发环境和生产环境下存在。

+ **-D:** 本地安装, 包名会被注册在package.json的devDependencies里面，包仅在开发环境下存在。

**2. 创建项目**

+ 新建项目空白目录，并运行 `npm init –y` 命令，初始化包管理配置文件 package.json

+ 新建 src 源代码目录, 里面用于存放前端代码

+ 在项目根目录下, 通过`npm install xxx -D/-S`将需要的包导入项目

    如: 通过`npm install jquery -S`下载jquery

    该命令会在执行路径下创建一个`node_modules`目录, 并将包下载到该目录。`node_modules`目录用于存放项目所需要的各种包。

    同时由于-S选项的存在, 包名会被注册在package.json的dependencies中

+ 在项目根目录创建 webpack.config.js 

```js
module.exports = {
	mode: 'development' // mode 用来指定构建模式, 有development和production两种模式
}
```

+ 配置package.json

```就是\
"scripts": {
	"dev": "webpack" // script 节点下的脚本，可以通过 npm run 执行
}
```

**3. 编码**

**4. 通过webpack打包**

`nmp run dev`命令启动webpack进行项目打包



