package com.nettyserver.org.service.impl;

import com.nettyserver.org.config.SqlConfig;
import com.nettyserver.org.dao.ServerFileMapper;
import com.nettyserver.org.dao.ServerUserMapper;
import com.nettyserver.org.pojo.ServerFile;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.FileRegion;
import io.netty.handler.codec.http.*;
import org.apache.ibatis.session.SqlSession;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.Callable;

/**
 * @author SuanCaiYv
 * @time 2020/2/18 下午11:13
 */
public class FileDownload implements Callable<Void>
{
    private static SqlSession sqlSession = SqlConfig.sqlSession();

    private static ServerUserMapper serverUserMapper = (ServerUserMapper) SqlConfig.getMapperImpl(ServerUserMapper.class);

    private static ServerFileMapper serverFileMapper = (ServerFileMapper) SqlConfig.getMapperImpl(ServerFileMapper.class);
    /**
     * 用于写出回复的"全局管家"
     */
    private ChannelHandlerContext ctx;
    /**
     * 请求参数键值对
     */
    private HashMap<String, String> parameters;

    public FileDownload(ChannelHandlerContext ctx, HashMap<String, String> parameters)
    {
        this.ctx = ctx;
        this.parameters = parameters;
    }

    /**
     * 有一点, 进行回复写出时, 因为已经不再管道里, 所以必须手动flush(), 不然无法刷出管道流
     * @return NA
     */
    @Override
    public synchronized Void call()
    {
        String fileUuid = parameters.get("fileUuid");
        ServerFile serverFile = serverFileMapper.selectByFileUuid(fileUuid);
        File file = new File(serverFile.getFilePath());
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        assert inputStream != null;
        FileRegion fileRegion = new DefaultFileRegion(inputStream.getChannel(), 0, file.length());
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        String fileName = serverFile.getFileName();
        response.headers().set("Content-Disposition", "attachment; filename="+ URLEncoder.encode(fileName, StandardCharsets.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, file.length());
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.FILE);
        response.headers().set(HttpHeaderNames.CONTENT_ENCODING, StandardCharsets.UTF_8);
        response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.headers().set(HttpHeaderNames.CACHE_CONTROL, HttpHeaderValues.NO_CACHE);
        ctx.write(response);
        ctx.write(fileRegion);
        ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        return null;
    }
}
