package com.nettyserver.org.service.impl;

import com.nettyserver.org.service.HttpFormService;
import com.nettyserver.org.util.HttpParseUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Future;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * @author SuanCaiYv
 * @time 2020/2/17 下午5:35
 */
public class HttpFormServiceImpl implements HttpFormService
{
    private HttpHeaders headers0;
    private HttpVersion version0;
    private HashMap<String, String> parameters0;
    private StringBuilder formParaStringBuilder0 = null;

    private static EventExecutorGroup executors = new DefaultEventExecutorGroup(12);

    @Override
    public void loginIn(ChannelHandlerContext ctx0, Object msg)
    {
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            headers0 = request.headers();
            version0 = request.protocolVersion();
            parameters0 = HttpParseUtil.getParameters(request);
            formParaStringBuilder0 = new StringBuilder();
        }
        else {
            if (msg instanceof LastHttpContent) {
                LastHttpContent content = (LastHttpContent) msg;
                ByteBuf byteBuf = content.duplicate().content();
                int readable = byteBuf.readableBytes();
                byte[] bytes = new byte[readable];
                byteBuf.readBytes(bytes);
                formParaStringBuilder0.append(new String(bytes, StandardCharsets.UTF_8));
                String string = formParaStringBuilder0.toString();
                String[] kVs = string.split("&");
                if (!"".equals(string)) {
                    for (String kV : kVs) {
                        String[] strs = kV.split("=");
                        parameters0.put(strs[0], strs[1]);
                    }
                }
                Future<Void> future = executors.submit(new LoginIn(ctx0, parameters0));
            }
            else {
                HttpContent content = (HttpContent) msg;
                ByteBuf byteBuf = content.duplicate().content();
                byte[] bytes = new byte[byteBuf.readableBytes()];
                byteBuf.readBytes(bytes);
                formParaStringBuilder0.append(new String(bytes, StandardCharsets.UTF_8));
            }
        }
    }
}
