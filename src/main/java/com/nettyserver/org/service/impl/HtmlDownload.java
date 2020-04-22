package com.nettyserver.org.service.impl;

import com.alibaba.fastjson.JSON;
import com.nettyserver.org.result.ResultBean;
import com.nettyserver.org.system.SystemConstant;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.FileRegion;
import io.netty.handler.codec.http.*;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

/**
 * @author SuanCaiYv
 * @time 2020/2/23 下午11:14
 */
public class HtmlDownload implements Callable<Void>
{
    private ChannelHandlerContext ctx;
    private String fileName;

    public HtmlDownload(ChannelHandlerContext ctx, String fileName)
    {
        this.ctx = ctx;
        this.fileName = fileName;
    }

    @Override
    public Void call() throws Exception
    {
        File file = new File(SystemConstant.TEMPLATE_PATH + fileName);
        FileInputStream fileInputStream = new FileInputStream(file);
        FileRegion fileRegion = new DefaultFileRegion(fileInputStream.getChannel(), 0, file.length());
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, file.length());
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.FILE);
        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        ctx.write(response);
        ctx.write(fileRegion);
        ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        return null;
    }
}
