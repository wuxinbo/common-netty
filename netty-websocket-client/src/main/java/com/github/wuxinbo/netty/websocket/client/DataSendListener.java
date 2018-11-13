package com.github.wuxinbo.netty.websocket.client;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消息发送监听器,可以在此处理消息的发送状态
 */
public class DataSendListener implements ChannelFutureListener {

    private Logger logger= LoggerFactory.getLogger(getClass());

//    @Override
    public void operationComplete(ChannelFuture future) throws Exception {
        if (future.isSuccess()) { //消息发送成功
            logger.info("msg is send!");
        }else{
            logger.info("msg is not send",future.cause());
        }
    }
}
