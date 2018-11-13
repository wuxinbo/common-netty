package com.github.wuxinbo.netty.websocket.client;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

/**
 * webclient 默认实现。
 */
public class NettyWebsocketClient implements WebSoketClient{
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 连接上服务器后才会有
     */
    private Channel channel;

    private EventLoopGroup lookGroup = null;
    /**
     * 配置类
     */
    private Bootstrap bootstrap = null;
    /**
     * 配置信息
     */
    private ClientConfig clientConfig=null;
    //数据发送监听器
    protected static ChannelFutureListener dataSendListener=new DataSendListener();
    public Channel connect() {
        return conn();
    }

    /**
     * 断开连接,
     */
    public void disconnect() {
        if (channel != null) {
            channel.disconnect();
        }
        this.channel = null;
    }

    public void sendText(String msg, ChannelFutureListener listener) {
        if (online()&& !StringUtil.isNullOrEmpty(msg)){ //没有掉线就发起请求
            listener =listener==null?dataSendListener:listener;
            channel.writeAndFlush(new TextWebSocketFrame(msg)).addListener(listener);
        }
    }

    public ClientConfig getClientConfig() {
        return clientConfig;
    }

    public void setClientConfig(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    /**
     * 判断连接是否存在。
     * @return true表示在线，false表示下线
     */
    public boolean online() {
        return getChannel() != null && getChannel().isActive();
    }

    /**
     * 初始化client
     */
    private Bootstrap init() {
        lookGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        //保持长连接
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true).
                option(ChannelOption.SO_BACKLOG, 1024 * 1024 * 10).
                group(lookGroup).
                channel(NioSocketChannel.class).
                handler(new LoggingHandler()).
                handler(clientConfig.getChannelInitializer());
        return bootstrap;
    }

    /**
     * 资源释放
     */
    public void releaseResource() {
        //先断开连接
        disconnect();
        try {
            if (lookGroup != null) { //关闭线程
                lookGroup.awaitTermination(10 * 1000, TimeUnit.SECONDS);
            }
//            if (bootstrap!=null){
//            }
        } catch (InterruptedException e) {
            logger.error("netty shutdown fail ", e);
        }

    }

    /**
     * 连接服务端
     *
     * @return
     */
    public Channel conn() {
        //获取连接参数
        Bootstrap boot = init();
        try {

            URI uri = new URI(clientConfig.getUrl());
            DefaultHttpHeaders headers = new DefaultHttpHeaders();
            WebSocketClientHandshaker webSocketClientHandshaker = new CustomWebSocketClienthandshark13(uri,
                    WebSocketVersion.V13,
                    null,
                    true,
                    headers,
                    65535);

            logger.info("conn .....");
            channel = boot.connect(uri.getHost(), uri.getPort()).sync().channel();
            WebSocketClientHandler handler = (WebSocketClientHandler) channel.pipeline().get("handler");
            handler.setHandshaker(webSocketClientHandshaker);
            handler.setClient(this);
            handler.getHandshaker().handshake(channel);
            handler.getPromise().sync();
            return channel;
        } catch (URISyntaxException e) {
            logger.error("url not correct", e);
        } catch (InterruptedException e) {
            logger.error("conn is fail ", e);
        }
        return null;
    }

    private Channel getChannel() {
        return channel;
    }

}
