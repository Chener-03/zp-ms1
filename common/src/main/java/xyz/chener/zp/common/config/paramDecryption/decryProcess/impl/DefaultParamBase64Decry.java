package xyz.chener.zp.common.config.paramDecryption.decryProcess.impl;

import xyz.chener.zp.common.config.paramDecryption.decryProcess.DecryInterface;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @Author: chenzp
 * @Date: 2023/03/10/11:17
 * @Email: chen@chener.xyz
 */
public class DefaultParamBase64Decry implements DecryInterface {
    @Override
    public String  decry(String  obj) {
        return new String(Base64.getDecoder().decode(obj), StandardCharsets.UTF_8);
    }
}
