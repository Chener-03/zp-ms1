package xyz.chener.zp.zpusermodule.ws;

import jakarta.websocket.Session;
import xyz.chener.zp.common.config.ctx.ApplicationContextHolder;
import xyz.chener.zp.common.entity.LoginUserDetails;
import xyz.chener.zp.common.utils.Jwt;
import xyz.chener.zp.common.utils.ObjectUtils;
import xyz.chener.zp.zpusermodule.ws.entity.WsMessage;
import xyz.chener.zp.zpusermodule.ws.queue.ConnectQueueManager;

import java.util.concurrent.atomic.AtomicBoolean;

public class WsMessageProcesser {

    public static void checkMessageLegal(WsMessage message, Session session) {
        if (message.getJwt() == null) {
            WsConnector.close(session);
            return;
        }
        Jwt jwt = ApplicationContextHolder.getApplicationContext().getBean(Jwt.class);
        LoginUserDetails u = jwt.decode(message.getJwt());
        if (u == null) {
            WsConnector.close(session);
            return;
        }
        message.setUsername(u.getUsername());
    }

    public static void heartBeat(WsMessage message, Session session) {
        System.out.println("heartBeat："+session.getId());
        ConnectQueueManager.getInstance().renewal(session.getId(), message.getUsername());
        WsConnector.sendObject(message, session.getId());
    }

    public static void sendAll(WsMessage message) {
        ConnectQueueManager.getInstance().getValidConnection().forEach(wsConnect -> {
            WsConnector.sendObject(message, wsConnect.getConnect_uid());
        });
    }

    public static boolean sendUser(WsMessage message, String username) {
        AtomicBoolean rt = new AtomicBoolean(false);
        ConnectQueueManager.getInstance().getValidConnection().forEach(wsc -> {
            if(ObjectUtils.nullSafeEquals(wsc.getConnect_user(),username)){
                WsConnector.sendObject(message, wsc.getConnect_uid());
                rt.set(true);
            }
        });
        return rt.get();
    }


}
