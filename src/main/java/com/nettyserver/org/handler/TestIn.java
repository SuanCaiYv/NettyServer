package com.nettyserver.org.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpContent;

/**
 * @author SuanCaiYv
 * @time 2020/2/14 上午12:13
 */
public class TestIn extends ChannelInboundHandlerAdapter
{
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {
        if (msg instanceof HttpContent) {
            ByteBuf byteBuf = ((HttpContent) msg).content().duplicate();
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);
            String str = new String(bytes);
            System.out.println(str);
        }
    }
}
