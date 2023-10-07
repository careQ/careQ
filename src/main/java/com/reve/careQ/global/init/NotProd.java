package com.reve.careQ.global.init;

import com.reve.careQ.domain.Member.entity.Member;
import com.reve.careQ.domain.Member.service.MemberService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@Profile({"local", "test"})
public class NotProd {
    @Bean
    CommandLineRunner initData(
            MemberService memberService
    ) {
        return new CommandLineRunner() {
            @Override
            @Transactional
            public void run(String... args) throws Exception {

                Member member1 = memberService.join("user1", "1234", "user1@test.com", "일반회원").getData();
                Member member2 = memberService.join("user2", "1234", "user2@test.com", "일반회원").getData();
                Member member3 = memberService.join("user3", "1234", "user3@test.com", "일반회원").getData();
                Member member4 = memberService.join("user4", "1234", "user4@test.com", "일반회원").getData();

            }
        };
    }
}