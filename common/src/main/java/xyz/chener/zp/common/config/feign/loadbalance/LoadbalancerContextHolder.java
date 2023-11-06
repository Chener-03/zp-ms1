package xyz.chener.zp.common.config.feign.loadbalance;


public class LoadbalancerContextHolder {
    private static final ThreadLocal<ServerInstance> threadLocal = new InheritableThreadLocal<>();

    public static ServerInstance setNextInstance(ServerInstance instance)
    {
        threadLocal.set(instance);
        return instance;
    }

    public static ServerInstance getNextInstance()
    {
        ServerInstance instance = threadLocal.get();
        clearNextInstance();
        return instance;
    }

    public static void clearNextInstance()
    {
        threadLocal.remove();
    }
}
