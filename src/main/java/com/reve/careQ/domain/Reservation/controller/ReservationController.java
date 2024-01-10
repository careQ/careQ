package com.reve.careQ.domain.Reservation.controller;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.Admin.service.AdminService;
import com.reve.careQ.domain.Hospital.service.HospitalService;
import com.reve.careQ.domain.Reservation.dto.ReservationDto;
import com.reve.careQ.domain.Reservation.entity.Reservation;
import com.reve.careQ.domain.Reservation.service.ReservationService;
import com.reve.careQ.domain.Subject.service.SubjectService;
import com.reve.careQ.global.rq.Rq;
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
@RequestMapping("/members/subjects/{subject-id}/hospitals/{hospital-id}/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final SubjectService subjectService;
    private final HospitalService hospitalService;
    private final ReservationService reservationService;
    private final AdminService adminService;

    private final SimpMessageSendingOperations sendingOperations;
    private final Rq rq;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ModelAndView showReservation(@PathVariable("subject-id") Long subjectId, @PathVariable("hospital-id") Long hospitalId, Model model) {
        ModelAndView mv = new ModelAndView();

        mv.addObject("subject",subjectService.findById(subjectId).get());
        mv.addObject("hospital",hospitalService.findById(hospitalId).get());
        mv.setViewName("members/reservations");
        return mv;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public String createReservation(@PathVariable("subject-id") Long subjectId,
                                    @PathVariable("hospital-id") Long hospitalId,
                                    @RequestParam("selectedDate") String selectedDate,
                                    @RequestParam("selectedTime") String selectedTime) {
        try {
            String redirectUrl = reservationService.createReservationAndReturnRedirectUrl(hospitalId, subjectId, selectedDate, selectedTime);
            return "redirect:" + redirectUrl;
        } catch (Exception e) {
            return rq.historyBack(RsData.of("F-5", e.getMessage()));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{reservationId}")
    public ModelAndView waitReservation(@PathVariable("reservationId") Long reservationId,
                                        @PathVariable("subject-id") Long subjectId,
                                        @PathVariable("hospital-id") Long hospitalId,
                                        Model model) {
        ModelAndView mv = new ModelAndView();

        mv.addObject("subject",subjectService.findById(subjectId).get());
        mv.addObject("hospital",hospitalService.findById(hospitalId).get());
        mv.setViewName("members/reservations-wait");
        return mv;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{reservationId}")
    public ResponseEntity<?> deleteReservation(@PathVariable("reservationId") Long reservationId) {
        RsData<String> deleteResult = reservationService.deleteReservation(reservationId);

        if (deleteResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                    .location(URI.create("/members"))
                    .build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(deleteResult);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{reservationId}/confirm")
    public ModelAndView confirmReservation(@PathVariable("reservationId") Long reservationId,
                                           @PathVariable("subject-id") Long subjectId,
                                           @PathVariable("hospital-id") Long hospitalId){
        ModelAndView mv= new ModelAndView();

        mv.addObject("subjectName",subjectService.findById(subjectId).get().getName());
        mv.addObject("hospitalName", hospitalService.findById(hospitalId).get().getName());
        mv.setViewName("members/reservations-confirm");

        return mv;
    }

    @MessageMapping("/reservation")
    public void send(ReservationDto reservationDto) throws Exception {
        Thread.sleep(1000); // simulated delay

        Admin admin = adminService.findByHospitalIdAndSubjectId(reservationDto.getHospitalId(),reservationDto.getSubjectId()).get();
        Reservation reservation = reservationService.findByAdminIdAndMemberId(admin.getId(), reservationDto.getMemberId()).get();
        if((reservationDto.getUserType() == ReservationDto.UserType.ADMIN)&&(reservation.getStatus() != reservationDto.getStatus())){
            reservationService.updateStatus(reservation, reservationDto.getStatus());
        }

        sendingOperations.convertAndSend("/topic/members/"+reservationDto.getMemberId()+"/subjects/"+reservationDto.getSubjectId()
                +"/hospitals/"+reservationDto.getHospitalId(), reservationDto);
    }
}

