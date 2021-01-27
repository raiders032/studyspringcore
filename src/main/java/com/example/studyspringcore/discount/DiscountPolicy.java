package com.example.studyspringcore.discount;

import com.example.studyspringcore.member.Member;

public interface DiscountPolicy {
    int discount(Member member, int price);
}
