## 一、WebSocket

### 1. WebSocket简介

> WebSocket 是 HTML5 开始提供的一种在单个 TCP 连接上进行全双工通讯的协议。
>
> WebSocket 使得客户端和服务器之间的数据交换变得更加简单，允许服务端主动向客户端推送数据。在 WebSocket API 中，浏览器和服务器只需要完成一次握手，两者之间就直接可以创建持久性的连接，并进行双向数据传输。
>

### 2. 应用场景

+ 网页即时通讯
+ 服务端消息推送

### 3. WebSocket与Ajax

有些网站为了实现推送技术，所用的技术都是 Ajax 轮询。轮询是在特定的的时间间隔（如每1秒），由浏览器对服务器发出HTTP请求，然后由服务器返回最新的数据给客户端的浏览器。这种传统的模式带来很明显的缺点，即浏览器需要不断的向服务器发出请求，然而HTTP请求可能包含较长的头部，其中真正有效的数据可能只是很小的一部分，显然这样会浪费很多的带宽等资源。

HTML5 定义的 WebSocket 协议，能更好的节省服务器资源和带宽，并且能够更实时地进行通讯。

### 4. Websocket与HTTP、TCP

WebSocket客户端与服务端的通信全过程包括三个阶段。握手阶段、数据传输阶段、分手阶段。

其中握手阶段与分手阶段是通过HTTP发送握手和分手请求, 数据传输阶段是通过TCP建立长连接, 实现数据传输。



## 二、客户端API

使用客户端WebSocket API需要引入`WebSocket.js`

### 1. API介绍

```js
//一个对象
var webSocket = new WebSocket(url, [protocol]);

//两个属性
/*
	连接状态
	0 - 表示连接尚未建立
	1 - 表示连接已建立，可以进行通信
	2 - 表示连接正在进行关闭
	3 - 表示连接已经关闭或者连接不能打开
*/
webSocket.readyState
/*
	只读属性bufferedAmount已被send()放入正在队列中等待传输，但是还没有发出的UTF-8文本字节数
*/
webSocket.bufferedAmount

//两个方法
//发送数据
webSocket.send()
//关闭连接
webSocket.close()

//四个事件
//创建连接
webSocket.onopen = function(event){}
//收到服务端消息
webSocket.onmessage = function(event){}
//出现错误
webSocket.onerror = function(event){}
//关闭连接, 在连接中断或者调用close()方法后调用
webSocket.onclose = function(event){}
```

### 2. 实例

```javascript
//路径/webSocket需与服务端foncig类的配置一致
var webSocket = new WebSocket("ws://127.0.0.1:8083/webSocket");

webSocket.onopen = function () { webSocket.send("连接创建成功!") };

webSocket.onmessage = function(event){ 
    //服务端可以把消息封装成一个json, 里面封装from、to、message等属性, 更加方便
    var data = $.parseJSON(event.data); 
    //对消息进行相应处理...
}

webSocket.onclose = function(event){ 
    //关闭连接后的操作
}
```



## 三、服务端(Spring WebSocket)

需要导入`spring-websocket.jar`

### 1. 编写配置类

```java
@Configuration
@EnableWebMvc
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer{
    //注入处理器
    @Autowired
    private WebSocketHandler webSocketHandler;
    //注入拦截器
    @Autowired
    private WebSocketInterceptor webSocketInterceptor;
    
    //注册具体服务
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry){
 
 //注册handler和interceptor, 并配置客户端的连接请求路径(/webSocket)
        registry.addHandler(webSocketHandler,"/webSocket").addInterceptors(webSocketInterceptor);
        
        //withSockJS由spring提供用于处理不同浏览器对WebSocket的兼容性问题
        registry.addHandler(webSocketHandler, "/sockjs").addInterceptors(webSocketInterceptor).withSockJS();
    }
}
```

### 2. 编写Interceptor

过滤器对WebSocket的连接进行过滤，可以对连接前和连接后自定义处理。

```java
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.http.server.ServerHttpRequest;  
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;  
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;  

@Component
public class WebSocketInterceptor extends HttpSessionHandshakeInterceptor{  
  
    //握手前的操作, 返回值为true即表示通过
    @Override  
    public boolean beforeHandshake(ServerHttpRequest request,  
            ServerHttpResponse response, WebSocketHandler wsHandler,  
            Map<String, Object> attributes) throws Exception {  
        System.out.println("Before Handshake");  
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpSession session = servletRequest.getServletRequest().getSession(false);
            if (session != null) {
                //获取当前用户的用户名
                String userName = (String) session.getAttribute("userName");
                if (userName==null) {
                    userName="default";
                }
                //将用户名保存在该用户对应的WebSocketSession中(注意不是HttpSession)
                attributes.put("userName",userName);
            }
        }
        return super.beforeHandshake(request, response, wsHandler, attributes);  
    }  
  
    //握手后的操作
    @Override  
    public void afterHandshake(ServerHttpRequest request,  
            ServerHttpResponse response, WebSocketHandler wsHandler,  
            Exception ex) {  
        System.out.println("After Handshake");  
        super.afterHandshake(request, response, wsHandler, ex);  
    }  
  
}  
```

### 3. 编写Handler

处理器负责处理消息发送接收的逻辑

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class SpringWebSocketHandler extends TextWebSocketHandler {

    private static Logger logger = LoggerFactory.getLogger(SpringWebSocketHandler.class);

    //Map来存储WebSocketSession，key为userName 这个Map代表在线用户列表
    //每一个用户都有一个自己的WebSocketSession
    private static final Map<String, WebSocketSession> users;  

    static {
        users =  new HashMap<String, WebSocketSession>();
    }

    public SpringWebSocketHandler() {}

    /**
     * 连接成功时触发
     */
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        System.out.println("成功建立websocket连接!");
        String userId = (String) session.getHandshakeAttributes().get("userName");
        //连接成功时将用户添加到用户列表
        users.put(userId,session);
        System.out.println("当前线上用户数量:"+users.size());

        //这块会实现自己业务，比如，当用户登录后，会把离线消息推送给用户
        //TextMessage returnMessage = new TextMessage("成功建立socket连接，你将收到的离线");
        //session.sendMessage(returnMessage);
    }

    /**
     * 关闭连接时触发
     */
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        logger.debug("关闭websocket连接");
        String userId= (String) session.getHandshakeAttributes().get("userName");
        System.out.println("用户"+userId+"已退出！");
        users.remove(userId);
        System.out.println("剩余在线用户"+users.size());
    }

    /**
     * js调用websocket.send时候，会调用该方法进行消息处理
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        super.handleTextMessage(session, message);

        /**
         * 收到消息，自定义处理机制，实现业务
         */
        System.out.println("服务器收到消息："+message);
		
        //getPayload()方法用于获得消息内容
        if(message.getPayload().startsWith("#anyone#")){ //单发某人

            sendMessageToUser((String)session.getHandshakeAttributes().get("userName"), new TextMessage("服务器单发：" +message.getPayload())) ;

        }else if(message.getPayload().startsWith("#everyone#")){

            sendMessageToUsers(new TextMessage("服务器群发：" +message.getPayload()));

        }else{

        }

    }

    //传输错误时触发
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if(session.isOpen()){
            session.close();
        }
        logger.debug("传输出现异常，关闭websocket连接... ");
        String userId= (String) session.getHandshakeAttributes().get("userName");
        users.remove(userId);
    }

    
   	//是否消息分片
    public boolean supportsPartialMessages() {
        return false;
    }


    /**
     * 给某个用户发送消息
     *
     * @param userId
     * @param message
     */
    public void sendMessageToUser(String userId, TextMessage message) {
        for (String id : users.keySet()) {
            if (id.equals(userId)) {
                try {
                    if (users.get(id).isOpen()) {
                        //通过用户对应的WebSocketSession的sendMessage()方法发送消息
                        users.get(id).sendMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    /**
     * 给所有在线用户发送消息
     *
     * @param message
     */
    public void sendMessageToUsers(TextMessage message) {
        for (String userId : users.keySet()) {
            try {
                if (users.get(userId).isOpen()) {
                    users.get(userId).sendMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
```

