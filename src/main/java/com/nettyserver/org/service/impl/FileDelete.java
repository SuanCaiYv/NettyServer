package com.nettyserver.org.service.impl;

import com.nettyserver.org.config.SqlConfig;
import com.nettyserver.org.dao.ServerFileMapper;
import com.nettyserver.org.dao.ServerUserMapper;
import com.nettyserver.org.pojo.ServerFile;
import com.nettyserver.org.pojo.ServerUser;
import org.apache.ibatis.session.SqlSession;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.concurrent.Callable;

/**
 * @author SuanCaiYv
 * @time 2020/2/18 下午11:19
 */
public class FileDelete implements Callable<Void>
{
    /**
     * dao组件
     */

    private static SqlSession sqlSession = SqlConfig.sqlSession();

    private static ServerUserMapper serverUserMapper = (ServerUserMapper) SqlConfig.getMapperImpl(ServerUserMapper.class);

    private static ServerFileMapper serverFileMapper = (ServerFileMapper) SqlConfig.getMapperImpl(ServerFileMapper.class);
    /**
     * 请求参数的键值对
     */
    private HashMap<String, String> parameters;

    public FileDelete(HashMap<String, String> parameters)
    {
        this.parameters = parameters;
    }

    /**
     * 删除文件还要记得删除数据库中的数据, 并更新数据库
     * @return NA
     * @throws Exception NA
     */
    @Override
    public Void call() throws Exception
    {
        String fileUuid = parameters.get("fileUuid");
        ServerFile serverFile = serverFileMapper.selectByFileUuid(fileUuid);
        ServerUser serverUser = serverUserMapper.selectByEmail(serverFile.getMasterEmail());
        serverUser.setServerFiles(serverUser.getServerFiles().replace(fileUuid+",", ""));
        serverUserMapper.updateByUserEmail(serverUser);
        sqlSession.commit();
        serverFileMapper.deleteByFileUuid(fileUuid);
        sqlSession.commit();
        Path path = Paths.get(serverFile.getFilePath());
        Files.delete(path);
        return null;
    }
}
