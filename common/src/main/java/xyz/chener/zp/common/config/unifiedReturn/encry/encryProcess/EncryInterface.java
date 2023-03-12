package xyz.chener.zp.common.config.unifiedReturn.encry.encryProcess;

import xyz.chener.zp.common.config.unifiedReturn.annotation.EncryField;

public interface EncryInterface {
    String encry(String data, EncryField encryField);
}
