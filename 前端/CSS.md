+ ```css
    span::before {
    	content: '⇰'; /*content必须设置, 如果没有内容则设置为''*/
    	color: red;
    }
    
    h2::before {
    	content: attr(title);
    }
    <h2 title="后盾人">houdunren.com</h2>
    ```
    
+ `cursor: pointer `当鼠标移动上去时出现小手

+ `flex: 1`、`grid-template-rows: 100px 1fr 100px`可用于自适应填充

+ 如果要选择main中的最后一个div, 则应该是`main div:last-child`而不是`main:last-child`。对于伪类选择器的理解应该是"选择具有xxx特点的元素"。如: `element:focus`表示选择具有focus状态的element。

+ `vertical-align`只对行内元素、表格单元格元素生效, **不能使用它垂直对齐块级元素。**

+ `line-height`的值如果是百分比则是相对于字体大小而不是相对于父容器大小。如设置`line-height: 200%`, 则行高就是字体大小的2倍数。如果知道文本父容器的具体高度, 我们可以使用`line-height: xxxpx`来使文本垂直居中, 但是如果文本有多行或不知道父容器具体高度, 则无法实现垂直居中。

    在CSS中使用百分比, 一定要注意百分比的参照是什么。
    
+ 如果一个Nav使用Flex布局, 希望里面的前三个元素向左对齐, 最后一个元素向右对齐, 则可以在最后一个元素之前添加一个div, 设置其`flex-grow: 1`使其自动填充, 将最后一个元素顶到最右边。或者还有一个小技巧, 给倒数第二个元素设置`margin-right: auto`也可以实现相同的效果。

+ 为什么使用`:after`时添加`position: relative`可以防止添加的元素超出范围?

    在使用`:after`时我们一般会给添加的元素设置`position: aabsolute`, 此时**由于其父元素是static, 所以它会相对于根节点定位和设置宽高**, 所以我们可以给父元素设置`position: relative`, 使绝对定位的元素相对于相对定位的元素设置宽高。

    所以如果遇到元素内的子元素需要相对于该父元素绝对定位, 应该将父元素设置为`position: relative`

+ 在使用absolute定位时, 想要让元素居中, 如果使用`top: 50%; left: 50%;`并不会达到居中效果。因为偏移是从元素左上角开始的, 所以此时元素多想下和右偏移了一点。我们以前会使用`margin-left: -xxxpx;margin-top: -xxxpx`;来使元素居中。我们还可以使用`translate(-50%, -50%)`来实现, 这是因为translate的百分比是相对于元素本身的。

+ 使用`:after`、`:before`的效果就是在**元素内最前面或最后面**添加一个元素。不使用伪类直接在元素内添加一个元素也是一样的效果。

    一般会将`:after`、`:before`添加的元素设置为absolute定位, 将父元素设置为relative定位, 这样方便为新添加的元素定位。

+ 斜向移动就是x移动+y移动的组合

+ `nav:hover li:nth-child(1)`选中hover状态下的nav里的第一个li

+ 在设计动画时, 让元素先隐藏可以使用`scale(0)`

+ 一个元素旋转后, 它的内容也是旋转后的, 如果希望内容变正, 可以将内容再装进一个div, 将该div反向转回去即可。

+ `transform`不仅可以用来实现动画(结合伪类), 还可以用来布局、构建图形。

+ 通过`transform`实现动画或构建图形时往往会把里面的元素定位设置为`absolute`

+ 如何自己画一个心型

    ![](https://blog-1258617239.cos.ap-chengdu.myqcloud.com/blog_images/自己画一个爱心.png)

+ `filter: blur(5px)`?

+ js优化动画代码? js实现动画?

+ `object-fit` CSS 属性指定可替换元素的内容应该如何适应到其使用的高度和宽度确定的框。

    取值有contain、cover、fill、none、scale-down

+ 纯CSS实现点击按钮菜单栏展开/收缩。我们可以使用checkbox, 利用其选中状态`xxx:checked`来实现选中和未选中的样式的切换。

+ 给一个元素设置`transition`, 改变其`font-size`、`height`等样式, 也有动画效果。我们可以通过js操作`dom.style.xxx = `来修改元素的样式

+ 在使用`linear-gradient`时, 应该在`background`或`background-image`属性中设置, 而不是在`background-color`中设置

+ 按钮有一个`onclick`属性, 可以直接绑定js方法, 如`onclick = "move()"`

+ `margin: 0 auto`不生效可能是因为元素未设置为块元素

+ 不要自己设置`input`的高度, 样式会很难看。可以直接改变字体的大小即可。

+ `margin-top`、`padding-top`的百分比是相对于父元素的**width**来设置的

+ ![image-20200615171537381](https://blog-1258617239.cos.ap-chengdu.myqcloud.com/blog_images/image-20200615171537381.png)

    