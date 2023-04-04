package xyz.chener.zp.datasharing.commons;

import xyz.chener.zp.common.config.unifiedReturn.annotation.EncryField;
import xyz.chener.zp.common.config.unifiedReturn.encry.encryProcess.EncryInterface;

/**
 * @Author: chenzp
 * @Date: 2023/04/04/09:58
 * @Email: chen@chener.xyz
 */
public class PasswordEncry implements EncryInterface {
    @Override
    public String encry(String data, EncryField encryField) {
        return "**********";
    }
}
