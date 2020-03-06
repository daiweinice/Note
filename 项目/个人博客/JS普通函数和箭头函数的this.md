**在普通函数中, this是在函数被调用时才发生绑定**

+ 一般方法中，this代指全局对象 window

```javascript
function fun() {
    console.log(this);
}

fun(); // window
```

+ 作为对象方法调用，this代指当前对象

```javascript
var obj = {
    fun: function(){
        console.log(this);
    }
}

obj.fun(); // obj
```

+ 作为构造函数调用，this 指代new 出的对象

```javascript
function fun(){
	console.log(this);
}

var obj = new fun(); // this为obj
```

+ 调用函数对象的`apply()`和`call()`方法

    `call()`和`apply()` 都是函数对象的方法, 函数对象调用这两个方法都会调用函数执行。使用`call()`和`apply()`时可以传递一个对象作为第一个参数, 这个参数将成为函数的this对象, 即可以通过这种方式, 改变this代表的对象。

    `call()`和`apply()`从第二个参数起为函数对象需要的参数, `call()`依次填入即可, `apply()`需要把参数封装成一个数组作为其第二个参数。

```javascript
function fun(){
    console.log(this);
}
fun(); // window

var obj = new Object();
fun.call(obj) // obj
```





**在箭头函数中, this是在定义函数的时候就发生了绑定**

箭头函数的`this`总是指向词法作用域，即**始终指向自身外的第一个 this**

实际上, 箭头函数里面根本没有自己的 this，而是引用外层的 this

由于`this`在箭头函数中已经按照词法作用域绑定了，所以，用`call()`或者`apply()`调用箭头函数时，无法对`this`进行绑定，即传入的第一个参数被忽略。





**相关实例:**

```javascript
var obj = {
    a : function(){console.log(this)},
    b : () => {console.log(this)},
    c : {
        d: function(){console.log(this)},
        f: () => {console.log(this)}
    },
}

obj.a(); // obj对象
obj.b(); // window
obj.c.d(); // c对象
obj.c.f(); // window
```



```javascript
var obj = {
    birth: 1990,
    getAge: function () {
        var b = this.birth; // 1990
        var fn = function () {
            return new Date().getFullYear() - this.birth;
        };
        return fn();
    }
}
/*
	obj.getAge()的值是一个函数对象fn
	所以这段代码的执行相当于fn(), 属于一般方法调用的情况
	fn中的this应该是window
*/
obj.getAge(); //NaN


var obj = {
    birth: 1990,
    getAge: function () {
        var b = this.birth; // 1990
        var fn = () => new Date().getFullYear() - this.birth;
        return fn();
    }
};
/*
	箭头函数在定义时this就发生了绑定, 为自身外的第一个this
	这里自身外的第一个this就是getAge函数中的this
	getAge函数中的this为obj对象
	所以this为obj对象
*/
obj.getAge(); // 30
```
