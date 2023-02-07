package xyz.chener.zp.common.config.dynamicVerification.rules.impl;

import xyz.chener.zp.common.config.dynamicVerification.rules.DynamicVerAbstract;
import xyz.chener.zp.common.utils.Md5Utiles;

/**
 * @Author: chenzp
 * @Date: 2023/02/03/11:18
 * @Email: chen@chener.xyz
 */


public class DefaultDynamicVer extends DynamicVerAbstract {
    public DefaultDynamicVer(String dsKey) {
        super(dsKey);
    }

    @Override
    public String verify(Object... objects) {
        StringBuilder sb = new StringBuilder();
        for (Object o : objects) {
            sb.append(o.toString());
        }
        sb.append(getDsKey());
        return Md5Utiles.getStrMd5(sb.toString());
    }
}
