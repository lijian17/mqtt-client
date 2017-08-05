## 概述

MQTT(Message Queuing Telemetry Transport)是面向"M2M"(Machine-to-Machine)和"物联网"的连接协议，采用轻量级发布/订阅(Publish/Subscribe)模式。
由于规范很简单，非常适合需要低功耗和网络带宽有限的IoT场景

mqtt-client向MQTT提供了一个ASL 2.0许可的API。 
当网络不稳定时，它能自动重连到您的MQTT服务器并还原您的客户端session。
您可以使用阻塞式API、基于Futures式API或者callback/continuations式API。

## 在Maven中使用

添加下面配置到您的maven `pom.xml` 文件.

    <dependency>
      <groupId>org.fusesource.mqtt-client</groupId>
      <artifactId>mqtt-client</artifactId>
      <version>1.12</version>
    </dependency>
	
## 在Gradle中使用

添加下面配置到您的 gradle 文件.

    compile 'org.fusesource.mqtt-client:mqtt-client:1.12'


## 在任何其他Build System中使用

下载这个 
[uber jar file](https://repository.jboss.org/nexus/content/groups/fs-public/org/fusesource/mqtt-client/mqtt-client/1.7/mqtt-client-1.7-uber.jar) 
并添加到您的build. 这个 uber 包含所有 mqtt-client 的依赖.

## 在 Java 1.4 中使用

我们还提供与Java 1.4 JVM兼容的
[java 1.4 uber jar file](https://repository.jboss.org/nexus/content/groups/fs-public/org/fusesource/mqtt-client/mqtt-client-java1.4-uber/1.7/mqtt-client-java1.4-uber-1.7.jar)
文件。 此版本的jar不支持SSL连接，因为用于在NIO上实现SSL的SSLEngine类直到Java 1.5才被引入。

## 配置MQTT连接

blocking, future, 和 callback APIs 都共享相同的连接配置.
您创建一个新的 `MQTT` 实例, 并通过connection和socket相关配置，至少要调用`setHost`方法尝试连接

    MQTT mqtt = new MQTT();
    mqtt.setHost("localhost", 1883);
    // 或 
    mqtt.setHost("tcp://localhost:1883");
    
### MQTT设置说明

* `setClientId` : 用于设置会话的客户端ID。 这是一个MQTT服务器用来标识一个会话，其中 `setCleanSession(false);` 正在被使用。 
  该ID必须为23个字符或更少。 默认为自动生成的ID(根据您的socket地址，端口和时间戳)。

* `setCleanSession` : 连接前清空会话信息 ,若设为false，MQTT服务器将持久化客户端会话的主体订阅和ACK位置，默认为true

* `setKeepAlive` : 设置心跳时间 ,定义客户端传来消息的最大时间间隔秒数，服务器可以据此判断与客户端的连接是否已经断开，从而避免TCP/IP超时的长时间等待

* `setUserName` : 设置用于对服务器进行身份验证的用户名。

* `setPassword` : 设置用于对服务器进行身份验证的密码。

* `setWillTopic`: 设置“遗嘱”消息的话题，若客户端与服务器之间的连接意外中断，服务器将发布客户端的“遗嘱”消息.

* `setWillMessage`:  设置“遗嘱”消息的内容，默认是长度为零的消息

* `setWillQos` : 设置“遗嘱”消息的QoS，默认为QoS.AT_MOST_ONCE

* `setWillRetain`: 若想要在发布“遗嘱”消息时拥有retain选项，则为true

* `setVersion`: 设置为“3.1.1”以使用MQTT 3.1.1版。 否则默认为3.1协议版本。

### 失败重连接设置说明

如果发生任何网络错误，Connection将自动重新连接并重新建立消息传递会话。 您可以控制重新连接的频率，并使用以下方法定义重新连接的最大次数：

* `setConnectAttemptsMax` : 在客户端首次尝试连接到服务器时，会将错误之前的重新连接尝试的最大次数报告回客户端。 设置为-1以使用无限次尝试。 默认为-1。
* `setReconnectAttemptsMax` : 在建立服务器连接之后，将错误之前的重新连接尝试的最大次数报告回客户端。 设置为-1以使用无限次尝试。 默认为-1。
* `setReconnectDelay` : 在第一次重新连接尝试之前等待多长时间。 默认为10。
* `setReconnectDelayMax` : 在重新连接尝试之间等待的最大时间（以ms为单位）。 默认为30,000。
* `setReconnectBackOffMultiplier` : 在重新连接尝试之间使用指数回归。 设置为1以停用指数回归。 默认为2。

### Socket设置说明

您可以使用以下方法调整一些socket选项：

* `setReceiveBufferSize` : 设置socket接收缓冲区大小，默认为65536（64k）

* `setSendBufferSize` : 设置socket发送缓冲区大小，默认为65536（64k）

* `setTrafficClass` : 设置发送数据包头的流量类型或服务类型字段，默认为 `8` ，意为吞吐量最大化传输

### 带宽限制设置说明

如果要减慢连接的读取或写入速率，请使用以下方法：

* `setMaxReadRate` : 设置连接的最大接收速率，单位为bytes/s。默认为0，即无限制

* `setMaxWriteRate` : 设置连接的最大发送速率，单位为bytes/s。默认为0，即无限制

### 使用 SSL 连接

如果要通过SSL/TLS而不是TCP连接，请对 `host` 字段使用 "ssl://" 或 "tls://" URI前缀，而不是 "tcp://" 。 对于使用哪种算法进行更精细的粒度控制。 支持的协议值有：

* `ssl://`    - 使用JVM默认版本的SSL算法。
* `sslv*://`  - 使用特定的SSL版本，其中 `*` 是您的JVM支持的版本。 示例： `sslv3`
* `tls://`    - 使用JVM默认版本的TLS算法。
* `tlsv*://`  - 使用特定的TLS版本，其中 `*` 是您的JVM支持的版本。 示例：`tlsv1.1`

客户端将使用通过JVM系统属性配置的默认JVM  `SSLContext`，除非您使用 `setSslContext` 方法配置MQTT实例。

SSL连接对内部线程池执行阻塞操作，除非您调用 `setBlockingExecutor` 方法来配置它们将使用的执行程序。

### 选择调度队列
[HawtDispatch](http://hawtdispatch.fusesource.org/) 调度队列用于同步对连接的访问。 如果未通过 `setDispatchQueue` 方法配置显式队列，则将为该连接创建一个新的队列。 
如果希望多个连接共享相同的同步队列，设置显式队列可能会很方便。

## 使用阻塞API

`MQTT.connectBlocking` 方法建立一个连接并提供与阻塞API的连接。

    BlockingConnection connection = mqtt.blockingConnection();
    connection.connect();

使用`publish`方法向主题发布消息：

    connection.publish("foo", "Hello".getBytes(), QoS.AT_LEAST_ONCE, false);

您可以使用`subscribe`方法订阅多个主题：
    
    Topic[] topics = {new Topic("foo", QoS.AT_LEAST_ONCE)};
    byte[] qoses = connection.subscribe(topics);

然后使用`receive`和`ack`方法接收并确认消息：
    
    Message message = connection.receive();
    System.out.println(message.getTopic());
    byte[] payload = message.getPayload();
    // 然后处理消息：
    message.ack();

最后断开连接：

    connection.disconnect();

## 使用Future based API

`MQTT.connectFuture`方法建立一个连接，并提供与futures风格API的连接。 对连接的所有操作都是非阻塞的，并通过Future返回结果。

    FutureConnection connection = mqtt.futureConnection();
    Future<Void> f1 = connection.connect();
    f1.await();

    Future<byte[]> f2 = connection.subscribe(new Topic[]{new Topic(utf8("foo"), QoS.AT_LEAST_ONCE)});
    byte[] qoses = f2.await();

    // 我们可以开始future接收..
    Future<Message> receive = connection.receive();

    // 发送message..
    Future<Void> f3 = connection.publish("foo", "Hello".getBytes(), QoS.AT_LEAST_ONCE, false);

    // 然后将得到的信息接收.
    Message message = receive.await();
    message.ack();
    
    Future<Void> f4 = connection.disconnect();
    f4.await();


## 使用Callback/Continuation Passing based API

`MQTT.connectCallback` 方法建立一个连接，并提供与callback样式API的连接。 这是使用API风格最复杂的，但可以提供最佳性能。 
future和阻塞API使用覆盖下的回调api。 连接上的所有操作都是非阻塞的，操作的结果将传递给您实现的回调接口。

示例:

    final CallbackConnection connection = mqtt.callbackConnection();
    connection.listener(new Listener() {
      
        public void onDisconnected() {
        }
        public void onConnected() {
        }

        public void onPublish(UTF8Buffer topic, Buffer payload, Runnable ack) {
            // 您现在可以从主题处理收到的消息。
            // 一旦进程执行ack runnable。
            ack.run();
        }
        public void onFailure(Throwable value) {
            connection.close(null); // 发生连接失败。
        }
    })
    connection.connect(new Callback<Void>() {
        public void onFailure(Throwable value) {
            result.failure(value); // 如果我们无法连接到服务器。
        }
  
        // 一旦我们连接..
        public void onSuccess(Void v) {
        
            // 订阅主题
            Topic[] topics = {new Topic("foo", QoS.AT_LEAST_ONCE)};
            connection.subscribe(topics, new Callback<byte[]>() {
                public void onSuccess(byte[] qoses) {
                    // subcribe 请求的结果.
                }
                public void onFailure(Throwable value) {
                    connection.close(null); // 订阅失败
                }
            });

            // 发送消息到主题
            connection.publish("foo", "Hello".getBytes(), QoS.AT_LEAST_ONCE, false, new Callback<Void>() {
                public void onSuccess(Void v) {
                  // 发布操作成功完成.
                }
                public void onFailure(Throwable value) {
                    connection.close(null); // 发布失败.
                }
            });
            
            // 断开..
            connection.disconnect(new Callback<Void>() {
                public void onSuccess(Void v) {
                  // 一旦连接断开连接就调用.
                }
                public void onFailure(Throwable value) {
                  // 断开连接永远不会失败.
                }
            });
        }
    });

每个连接都有一个[HawtDispatch](http://hawtdispatch.fusesource.org/) 调度队列，用于处理套接字的IO事件。 
调度队列是提供IO和处理事件的串行执行的执行程序，用于确保连接的同步访问。

回调将执行与连接相关联的调度队列，因此可以安全地使用回调中的连接，但您不得在回调中执行任何阻止操作。 
如果您需要执行可能阻止的某些处理，则必须将其发送到另一个线程池进行处理。 
此外，如果另一个线程需要与连接交互，那么它只能通过使用提交到连接的调度队列的Runnable来实现。

在连接的调度队列上执行Runnable的示例:

    connection.getDispatchQueue().execute(new Runnable(){
        public void run() {
          connection.publish( ..... );
        }
    });
