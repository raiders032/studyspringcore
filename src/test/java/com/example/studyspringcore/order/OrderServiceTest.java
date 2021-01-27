package com.example.studyspringcore.order;

import com.example.studyspringcore.member.Grade;
import com.example.studyspringcore.member.Member;
import com.example.studyspringcore.member.MemberRepository;
import com.example.studyspringcore.member.MemoryMemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class OrderServiceTest {

    private final OrderService orderService = new OrderServiceImpl();
    private final MemberRepository memberRepository = new MemoryMemberRepository();

    @Test
    void createOrder(){
        //given
        Long memberId = 1L;
        Member member = new Member(memberId, "nys", Grade.VIP);
        memberRepository.save(member);

        //when
        Order order = orderService.createOrder(memberId, "spring", 10000);

        //then
        Assertions.assertThat(order.getDiscountPrice()).isEqualTo(1000);
        Assertions.assertThat(order.calculatePrice()).isEqualTo(9000);
    }
}
