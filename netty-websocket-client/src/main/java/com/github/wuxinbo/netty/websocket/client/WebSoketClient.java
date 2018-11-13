package com.github.wuxinbo.netty.websocket.client;


import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

/**
 * 客户端接口
 */
public interface WebSoketClient {
    /**
     * 服务器连接
     * @return 连接成功后打开通道，返回对应的通道
     */
    Channel connect();

    /**
     * 断开连接
     */
    void disconnect();

    /**
     * 是否在线
     * @return true 是，false 否
     */
    boolean online();
    /**
     * 发送简单文本，同时可以注入一个监听器,只有当消息不为空或者通道在线时才会发送
     * @param msg 文本消息
     * @param listener 消息发送监听器，如果参数为空将使用默认的监听器{@link DataSendListener}
     * @see DataSendListener 默认的消息发送监听器
     */
    void sendText(String msg, ChannelFutureListener listener);

}
