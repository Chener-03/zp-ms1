package xyz.chener.zp.common.entity.vo;

import lombok.Data;

import java.io.Serializable;


@Data
public class PageParams implements Serializable {

    private int page = 1;

    private int size = 10;

}
