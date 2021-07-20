MyServer服务端实现类，在该类中定义了

> **serverSocketChannel: **用于`ServerSocketChannel`的简历和端口的绑定；
>
> **byteBuffer: **用于不同`Channel`之间的数据交互；
>
> **selector:** 用于监听服务器各个 `Channel`上数据的变化并做出响应。

在类构造函数中调用了初始化`ServerSocketChannel`的操作，定义了`listener`方法来监听`Channel`上的数据变化，解析客户端的数据并对客户换的请求做出响应。


定义MyClient类来实现客户端的`Channel`逻辑。其中，

> **connectServer: **方法用于和服务端建立连接，
>
> **receive: **方法用于接收服务端发来的数据，
>
> **send2Server：** 用于想服务器发送数据。

