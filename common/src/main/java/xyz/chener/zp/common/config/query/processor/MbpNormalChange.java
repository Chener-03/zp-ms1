package xyz.chener.zp.common.config.query.processor;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import xyz.chener.zp.common.config.query.QueryHelper;
import xyz.chener.zp.common.config.query.entity.ChainParam;
import xyz.chener.zp.common.utils.chain.AbstractChainTreeExecute;

/**
 * @Author: chenzp
 * @Date: 2023/03/09/14:31
 * @Email: chen@chener.xyz
 */
public class MbpNormalChange extends AbstractChainTreeExecute {
    @Override
    protected AbstractChainTreeExecute changeNext(Object param) {
        if (param instanceof ChainParam p){
            if (p.entityClass.getSuperclass().equals(Model.class)) {
                return new MbpProcess();
            }
            return new NormalProcess();
        }
        return null;
    }

    @Override
    protected Object handle(Object param) throws Exception {
        return param;
    }
}
