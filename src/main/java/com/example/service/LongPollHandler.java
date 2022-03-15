package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class LongPollHandler {
    private ScheduledExecutorService scheduledExecutorService;
    private ArrayBlockingQueue<Worker> queue;

    @Autowired
    OrderService orderService;

    private int expire = 3000;


    @PostConstruct
    public void init() {
        int i = Runtime.getRuntime().availableProcessors();
        int num = RedisMock.getStore() > i ? i : RedisMock.getStore();
        scheduledExecutorService = new ScheduledThreadPoolExecutor(num);
        queue = new ArrayBlockingQueue<>(RedisMock.getStore());
    }

    public void submitTask(HttpServletRequest request, HttpServletResponse response) {
        AsyncContext asyncContext = request.startAsync(request, response);
        Worker worker = new Worker(asyncContext);
        try {
            if (!orderService.isHavaStore()) {
                worker.response("secKill fail");
                return;
            }
            if (worker.isExpire(expire)) {
                worker.response("please retry late");
                return;
            }
            boolean offer = queue.offer(worker);
            if (offer) {
                scheduledExecutorService.schedule(() -> task(), 1, TimeUnit.SECONDS);
            } else {
                worker.response("please retry late");
                return;
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    public void task() {
        try {
            Worker take = queue.take();
            if (!orderService.isHavaStore()) {
                take.response("secKill fail");
                return;
            }
            //可以响应更快 但是可能出现第一秒抢不完
            if (take.isExpire(expire)) {
                take.response("please retry late");
                return;
            }
            if (RedisMock.canBuy()) {
                orderService.createOrder();
                take.response("secKill Success");
            } else {
                orderService.setHavaStore(false);
                take.response("secKill fail");
            }
        } catch (InterruptedException | IOException e) {
            //e.printStackTrace();
        }
    }

}
