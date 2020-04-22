package com.nettyserver.org.service;

import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

/**
 * @author SuanCaiYv
 * @time 2020/2/14 下午3:53
 */
public interface HttpRequestService
{
    /**
     * 处理"散装"HttpRequest请求, 注意, 包括写回HttpResponse
     * 但是真实的逻辑处理依旧分散给各个service
     * @param ctx NA
     * @param msg NA
     * @throws IOException NA
     */
    void dealHttpRequest(ChannelHandlerContext ctx, Object msg) throws Exception;
}
