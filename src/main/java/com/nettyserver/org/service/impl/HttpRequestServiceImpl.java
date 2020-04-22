package com.nettyserver.org.service.impl;

import com.alibaba.fastjson.JSON;
import com.nettyserver.org.result.ResultBean;
import com.nettyserver.org.service.HttpFileService;
import com.nettyserver.org.service.HttpFormService;
import com.nettyserver.org.service.HttpGetService;
import com.nettyserver.org.service.HttpRequestService;
import com.nettyserver.org.util.HttpParseUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.nio.charset.StandardCharsets;

/**
 * @author SuanCaiYv
 * @time 2020/2/14 下午3:54
 */
public class HttpRequestServiceImpl implements HttpRequestService
{
    /**
     * 请求方法
     */
    private HttpMethod method;
    /**
     * 请求的uri
     */
    private String uri;
    /**
     * 请求头
     */
    private HttpHeaders headers;

    private HttpVersion version;

    private static final HttpFileService fileService = new HttpFileServiceImpl();

    private static final HttpFormService formService = new HttpFormServiceImpl();

    private static final HttpGetService getService = new HttpGetServiceImpl();

    /**
     * 请求类型
     */
    private static int OPTION = 0;
    private static final int FORM = 0;
    private static final int FILE = 1;
    private static final int JSONDATA = 2;
    private static final int NONE = 3;
    private static final int GET = 4;
    @Override
    public void dealHttpRequest(ChannelHandlerContext ctx, Object msg) throws Exception
    {
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            method = request.method();
            uri = HttpParseUtil.getUri(request);
            headers = request.headers();
            version = request.protocolVersion();
            judgeType();
        }
        dispatcher(ctx, msg);
    }

    /**
     * 做分发处理
     */
    private void dispatcher(ChannelHandlerContext ctx, Object msg) throws Exception
    {
        if ("/index".equals(uri) || "/".equals(uri) || "".equals(uri)) {
            getService.getHtml(ctx, "index.html");
        }
        else if ("/loginIn".equals(uri)) {
            formService.loginIn(ctx, msg);
        }
        else if ("/file/upload".equals(uri) && OPTION == FILE) {
            fileService.saveFileContinually(ctx, msg);
        }
        else if ("/file/download".equals(uri)) {
            fileService.downloadFile(ctx, msg);
        }
        else if ("/file/delete".equals(uri)) {
            fileService.deleteFile(ctx, msg);
        }
        else if ("/favicon.ico".equals(uri)) {
            ctx.writeAndFlush("http://q1.qlogo.cn/g?b=qq&nk=2508826394&s=100");
        }
        else if (uri.contains(".html")) {
            getService.getHtml(ctx, msg);
        }
        else {
            doDefault(ctx, msg);
        }
    }

    /**
     * 判断请求类型及请求内容体的类型
     */
    private void judgeType()
    {
        if (method.equals(HttpMethod.POST)) {
            String type = headers.get("Content-Type");
            int tempIndex = type.indexOf(";");
            if (tempIndex != -1) {
                type = type.substring(0, tempIndex);
            }
            // 进行传输类型判断
            if (HttpParseUtil.FORM.equals(type)) {
                OPTION = FORM;
            }
            else if (HttpParseUtil.FILE.equals(type)) {
                OPTION = FILE;
            }
        }
        // GET方法没有请求体, 但是有LastHttpContent
        else if (HttpMethod.GET.equals(method)) {
            OPTION = GET;
        }
    }

    /**
     * 处理默认关系
     * @param ctx NA
     * @param msg NA
     */
    private void doDefault(ChannelHandlerContext ctx, Object msg)
    {
        HttpResponse response = new DefaultHttpResponse(version, HttpResponseStatus.OK);
        ResultBean<String> resultBean = new ResultBean<>("Default");
        resultBean.setCode(ResultBean.OTHER);
        resultBean.setData("you seem attach nowhere");
        String json = JSON.toJSONString(resultBean);
        ByteBuf byteBuf = Unpooled.copiedBuffer(json.getBytes(StandardCharsets.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        ctx.write(response);
        ctx.write(byteBuf);
        ctx.write(LastHttpContent.EMPTY_LAST_CONTENT);
    }

}
