### vue + vue-shown + semantic-ui-vue的页面渲染问题

**问题:**

使用 vue-shown 动态的将`.md`解析为 HTML 后输出到页面, 通过 vue-shown 的 extension 插件将`<table>`等表格标签替换成了`<sui-table celled>`等 semantic-ui-vue 的标签, 结果页面无法渲染出 semantic-ui 的表格样式。



**解决:**

Vue 有一个编译器, 用于解析 Vue 的模板语法。我们将`<table>`等标签替换成了 semantic-ui-vue 的标签, 这些标签就是一个个 Vue 组件, 如果要正确渲染则需要编译器解析。当使用 vue-loader 或 vueify 的时候，`*.vue`文件内部的模板会在构建时预编译成 JavaScript , **打包时默认不需要加入编译器**, 所以后续动态生成的HTML无法被解析和渲染。

解决这个问题需要以下两个步骤:

1. 设置 vue-shown 的组件的 Props ,  **将 `:vueTemplate` 的值设置为true**。这意味着将解析后的 HTML 字符串当作 Vue 模板，允许你在 Markdown 中使用 Vue 模板语法。

    ```javascript
    <VueShowdown :markdown="content" flavor="github" :options="{ emoji: true, tables: true}" :extensions="[myExt]" :vueTemplate="true" /> 
    ```

2. 在打包工具里配置一个别名, **将编译器一起打包**, 即使用完整版的 Vue。

    ```javascript
    //vue.config.js
    module.exports = {
      configureWebpack: {
          resolve: {
         	alias: {
            	'vue$': 'vue/dist/vue.esm.js'
          	}
         }
      }
    }
    ```



**补充:** 

+ 我们在使用`v-html`时, 如果动态改变`v-html`的值, 页面是能够发生变化且正确渲染出样式的, 但是如果`v-html`中使用了Vue的模板语法如: `v-xxx`、`{{}}`、`@click`等, 这些模板语法是无法生效的, 因为**`v-html`无法调用模板解析器进行解析。**同理, 如果在`v-html`中使用了 Vue 的组件, 这些组件也是无法生效的。

    解决该问题最好的办法就是将`v-html`中的内容封装成组件, 用组件来代替`v-html`。