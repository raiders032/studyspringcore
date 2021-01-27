package com.example.studyspringcore;

import com.example.studyspringcore.discount.FixDiscountPolicy;
import com.example.studyspringcore.member.MemberService;
import com.example.studyspringcore.member.MemberServiceImpl;
import com.example.studyspringcore.member.MemoryMemberRepository;
import com.example.studyspringcore.order.OrderService;
import com.example.studyspringcore.order.OrderServiceImpl;

public class AppConfig {

    public MemberService memberService(){
        return new MemberServiceImpl(new MemoryMemberRepository());
    }

    public OrderService orderService(){
        return new OrderServiceImpl(new FixDiscountPolicy(), new MemoryMemberRepository());
    }

}
