package xyz.chener.zp.common.config.antiShaking.processer;

/**
 * @Author: chenzp
 * @Date: 2023/03/14/10:48
 * @Email: chen@chener.xyz
 */
public interface AntiShakingInterface {

    Boolean check(String key, int limitTimeMs);

}
