package com.nettyserver.org.service;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author SuanCaiYv
 * @time 2020/2/17 下午5:31
 */
public interface HttpFormService
{
    /**
     * 登录验证
     * @param ctx NA
     * @param msg NA
     */
    void loginIn(ChannelHandlerContext ctx, Object msg);
}
