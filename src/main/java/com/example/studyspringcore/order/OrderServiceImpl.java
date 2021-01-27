package com.example.studyspringcore.order;

import com.example.studyspringcore.discount.DiscountPolicy;
import com.example.studyspringcore.discount.FixDiscountPolicy;
import com.example.studyspringcore.member.Member;
import com.example.studyspringcore.member.MemberRepository;
import com.example.studyspringcore.member.MemoryMemberRepository;

public class OrderServiceImpl implements OrderService {

    private final DiscountPolicy discountPolicy = new FixDiscountPolicy();
    private final MemberRepository memberRepository = new MemoryMemberRepository();

    @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        Member findMember = memberRepository.findById(memberId);
        int discountPrice = discountPolicy.discount(findMember, itemPrice);
        return new Order(memberId, itemName, itemPrice, discountPrice);
    }
}
