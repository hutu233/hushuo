package github.hutu233.hushuo;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class Client {

    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);

    private final Session session;

    private final Listener listener;

    private ClientHandler handler;

    public Client(Listener listener, String host, int port) {
        this.listener = listener;
        this.session = new Session(host, port);
    }

    public void connect() {
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup(1);
        this.handler = new ClientHandler(listener, session);
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .remoteAddress(new InetSocketAddress(this.session.getHost(), this.session.getPort()))
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new LengthFieldPrepender(Common.LENGTH_FIELD_LENGTH)
                                , new LengthFieldBasedFrameDecoder(Common.MAX_FRAME_LENGTH
                                        , Common.LENGTH_FIELD_OFFSET
                                        , Common.LENGTH_FIELD_LENGTH
                                        , Common.LENGTH_ADJUSTMENT
                                        , Common.INITIAL_BYTES_TO_STRIP)
                                , handler);
                    }
                });
        try {
            ChannelFuture future = bootstrap.connect().sync();
            if (future.isSuccess()) {
                session.ok(group);
            }
        } catch (Exception e) {
//            LOGGER.error("连接服务端出错", e);
//            throw new RuntimeException(e);
        }
    }

    public void subscribe(String[] topics) {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        buf.writeChar(Common.MESSAGE_TYPE_SUBSCRIBE);
        for (String topic : topics) {
            buf.writeShortLE(topic.length());
            buf.writeCharSequence(topic, CharsetUtil.UTF_8);
        }
        this.publish(buf);
    }
    
    public void publish(ByteBuf buf, String topic) {
        ByteBuf newBuf = buf.alloc().buffer(buf.readableBytes() + 2 + 2 + topic.length());
        newBuf.writeChar(Common.MESSAGE_TYPE_PUBLISH);
        newBuf.writeShortLE(topic.length());
        newBuf.writeCharSequence(topic, CharsetUtil.UTF_8);
        newBuf.writeBytes(buf);
        this.publish(newBuf);
    }

    public boolean isOk() {
        return session.isOk();
    }

    public void close() {
       this.session.nok();
    }

    private void publish(ByteBuf buf) {
        this.session.publish(buf);
    }
}