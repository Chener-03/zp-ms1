package xyz.chener.zp.common.entity;

import lombok.Data;

import java.io.Serializable;



@Data
public class Auth2FaRegisterMetadata implements Serializable {

    private String url;

    private Boolean require;

}
