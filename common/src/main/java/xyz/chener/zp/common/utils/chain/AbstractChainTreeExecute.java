package xyz.chener.zp.common.utils.chain;

/**
 * @Author: chenzp
 * @Date: 2023/03/01/11:09
 * @Email: chen@chener.xyz
 */
public abstract class AbstractChainTreeExecute {
    protected abstract Object handle(Object param) throws Exception;

    protected AbstractChainTreeExecute changeNext(Object param){
        return null;
    }

    private AbstractChainTreeExecute next;

    private void setNext(AbstractChainTreeExecute next) {
        this.next = next;
    }

    Object doExecute(Object param) throws Exception {
        AbstractChainTreeExecute next = changeNext(param);
        Object result = handle(param);
        setNext(next);
        if (next != null) {
            return next.doExecute(result);
        }
        return result;
    }

}
