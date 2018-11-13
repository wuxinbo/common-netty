package com.github.wuxinbo.netty.websocket.client;

import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker13;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static io.netty.handler.codec.http.HttpHeaderNames.SEC_WEBSOCKET_KEY;

/**
 * 自定义WebSocket握手处理器，解决两个问题:
 * <p>1.解决sec-websocket-key 首字母没有大写，导致服务端获取不到key。</p>
 * <p>2.修复<a href="https://github.com/netty/netty/issues/795">#795</a>的bug,
 * 该bug会导致websocket使用http协议握手失败。</p>
 */
public class CustomWebSocketClienthandshark13 extends WebSocketClientHandshaker13 {
    private Logger logger= LoggerFactory.getLogger(getClass());
    /**
     * 计算出来的sec-websocket-accept
     */
    private String computeWebSocketAccept;
    public CustomWebSocketClienthandshark13(URI webSocketURL,
                                            WebSocketVersion version,
                                            String subprotocol,
                                            boolean allowExtensions,
                                            HttpHeaders customHeaders,
                                            int maxFramePayloadLength) {
        super(webSocketURL, version, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength);
    }

    @Override
    protected FullHttpRequest newHandshakeRequest() {
        FullHttpRequest fullHttpRequest = super.newHandshakeRequest();
        //由于网关不识别小写的 Sec-Websocket-Key 大写，所以这里需要大写
        String key =fullHttpRequest.headers().get(SEC_WEBSOCKET_KEY);
        fullHttpRequest.headers().add("Sec-WebSocket-Key",key);
        //计算sec-websocket-key
        try {
            computeWebSocketAccept=computeWebSocketAccept(key);
        } catch (NoSuchAlgorithmException e) {
            logger.error("NoSuchAlgorithmException",e );
        } catch (UnsupportedEncodingException e) {
            logger.error("UnsupportedEncodingException",e );
        }
//        fullHttpRequest.headers().remove(SEC_WEBSOCKET_KEY);
        return fullHttpRequest;
    }

    /**
     * 计算sec-websocket-accept.计算过程如下：
     * <ol>
     *    <li>将发送给服务端的sec-websocket-key和MAGIC_UUID进行拼接得到一个新的字符串。
     *    MAGIC_UUID的值为258EAFA5-E914-47DA-95CA-C5AB0DC85B11，该字符串来自于<a href="https://tools.ietf.org/html/rfc6455">
     *        rfc 6455</a>。
     *    </li>
     *    <li>将上一步得到字符串使用sha1算法加密得到byte数组。</li>
     *    <li>将byte数组使用base64Encode进行编码后得到加密后的串。</li>
     * </ol>
     * @param key sec-websocket-key
     * @return sec-websocket-accept
     */
    protected String computeWebSocketAccept(String key) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest=MessageDigest.getInstance("sha1");
        digest.update((key+MAGIC_GUID).getBytes("utf-8"));
        BASE64Encoder encoder=new BASE64Encoder();
        return  encoder.encode(digest.digest());
    }
    @Override
    protected void verify(FullHttpResponse response) {
        final HttpResponseStatus status = HttpResponseStatus.SWITCHING_PROTOCOLS;
        final HttpHeaders headers = response.headers();

        if (!response.status().equals(status)) {
            throw new WebSocketHandshakeException("Invalid handshake response getStatus: " + response.status());
        }

        CharSequence upgrade = headers.get(HttpHeaderNames.UPGRADE);
        if (!HttpHeaderValues.WEBSOCKET.contentEqualsIgnoreCase(upgrade)) {
            throw new WebSocketHandshakeException("Invalid handshake response upgrade: " + upgrade);
        }

        if (!headers.containsValue(HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE, true)) {
            throw new WebSocketHandshakeException("Invalid handshake response connection: "
                    + headers.get(HttpHeaderNames.CONNECTION));
        }

        CharSequence accept = headers.get(HttpHeaderNames.SEC_WEBSOCKET_ACCEPT);
        //验证没有通过暂时关闭
        if (accept == null || !accept.equals(computeWebSocketAccept)) {
            throw new WebSocketHandshakeException(String.format(
                    "Invalid challenge. Actual: %s. Expected: %s", accept, computeWebSocketAccept));
        }

    }
}
