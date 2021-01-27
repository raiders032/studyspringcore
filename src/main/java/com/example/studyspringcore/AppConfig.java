package com.example.studyspringcore;

import com.example.studyspringcore.discount.DiscountPolicy;
import com.example.studyspringcore.discount.FixDiscountPolicy;
import com.example.studyspringcore.member.MemberRepository;
import com.example.studyspringcore.member.MemberService;
import com.example.studyspringcore.member.MemberServiceImpl;
import com.example.studyspringcore.member.MemoryMemberRepository;
import com.example.studyspringcore.order.OrderService;
import com.example.studyspringcore.order.OrderServiceImpl;

public class AppConfig {

    private MemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }

    private DiscountPolicy discountPolicy() {
        return new FixDiscountPolicy();
    }

    public MemberService memberService(){
        return new MemberServiceImpl(memberRepository());
    }

    public OrderService orderService(){
        return new OrderServiceImpl(discountPolicy(), memberRepository());
    }

}
