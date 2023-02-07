package xyz.chener.zp.common.utils;

import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.UUID;

/**
 * @Author: chenzp
 * @Date: 2023/01/16/16:11
 * @Email: chen@chener.xyz
 */
public class TransactionUtils {
    public static TransactionDefinition getTransactionDefinition(String name,int propagationBehavior)
    {
        DefaultTransactionDefinition td = new DefaultTransactionDefinition();
        td.setName(name);
        td.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        return td;
    }

    public static TransactionDefinition getTransactionDefinition(int propagationBehavior)
    {
        return getTransactionDefinition(UUID.randomUUID().toString().replace("-",""),
                propagationBehavior);
    }

    public static TransactionDefinition getTransactionDefinition()
    {
        return getTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED);
    }
}
