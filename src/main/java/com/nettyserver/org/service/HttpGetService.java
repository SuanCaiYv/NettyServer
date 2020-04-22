package com.nettyserver.org.service;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author SuanCaiYv
 * @time 2020/2/23 下午11:08
 */
public interface HttpGetService
{
    /**
     * 完成html界面的下载
     * @param ctx NA
     * @param msg NA
     */
    void getHtml(ChannelHandlerContext ctx, Object msg);
    void getHtml(ChannelHandlerContext ctx, String fileName);
}
