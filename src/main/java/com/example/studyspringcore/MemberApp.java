package com.example.studyspringcore;

import com.example.studyspringcore.member.Grade;
import com.example.studyspringcore.member.Member;
import com.example.studyspringcore.member.MemberService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MemberApp {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        MemberService memberService = applicationContext.getBean("memberService", MemberService.class);

        Member memberA = new Member(1L, "memberA", Grade.VIP);
        memberService.join(memberA);
        Member findMember = memberService.findMember(1L);

        System.out.println("new member = " + memberA.getName() );
        System.out.println("find member = " + findMember.getName());
    }
}
