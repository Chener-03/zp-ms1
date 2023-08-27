package xyz.chener.zp.zpusermodule.ws.entity;

import jakarta.websocket.Session;

/**
 * @Author: chenzp
 * @Date: 2023/03/14/17:25
 * @Email: chen@chener.xyz
 */
public class WsClient {

    private String sessionId;
    private Session session;
    private String username;
    private String system;

    private String token;

    private String ip;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
