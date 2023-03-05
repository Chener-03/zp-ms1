package xyz.chener.zp.zpstoragecalculation.config.feign.loadbalance;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.SelectedInstanceCallback;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Mono;
import xyz.chener.zp.zpstoragecalculation.config.StorageProperties;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 文件服务 自定义负载均衡 按照硬件uid分配
 */
public class CustomLoadBalance implements ReactorServiceInstanceLoadBalancer {


    final AtomicInteger position;

    final String serviceId;

    ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;

    public CustomLoadBalance(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
                             String serviceId) {
        this(serviceInstanceListSupplierProvider, serviceId, new Random().nextInt(1000));
    }

    public CustomLoadBalance(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
                             String serviceId, int seedPosition) {
        this.serviceId = serviceId;
        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
        this.position = new AtomicInteger(seedPosition);
    }

    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider
                .getIfAvailable(NoopServiceInstanceListSupplier::new);
        Instance nextInstance = LoadbalancerContextHolder.getNextInstance();
        if (nextInstance == null)
            nextInstance = new Instance(null);
        String hardwareUid = nextInstance.hardwareUid();

        return supplier.get(request).next()
                .map(serviceInstances -> {
                    Response<ServiceInstance> response;
                    if (serviceInstances.isEmpty()) {
                        response = new EmptyResponse();
                    }else
                    {
                        List<ServiceInstance> instances;
                        if (hardwareUid != null)
                        {
                            instances = serviceInstances.stream().filter(serviceInstance -> {
                                String s = serviceInstance.getMetadata().get(StorageProperties.HARDWARE_UID);
                                return s != null && s.equals(hardwareUid);
                            }).toList();
                        }else
                        {
                            instances = serviceInstances;
                        }
                        if (instances.size() == 1) {
                            response = new DefaultResponse(instances.get(0));
                        }else if(instances.size() == 0)
                        {
                            response = new EmptyResponse();
                        }else
                        {
                            int pos = position.incrementAndGet() & Integer.MAX_VALUE;
                            ServiceInstance instance = instances.get(pos % instances.size());
                            response = new DefaultResponse(instance);
                        }
                    }
                    if (supplier instanceof SelectedInstanceCallback && response.hasServer()) {
                        ((SelectedInstanceCallback) supplier).selectedServiceInstance(response.getServer());
                    }
                    return response;
                });
    }
}
