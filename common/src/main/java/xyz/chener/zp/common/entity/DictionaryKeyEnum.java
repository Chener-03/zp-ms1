package xyz.chener.zp.common.entity;

/**
 * @Author: chenzp
 * @Date: 2023/02/03/09:48
 * @Email: chen@chener.xyz
 */
public enum DictionaryKeyEnum {

    DEFAULT_PASSWORD(2),
    DEFAULT_ROLE(1)
    ;

    private final Integer key;

    DictionaryKeyEnum(Integer key) {
        this.key = key;
    }

    public Integer get() {
        return key;
    }

}
