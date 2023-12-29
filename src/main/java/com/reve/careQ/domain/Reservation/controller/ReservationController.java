package com.reve.careQ.domain.Reservation.controller;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.Admin.service.AdminService;
import com.reve.careQ.domain.Hospital.service.HospitalService;
import com.reve.careQ.domain.Reservation.entity.ReservationDto;
import com.reve.careQ.domain.Reservation.entity.Reservation;
import com.reve.careQ.domain.Reservation.service.ReservationServiceImpl;
import com.reve.careQ.domain.Subject.service.SubjectService;
import com.reve.careQ.global.rq.Rq;
import com.reve.careQ.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/members/subjects/{subject-id}/hospitals/{hospital-id}/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final SubjectService subjectService;
    private final HospitalService hospitalService;
    private final ReservationServiceImpl reservationServiceImpl;
    private final Rq rq;
    private final AdminService adminService;

    private final SimpMessageSendingOperations sendingOperations;

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
                                    @RequestParam("selectedTime") String selectedTime
    ) {
        try {
            String redirectUrl = reservationServiceImpl.createReservationAndReturnRedirectUrl(hospitalId, subjectId, selectedDate, selectedTime);
            return "redirect:" + redirectUrl;
        } catch (Exception e) {
            // 예약 생성 실패
            return rq.historyBack(e.getMessage());
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

    @PostMapping("/{reservationId}")
    public String deleteReservation(@PathVariable("reservationId") Long reservationId) {
        RsData<String> deleteResult = reservationServiceImpl.deleteReservation(reservationId);

        if (deleteResult.isSuccess()) {
            return "redirect:/members";
        } else {
            return rq.historyBack(deleteResult);
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
        Reservation reservation = reservationServiceImpl.findByAdminIdAndMemberId(admin.getId(), reservationDto.getMemberId()).get();
        if((reservationDto.getUserType() == ReservationDto.UserType.ADMIN)&&(reservation.getStatus() != reservationDto.getStatus())){
            reservationServiceImpl.updateStatus(reservation, reservationDto.getStatus());
        }

        sendingOperations.convertAndSend("/topic/members/"+reservationDto.getMemberId()+"/subjects/"+reservationDto.getSubjectId()
                +"/hospitals/"+reservationDto.getHospitalId(), reservationDto);
    }
}

