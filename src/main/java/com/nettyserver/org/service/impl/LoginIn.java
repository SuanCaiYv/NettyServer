package com.nettyserver.org.service.impl;

import com.alibaba.fastjson.JSON;
import com.nettyserver.org.config.SqlConfig;
import com.nettyserver.org.dao.ServerFileMapper;
import com.nettyserver.org.dao.ServerUserMapper;
import com.nettyserver.org.pojo.ServerUser;
import com.nettyserver.org.result.ResultBean;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.apache.ibatis.session.SqlSession;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author SuanCaiYv
 * @time 2020/2/18 下午10:30
 */
public class LoginIn implements Callable<Void>
{
    /**
     * dao组件
     */

    private static SqlSession sqlSession = SqlConfig.sqlSession();

    private static ServerFileMapper serverFileMapper = (ServerFileMapper) SqlConfig.getMapperImpl(ServerFileMapper.class);

    private static ServerUserMapper serverUserMapper = (ServerUserMapper) SqlConfig.getMapperImpl(ServerUserMapper.class);
    /**
     * 入参请求键值对
     */
    private HashMap<String, String> parameters;
    /**
     * "全局管家"
     */
    private ChannelHandlerContext ctx;

    public LoginIn(ChannelHandlerContext ctx, HashMap<String, String> parameters)
    {
        this.ctx = ctx;
        this.parameters = parameters;
    }

    /**
     * 由于编码问题, 需要手动对'@'符号进行转义
     * @return NA
     */
    @Override
    public Void call()
    {
        String email = parameters.get("email").replace("%40", "@");
        String password = parameters.get("password");
        ResultBean<ServerUser> resultBean = null;
        int opt = doCheck(email, password);
        if (opt == 2) {
            resultBean = new ResultBean<>("No User");
            resultBean.setCode(ResultBean.NO_USER);
        }
        else if (opt == 3) {
            resultBean = new ResultBean<>("Error Password");
            resultBean.setCode(ResultBean.PASSWD_ERROR);
        }
        else if (opt == 0) {
            resultBean = new ResultBean<>();
            ServerUser serverUser = serverUserMapper.selectByEmail(email);
            StringBuilder stringBuilder = new StringBuilder();
            String[] fileUuids = serverUserMapper.selectByEmail(email).getServerFiles().trim().split(",");
            if (!"".equals(fileUuids[0])) {
                for (String fileUuid : fileUuids) {
                    stringBuilder.append(serverFileMapper.selectByFileUuid(fileUuid).getFileName()).append(",");
                }
            }
            serverUser.setFileNames(stringBuilder.toString());
            resultBean.setData(serverUser);
        }
        String jsonData = JSON.toJSONString(resultBean);
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        ByteBuf out = Unpooled.copiedBuffer(jsonData.getBytes(StandardCharsets.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, out.readableBytes());
        response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.headers().set(HttpHeaderNames.CACHE_CONTROL, HttpHeaderValues.NO_CACHE);
        response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, "*");
        ctx.write(response);
        ctx.write(out);
        ctx.writeAndFlush(FullHttpResponse.EMPTY_LAST_CONTENT);
        return null;
    }


    /**
     * 验证登录
     * @param email 邮箱
     * @param password 密码
     * @return NA
     */
    private int doCheck(String email, String password)
    {
        ServerUser serverUser = serverUserMapper.selectByEmail(email);
        if (serverUser == null) {
            return 2;
        }
        else if (!serverUser.getPassword().equals(password)) {
            return 3;
        }
        else {
            return 0;
        }
    }
}
