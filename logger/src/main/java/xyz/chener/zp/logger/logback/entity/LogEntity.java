package xyz.chener.zp.logger.logback.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * @Author: chenzp
 * @Date: 2023/03/16/11:07
 * @Email: chen@chener.xyz
 */
public class LogEntity {

    /**
     *         &quot;time&quot;:&quot;%d{yyyy-MM-dd HH:mm:ss.SSS}&quot;,
     *         &quot;tid&quot;:&quot;%tid&quot;,
     *         &quot;sId&quot;:&quot;%sId&quot;,
     *         &quot;iId&quot;:&quot;%iId&quot;,
     *         &quot;level&quot;:&quot;%level&quot;,
     *         &quot;thread&quot;:&quot;%t&quot;,
     *         &quot;logger&quot;:&quot;%logger&quot;,
     *         &quot;message&quot;:&quot;%msg&quot;
     */

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "GMT+8")
    private Date time;

    private String tid;

    private String sId;

    private String iId;

    private String level;

    private String thread;

    private String logger;

    private String message;

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getsId() {
        return sId;
    }

    public void setsId(String sId) {
        this.sId = sId;
    }

    public String getiId() {
        return iId;
    }

    public void setiId(String iId) {
        this.iId = iId;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getThread() {
        return thread;
    }

    public void setThread(String thread) {
        this.thread = thread;
    }

    public String getLogger() {
        return logger;
    }

    public void setLogger(String logger) {
        this.logger = logger;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
