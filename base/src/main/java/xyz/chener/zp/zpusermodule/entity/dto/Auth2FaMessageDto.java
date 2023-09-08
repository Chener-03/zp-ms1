package xyz.chener.zp.zpusermodule.entity.dto;

import lombok.Data;
import xyz.chener.zp.common.config.dynamicVerification.annotation.DsEntityField;
import xyz.chener.zp.common.config.paramDecryption.annotation.DecryField;
import xyz.chener.zp.common.config.unifiedReturn.annotation.EncryField;

import java.io.Serializable;


@Data
public class Auth2FaMessageDto implements Serializable {

    @DecryField(required = false)
    private Boolean success;

    @EncryField
    @DsEntityField(order = 0)
    private String key;

    @EncryField
    private String url;

    @EncryField
    @DsEntityField(order = 1)
    private String code;

}
