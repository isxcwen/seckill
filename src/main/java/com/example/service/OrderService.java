package com.example.service;

import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private volatile boolean havaStore = true;

    public int getRecordCnt() {
        return recordCnt;
    }

    public void setRecordCnt(int recordCnt) {
        this.recordCnt = recordCnt;
    }

    private volatile int recordCnt = 0;


    public void createOrder() throws InterruptedException {
        recordCnt++;
        Thread.sleep(2000);
        System.out.println("create order");
    }

    public boolean isHavaStore() {
        return havaStore;
    }

    public void setHavaStore(boolean havaStore) {
        this.havaStore = havaStore;
    }
}
