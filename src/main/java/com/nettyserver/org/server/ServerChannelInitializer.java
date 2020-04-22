package com.nettyserver.org.server;

import com.nettyserver.org.handler.HttpRequestHandler;
import com.nettyserver.org.handler.LastIn;
import com.nettyserver.org.handler.LastOut;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @author SuanCaiYv
 * @time 2020/2/14 上午12:19
 */
public class ServerChannelInitializer extends ChannelInitializer<SocketChannel>
{
    @Override
    protected void initChannel(SocketChannel ch) throws Exception
    {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new LastOut());
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpRequestHandler());
        // pipeline.addLast(new LastIn());
    }
}
