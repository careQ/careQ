package com.reve.careQ.global.init;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.Admin.service.AdminService;
import com.reve.careQ.domain.Hospital.entity.Hospital;
import com.reve.careQ.domain.Hospital.service.HospitalService;
import com.reve.careQ.domain.Member.entity.Member;
import com.reve.careQ.domain.Member.service.MemberService;
import com.reve.careQ.domain.Subject.entity.Subject;
import com.reve.careQ.domain.Subject.service.SubjectService;
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
            MemberService memberService,
            AdminService adminService,
            HospitalService hospitalService,
            SubjectService subjectService
    ) {
        return new CommandLineRunner() {
            @Override
            @Transactional
            public void run(String... args) throws Exception {

                Member member1 = memberService.join("careQ", "useruser1", "1234", "user1@test.com").getData();
                Member member2 = memberService.join("careQ", "useruser2", "1234", "user2@test.com").getData();
                Member member3 = memberService.join("careQ", "useruser3", "1234", "user3@test.com").getData();

                Hospital hospital1 = hospitalService.insert("A0000028","세브란스병원","서울특별시","강남구").getData();
                Subject subject1 = subjectService.insert("D001","내과").getData();
                Subject subject2 = subjectService.insert("D002","소아청소년과").getData();
                Subject subject3 = subjectService.insert("D003","신경과").getData();

                Admin admin1 = adminService.join("A0000028","D001","adminadmin1","aaaa").getData();
            }
        };
    }
}