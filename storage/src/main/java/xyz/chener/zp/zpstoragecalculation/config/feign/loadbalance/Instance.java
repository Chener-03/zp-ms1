package xyz.chener.zp.zpstoragecalculation.config.feign.loadbalance;

public record Instance(String hardwareUid) implements AutoCloseable {
    @Override
    public void close() throws Exception {
        LoadbalancerContextHolder.clearNextInstance();
    }
}
