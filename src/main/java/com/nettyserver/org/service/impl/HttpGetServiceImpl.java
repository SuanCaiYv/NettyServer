package com.nettyserver.org.service.impl;

import com.nettyserver.org.config.ThreadPool;
import com.nettyserver.org.service.HttpGetService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * @author SuanCaiYv
 * @time 2020/2/23 下午11:10
 */
public class HttpGetServiceImpl implements HttpGetService
{
    private String fileName;

    private static EventExecutorGroup executors = ThreadPool.getExecutors();

    @Override
    public void getHtml(ChannelHandlerContext ctx, Object msg)
    {
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            String uri = request.uri();
            fileName = uri.substring(uri.lastIndexOf("/")+1);
        }
        else {
            if (msg instanceof LastHttpContent) {
                getHtml(ctx, fileName);
            }
        }
    }

    @Override
    public void getHtml(ChannelHandlerContext ctx, String fileName)
    {
        executors.submit(new HtmlDownload(ctx, fileName));
    }
}
