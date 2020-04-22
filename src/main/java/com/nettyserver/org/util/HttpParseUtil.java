package com.nettyserver.org.util;

import io.netty.handler.codec.http.HttpRequest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * @author SuanCaiYv
 * @time 2020/2/13 下午10:57
 */
public class HttpParseUtil
{
    public static final String FILE = "multipart/form-data";
    public static final String HTML = "text/html";
    public static final String TEXT = "text/plain";
    public static final String XML = "text/xml";
    public static final String XML_DATA = "application/xml";
    public static final String JSON = "application/json";
    public static final String FORM = "application/x-www-form-urlencoded";
    public static final String NONE = "";


    /**
     * 只是对uri处理, 不包括请求体里面的参数
     * @param request NA
     * @return NA
     */
    public static HashMap<String, String> getParameters(HttpRequest request)
    {
        String uri = request.uri();
        HashMap<String, String> map = new HashMap<>();
        // 处理uri里面的参数
        int fromIndex = uri.indexOf("?");
        int lastIndex = uri.indexOf("/");
        // 添加路径参数
        if (lastIndex != -1) {
            map.put("_value", uri.substring(lastIndex+1));
        }
        // 获取uri里面的键值对
        if (fromIndex != -1) {
            String tempStr = uri.substring(fromIndex+1);
            String[] kVs = tempStr.split("&");
            for (String kV : kVs) {
                String[] strs = kV.split("=");
                map.put(strs[0], strs[1]);
            }
        }
        return map;
    }

    /**
     * 获取更新的uri
     * @param request NA
     * @return NA
     */
    public static String getUri(HttpRequest request)
    {
        String uri = request.uri();
        int index = uri.indexOf("?");
        if (index != -1) {
            uri = uri.substring(0, index);
        }
        return uri;
    }
    /**
     * 在网上扒的, 一个根据请求体获取文件名的代码段, 很秀了~
     * 解释, 因为请求体的组成格式是: 信息-文件-信息, 而信息和文件之间的格式是固定的(其实是有四个'\r'), 所以根据这一特性, 就能解析出信息
     * @param inputStream 根据请求体构建的输入流
     * @return 文件名
     * @throws IOException NA
     */
    public static String getFileName(InputStream inputStream) throws IOException
    {
        StringBuilder sb = new StringBuilder();
        int count = 0;
        // 不是读到尾, 读到四个'\r'就行了
        while (true) {
            int a = inputStream.read();
            sb.append((char) a);
            if (a == '\r') {
                count++;
            }
            if (count == 4) {
                a = inputStream.read();
                break;
            }
        }
        // 此时已经解析出了请求体里的信息
        String title = sb.toString();
        // 处理乱码
        title = new String(title.getBytes(), StandardCharsets.UTF_8);
        // 分割
        String[] titles = title.split("\r\n");
        // 获取文件名
        String fileName = titles[1].split(";")[2].split("=")[1].replace("\"", "");
        return fileName;
    }
}
