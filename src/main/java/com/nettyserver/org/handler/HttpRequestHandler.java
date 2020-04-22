package com.nettyserver.org.handler;

import com.nettyserver.org.service.HttpRequestService;
import com.nettyserver.org.service.impl.HttpRequestServiceImpl;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * @author SuanCaiYv
 * @time 2020/2/14 上午12:20
 */
public class HttpRequestHandler extends ChannelInboundHandlerAdapter
{
    private static HttpRequestService requestService = new HttpRequestServiceImpl();
    private static Logger logger = LoggerFactory.getLogger(HttpRequestHandler.class);
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 不做业务处理, 只做拦截HttpRequest消息, 然后交给service处理
     * 不要随便的释放资源!!!!!!
     * @param ctx NA
     * @param msg NA
     * @throws Exception NA
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {
        long curr = System.currentTimeMillis();
        String time = format.format(curr);
        try {
            requestService.dealHttpRequest(ctx, msg);
            if (ReferenceCountUtil.refCnt(msg) > 0) {
                ReferenceCountUtil.release(msg);
            }
            logger.info("程序执行到: "+this.getClass().getName()+"channelRead");
        } catch (IOException e) {
            logger.error("程序在: "+this.getClass().getName()+".channelRead捕获异常, 为: "+e.getMessage());
        }
    }
}
