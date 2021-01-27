package com.example.studyspringcore;

import com.example.studyspringcore.member.Grade;
import com.example.studyspringcore.member.Member;
import com.example.studyspringcore.member.MemberService;
import com.example.studyspringcore.member.MemberServiceImpl;

public class MemberApp {
    public static void main(String[] args) {
        MemberService memberService = new MemberServiceImpl();
        Member memberA = new Member(1L, "memberA", Grade.VIP);
        memberService.join(memberA);
        Member findMember = memberService.findMember(1L);
        System.out.println("new member = " + memberA.getName() );
        System.out.println("find member = " + findMember.getName());
    }
}
