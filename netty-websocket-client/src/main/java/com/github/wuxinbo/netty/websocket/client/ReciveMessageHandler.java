package com.github.wuxinbo.netty.websocket.client;

import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * websocket 消息处理接口
 */
public interface ReciveMessageHandler {
    /**
     * 处理二进制消息
     */
    void handleTextMessage(TextWebSocketFrame text);

}
