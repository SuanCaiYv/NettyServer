package com.nettyserver.org.service.impl;

import com.alibaba.fastjson.JSON;
import com.nettyserver.org.config.SqlConfig;
import com.nettyserver.org.dao.ServerFileMapper;
import com.nettyserver.org.dao.ServerUserMapper;
import com.nettyserver.org.pojo.ServerFile;
import com.nettyserver.org.pojo.ServerUser;
import com.nettyserver.org.result.ResultBean;
import com.nettyserver.org.system.SystemConstant;
import com.nettyserver.org.util.BaseUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;
import org.apache.ibatis.session.SqlSession;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Callable;

/**
 * @author SuanCaiYv
 * @time 2020/2/18 下午11:01
 */
public class FileSave implements Callable<Void>
{

    private static SqlSession sqlSession = SqlConfig.sqlSession();

    private static ServerUserMapper serverUserMapper = (ServerUserMapper) SqlConfig.getMapperImpl(ServerUserMapper.class);

    private static ServerFileMapper serverFileMapper = (ServerFileMapper) SqlConfig.getMapperImpl(ServerFileMapper.class);
    /**
     * 保存了分散开的文件部分
     */
    private LinkedList<HttpContent> httpContents;
    /**
     * 请求参数键值对
     */
    private HashMap<String, String> parameters;
    /**
     * 分隔符
     * 前分隔符为: --+boundary
     * 后分隔符为: boundary+--
     */
    private final String boundary;
    /**
     * 负责写出的"全局管家"
     */
    private final ChannelHandlerContext ctx;
    /**
     * 文件相关的参数
     */
    private File file = null;
    private FileInputStream fileInputStream = null;
    private FileOutputStream fileOutputStream = null;

    public FileSave(LinkedList<HttpContent> httpContents, HashMap<String, String> parameters, String boundary, ChannelHandlerContext ctx)
    {
        this.httpContents = httpContents;
        this.parameters = parameters;
        this.boundary = boundary;
        this.ctx = ctx;
        this.file = null;
        this.fileInputStream = null;
        this.fileOutputStream = null;
    }

    /**
     * @return NA
     * @throws Exception NA
     */
    @Override
    public Void call() throws Exception
    {
        if (httpContents.size() == 1) {
            HttpContent firstContent = httpContents.getFirst();
            httpContents.removeFirst();
            byte[] bytesFirst = new byte[firstContent.content().readableBytes()];
            firstContent.content().readBytes(bytesFirst);
            ReferenceCountUtil.release(firstContent);
            InputStream inputStream = new ByteArrayInputStream(bytesFirst);
            // 去除头部分隔符和提取出文件名
            StringBuilder stringBuilder = new StringBuilder();
            int num = 0;
            int cnt = 0;
            while (true) {
                int a = inputStream.read();
                char c = (char) a;
                stringBuilder.append(c);
                if (a == '\r') {
                    ++cnt;
                }
                if (cnt == 4) {
                    a = inputStream.read();
                    break;
                }
                ++num;
            }
            String title = stringBuilder.toString();
            title = new String(title.getBytes(StandardCharsets.ISO_8859_1));
            String[] titles = title.split("\r\n");
            String fileName = titles[1].split(";")[2].split("=")[1].replace("\"", "");
            file = BaseUtil.forceCreateFile(SystemConstant.BASE_PATH + fileName);
            fileOutputStream = new FileOutputStream(file);
            // +2是因为还有'\r\n'这两个字符
            fileOutputStream.write(bytesFirst, num + 2, bytesFirst.length - boundary.length() - num - 2 - 5);
            fileOutputStream.flush();
        }
        else {
            HttpContent firstContent = httpContents.getFirst();
            httpContents.removeFirst();
            byte[] bytesFirst = new byte[firstContent.content().readableBytes()];
            firstContent.content().readBytes(bytesFirst);
            ReferenceCountUtil.release(firstContent);
            InputStream inputStream = new ByteArrayInputStream(bytesFirst);
            // 去除头部分隔符和提取出文件名
            StringBuilder stringBuilder = new StringBuilder();
            int num = 0;
            int cnt = 0;
            while (true) {
                int a = inputStream.read();
                char c = (char) a;
                stringBuilder.append(c);
                if (a == '\r') {
                    ++cnt;
                }
                if (cnt == 4) {
                    a = inputStream.read();
                    break;
                }
                ++num;
            }
            String title = stringBuilder.toString();
            title = new String(title.getBytes(StandardCharsets.ISO_8859_1));
            String[] titles = title.split("\r\n");
            String fileName = titles[1].split(";")[2].split("=")[1].replace("\"", "");
            file = BaseUtil.forceCreateFile(SystemConstant.BASE_PATH + fileName);
            fileOutputStream = new FileOutputStream(file);
            // +2是因为还有'\r\n'这两个字符
            fileOutputStream.write(bytesFirst, num + 2, bytesFirst.length - num - 2);
            HttpContent lastContent = httpContents.getLast();
            httpContents.removeLast();
            byte[] bytesLast = new byte[lastContent.content().readableBytes()];
            lastContent.content().readBytes(bytesLast);
            ReferenceCountUtil.release(lastContent);
            while (httpContents.size() > 0) {
                HttpContent httpContent = httpContents.getFirst();
                byte[] bytes = new byte[httpContent.content().readableBytes()];
                httpContent.content().readBytes(bytes);
                fileOutputStream.write(bytes, 0, bytes.length);
                httpContents.removeFirst();
                ReferenceCountUtil.release(httpContent);
            }
            // -5是因为, 有两个'\r\n'和一个'\r'一共五个不可见字符也要算进去
            fileOutputStream.write(bytesLast, 0, bytesLast.length - boundary.length() - 5);
            fileOutputStream.flush();
        }
        f();
        return null;
    }

    /**
     * 在这里, 同样要记得flush()数据, 不然会接收不到
     */
    public void f()
    {
        String uuid = null;
        String fileName = file.getName();
        String masterEmail = parameters.get("email");
        ServerFile serverFile = serverFileMapper.selectByFileNameAndEmail(fileName, masterEmail);
        if (serverFile != null) {
            long createTime = System.currentTimeMillis();
            serverFile.setCreateTime(createTime);
            serverFileMapper.updateByFileUuid(serverFile);
            sqlSession.commit();
            uuid = serverFile.getFileUuid();
        }
        else {
            String fileUuid = BaseUtil.getUuid();
            long createTime = System.currentTimeMillis();
            String filePath = file.getPath();
            serverFile = new ServerFile();
            serverFile.setFileName(fileName);
            serverFile.setFileUuid(fileUuid);
            serverFile.setFilePath(filePath);
            serverFile.setCreateTime(createTime);
            serverFile.setMasterEmail(masterEmail);
            serverFileMapper.insertServerFile(serverFile);
            sqlSession.commit();
            ServerUser serverUser = serverUserMapper.selectByEmail(masterEmail);
            String str = serverUser.getServerFiles();
            str += fileUuid + ",";
            serverUser.setServerFiles(str);
            serverUserMapper.updateByUserEmail(serverUser);
            sqlSession.commit();
            uuid = fileUuid;
        }
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        ResultBean<String> resultBean = new ResultBean<>("File UUID");
        resultBean.setData(uuid);
        String jsonData = JSON.toJSONString(resultBean);
        ByteBuf out = Unpooled.copiedBuffer(jsonData.getBytes());
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, out.readableBytes());
        response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.headers().set(HttpHeaderNames.CACHE_CONTROL, HttpHeaderValues.NO_CACHE);
        response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, "*");
        ctx.write(response);
        ctx.write(out);
        ctx.writeAndFlush(FullHttpResponse.EMPTY_LAST_CONTENT);
    }
}
