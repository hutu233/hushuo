package github.hutu233.hushuo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;

public final class Session {

    private final String host;

    private final int port;

    private ChannelHandlerContext ctx;

    private EventLoopGroup group;

    public Session(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void publish(ByteBuf buf) {
        this.ctx.writeAndFlush(buf);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public boolean isOk() {
        return this.ctx != null && this.group != null;
    }

    public void ok(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public void ok(EventLoopGroup group) {
        this.group = group;
    }

    public void nok() {
        if (this.ctx != null) {
            ctx.close();
        }
        if (this.group != null) {
            this.group.shutdownGracefully();
        }
    }
}
