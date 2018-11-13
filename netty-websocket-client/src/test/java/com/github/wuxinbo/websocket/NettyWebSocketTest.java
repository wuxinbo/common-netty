package com.github.wuxinbo.websocket;

import com.github.wuxinbo.netty.websocket.client.ClientConfig;
import com.github.wuxinbo.netty.websocket.client.NettyWebsocketClient;
import com.github.wuxinbo.netty.websocket.client.WebSocketClientChannelInitializer;
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
        client.conn();
        client.sendText("hello,world",null );
        client.releaseResource();
        Thread.sleep(3000);
    }
}
