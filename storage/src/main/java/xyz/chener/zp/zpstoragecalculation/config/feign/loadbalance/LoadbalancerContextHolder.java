package xyz.chener.zp.zpstoragecalculation.config.feign.loadbalance;

public class LoadbalancerContextHolder {
    private static final ThreadLocal<Instance> threadLocal = new ThreadLocal<>();

    public static Instance setNextInstance(Instance instance)
    {
        threadLocal.set(instance);
        return instance;
    }

    public static Instance getNextInstance()
    {
        Instance instance = threadLocal.get();
        clearNextInstance();
        return instance;
    }

    public static void clearNextInstance()
    {
        threadLocal.remove();
    }

}
