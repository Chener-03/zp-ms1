package xyz.chener.zp.zpgateway.config.loadbalance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.*;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.SelectedInstanceCallback;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class LoadBalanceDispatch implements ReactorServiceInstanceLoadBalancer {

    public static final String HTTP_TAG_HEADER = "X-LB-TAG-1";

    private static final Logger log = LoggerFactory.getLogger(LoadBalanceDispatch.class);

    final AtomicInteger position;

    final String serviceId;

    ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;


    public LoadBalanceDispatch(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
                               String serviceId) {
        this(serviceInstanceListSupplierProvider, serviceId, new Random().nextInt(1000));
    }


    public LoadBalanceDispatch(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
                               String serviceId, int seedPosition) {
        this.serviceId = serviceId;
        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
        this.position = new AtomicInteger(seedPosition);
    }


    @SuppressWarnings("rawtypes")
    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider
                .getIfAvailable(NoopServiceInstanceListSupplier::new);
        ServerInstance nextInstance = LoadbalancerContextHolder.getNextInstance();
        return supplier.get(request).next()
                .map(serviceInstances -> processInstanceResponse(supplier, serviceInstances,nextInstance,request));
    };

    private Response<ServiceInstance> processInstanceResponse(ServiceInstanceListSupplier supplier,
                                                              List<ServiceInstance> serviceInstances,
                                                              ServerInstance nextInstance,
                                                              Request request) {
        Response<ServiceInstance> serviceInstanceResponse = getInstanceResponse(serviceInstances,nextInstance,request);
        if (supplier instanceof SelectedInstanceCallback && serviceInstanceResponse.hasServer()) {
            ((SelectedInstanceCallback) supplier).selectedServiceInstance(serviceInstanceResponse.getServer());
        }
        return serviceInstanceResponse;
    }

    private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances
            ,ServerInstance nextInstance,Request request) {
        if (instances.isEmpty() && nextInstance == null) {
            if (log.isWarnEnabled()) {
                log.warn("No servers available for service: " + serviceId);
            }
            return new EmptyResponse();
        }

        if (request.getContext() instanceof RequestDataContext rdc){
            List<String> tags = Optional.ofNullable(rdc.getClientRequest().getHeaders().get(HTTP_TAG_HEADER)).orElse(Collections.emptyList());
            if (!tags.isEmpty()){
                List<ServiceInstance> list = instances.stream().filter(e -> {
                    Map<String, String> metadata = e.getMetadata();
                    return ObjectUtils.nullSafeEquals(metadata.get(HTTP_TAG_HEADER), tags.getFirst());
                }).toList();
                if (!list.isEmpty()){
                    instances = list;
                }
            }
        }

        if (nextInstance != null){
            for (ServiceInstance instance : instances) {
                if (instance.getHost().equals(nextInstance.host()) && instance.getPort() == nextInstance.port()){
                    return new DefaultResponse(instance);
                }
            }
            log.warn("自定义IP负载均衡:未找到指定的实例[{}:{}],将返回空实例", nextInstance.host(), nextInstance.port());
            return new EmptyResponse();
        }

        if (instances.size() == 1) {
            return new DefaultResponse(instances.get(0));
        }

        int pos = this.position.incrementAndGet() & Integer.MAX_VALUE;

        ServiceInstance instance = instances.get(pos % instances.size());

        return new DefaultResponse(instance);
    }

}
