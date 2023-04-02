package xyz.chener.zp.datasharing.requestProcess.entity.pe;

import lombok.Data;
import xyz.chener.zp.common.utils.chain.AbstractChainExecute;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class AuthPe implements Serializable {

    public static final String MD5 = "md5_xyz.chener.zp.datasharing.requestProcess.entity.pe.AuthPe";

    public static final String IP = "ip_xyz.chener.zp.datasharing.requestProcess.entity.pe.AuthPe";

    public static final String HEAD = "head_xyz.chener.zp.datasharing.requestProcess.entity.pe.AuthPe";

    private List<AuthItem> authItems = new ArrayList<>();


    @Data
    public static class AuthItem{

        // ip md5
        private String authType;

        // 需要ds的字段
        private List<String> paramKeys = new ArrayList<>();

        private String md5Slat;

        // 存放ds值的字段
        private String md5ParamKey;

        // ip 列表
        private List<String> ips = new ArrayList<>();


        // head 列表  key value ->  key0=value0&key1=value1 ........
        private String heads;

    }
}