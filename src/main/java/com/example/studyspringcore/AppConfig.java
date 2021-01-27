package com.example.studyspringcore;

import com.example.studyspringcore.discount.DiscountPolicy;
import com.example.studyspringcore.discount.RateDiscountPolicy;
import com.example.studyspringcore.member.MemberRepository;
import com.example.studyspringcore.member.MemberService;
import com.example.studyspringcore.member.MemberServiceImpl;
import com.example.studyspringcore.member.MemoryMemberRepository;
import com.example.studyspringcore.order.OrderService;
import com.example.studyspringcore.order.OrderServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public MemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }

    @Bean
    public DiscountPolicy discountPolicy() {
        return new RateDiscountPolicy();
    }

    @Bean
    public MemberService memberService() {
        return new MemberServiceImpl(memberRepository());
    }

    @Bean
    public OrderService orderService() {
        return new OrderServiceImpl(discountPolicy(), memberRepository());
    }

}
