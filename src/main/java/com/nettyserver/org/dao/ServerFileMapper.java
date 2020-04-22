package com.nettyserver.org.dao;

import com.nettyserver.org.pojo.ServerFile;

public interface ServerFileMapper {
    int deleteByFileUuid(String fileUuid);

    int insertServerFile(ServerFile record);

    ServerFile selectByFileUuid(String fileUuid);

    int updateByFileUuid(ServerFile record);

    ServerFile selectByFileNameAndEmail(String name, String email);
}