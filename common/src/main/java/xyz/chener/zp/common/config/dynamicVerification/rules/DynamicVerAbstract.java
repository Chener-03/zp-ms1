package xyz.chener.zp.common.config.dynamicVerification.rules;

/**
 * @Author: chenzp
 * @Date: 2023/02/03/11:16
 * @Email: chen@chener.xyz
 */
public abstract class DynamicVerAbstract implements DynamicVerRuleInterface{

    private String dsKey;

    private DynamicVerAbstract(){}

    public DynamicVerAbstract(String dsKey) {
        this.dsKey = dsKey;
    }

    public String getDsKey() {
        return dsKey;
    }

    public void setDsKey(String dsKey) {
        this.dsKey = dsKey;
    }

}
