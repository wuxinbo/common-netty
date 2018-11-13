package com.github.wuxinbo.websocket;

import com.github.wuxinbo.netty.websocket.client.ClientConfig;
import com.github.wuxinbo.netty.websocket.client.NettyWebsocketClient;
import com.github.wuxinbo.netty.websocket.client.WebSocketClientChannelInitializer;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.testng.annotations.Test;

/**
 * websocket 测试
 */
public class NettyWebSocketTest {

    /**
     * 连接服务端测试
     */
    @Test
    public void conn() throws InterruptedException {
        NettyWebsocketClient client=new NettyWebsocketClient();
        ClientConfig config =new ClientConfig("ws://121.40.165.18:8800", new WebSocketClientChannelInitializer());
        client.setClientConfig(config);
        Channel conn = client.conn();
        TextWebSocketFrame frame =new TextWebSocketFrame("hello,world");
        conn.writeAndFlush(frame);
        Thread.sleep(3000);
    }
}
