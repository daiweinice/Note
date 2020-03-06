### 1. Highlight.js简介

Hilight.js是一款代码高亮插件, 支持上百种语言和几十种高亮样式。



### 2. 使用

```vue
<script>
import hljs from 'highlight.js'
import 'highlight.js/styles/atom-one-dark.css' //需要什么样式就引入对应的css文件

const highlightCode = () => {
  const preEl = document.querySelectorAll('pre') //获取所有的<pre>元素

  preEl.forEach((el) => {
    hljs.highlightBlock(el) //实现高亮的核心代码
  })
}

export default {
    mounted () {
        highlightCode()
    },

    updated () {
        highlightCode()
    }
}
</script>
```



### 3. API

+ `highlight(name, value, ignore_illegals, continuation)`

    参数:

    name：语言名称
    value：原始HTML字符串
    ignore_illegals：是否忽略非法字符
    continuation：是否继续未完成的解析

    返回值：对象{language: , relevance: , value: }
    language: 指定的语言，和输入的一致
    relevance: 整数值
    value: 处理后的高丽HTML字符串

    

+ `highlightAuto()`

    自动检测语言

    

+ `fixMarkup()`

    

+ `highlightBlock(block)`

    传入的参数是DOM节点，该函数默认使用语言检测，但是可以在DOM节点的类属性中指定语言

    

+ `configure(options)`

    Configures global options:

    tabReplace：用于替换缩进中的TAB字符的字符串

    

+ `initHighlightingOnLoad()`

    这个方法等到页面加载完成之后，再对页面的`<pre><code>..</code></pre>`代码段高亮

    

+ `initHighlighting()`

    把高亮运用到页面的每个`<pre><code>..</code></pre>`中，这个方法和initHighlightingOnLoad区别就是，这个不会等待页面加载完成再执行。