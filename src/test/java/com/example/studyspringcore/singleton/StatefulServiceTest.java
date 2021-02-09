package com.example.studyspringcore.singleton;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import static org.junit.jupiter.api.Assertions.*;

class StatefulServiceTest {

    @Test
    @DisplayName("stateful 싱글톤")
    void statefulServiceSingleton(){
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(TestConfig.class);
        StatefulService statefulService = applicationContext.getBean("statefulService", StatefulService.class);

        // member1 주문
        statefulService.order("member1", 10000);
        // member2 주문
        statefulService.order("member2", 20000);

        // member1 주문 금액 조회
        int price = statefulService.getPrice();
        Assertions.assertThat(price).isEqualTo(20000);
    }

    static class TestConfig{
        @Bean
        public StatefulService statefulService(){
            return new StatefulService();
        }
    }
}