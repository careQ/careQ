package com.reve.careQ.domain.RegisterChart.controller;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.Admin.service.AdminService;
import com.reve.careQ.domain.RegisterChart.dto.RegisterChartDto;
import com.reve.careQ.domain.RegisterChart.entity.RegisterChart;
import com.reve.careQ.domain.RegisterChart.dto.RegisterChartInfoDto;
import com.reve.careQ.domain.RegisterChart.entity.RegisterChartStatus;
import com.reve.careQ.domain.RegisterChart.service.RegisterChartService;
import com.reve.careQ.domain.Reservation.dto.ReservationDto;
import com.reve.careQ.domain.Reservation.entity.Reservation;
import com.reve.careQ.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.net.URI;

@Controller
@RequestMapping("/members/subjects/{subject-id}/hospitals/{hospital-id}/queues")
@RequiredArgsConstructor
public class RegisterChartController {
    private final RegisterChartService registerChartService;
    private final AdminService adminService;
    private final SimpMessageSendingOperations sendingOperations;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ModelAndView showQueue(@PathVariable("subject-id") Long subjectId, @PathVariable("hospital-id") Long hospitalId, Model model){
        ModelAndView mv = new ModelAndView();

        RegisterChartInfoDto registerChartInfo = registerChartService.getRegisterChartInfo(hospitalId, subjectId);

        mv.addObject("register", registerChartInfo.getRegisterChart());
        mv.addObject("subject", registerChartInfo.getSubject());
        mv.addObject("hospital", registerChartInfo.getHospital());
        mv.setViewName("members/queues");

        return mv;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<?> createRegister(@PathVariable("subject-id") Long subjectId,
                                            @PathVariable("hospital-id") Long hospitalId) {

        RsData<RegisterChart> registerChartRs = registerChartService.insert(hospitalId, subjectId);

        if (registerChartRs.isSuccess()) {
            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                    .location(URI.create("/members/subjects/" + subjectId + "/hospitals/" + hospitalId + "/queues"))
                    .build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(registerChartRs);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping
    public ResponseEntity<?> deleteRegister(@PathVariable("subject-id") Long subjectId,
                                            @PathVariable("hospital-id") Long hospitalId) {
        try {
            registerChartService.processRegisterChart(hospitalId, subjectId);
            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                    .location(URI.create("/members"))
                    .build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(RsData.failOf("F-0", e.getMessage()));
        }
    }

    @MessageMapping("/queue")
    public void send(RegisterChartDto registerChartDto) throws Exception {
        Thread.sleep(1000); // simulated delay

        Admin admin = adminService.findByHospitalIdAndSubjectId(registerChartDto.getHospitalId(),registerChartDto.getSubjectId()).get();
        RegisterChart registerChart = registerChartService.findByAdminIdAndMemberIdAndIsDeletedFalse(admin.getId(), registerChartDto.getMemberId()).get();
        if((registerChartDto.getUserType() == RegisterChartDto.UserType.ADMIN)&&(registerChart.getStatus() != registerChartDto.getStatus())){
            registerChartService.updateStatusByAdminAndMember(admin, registerChartDto.getMemberId(), registerChartDto.getStatus());
        }

        sendingOperations.convertAndSend("/topic/queues/members/"+registerChartDto.getMemberId()+"/subjects/"+registerChartDto.getSubjectId()
                +"/hospitals/"+registerChartDto.getHospitalId(), registerChartDto);
    }

    @MessageMapping("/main")
    public void sendMain(RegisterChartDto registerChartDto) throws Exception {
        Thread.sleep(1000); // simulated delay

        sendingOperations.convertAndSend("/topic/queues/main/subjects/"+registerChartDto.getSubjectId()
                +"/hospitals/"+registerChartDto.getHospitalId(), registerChartDto);
    }
}
