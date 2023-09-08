package xyz.chener.zp.zpusermodule.ws.queue.listener;

import xyz.chener.zp.zpusermodule.ws.WsConnector;
import xyz.chener.zp.zpusermodule.ws.queue.listener.QueueListener;
import xyz.chener.zp.zpusermodule.ws.queue.entity.WsConnect;

public class ConnectExpListener implements QueueListener<WsConnect> {
    @Override
    public void onEvent(Object wsConnect) {
        if (wsConnect instanceof WsConnect connect)
        {
            WsConnector.close(connect.getConnect_uid());
        }

    }
}
