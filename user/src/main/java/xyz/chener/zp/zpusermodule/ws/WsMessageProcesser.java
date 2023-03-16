package xyz.chener.zp.zpusermodule.ws;

import jakarta.websocket.Session;
import xyz.chener.zp.common.config.ctx.ApplicationContextHolder;
import xyz.chener.zp.common.entity.LoginUserDetails;
import xyz.chener.zp.common.utils.Jwt;
import xyz.chener.zp.common.utils.ObjectUtils;
import xyz.chener.zp.zpusermodule.ws.entity.WsClient;
import xyz.chener.zp.zpusermodule.ws.entity.WsMessage;

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
        WsClient unAuthConnect = WsCache.getUnAuthConnect(session.getId());
        if (unAuthConnect != null) {
            WsCache.unAuthConnect.invalidate(session.getId());
            unAuthConnect.setUsername(message.getUsername());
        }
        WsCache.putAuthConnect(session.getId(), unAuthConnect);
        WsConnector.sendObject(message, session.getId());
    }

    public static void sendAll(WsMessage message) {

        WsCache.getAllAuthConnect().forEach((wsClient) -> {
            WsConnector.sendObject(message, wsClient.getSessionId());
        });

    }

    public static boolean sendUser(WsMessage message, String username) {
        AtomicBoolean rt = new AtomicBoolean(false);

        WsCache.getAllAuthConnect().forEach((wsClient) -> {
            if (ObjectUtils.nullSafeEquals(wsClient.getUsername(), username)) {
                WsConnector.sendObject(message, wsClient.getSessionId());
                rt.set(true);
            }
        });
        return rt.get();
    }


}
