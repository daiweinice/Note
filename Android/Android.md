## Android

### 1. Hello Android

+ 多个组件、多个入口

+ 项目重要文件
    + app > manifests > AndroidManifest.xml
    + app > java > com.example.myfirstapp > MainActivity
    + app > res > layout > activity_main.xml
    
+ ViewGroup对象和View对象, ViewGroup里容纳View对象, 并为他们布局。

+ ConstraintLayout布局、约束、match constraints、fixed、wrap content、约束可以不相对于屏幕边缘而是相对某个其他控件或辅助线、善用水平/竖直辅助线布局

    ConstraintLayout的出现解决了图形化界面开发的痛点, 所以现在使用图形化界面开发更加方便。

+ `String.xml`字符串资源文件

+ Activity之间的跳转

    From:

    + 按钮绑定方法
    + `findViewById`获取view对象, 获取view对象的相关信息
    + 通过`Intent`携带键值对并跳转到其他Activity

    ---

    To: 

    + 生命周期`onCreate()`, 通过`Intent`获取键值对, 进行相关操作
    + 在manifest中添加该activity的向上导航功能, 即指定父activity 
    
+ activity生命周期

![activity生命周期](https://blog-1258617239.cos.ap-chengdu.myqcloud.com/blog_images/activity生命周期.png)

+ View对象
    + 通过视图id获取view对象 `TextView textView = findViewById(R.id.viewId)`, `R.id.viewId`的值是一个整数, 每一个View对象都会被一个整数唯一标识
    + `button.setOnclickListener(new View.onclickListener(){ onclick()... })` 添加事件绑定

+ 本地化: 在`String.xml`中点击地球图标设置k-v的其他语言版本。 

    除了字符串可以支持本地化, 页面布局也支持本地化

+ 通过@引用字符串资源

    在ac中通过`getString(R.string.XXX)`获取对应资源

+ 屏幕旋转问题

    通过manifest中设置activity的screenOrientation属性 

    portrait竖直 lanscape横向

    根据不同方向设置不同布局--点击反转图标, 创建副本

    屏幕的反转会destroy当前ac然后再创建一个新的ac, 所以原来的ac中的数据就会被清除, 如果希望屏幕反转后原来的数据仍然保留, 就需要使用到`onSaveInstanceState`方法保存相关的数据

+ ViewModel

    JetPack是Android的官方库, 是的开发者的开发工作变得更简单。要使用JetPack需要再项目创建时勾选Androidx选项

    ViewModel是JetPack的一个模块, 它用于实现安卓开发的mvc模式。即通过ViewModel来存储数据, 使得UI与数据分离。

    同时, 屏幕反转造成的数据丢失问题也能被很好的解决。

    ```java
    public class MyViewModel extends ViewModel {
        public int number = 0;
    }
    
    public class MainActivity AppCompatActivity {
    	MyViewModel myViewModel;
        
        protected void onCreate(...){
            myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);
            //后续就可以通过myViewModel对象访问数据
            myViewModel.number...
        }
    }
    ```

+ LiveData

    实现数据改变, 页面动态更新数据

    如果只使用ViewModel, 在改变ViewModel的值后, 还是需要在ac中手动设置对应组件的text才能实现数据在UI上的改变。

    如果再配合上LiveData, 即可更好的实现解耦, 此时的控制器ac就不需要在做更多对于数据的操作。 

    <u>为每一个LiveData都需要绑定一个观察, 如果LiveData很多, 写大量的LiveData观察也很麻烦, 一般一个页面会将所有数据封装成一个对象, 将该对象转成LiveData, 绑定观察即可.</u>

+ 矢量图标库

    安卓提供了很多官方矢量图标库, 可以在res目录下右键选择vector asset添加图标。

    创建后会在drawable中创建一个xml文件
    
+ DataBind

    使控制器(ac)不再与viewgroup建立直接联系

    通过布局xml将数据绑定到对应的view上, 这时ac中就不需要写各种findViewById

    在搭配上livedata和viewModel, 使得代码结构更清晰

    使用了DataBind后, 布局xml就像是jsp一样, 通过@{}、@{()->}绑定数据与事件
    
    同时, 在activity中创建了`DataBinding`对象, 可以通过`binding.view对象的ID`来获取到对应的view对象
    
+ ViewModel+LiveData+DataBinding总结

    ViewModel的作用是将数据与ac分离

    LiveData的作用是实现数据与UI的绑定

    DataBinding的作用是使对UI操作和ac分离

    ViewModel中存放LiveData, Databinding将ViewModel与UI绑定

    ![](https://blog-1258617239.cos.ap-chengdu.myqcloud.com/blog_images/mvc3.png)

+ Viewmodel+LiveData+DataBinding示例

1. build.gradle

```java
// 开启dataBinding
defaultConfig {
    dataBinding{
    	enabled true
    }
}

// 导入ViewModel和LiveData
def lifecycle_version = "2.2.0"
// ViewModel
implementation "androidx.lifecycle:lifecycle-viewmodel:$lifecycle_version"
// LiveData
implementation "androidx.lifecycle:lifecycle-livedata:$lifecycle_version"
```

2. 编写MyViewModel

```java
public class MyViewModel extends ViewModel {
    private MutableLiveData<Integer> scoreA;
    private MutableLiveData<Integer> scoreB;

    public MutableLiveData<Integer> getScoreA() {
        if (scoreA == null){
            scoreA = new MutableLiveData<>();
            scoreA.setValue(0);
        }
        return scoreA;
    }

    public MutableLiveData<Integer> getScoreB() {
        if (scoreB == null){
            scoreB = new MutableLiveData<>();
            scoreB.setValue(0);
        }
        return scoreB;
    }

    public void addA(int n){
        scoreA.setValue(scoreA.getValue() + n);
    }

    public void addB(int n){
        scoreB.setValue(scoreB.getValue() + n);
    }
}
```

3. 将activity_main.xml转换成dataBinding形式(通过小灯泡转换)

```xml
<data>
<variable
	name="myViewModel"
	type="com.dw.scorerecord.viewModel.MyViewModel" />
</data>

<TextView android:text="@{String.valueOf(myViewModel.scoreA)}" />
<Button android:onClick="@{()->myViewModel.addA(1)}" />
```

4. 在ac中配置

```java
public class MainActivity extends AppCompatActivity {
    MyViewModel myViewModel;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        //设置绑定的ViewModel
        binding.setMyViewModel(myViewModel);
        //开启LiveData
        binding.setLifecycleOwner(this);
    }
}
```

+ 页面的样式设置也可以将其转换成一个资源文件(values/xxx.xml), 这样其他样式可以通过引用样式文件的值达到同样的样式

+ ViewModel虽然可以保存数据, 在反转屏幕等操作下依然保留数据, 但是当后台进程被杀死后, 重新打开APP, 此时的ViewModel又是一个新创建的, 所以以前的数据会丢失。

    如果希望后台进程被杀死后, 仍然保留数据, 则而已使用`Bundle state`来将viewmodel存储进去, 这样后台在次打开后, 就仍然可以使用原来的viewmodel

    我们还可以使用`SavedStateHandle`实现数据持久化, `SavedStateHandle`里面保存所有的`LiveData`

    **Tips:** 后台进程被杀死的情况下数据仍然保存, 但是如果是按底部第一个退出键, 程序会关闭, 此时数据仍然是会丢失的

+ android中导入相关包

+ 数据存储类型

    + 内部: 只有App内部可以访问
    + 外部: 可以允许非本App访问
    + shared preference:  内部存储的一种, 但是只支持简单数据类型
    + Database

+ Shared preference

    会在其App的包中创建一个xml文件, 里面就保存有我们添加的数据

    可以把SharedPreference理解为一个简易的"数据库"

    由于数据需要写入文件, 所以是非常耗时的, 如果每一次修改都重写入文件则非常消耗资源, 所以可以在生命周期的`onpouse`中统一写入文件

+ Activity继承自ContextWrapper继承自Context

    `getApplicationContext()`获得的context是App最顶级的引用

    使用`getApplicationContext()`可以有效避免内存泄漏

+ AndroidViewModel继承自ViewModel, 它内部提供了一个顶层context对象

    只要继承了该类, 我们就可以直接`getApplication()`获取全局资源(Application继承自Context)

    如`getApplication().getResources().getString(R.string.xxx)`

+ 在ViewModel中通过getNumber方法获取数字, 在@{}中的写法可以是@{data.number}

+ 注意: `@{data.num}`中的num需要转化为字符串类型`@{String.valueOf(data.num)}`才行, 否则会报错

+ 问题:

    ```java
    public Integer getNumber(){
        return (Integer)handle.getLiveData("num").getValue();
    }
    //返回值是Integer, 下面的表达式, 点击按钮时, 数据无法更新
    @{String.valueOf(data.getNumber())} 
    
    
    public LiveDate<Integer> getNumber(){
        return handle.getLiveData("num");
    }
    //返回值是LiveData, 点击按钮, 数据就可以正常更新
    @{String.valueOf(data.getNumber())} 
    ```

    ---
    
+ Navhost: 容器, 用于容纳fragment

    Fragment: 页面中的一块区域, Fragment依存于AC, 一个AC里可以有多个Fragment

    NavController: 控制切换

    NavGraph: 用于表示导航路线, 每一个NavGraph就代表一个切换动作

+ 在fragment中添加一个constraint layout, 方便布局

+ NavGraph需要创建一个type为navigation的资源文件, 在其中可以定义fragment之间的切换逻辑, 每一个切换逻辑就是一个NavGraph, 它有自己的ID, 可以通过ID得到该动作

    每一个NavGraph可以设置切换动画, 提高用户体验

+ 在ac中添加一个container->NavHostFragment

+ 在fragment中的按钮中绑定事件, 通过NavController实现切换

+ 可以设置NavGraph或Fragment的argument属性, 添加k-v, fragment中可以通过`getFragment().getString("key")`获取

+ 传递动态参数可以通过Bundle, 在Bundle中添加k-v, 通过NavController传递过去

+ 导航的切换可以通过fragment的id, 也可以通过navGraph的action

+ 自定义fragment之间的切换动画, 通过创建type为anim的资源文件, 自定义动画, 后续在NavGraph中引入该动画

    动画采用的是关键帧keyframe技术

+ fragment在xml中使用databinding的方法与ac一样, 不过在fragment的代码文件中有些许不同

+ 每一个Fragment对应的Binding命名为`FragmentxxxBinding`

+ 使用Fragment时, 一般按钮绑定切换页面操作的代码直接写在fragment中即可, 其他的与数据操作有关的代码写在ViewModel中, 然后在xml中绑定

+ `String.xml`中可以使用`%d`表示一个动态的整型数, 在动态绑定时通过`@{@string/score(data.highScore)}`绑定

    可以使用safeUnbox避免空值`@{@string/score(safeUnbox(data.highScore))}`

+ ?是特殊符号, 需要使用\转义

+ 多个view组件可以同时选中, 鼠标右键选择对应操作使它们之间进行串联、对齐等操作

+ AlertDailog组件

+ `MainActivity`的`onBackPressed()`

+ Fragment的`Android:label`用于设置APP顶部的页面名字

+ Activity中有一个`finish()`方法, 用于结束当前activity, 效果类似于按下底部退出键

+ `MyViewModel`中定义了`getScore()`, 在xml中只需要通过`@data.score`即可获取到方法的返回值。

    如果返回值是`LiveData`, 还需要通过`@{String.valueOf(data.score)}`
    
+ `R.xxx.xxx`可以访问`res`文件夹中的各种资源

+ 多个Activity或Fragment之间如果想要使用同一个`ViewModel`, 需要注意在创建时将他们的`LifecycleOwner`设置为同一个, 否则每个Ac或Fr都有一个自己的`ViewModel`, 这些`ViewModel`是相互独立的, 所以数据也没办法共享。

+ 将layout中的`textview`改为`chronometer`, 为一个计时器组件

+ 将变量设置为static, 在翻转屏幕时也不会重置该变量, 但是还是建议使用ViewModel和LiveData

+ 让组件感知生命周期: 自定义组件`implements LifecycleObserver`可以将生命周期精确到一个组件, 同时还能实现acticity的进一步解耦

+ `Room`是SQLite数据库的抽象, 可以更流畅的访问数据库

    Android的数据库是用于本地存储的, 因为如果把数据存在xml中, 读取起来比较慢, 存在数据库中可以提高读取速度。

    + Entity
        + Entity类用`@Entity`注解
    + Dao
        + 一般为接口, 通过`@Dao`注解。 Dao: Database access object
    + Database
        + 为抽象类, 通过`@Database`注解, 需要extends RoomDatabase
        + 一般需要将Database设置为单例的, 因为创建数据库实例是很耗资源的事情。
    + AsyncTask
        + doInBackground()
        + onPostExcute() //结束时调用
        + onProgressUpdate() //进度更新时调用  配合publishProgress()使用
        + preExcute() //任务执行前执行
    + ~~结合ViewModel+LiveData~~
        + ~~一般将dao对象和其他数据都放入viewmodel中~~
        + ~~将AsyncTask内部类也放到viewmodel中, 然后在viewmodel中定义方法, 使用AsyncTask对象执行数据库操作。~~
        + `LivData<List<word>> list = worldDao.getAll()`
    + Repository
        + 类似于Utils, 封装对数据库的操作
        + 然后再ViewModel中创建Repository实例, 操作数据库

+ 数据库操作一般不建议运行在主线程, 因为数据库操作可能比较耗时, 如果运行在主线程, 可能会造成应用卡顿。

+ AS中有一个Databse Navigator插件用于可视化数据库

+ ScrollView里需要放置TextView

+ ---

+ 

+ `RecycleView`可以将没出现再页面上的视图回收, 提高资源利用效率。

    `RecycleView`里面是一个个单元视图, 一般他们的布局和界面都是一样的, 所以可以新建一个layout类型的资源文件cell.xml(可以通过R.layout.xxx访问), 作为单元视图, 后续向`RecycleView`中添加单元视图即可。这个单元视图其实就是一个自定义控件。

    使用`RecycleView`需要自定义一个Adapter, 它就是`RecycleView`的内容管理器。

    单元视图可以采用CardView, 设置margin以实现卡片的效果

+ 不同layout文件的view的id可以相同, 所以可以很方便的使用一套id, 操作两种不同布局的view对象

+ 设置单元视图点击特效: 设置layout的clickable属性为true, background属性添加selectxxx的资源文件

    如果是卡片视图, 则应把background设置为foreground

+ 点击跳转网页查询单词

+ LiveData能否实现双向绑定 如文本框输入字符与livedata自动同步

+ ![](https://blog-1258617239.cos.ap-chengdu.myqcloud.com/blog_images/view的派生类.webp)

XXXLayout也是view的子类, 所以继承XXXLayout, 也可以实现一个自定义组件

+ 自定义控件、自定义属性、xml命名空间

+ 数据库版本迁移

    在不修改数据库原有数据的情况下, 对数据库进行修改, 如增加字段、删除字段等

    具体方法:

    + 每一次对Entity进行修改后, 需要修改database对应的version

+ 设置文本不可见有INVISIBLE、GONE, 前者不可见但仍然占据空间, 后者不显示也不占据空间

+ 如何将switch的点击范围扩大? 设置padding, 把switch的可点击范围变大。

+ BottomNavigation

    按照谷歌设计, 底部导航的页面之间一般是相互独立的, 即页面之间没有太多逻辑关系。

    每一个页面可以是一个Fragment, 只是他们之间没有连线。

    menu组件的id与fragment的id必须一致

    将menu添加到bottomNavigation即可实现一个底部导航菜单

    我们可以在创建项目时选择带有底部导航的模板

+ `ObjectAnimator`可以给一个View添加动画

+ ---

+ 悬浮按钮组件`FloatingActionButton`, 通过设置`layout_gravity`属性调整位置

+ 在Fragment中使用Activity需要通过`requireActivity()`获取ac对象

+ 默认情况下键盘弹出后, 会挤压页面布局, 可以在manifest中设置ac的`windowSoftInputMode="adjustNothing"`来取消挤压

+ 顶部菜单的内容可以用Menu来完成。需要创建一个类型为Menu的资源文件。

    其中有Search Item等组件, 可以设置其showAsAction属性使其显示在菜单栏上, 而不是下拉栏里。

+ `Log.d()`可以在控制台打印日志

+ `CordinateLayout`可以实现布局协调, 如当SnackBar从底部弹出式, 页面整体向上挤压。

+ 了解Activity和Fragment的声明周期非常重要!!!!! 只有了解了生命周期, 我们才知道再对应的周期应该干什么事。

    如再Fragment的`onCreateView()`周期时, view还没有被加载, 此时无法通过`findViewById()`来获取view对象。但是在`onViewCreated()`周期时, view已被加载, 此时便可以获取到view对象进行相关的操作。
    
    Fragment生命周期: https://www.jianshu.com/p/b5ea7514c48f、https://www.jianshu.com/p/70d7bfae18f3