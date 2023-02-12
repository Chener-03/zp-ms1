package xyz.chener.zp.zpusermodule.ws.mq.listener;

import xyz.chener.zp.common.utils.ObjectUtils;
import xyz.chener.zp.zpusermodule.ws.WsConnector;
import xyz.chener.zp.zpusermodule.ws.WsMessageProcesser;
import xyz.chener.zp.zpusermodule.ws.entity.WsMessage;
import xyz.chener.zp.zpusermodule.ws.entity.WsMessageConstVar;
import xyz.chener.zp.zpusermodule.ws.mq.entity.NotifyMessage;
import xyz.chener.zp.zpusermodule.ws.queue.ConnectQueueManager;
import xyz.chener.zp.zpusermodule.ws.queue.entity.WsConnect;

public class NotifyMessageListener implements MqListener<NotifyMessage>{
    @Override
    public void onMessage(Object message) {
       if (message instanceof NotifyMessage msg){
           switch (msg.getType()) {
               case NotifyMessage.TYPE.ALL_USER -> {
                   WsMessage m = new WsMessage();
                   m.setMessage(msg.getContent());
                   m.setCode(WsMessageConstVar.MESSAGE_NOTIFY);
                   WsMessageProcesser.sendAll(m);
               }
               case NotifyMessage.TYPE.ONE_USER -> {
                   WsMessage m = new WsMessage();
                   m.setMessage(msg.getContent());
                   m.setCode(WsMessageConstVar.MESSAGE_NOTIFY);
                   WsMessageProcesser.sendUser(m,msg.getUser());
               }
           }
       }
    }
}
