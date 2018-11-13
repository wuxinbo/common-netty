package com.github.wuxinbo.websocket.client.handler;

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
