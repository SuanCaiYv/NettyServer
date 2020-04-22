package com.nettyserver.org.service.impl;

import com.alibaba.fastjson.JSON;
import com.nettyserver.org.config.ThreadPool;
import com.nettyserver.org.exception.UnhandledException;
import com.nettyserver.org.result.ResultBean;
import com.nettyserver.org.service.HttpFileService;
import com.nettyserver.org.system.SystemConstant;
import com.nettyserver.org.util.HttpParseUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author SuanCaiYv
 * @time 2020/2/16 下午1:22
 */
public class HttpFileServiceImpl implements HttpFileService
{
    private HttpHeaders headers0;
    private HttpVersion version0;
    private HashMap<String, String> parameters0;
    private StringBuilder formParaStringBuilder0 = null;
    private String boundary0;
    private LinkedList<HttpContent> httpContents = null;
    private FileSave fileSave;

    private static EventExecutorGroup executors = ThreadPool.getExecutors();

    /**
     * 此方法会被不同的调用, 直到读取一个完整的Http请求为止
     * @param ctx NA
     * @param msg 读取的HttpRequest块
     * @throws UnhandledException
     */
    @Override
    public void saveFileContinually(ChannelHandlerContext ctx, Object msg) throws UnhandledException
    {
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            headers0 = request.headers();
            version0 = request.protocolVersion();
            parameters0 = HttpParseUtil.getParameters(request);
            parameters0.put("File-Length", headers0.get("Content-Length"));
            String str = request.headers().toString();
            int index1 = str.indexOf("boundary=");
            int index2 = str.indexOf(",", index1);
            boundary0 = "--"+str.substring(index1+9, index2);
            httpContents = new LinkedList<>();
            fileSave = new FileSave(httpContents, parameters0, boundary0, ctx);
        }
        else {
            HttpContent content = (HttpContent) msg;
            ReferenceCountUtil.retain(content);
            httpContents.add(content);
            if (msg instanceof LastHttpContent) {
                if (httpContents.size() == 0) {
                    throw new UnhandledException("HttpRequest Exception");
                }
                executors.submit(fileSave);
            }
        }
    }

    /**
     * 实现文件下载逻辑
     * @param ctx NA
     * @param msg NA
     */
    @Override
    public void downloadFile(ChannelHandlerContext ctx, Object msg)
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
                // 开辟线程完成下载工作
                Future<Void> future = executors.submit(new FileDownload(ctx, parameters0));
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

    /**
     * 实现文件删除逻辑
     * @param ctx NA
     * @param msg NA
     */
    @Override
    public void deleteFile(ChannelHandlerContext ctx, Object msg)
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
                ByteBuf byteBuf = content.content();
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
                // 开辟线程去删除
                Future<Void> future = executors.submit(new FileDelete(parameters0));
                FutureListener<Void> futureListener = new FutureListener<Void>()
                {
                    @Override
                    public void operationComplete(Future<Void> future) throws Exception
                    {
                        HttpResponse response = new DefaultHttpResponse(version0, HttpResponseStatus.OK);
                        ResultBean<String> resultBean = new ResultBean<>("State");
                        resultBean.setData("Deleted");
                        String jsonData = JSON.toJSONString(resultBean);
                        ByteBuf out = Unpooled.copiedBuffer(jsonData.getBytes());
                        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
                        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, out.readableBytes());
                        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                        response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
                        response.headers().set(HttpHeaderNames.CACHE_CONTROL, HttpHeaderValues.NO_CACHE);
                        response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, "*");
                        ctx.write(response);
                        ctx.write(out);
                        ctx.writeAndFlush(FullHttpResponse.EMPTY_LAST_CONTENT);
                    }
                };
                future.addListener(futureListener);
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

