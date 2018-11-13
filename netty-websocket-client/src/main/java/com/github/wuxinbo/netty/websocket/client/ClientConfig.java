package com.github.wuxinbo.netty.websocket.client;


import io.netty.channel.ChannelInitializer;

/**
 * 客户端配置信息
 */
public class ClientConfig {

    /**
     * 服务端连接地址
     */
    private String url;
    /**
     * 通道初始化
     */
    private ChannelInitializer channelInitializer;
    /**
     * 消息处理器
     */
    private ReciveMessageHandler handler;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ChannelInitializer getChannelInitializer() {
        return channelInitializer;
    }

    public void setChannelInitializer(ChannelInitializer channelInitializer) {
        this.channelInitializer = channelInitializer;
    }

    public ClientConfig(String url, ChannelInitializer channelInitializer) {
        this.url = url;
        this.channelInitializer = channelInitializer;
    }
}
