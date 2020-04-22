package com.nettyserver.org.result;

/**
 * @author SuanCaiYv
 * @time 2020/2/13 下午6:09
 */
public class ResultBean<T>
{
    public static final int SUCCESS = 0;
    public static final int FAIL = 1;
    public static final int OTHER = -1;
    public static final int NO_USER = 2;
    public static final int PASSWD_ERROR = 3;
    private String msg = "success";
    private int code = SUCCESS;
    private T data;

    public ResultBean(String msg)
    {
        this.msg = msg;
    }

    public ResultBean()
    {
    }

    public ResultBean(String msg, int code, T data)
    {
        this.msg = msg;
        this.code = code;
        this.data = data;
    }

    public String getMsg()
    {
        return msg;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }

    public int getCode()
    {
        return code;
    }

    public void setCode(int code)
    {
        this.code = code;
    }

    public T getData()
    {
        return data;
    }

    public void setData(T data)
    {
        this.data = data;
    }
}
