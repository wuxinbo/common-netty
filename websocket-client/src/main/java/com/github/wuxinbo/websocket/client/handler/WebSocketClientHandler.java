package com.github.wuxinbo.websocket.client.handler;

import com.github.wuxinbo.websocket.client.WebSoketClient;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {
    /**
     * 日志记录
     */
    private Logger logger= LoggerFactory.getLogger(getClass());
    private WebSocketClientHandshaker handshaker;
    private ChannelPromise promise;

    private WebSoketClient client=null;
    /**
     * 消息处理，支持处理文本和二进制
     */
    private ReciveMessageHandler handler=new DefaultMessageHandler();


    public WebSocketClientHandshaker getHandshaker() {
        return handshaker;
    }

    public void setHandshaker(WebSocketClientHandshaker handshaker) {
        this.handshaker = handshaker;
    }

    public WebSoketClient getClient() {
        return client;
    }

    public void setClient(WebSoketClient client) {
        this.client = client;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!handshaker.isHandshakeComplete()){
            logger.info("handshark not complete");
            handshaker.finishHandshake(ctx.channel(), (FullHttpResponse) msg);
            promise.setSuccess();
            logger.info("websocket is connect");
            return;
        }else{ //默认为二进制数据
            try {
                if (msg instanceof TextWebSocketFrame){
                    handler.handleTextMessage((TextWebSocketFrame) msg);
                }
            }catch (Exception e){
                logger.info("msg is handle fail!",e);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        super.exceptionCaught(ctx, cause);
        logger.error("websocket is disconnect", cause);
        client.disconnect();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        super.channelActive(ctx);
        logger.info("channel is active");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        super.channelInactive(ctx);
        logger.info("channel is inActive");
        //断线重连
        client.connect();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
        promise =ctx.newPromise();
    }

    public ChannelPromise getPromise() {
        return promise;
    }

    public void setPromise(ChannelPromise promise) {
        this.promise = promise;
    }
}
