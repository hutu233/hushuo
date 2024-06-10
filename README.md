### 胡说
### hushuo（胡说），基于 **Netty** 实现的 **发布/订阅（publish/subscribe）** 模式轻量通讯中间件。

### 客户端使用步骤
> 1. 选择合适的 slf4j 实现
> 2. 引入 Netty 相关依赖
> 3. 实现Listener接口
> 4. 创建Client对象
> 5. 发送订阅主题的请求
> 6. 通过onMessage()接收消息
```java
public class TestListener implements Listener {

    public void onMessage(ByteBuf byteBuf) {
        return;
    }
}

public class TestMain {
    public static void main(String[] args) {
        TestListener testListener = new TestListener();
        Client client = new Client(testListener, "127.0.0.1", 11111);
        client.connect();
    }
}
```

### 服务端使用步骤
> 1. java -jar hushuo-server.jar 服务端口