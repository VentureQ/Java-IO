package IO.NIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class MyServer {
    private int size = 1024;
    private ServerSocketChannel serverSocketChannel;
    private ByteBuffer byteBuffer;
    private Selector selector;
    private int remoteClientNum = 0;
    public MyServer(int port){
        try {
            initChannel(port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Channel的初始化
     * @param port
     * @throws IOException
     */
    public void initChannel(int port) throws IOException {
        //打开 Channel
        serverSocketChannel = ServerSocketChannel.open();
        //设置为非阻塞模式
        serverSocketChannel.configureBlocking(false);
        //绑定端口
        serverSocketChannel.bind(new InetSocketAddress(port));
        System.out.println("listener on port: " + port);
        //创建选择器
        selector =Selector.open();
        //向选择器注册通道
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        //分配缓冲区的大小
        byteBuffer = ByteBuffer.allocate(size);
    }

    /**
     * 监听器，用于监听Channel上的数据变化
     * @throws Exception
     */
    private void listener() throws Exception {
        while (true) {
            //返回的int值表示有多少个Channel处于就绪状态
            int n =selector.select();
            if (n == 0){
                continue;
            }
            //每个selector对应多个SelectionKey,每个SelectionKey对应一个Channel
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                //如果SelectionKey处于连接就绪状态，则开始接收客户端的连接
                if (key.isAcceptable()){
                    //获取Channel
                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    //Channel接收连接
                    SocketChannel channel =server.accept();
                    //Channel注册
                    remoteClientNum++;
                    System.out.println("online clinent num = " + remoteClientNum);
                    write(channel,"hello client 123".getBytes());
                    //远程客户端的连接数统计
                }
                //如果通道以及处于读就绪状态，则读取通道上的数据
                if (key.isReadable()){
                    read(key);
                }
                iterator.remove();
            }
        }
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        int count;
        byteBuffer.clear();
        //从通道中读取数据到缓冲区
        while ((count =socketChannel.read(byteBuffer)) > 0){
            //byteBuffer写模式变为读模式
            byteBuffer.flip();
            while (byteBuffer.hasRemaining()){
                System.out.println((char)byteBuffer.get());
            }
            byteBuffer.clear();
        }
        if (count < 0) {
            socketChannel.close();
        }
    }

    private void write(SocketChannel channel, byte[] writeData) throws IOException {
        byteBuffer.clear();
        byteBuffer.put(writeData);
        //byteBuffer写模式变为读模式
        byteBuffer.flip();
        //将缓冲区的数据写入通道中
        channel.write(byteBuffer);
    }

    private void registerChannel(Selector selector,SocketChannel channel,int opRead) throws IOException {
        if (channel == null){
            return;
        }
        channel.configureBlocking(false);
        channel.register(selector,opRead);
    }

    public static void main(String[] args) {

        try {
            MyServer myServer = new MyServer(9999);
            myServer.listener();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
