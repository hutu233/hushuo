package github.hutu233.hushuo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {

    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) throws InterruptedException {
        // 创建两个线程组bossGroup和workerGroup, 含有的子线程NioEventLoop的个数默认为cpu核数的两倍
        // bossGroup只是处理连接请求 ,真正的和客户端业务处理，会交给workerGroup完成
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                // 使用NioServerSocketChannel作为服务器的通道实现
                .channel(NioServerSocketChannel.class)
                // 初始化服务器连接队列大小，服务端处理客户端连接请求是顺序处理的,所以同一时间只能处理一个客户端连接。
                // 多个客户端同时来的时候,服务端将不能处理的客户端连接请求放在队列中等待处理
                .option(ChannelOption.SO_BACKLOG, 128)
                // 开启 TCP 的 KEEP ALIVE 机制
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        //创建通道初始化对象，设置初始化参数，在 SocketChannel 建立起来之前执行
                        ch.pipeline().addLast(new LengthFieldPrepender(Common.LENGTH_FIELD_LENGTH)
                                , new LengthFieldBasedFrameDecoder(Common.MAX_FRAME_LENGTH
                                        , Common.LENGTH_FIELD_OFFSET
                                        , Common.LENGTH_FIELD_LENGTH
                                        , Common.LENGTH_ADJUSTMENT
                                        , Common.INITIAL_BYTES_TO_STRIP)
                                , new ServerHandler());
                    }
                });
        // 绑定一个端口并且同步, 生成了一个ChannelFuture异步对象，通过isDone()等方法可以判断异步事件的执行情况
        // 启动服务器(并绑定端口)，bind是异步操作，sync方法是等待异步操作执行完毕
        serverBootstrap.bind(Integer.parseInt(args[0])).sync().channel();
        LOGGER.info("Server started, port: {}", Integer.parseInt(args[0]));
    }
}
