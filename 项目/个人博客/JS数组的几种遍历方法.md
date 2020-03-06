### JS中数组的几种遍历方法



**注意:** 几种遍历方法的功能有差异, 但是它们的回调函数参数都是一致的。

+ item	必须, 当前遍历的元素
+ index    可选, 当前元素的索引值
+ arr	可选, 当前元素属于的数组对象



#### (1) every

`every()`关注的是数组元素的共性。只要有一个不满足，遍历就会结束，接下来的数据就不会继续判断。它的返回值是布尔型, 只有当所有的元素都满足要求时, 才返回true

```javascript
var numbers = [1, 2, 3, 4, 5, 6, 7, 8, 9];
 
let bool = numbers.every(function(item, index, arr) {
  console.log(item);
  return item % 2 == 0;
});

console.log(bool); //false
```



#### (2) some

`some()`关注数组元素的个性, 只要有一个满足, 遍历就会结束。

```javascript
let bool = numbers.some(function(item, index, arr) {
  console.log(item);
  return item == 4;
});

console.log(bool);
```



#### (3) filter

`filter()`致力于从已有的数组中筛选出符合一定条件的数据项，最后的返回值是所有符合条件的数据项构成的数组。它不会修改原来的数组。

```javascript
function City (province, school, level) {
  this.province = province;
  this.school = school;
  this.level = level;
}
 
let beijing = new City('北京都', ['北京大学', '清华大学'], 1);
let xian = new City('西安都', ['西安交通大学', '西北工业大学'], 2);
let hubei = new City('湖北省', ['武汉大学', '华中科技大学'], 2);
let hunan = new City('湖南省', ['湖南大学', '中南大学'], 3);
let sichuan = new City('四川省', ['四川大学', '电子科技大学'], 3);
 
let cities = [beijing, xian, hubei, hunan, sichuan];
let tops = cities.filter(function(item, index, arr) {
  return item.level == 1; //将判断为true的遍历元素加入tops数组
});
console.log(tops);
```



#### (4) map

`map()`将一个值从一种形式映射到另一种形式，比如将key映射到value。它的每一次遍历都会有一个返回值。这些返回值组合成最终的结果数组

```javascript
var numbers = [1, 2, 3, 4, 5, 6];
var capitals = ["北京都", "南京都", "广州都", "重庆都", "西安都", "拉萨都"];
let targets = numbers.map(function(item, index, arr) {
  return capitals[item-1];
});
console.log(targets);

/*
	返回结果:
	0: "北京都"
	1: "南京都"
	2: "广州都"
	3: "重庆都"
	4: "西安都"
	5: "拉萨都"
*/
```



#### (5) foreach

`foreach()`遍历每一个元素

```javascript
let capitals = ["北京都", "南京都", "广州都", "重庆都", "西安都", "拉萨都"];
capitals.forEach(function(item) {
  console.log(item);
});
```