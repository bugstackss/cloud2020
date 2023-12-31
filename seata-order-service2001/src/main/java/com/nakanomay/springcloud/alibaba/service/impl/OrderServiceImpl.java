package com.nakanomay.springcloud.alibaba.service.impl;

import com.nakanomay.springcloud.alibaba.dao.OrderDao;
import com.nakanomay.springcloud.alibaba.domain.Order;
import com.nakanomay.springcloud.alibaba.service.AccountService;
import com.nakanomay.springcloud.alibaba.service.OrderService;
import com.nakanomay.springcloud.alibaba.service.StorageService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author nakano_may丶
 * @create 2023/4/28
 * @Version 1.0
 * @description
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService
{

    @Resource
    private OrderDao orderDao;

    @Resource
    private AccountService accountService;

    @Resource
    private StorageService storageService;

    /**
     * 创建订单->调用库存服务扣减库存->调用账户服务扣减账户余额->修改订单状态
     * 简单说：下订单->扣库存->减余额->改状态
     */
    @Override
    @GlobalTransactional(name = "fsp-create-order",rollbackFor = Exception.class)
    public void create(Order order)
    {
        log.info("------》开始创建订单");
        // 1 新建订单
        orderDao.create(order);

        log.info("-------》订单微服务开始调用库存，做扣减Count");
        // 2 扣减库存
        storageService.decrease(order.getProductId(), order.getCount());
        log.info("-------》订单微服务开始调用库存，做扣减end");

        log.info("-------》订单微服务开始调用账户，做扣减");
        // 3 扣减账户
        accountService.decrease(order.getUserId(),order.getMoney());
        log.info("-------》订单微服务开始调用账户，做扣减end");

        // 修改订单的状态，从零到1，1代表已经完成！
        log.info("-------》修改订单状态开始");
        orderDao.update(order.getUserId(),0);
        log.info("-------》修改订单状态end");

        log.info("-------》下订单结束了，O(∩_∩)O哈哈~");

    }
}
