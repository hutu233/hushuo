package github.hutu233.hushuo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class ClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientHandler.class);

    private final Listener listener;

    private final Session session;

    public ClientHandler(Listener listener, Session session) {
        this.listener = listener;
        this.session = session;
    }

    /**
     * 当Channel接收到消息时被调用
     * @param ctx
     * @param byteBuf
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) {
        LOGGER.info("channelRead0...");
        this.listener.onMessage(byteBuf);
    }

    /**
     * 当Channel处于活动状态（连接已经建立）时被调用。
     *
     * @param ctx
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        LOGGER.info("channelActive...");
        this.session.ok(ctx);
    }

    /**
     * 当Channel处于非活动状态（连接已经断开）时被调用。
     *
     * @param ctx
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        LOGGER.info("channelInactive...");
        this.sendErrorMsg(ctx, Common.MESSAGE_TYPE_DISCONNECT);
    }

    /**
     * 当处理过程中发生异常时被调用，cause参数是异常对象。
     *
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.info("exceptionCaught...");
        this.sendErrorMsg(ctx, Common.MESSAGE_TYPE_ERROR);
    }

    /**
     * 当Channel从EventLoop中注销并且无法处理I/O时被调用。
     *
     * @param ctx
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        LOGGER.info("channelUnregistered...");
        this.sendErrorMsg(ctx, Common.MESSAGE_TYPE_ERROR);
    }

    private void sendErrorMsg(ChannelHandlerContext ctx, char err) {
        this.session.nok();
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        buf.writeByte(err);
        this.listener.onMessage(buf);
    }
}
