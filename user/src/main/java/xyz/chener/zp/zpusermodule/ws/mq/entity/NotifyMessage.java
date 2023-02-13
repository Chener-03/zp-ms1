package xyz.chener.zp.zpusermodule.ws.mq.entity;

import java.io.Serializable;

/**
 * @Description: MQ页面通知类消息实体类
 */
public class NotifyMessage implements Serializable {

    private String type;

    private String user;

    private String title;

    private String content;

    public static class TYPE{
        public static final String ALL_USER = "allUser";
        public static final String ONE_USER = "oneUser";

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
