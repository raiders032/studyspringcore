package com.example.studyspringcore;

import com.example.studyspringcore.member.Grade;
import com.example.studyspringcore.member.Member;
import com.example.studyspringcore.member.MemberRepository;
import com.example.studyspringcore.member.MemoryMemberRepository;
import com.example.studyspringcore.order.Order;
import com.example.studyspringcore.order.OrderService;
import com.example.studyspringcore.order.OrderServiceImpl;

public class OrderApp {
    public static void main(String[] args) {
        OrderService orderService = new OrderServiceImpl();
        MemberRepository memberRepository = new MemoryMemberRepository();
        Member member = new Member(1L, "nys", Grade.VIP);
        memberRepository.save(member);
        Order order = orderService.createOrder(1L, "spring", 10000);
        System.out.println("order = " + order);
        System.out.println("order.calculatePrice = " + order.calculatePrice());
    }
}
