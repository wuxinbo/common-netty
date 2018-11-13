package com.github.wuxinbo.netty.websocket.client;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;

/**
 * netty websocket client init
 * @author wuxinbo
 *
 */
public class WebSocketClientChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new ChannelHandler[]{new HttpClientCodec(),
                         new HttpObjectAggregator(1024*1024*10)
        });
        pipeline.addLast("handler",new WebSocketClientHandler());

    }
}
