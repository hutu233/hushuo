package github.hutu233.hushuo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.util.Arrays;
import java.util.Scanner;

public class TestMain2 {

    public static void main(String[] args) {
        try {
            TestListener testListener = new TestListener("2");
            Client client = new Client(testListener, "127.0.0.1", 11111);
            client.connect();
            while (!client.isOk()) {
                System.out.println("2秒后重连");
                Thread.sleep(2000L);
                client.connect();
            }
            String[] topics = new String[]{"xx2"};
            String send = "xx1";
            client.subscribe(topics);
            System.out.println("注册成功" + Arrays.toString(topics));
            while (true){
                Scanner sc = new Scanner(System.in);
                int num = sc.nextInt();
                ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
                buf.writeIntLE(num);
                client.publish(buf, send);
                System.out.println("send:"+ send + " " + num);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
