package xyz.chener.zp.common.entity;


import xyz.chener.zp.common.utils.MapBuilder;

import java.io.Serializable;
import java.util.Map;

public class R<T> implements Serializable {

    private int code;

    private String message;
    private T obj;

    public static class Builder<T>{

        protected R<T> r;

        public static <T> Builder<T> getInstance()
        {
            Builder<T> bd = new Builder<>();
            bd.r = new R<>();
            return bd;
        }

        public Builder<T> setCode(int code)
        {
            r.setCode(code);
            return this;
        }

        public Builder<T> setMessage(String message)
        {
            r.setMessage(message);
            return this;
        }

        public Builder<T> setObj(T obj)
        {
            r.setObj(obj);
            return this;
        }

        public R<T> build()
        {
            return r;
        }

    }

    public enum HttpCode{
        HTTP_OK(200),HTTP_ERR(500),HTTP_PAGE_NOT_FOND(404),BAD_REQUEST(400),METHOD_ERROR(405)
        ,HTTP_NO_LOGIN(401)
        ,HTTP_NO_ACCESS(403)
        ,HTTP_SERVER_MISS(503)
        ,HTTP_LIMIT(429)
        ,HTTP_NOT_ACCEPTABLE(406);
        private final int code;
        private HttpCode (int code)
        {
            this.code = code;
        }

        public int get(){
            return code;
        }

    }


    public enum ErrorMessage{
        HTTP_OK("success")
        ,HTTP_ERR("unknown error")
        ,HTTP_PAGE_NOT_FOND("page not found!")
        ,SQL_RUN_ERROR("执行出错")
        ,BAD_REQUEST("params error")
        ,METHOD_ERROR("method not allow")
        ,FEIGN_DECODER_ERROR("openfeign result decoder prase class error,type name is null")
        ,HTTP_NO_LOGIN("no login")
        ,HTTP_SERVER_MISS("Service downtime")
        ,HTTP_LIMIT("too many request")
        ,HTTP_IP_BAN("ip ban")
        ,HTTP_NO_ACCESS("NO ACCESS")
        ,HTTP_NOT_ACCEPTABLE("参数解析错误或者不被允许");
        private final String message;
        private ErrorMessage (String message)
        {
            this.message = message;
        }

        public String get(){
            return message;
        }
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }

    public Map<String,Object> toMap()
    {
        return MapBuilder.<String,Object>getInstance()
                .add("code",code)
                .add("message",message)
                .add("obj",obj).build();
    }

}