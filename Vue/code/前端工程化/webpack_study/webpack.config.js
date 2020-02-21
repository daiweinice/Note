const path = require('path')

module.exports = {
    // mode 用来指定构建模式, 有development和production两种
    // development模式为开发模式, 在打包时不会进行代码的压缩
    // production为生产环境模式, 在打包时会压缩代码
    mode: 'development',

    //webpack4.X版本默认指定入口为src/index.js, 出口为dist/main.js, 我们也可以进行手动设置
    entry: path.join(__dirname, 'src/index.js'),
    output: {
        path: path.join(__dirname, 'dist'), // 输出文件的存放路径
        filename: 'main.js' // 输出文件的名称
  },
}