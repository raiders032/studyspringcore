package com.example.studyspringcore;

import com.example.studyspringcore.member.*;
import com.example.studyspringcore.order.Order;
import com.example.studyspringcore.order.OrderService;
import com.example.studyspringcore.order.OrderServiceImpl;

public class OrderApp {
    public static void main(String[] args) {
        AppConfig appConfig = new AppConfig();
        OrderService orderService = appConfig.orderService();
        MemberService memberService = appConfig.memberService();
        Member member = new Member(1L, "nys", Grade.VIP);
        memberService.join(member);
        Order order = orderService.createOrder(1L, "spring", 10000);
        System.out.println("order = " + order);
        System.out.println("order.calculatePrice = " + order.calculatePrice());
    }
}
