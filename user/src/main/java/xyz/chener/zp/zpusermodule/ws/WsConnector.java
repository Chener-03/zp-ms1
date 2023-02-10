package xyz.chener.zp.zpusermodule.ws;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;

/**
 * @Author: chenzp
 * @Date: 2023/02/10/15:19
 * @Email: chen@chener.xyz
 */

@ServerEndpoint(value = "/ws/web/connect",decoders = WsDecoder.class,encoders = WsEncoder.class)
@Component
public class WsConnector {

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("open");
        session.getAsyncRemote().sendObject(WsMessage.EMPTY_MESSAGE);
    }

    @OnClose
    public void onClose(Session session,CloseReason reason){
        System.out.println("close");
    }

    @OnMessage
    public void onMessage(WsMessage message, Session session) {
        System.out.println("message");
    }



    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("error");
    }

}
