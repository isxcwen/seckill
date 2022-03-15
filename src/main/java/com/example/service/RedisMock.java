package com.example.service;

public class RedisMock {
    //库存10
    private static int store = 10;

    private static volatile int recoreCnt = 0;

    //redis核心线程是串行 用lua
    public static synchronized boolean canBuy() {
        recoreCnt++;
        if (store >= 1) {
            --store;
            return true;
        }
        return false;
    }

    public static int getStore() {
        recoreCnt++;
        return store;
    }

    public static int recordCnt() {
        return recoreCnt;
    }
}
