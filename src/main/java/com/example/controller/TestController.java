package com.example.controller;

import com.example.service.LongPollHandler;
import com.example.service.OrderService;
import com.example.service.RedisMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    OrderService orderService;

    @Autowired
    LongPollHandler longPollHandler;

    private static volatile int recoreCnt = 0;

    @RequestMapping("/1")
    public String test1(){
        return "success";
    }

    @RequestMapping("/2")
    public void test2(HttpServletRequest request, HttpServletResponse response) throws IOException {
        recoreCnt++;
        if(orderService.isHavaStore()){
            longPollHandler.submitTask(request, response);
        }else {
            response.getWriter().write("secKill fail");
        }
    }

    @RequestMapping("/3")
    public String test3(){
        return "req:" + recoreCnt + "  redis:" + RedisMock.recordCnt() + "  createOrder:" + orderService.getRecordCnt();
    }
}
