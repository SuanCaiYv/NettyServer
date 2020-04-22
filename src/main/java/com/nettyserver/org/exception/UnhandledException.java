package com.nettyserver.org.exception;

/**
 * @author SuanCaiYv
 * @time 2020/2/16 下午1:28
 */
public class UnhandledException extends Exception
{
    private String message;

    public UnhandledException(String message)
    {
        this.message = message;
    }

    public UnhandledException()
    {
        ;
    }

    @Override
    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}
