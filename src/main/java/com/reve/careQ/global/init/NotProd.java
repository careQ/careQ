package com.reve.careQ.global.init;

import com.reve.careQ.domain.Admin.service.AdminService;
import com.reve.careQ.domain.Chat.service.ChatService;
import com.reve.careQ.domain.Hospital.service.HospitalService;
import com.reve.careQ.domain.Member.service.MemberService;
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
            ChatService chatService
    ) {
        return new CommandLineRunner() {
            @Override
            @Transactional
            public void run(String... args) throws Exception {

                memberService.join("careQ", "useruser1", "1234", "user1@test.com").getData();
                memberService.join("careQ", "useruser2", "1234", "user2@test.com").getData();
                memberService.join("careQ", "useruser3", "1234", "user3@test.com").getData();

                hospitalService.insert("A0000028","세브란스병원","서울특별시","강남구").getData();

                adminService.join("A0000028","D001","adminadmin1","aaaa").getData();//서울특별시 강남구
                adminService.join("A1100311","D001","adminadmin2","aaaa").getData();//서울특별시 동대문구
                adminService.join("A1100411","D001","adminadmin3","aaaa").getData();//서울특별시 강서구
                adminService.join("A1100811","D001","adminadmin4","aaaa").getData();//서울특별시 서대문구
                adminService.join("A1101111","D001","adminadmin5","aaaa").getData();//서울특별시 노원구

                adminService.join("A1200111","D001","adminadmin6","aaaa").getData();//부산광역시 동구
                adminService.join("A1200011","D001","adminadmin7","aaaa").getData();//부산광역시 동래구
                adminService.join("A1200015","D001","adminadmin8","aaaa").getData();//부산광역시 기장군
                adminService.join("A1200016","D001","adminadmin9","aaaa").getData();//부산광역시 사상구
                adminService.join("A1200017","D001","adminadmin10","aaaa").getData();//부산광역시 동구
                adminService.join("A1200018","D001","adminadmin11","aaaa").getData();//부산광역시 동래구

                adminService.join("A1300018","D001","adminadmin12","aaaa").getData();//대구광역시 동구
                adminService.join("A1300019","D001","adminadmin13","aaaa").getData();//대구광역시 동구
                adminService.join("A1300011","D001","adminadmin14","aaaa").getData();//대구광역시 달서구
                adminService.join("A1300009","D001","adminadmin15","aaaa").getData();//대구광역시 동구
                adminService.join("A1300005","D001","adminadmin16","aaaa").getData();//대구광역시 북구
                adminService.join("A1300004","D001","adminadmin17","aaaa").getData();//대구광역시 남구

                adminService.join("A1500004","D001","adminadmin18","aaaa").getData();//광주광역시 광산구
                adminService.join("A1500005","D001","adminadmin19","aaaa").getData();//광주광역시 북구
                adminService.join("A1300006","D001","adminadmin20","aaaa").getData();//광주광역시 광산구
                adminService.join("A1300007","D001","adminadmin21","aaaa").getData();//광주광역시 북구
                adminService.join("A1300004","D001","adminadmin22","aaaa").getData();//대구광역시 남구

                chatService.insert((long)1,(long)1);
                chatService.insert((long)1,(long)3);
                chatService.insert((long)1,(long)5);
                chatService.insert((long)1,(long)7);
                chatService.insert((long)1,(long)9);

                chatService.insert((long)2,(long)1);
                chatService.insert((long)2,(long)2);
                chatService.insert((long)2,(long)5);

                chatService.insert((long)3,(long)2);
                chatService.insert((long)3,(long)4);

            }
        };
    }
}