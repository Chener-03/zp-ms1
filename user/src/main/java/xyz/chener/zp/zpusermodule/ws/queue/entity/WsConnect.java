package xyz.chener.zp.zpusermodule.ws.queue.entity;

import java.util.concurrent.atomic.AtomicLong;

public class WsConnect implements Comparable<WsConnect> {

    public static long nextMinute() {
        return System.currentTimeMillis() + 60 * 1000;
    }

    private String connect_uid;

    private String connect_user;

    private AtomicLong exp_time = new AtomicLong();

    public WsConnect() {
    }

    public WsConnect(String connect_uid, String connect_user, long exp_time) {
        this.connect_uid = connect_uid;
        this.connect_user = connect_user;
        this.exp_time.set(exp_time);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WsConnect conn)
            return this.toString().equals(conn.toString());
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return connect_uid;
    }


    public String getConnect_uid() {
        return connect_uid;
    }

    public void setConnect_uid(String connect_uid) {
        this.connect_uid = connect_uid;
    }

    public String getConnect_user() {
        return connect_user;
    }

    public void setConnect_user(String connect_user) {
        this.connect_user = connect_user;
    }

    public long getExp_time() {
        return exp_time.get();
    }

    public void setExp_time(long exp_time) {
        this.exp_time.set(exp_time);
    }

    @Override
    public int compareTo(WsConnect o) {
        if (o.getExp_time()  != this.getExp_time() )
            return o.getExp_time()  > this.getExp_time()  ? -1 : 1;
        return -1;
    }
}
