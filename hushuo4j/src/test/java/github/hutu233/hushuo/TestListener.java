package github.hutu233.hushuo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

public class TestListener implements Listener {

    private String node;

    public TestListener(String node) {
        this.node = node;
    }

    public void onMessage(ByteBuf byteBuf) {
        System.out.println(node + "--收到" + ByteBufUtil.hexDump(byteBuf));
    }
}
