package xyz.chener.zp.zpgateway.utils.chain;

/**
 * @Author: chenzp
 * @Date: 2023/03/01/10:52
 * @Email: chen@chener.xyz
 */
public abstract class AbstractChainExecute {

    protected abstract Object handle(Object param) throws Exception;

    private AbstractChainExecute next;

     Object doExecute(Object param) throws Exception {
        Object result = handle(param);
        if (next != null) {
            return next.doExecute(result);
        }
        return result;
    }

    public void setNext(AbstractChainExecute next) {
        this.next = next;
    }
}
