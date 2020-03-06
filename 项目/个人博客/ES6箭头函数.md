ES6标准新增了一种新函数, 箭头函数。

`x => x * x`

上面的代码相当于: 

```javascript
function (x) {
    return x * x;
}
```

箭头函数相当于匿名函数，并且简化了函数定义。如果箭头函数只有一条语句, 则可以省略`{...}`和`return`。反之则不能。

```javascript
x => {
    if(x > 0){
        return x;
    }else{
        return 0;
    }
}
```

如果箭头函数没有参数或参数不唯一, 则需要用 () 括起来

```javascript
(x, y) => x + y

() => 1
```

如果箭头函数返回值是一个对象, 则也不能使用最简写法

```javascript
x => {person: x} //错误写法

x => {{person: x}} //正确写法
```