package com.nettyserver.org.config;

import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * @author SuanCaiYv
 * @time 2020/2/17 下午6:20
 */
public class ThreadPool
{
    private static final EventExecutorGroup executors;
    static {
        executors = new DefaultEventExecutorGroup(12);
    }
    public static EventExecutorGroup getExecutors()
    {
        return executors;
    }
    public static void load()
    {
        getExecutors();
    }
}
