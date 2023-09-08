package xyz.chener.zp.zpusermodule.entity.dto;

import lombok.Data;

/**
 * @Author: chenzp
 * @Date: 2023/06/15/15:46
 * @Email: chen@chener.xyz
 */

@Data
public class QrCodeLoginRespDto {

    private Boolean success;

    private String ip;

    private String addressName;

    private String os;

    private String uuid;

}
