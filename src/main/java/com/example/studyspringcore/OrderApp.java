package com.example.studyspringcore;

import com.example.studyspringcore.member.Grade;
import com.example.studyspringcore.member.Member;
import com.example.studyspringcore.member.MemberService;
import com.example.studyspringcore.order.Order;
import com.example.studyspringcore.order.OrderService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class OrderApp {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        OrderService orderService = applicationContext.getBean("orderService", OrderService.class);
        MemberService memberService = applicationContext.getBean("memberService", MemberService.class);

        Member member = new Member(1L, "nys", Grade.VIP);
        memberService.join(member);
        Order order = orderService.createOrder(1L, "spring", 20000);

        System.out.println("order = " + order);
        System.out.println("order.calculatePrice = " + order.calculatePrice());
    }
}
