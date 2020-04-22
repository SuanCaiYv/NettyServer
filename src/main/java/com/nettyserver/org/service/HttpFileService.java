package com.nettyserver.org.service;

import com.nettyserver.org.exception.UnhandledException;
import io.netty.channel.ChannelHandlerContext;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 文件处理属于耗时操作, 应开辟新的线程处理
 * @author SuanCaiYv
 * @time 2020/2/16 下午1:18
 */
public interface HttpFileService
{
    /**
     * 进行文件上传处理, 因为请求体是分散的, 所以会多次调用此方法, 直到上传完成
     * @param ctx NA
     * @param msg NA
     * @throws UnhandledException NA
     * @throws IOException NA
     */
    void saveFileContinually(ChannelHandlerContext ctx, Object msg) throws UnhandledException, IOException;

    /**
     * 实现文件下载的逻辑
     * @param ctx NA
     * @param msg NA
     */
    void downloadFile(ChannelHandlerContext ctx, Object msg) throws FileNotFoundException;

    /**
     * 删除文件
     * @param ctx NA
     * @param msg NA
     */
    void deleteFile(ChannelHandlerContext ctx, Object msg);
}
