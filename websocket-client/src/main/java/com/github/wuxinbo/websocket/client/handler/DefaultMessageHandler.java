package com.github.wuxinbo.websocket.client.handler;

import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 默认的消息处理器
 */
public class DefaultMessageHandler implements ReciveMessageHandler {
    private Logger logger= LoggerFactory.getLogger(getClass());

    public void handleTextMessage(TextWebSocketFrame text) {
        logger.info("recive a messgae is ",text.text());
    }
}
