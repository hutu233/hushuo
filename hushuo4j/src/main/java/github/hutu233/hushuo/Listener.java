package github.hutu233.hushuo;

import io.netty.buffer.ByteBuf;

public interface Listener {

    /**
     * 客户端监听接口，需要实现用于接收消息
     * @param msg
     */
    void onMessage(ByteBuf msg);
}
