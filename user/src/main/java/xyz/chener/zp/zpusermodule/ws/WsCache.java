package xyz.chener.zp.zpusermodule.ws;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import jakarta.websocket.CloseReason;
import jakarta.websocket.Session;

import java.io.IOException;
import java.time.Duration;

/**
 * @Author: chenzp
 * @Date: 2023/03/14/16:40
 * @Email: chen@chener.xyz
 */
public class WsCache {

    public static Cache<String, WsClient> unAuthConnect;
    public static Cache<String, WsClient> authConnect;

    static {
        unAuthConnect = CacheBuilder.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(10000))
                .<String,WsClient>removalListener(notification->{

                })
                .build();
    }


    public static void closeConnect(String sessionId, Session session)  {
        try {
            session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "超时关闭"));
        } catch (Exception ignored) { }
    }

}
