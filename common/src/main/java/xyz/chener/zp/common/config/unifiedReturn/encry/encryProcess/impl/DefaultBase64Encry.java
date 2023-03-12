package xyz.chener.zp.common.config.unifiedReturn.encry.encryProcess.impl;

import org.springframework.util.StringUtils;
import xyz.chener.zp.common.config.unifiedReturn.annotation.EncryField;
import xyz.chener.zp.common.config.unifiedReturn.encry.encryProcess.EncryInterface;

import java.util.Base64;

public class DefaultBase64Encry implements EncryInterface {
    @Override
    public String encry(String data, EncryField encryField) {
        if (!StringUtils.hasText(data))
            return null;
        return Base64.getEncoder().encodeToString(data.getBytes());
    }
}
