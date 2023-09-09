package xyz.chener.zp.zpusermodule.ws;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.WsRemoteEndpointBasic;
import org.apache.tomcat.websocket.WsSession;
import org.apache.tomcat.websocket.server.WsRemoteEndpointImplServer;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import xyz.chener.zp.common.entity.CommonVar;
import xyz.chener.zp.zpusermodule.ws.coded.WsDecoder;
import xyz.chener.zp.zpusermodule.ws.coded.WsEncoder;
import xyz.chener.zp.zpusermodule.ws.entity.WsClient;
import xyz.chener.zp.zpusermodule.ws.entity.WsMessage;
import xyz.chener.zp.zpusermodule.ws.entity.WsMessageConstVar;

import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: chenzp
 * @Date: 2023/02/10/15:19
 * @Email: chen@chener.xyz
 */

@ServerEndpoint(value = "/ws/web/connect",decoders = WsDecoder.class,encoders = WsEncoder.class,configurator = WsConfig.WsConfigurator.class)
@Component
@Slf4j
public class WsConnector {

    public static ConcurrentHashMap<String,Session> cache = new ConcurrentHashMap<>();


    @OnOpen
    public void onOpen(Session session) {
        cache.put(session.getId(),session);
        WsClient client = new WsClient();
        client.setSession(session);
        client.setSessionId(session.getId());
        client.setIp(getIpBySession(session));
        WsCache.putUnAuthConnect(session.getId(),client);
    }

    private String getIpBySession(Session session){
        Object ip = session.getUserProperties().get(CommonVar.IP_HEAD);
        if (ip == null || !StringUtils.hasText(ip.toString())){
            return getIpByReference(session);
        }else {
            return ip.toString();
        }
    }
    private String getIpByReference(Session session){
        try {
            WsSession wsSession = (WsSession) session;
            WsRemoteEndpointBasic remoteEndpointBasic = (WsRemoteEndpointBasic) getObjectField(wsSession, "remoteEndpointBasic",null);
            WsRemoteEndpointImplServer base = (WsRemoteEndpointImplServer) getObjectField(remoteEndpointBasic, "base","org.apache.tomcat.websocket.WsRemoteEndpointBase");
            Object socketChannelImpl = getObjectField(getObjectField(getObjectField(base, "socketWrapper", null), "socket", "org.apache.tomcat.util.net.SocketWrapperBase"), "sc", null);
            InetSocketAddress address = (InetSocketAddress) getObjectField(socketChannelImpl, "remoteAddress", null);
            return address.getAddress().getHostAddress();
        }catch (Exception exception){
            return null;
        }
    }
    private Object getObjectField(Object obj,String fieldName,String className){
        try {
            Class clz;
            if (className!=null){
                clz = Class.forName(className);
            }else {
                clz = obj.getClass();
            }

            Field field = clz.getDeclaredField(fieldName);
            boolean access = field.canAccess(obj);
            field.setAccessible(true);
            Object o = field.get(obj);
            field.setAccessible(access);
            return o;
        }catch (Exception ex){
            if (ex instanceof InaccessibleObjectException ioe){
                log.error("无法通过反射获取Session中源IP,请添加虚拟机参数：--add-opens java.base/sun.nio.ch=ALL-UNNAMED");
            }
            return null;
        }
    }



    @OnClose
    public void onClose(Session session,CloseReason reason){
        cache.remove(session.getId());
        WsCache.removeConnect(session.getId());
    }

    @OnMessage
    public void onMessage(WsMessage message, Session session) {
        switch (message.getCode()){
            case WsMessageConstVar.HEART_BEAT_CODE:
                WsMessageProcesser.checkMessageLegal(message,session);
                WsMessageProcesser.heartBeat(message,session);
                break;

            case WsMessageConstVar.QRCODE_LOGIN_REQUEST:
                WsMessageProcesser.qrCodeLogin(message,session);
                break;

            default:
                WsMessageProcesser.checkMessageLegal(message,session);
                break;
        }
    }


    @OnError
    public void onError(Session session, Throwable error) {
        cache.remove(session.getId());
        WsCache.removeConnect(session.getId());
    }

    public static void close(String sessionId){
        try {
            Session session = cache.remove(sessionId);
            if (session != null)
                session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "超时关闭"));
        }catch (Exception ex){}
    }

    public static void close(Session session){
        try {
            cache.remove(session.getId());
            session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "超时关闭"));
        }catch (Exception ex){}
    }

    public static boolean sendObject(Object obj,String sessionId){
        Session session = cache.get(sessionId);
        if (session != null)
        {
            try {
                session.getAsyncRemote().sendObject(obj);
                return true;
            }catch (Exception ex){ }
        }
        return false;
    }

    public static boolean sendObject(Object obj,Session session){
        return sendObject(obj,session.getId());
    }

}
