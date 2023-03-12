package xyz.chener.zp.common.config.unifiedReturn.encry.encryProcess.impl;

import org.springframework.util.StringUtils;
import xyz.chener.zp.common.config.unifiedReturn.annotation.EncryField;
import xyz.chener.zp.common.config.unifiedReturn.encry.encryProcess.EncryInterface;

public class DefaultDesensitizationEncry implements EncryInterface {
    @Override
    public String encry(String data, EncryField encryField) {
        if (StringUtils.hasText(data)) {
            int length = data.length();
            int start = length / 3;
            int end = length * 2 / 3;
            return data.substring(0, start) + "****" + data.substring(end);
        }
        return null;
    }
}
