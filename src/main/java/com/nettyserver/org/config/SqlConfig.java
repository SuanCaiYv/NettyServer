package com.nettyserver.org.config;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;

/**
 * @author SuanCaiYv
 * @time 2020/2/13 下午2:11
 */
public class SqlConfig
{

    private static SqlSessionFactory sqlSessionFactory;

    private static SqlSession sqlSession;

    static {
        String resource = "mybatis-config.xml";
        Reader reader = null;
        try {
            reader = Resources.getResourceAsReader(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        sqlSession = sqlSessionFactory.openSession();
    }

    public static SqlSessionFactory sqlSessionFactory()
    {
        return sqlSessionFactory;
    }

    public static Object getMapperImpl(Class<?> mapperClass)
    {
        return sqlSession.getMapper(mapperClass);
    }

    public static SqlSession sqlSession()
    {
        return sqlSession;
    }

    public static void load()
    {
        sqlSessionFactory();
        sqlSession();
    }
}
