package com.nettyserver.org.dao;

import com.nettyserver.org.pojo.ServerUser;

public interface ServerUserMapper {
    int deleteByPrimaryKey(String email);

    int insert(ServerUser record);

    ServerUser selectByEmail(String email);

    int updateByUserEmail(ServerUser record);
}