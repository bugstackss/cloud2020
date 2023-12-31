package com.nakanomay.springcloud.lb;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 *
 * @author nakano_may丶
 * @date 2023/4/19
 * @Version 1.0
 * @description
 */
@Component
public class MyLB implements LoadBalancer
{

    private AtomicInteger atomicInteger = new AtomicInteger(0);

    public final int getAndIncrement()
    {
        int current;
        int next;
        do {
            current = this.atomicInteger.get();
            next = current >= 2147483647 ? 0 : current + 1;
        }
        while (!this.atomicInteger.compareAndSet(current, next));
        System.out.println("*******第几次访问，次数next:" + next);
        return next;
    }

    @Override
    public ServiceInstance instances(List<ServiceInstance> serviceInstance)
    {
        int index = getAndIncrement() % serviceInstance.size();

        return serviceInstance.get(index);
    }
}
