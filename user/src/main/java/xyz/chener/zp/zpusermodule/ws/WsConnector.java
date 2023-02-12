package xyz.chener.zp.zpusermodule.ws;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;
import xyz.chener.zp.zpusermodule.ws.coded.WsDecoder;
import xyz.chener.zp.zpusermodule.ws.coded.WsEncoder;
import xyz.chener.zp.zpusermodule.ws.entity.WsMessage;
import xyz.chener.zp.zpusermodule.ws.entity.WsMessageConstVar;
import xyz.chener.zp.zpusermodule.ws.queue.ConnectQueueManager;
import xyz.chener.zp.zpusermodule.ws.queue.entity.WsConnect;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: chenzp
 * @Date: 2023/02/10/15:19
 * @Email: chen@chener.xyz
 */

@ServerEndpoint(value = "/ws/web/connect",decoders = WsDecoder.class,encoders = WsEncoder.class)
@Component
public class WsConnector {

    public static ConcurrentHashMap<String,Session> cache = new ConcurrentHashMap<>();


    @OnOpen
    public void onOpen(Session session) {
        cache.put(session.getId(),session);
        ConnectQueueManager.getInstance().addConnect(new WsConnect(session.getId(),"",System.currentTimeMillis()+5000));
    }

    @OnClose
    public void onClose(Session session,CloseReason reason){
        cache.remove(session.getId());
        ConnectQueueManager.getInstance().removeConnect(session.getId());
    }

    @OnMessage
    public void onMessage(WsMessage message, Session session) {
        WsMessageProcesser.checkMessageLegal(message,session);
        switch (message.getCode()){
            case WsMessageConstVar.HEART_BEAT_CODE:
                WsMessageProcesser.heartBeat(message,session);
                break;
            default:
                break;
        }
    }


    @OnError
    public void onError(Session session, Throwable error) {
        cache.remove(session.getId());
        ConnectQueueManager.getInstance().removeConnect(session.getId());
    }

    public static void close(String sessionId){
        try {
            Session session = cache.remove(sessionId);
            if (session != null)
                session.close();
        }catch (Exception ex){}
    }

    public static void close(Session session){
        try {
            cache.remove(session.getId());
            session.close();
        }catch (Exception ex){}
    }

    public static boolean send(WsMessage wsMessage,String sessionId){
        Session session = cache.get(sessionId);
        if (session != null)
        {
            try {
                session.getAsyncRemote().sendObject(wsMessage);
                return true;
            }catch (Exception ex){ }
        }
        return false;
    }

}
