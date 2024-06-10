package github.hutu233.hushuo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ServerHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerHandler.class);

    private static final Map<String, Set<ChannelHandlerContext>> SUBSCRIBES = new HashMap<>();

    private final Map<Character, BiFunction<ChannelHandlerContext, ByteBuf, Void>> HANDLERS = new HashMap<>();

    public ServerHandler() {
        HANDLERS.put(Common.MESSAGE_TYPE_SUBSCRIBE, this::subscribe);
        HANDLERS.put(Common.MESSAGE_TYPE_PUBLISH, this::publish);
    }

    /**
     * 当Channel处于活动状态（连接已经建立）时被调用。
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        LOGGER.info("{} channelActive...", ctx);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) {
//        LOGGER.info("channelRead0...");
        ByteBuf newBuf = byteBuf.copy();
        if (newBuf.toString(CharsetUtil.UTF_8).length() < 2) {
            return;
        }
        // 获取消息类型
        char key = newBuf.readChar();
        BiFunction<ChannelHandlerContext, ByteBuf, Void> handler = HANDLERS.get(key);
        handler.apply(ctx, byteBuf);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.info("exceptionCaught...", cause);
        ctx.close();
    }

    /**
     * 接收订阅消息
     * @param ctx
     * @param byteBuf
     * @return
     */
    private Void subscribe(ChannelHandlerContext ctx, ByteBuf byteBuf) {
        ByteBuf newBuf = byteBuf.copy();
        newBuf.readChar();
        while (newBuf.isReadable()) {
            short length = newBuf.readShortLE();
            String topic = newBuf.readCharSequence(length, CharsetUtil.UTF_8).toString();
            if (SUBSCRIBES.containsKey(topic)) {
                SUBSCRIBES.get(topic).add(ctx);
            } else {
                Set<ChannelHandlerContext>  set = new HashSet<>();
                set.add(ctx);
                SUBSCRIBES.put(topic, set);
            }
            LOGGER.info("register:{}", topic);
        }
        return null;
    }

    /**
     * 接收到其他客户端的消息，根据topic推送给订阅了该topic的客户端
     * @param ctx
     * @param byteBuf
     * @return
     */
    private Void publish(ChannelHandlerContext ctx, ByteBuf byteBuf) {
        ByteBuf newBuf = byteBuf.copy();
        newBuf.readChar();
        short length = newBuf.readShortLE();
        String topic = newBuf.readCharSequence(length, CharsetUtil.UTF_8).toString();
        Set<ChannelHandlerContext> wCtxs = SUBSCRIBES.get(topic);
        if (wCtxs != null) {
            try {
                for (ChannelHandlerContext wCtx : wCtxs) {
                    LOGGER.info("{} publish:{}", wCtx, topic);
                    ChannelFuture future = wCtx.writeAndFlush(newBuf.copy());
                    if(!future.isSuccess()) {
                        LOGGER.info("Failed to send message...");
                    }
                }
            } catch (Exception e) {
                LOGGER.info("Failed to send message...", e);
            }
        }
        return null;
    }
}