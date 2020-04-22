package com.nettyserver.org.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;

/**
 * @author SuanCaiYv
 * @time 2020/2/13 下午2:36
 */
public class NettyServer
{
    private static Logger logger = LoggerFactory.getLogger(NettyServer.class);
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static void main(String[] args) throws InterruptedException
    {
        new NettyServer().run();
    }
    public void run() throws InterruptedException
    {
        EventLoopGroup bossGroup = new EpollEventLoopGroup();
        EventLoopGroup workGroup = new EpollEventLoopGroup(2);
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workGroup)
                .localAddress(new InetSocketAddress(8190))
                .channel(EpollServerSocketChannel.class)
                .childHandler(new ServerChannelInitializer());
        ChannelFuture channelFuture = serverBootstrap.bind().sync();
        long curr = System.currentTimeMillis();
        String time = format.format(curr);
        if (channelFuture.isSuccess()) {
            logger.info(time+": 绑定到本地端口");
        }
        else {
            logger.error(time+": 绑定到本地端口失败");
        }
    }
}
