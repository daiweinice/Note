### 1. Vue Showdown简介

Showdown.js 是一个在客户端解析 markdown 代码的库。Vue Showdown 是其 Vue 版本。

官方网址: https://vue-showdown.js.org/zh/



### 2. Vue Showdown使用

`npm install vue-showdown`

```vue
<template>
	<VueShowdown :markdown="content" flavor="github" :options="{ emoji: true, tables: true}" :extensions="[myExt]" :vueTemplate="true" />
</template>

<Script>
import { VueShowdown } from 'vue-showdown'

export default {
  data: () => {
    return {
      content: '',
      myExt: {
        type: 'output',  //有'lang'和'output'两种类型, 'lang'会在解析md之前进行文本替换, 'output'会在md解析成了HTML后再进行文本替换
        regex: '<table>',
        replace: '<sui-table celled>'
      }
    }
  },
  components:{
      VueShowdown
  }
}
</Script>
```

