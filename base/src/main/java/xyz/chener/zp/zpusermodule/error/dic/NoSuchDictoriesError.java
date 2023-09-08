package xyz.chener.zp.zpusermodule.error.dic;

import xyz.chener.zp.common.error.HttpErrorException;

/**
 * @Author: chenzp
 * @Date: 2023/02/22/16:41
 * @Email: chen@chener.xyz
 */
public class NoSuchDictoriesError extends HttpErrorException {
    public NoSuchDictoriesError() {
        super("字典不存在");
    }
}
