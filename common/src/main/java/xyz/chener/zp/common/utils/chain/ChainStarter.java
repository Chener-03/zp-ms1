package xyz.chener.zp.common.utils.chain;

import java.util.List;

/**
 * @Author: chenzp
 * @Date: 2023/03/01/11:02
 * @Email: chen@chener.xyz
 */
public class ChainStarter {


    public static Object start(List<AbstractChainExecute> chainExecutes,Object param) throws Exception {
        if (chainExecutes.size() == 0) {
            return null;
        }
        for (int i = 0; i < chainExecutes.size(); i++) {
            if (i == chainExecutes.size() - 1) {
                break;
            }
            chainExecutes.get(i).setNext(chainExecutes.get(i + 1));
        }
        return chainExecutes.get(0).doExecute(param);
    }

    public static Object startTree(AbstractChainTreeExecute chainExecutesHeader,Object param) throws Exception {
        return chainExecutesHeader.doExecute(param);
    }

}
