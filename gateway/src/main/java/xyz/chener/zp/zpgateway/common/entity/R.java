package xyz.chener.zp.zpgateway.common.entity;


import xyz.chener.zp.zpgateway.common.utils.MapBuilder;

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
        HTTP_OK(200)
        ,HTTP_ERR(500)
        ,HTTP_PAGE_NOT_FOND(404)
        ,BAD_REQUEST(400)
        ,METHOD_ERROR(405)
        ,HTTP_NO_LOGIN(401)
        ,HTTP_NO_ACCESS(403)
        ,HTTP_SERVER_MISS(503)
        ,HTTP_LIMIT(429)
        ,HTTP_NOT_ACCEPTABLE(406)
        ,HTTP_RETRY(449)
        ,HTTP_2FA_NOT_AUTH(451)
        ,HTTP_2FA__AUTH_FAIL(452);
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
        HTTP_OK("成功")
        ,HTTP_ERR("服务器错误")
        ,HTTP_PAGE_NOT_FOND("页面未找到")
        ,SQL_RUN_ERROR("执行出错")
        ,BAD_REQUEST("参数错误")
        ,METHOD_ERROR("请求方法不被允许")
        ,FEIGN_DECODER_ERROR("OpenFeign结果转换出错")
        ,HTTP_NO_LOGIN("登录已过期")
        ,HTTP_SERVER_MISS("服务未上线")
        ,HTTP_LIMIT("请求过多")
        ,HTTP_IP_BAN("IP被禁止")
        ,HTTP_NO_ACCESS("没有权限")
        ,HTTP_NOT_ACCEPTABLE("参数解析错误或者不被允许")
        ,HTTP_RETRY("此次执行异常,请重试")
        ,HTTP_2FA_NOT_AUTH("未进行2FA认证")
        ,HTTP_2FA__AUTH_FAIL("2FA认证失败");
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