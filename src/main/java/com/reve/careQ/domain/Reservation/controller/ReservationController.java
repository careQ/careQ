package com.reve.careQ.domain.Reservation.controller;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.Admin.service.AdminService;
import com.reve.careQ.domain.Hospital.service.HospitalService;
import com.reve.careQ.domain.Member.entity.Member;
import com.reve.careQ.domain.Member.service.MemberService;
import com.reve.careQ.domain.Reservation.entity.ReservationDto;
import com.reve.careQ.domain.Reservation.entity.Reservation;
import com.reve.careQ.domain.Reservation.service.ReservationService;
import com.reve.careQ.domain.Subject.service.SubjectService;
import com.reve.careQ.global.compositePKEntity.CompositePKEntity;
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

import java.util.Optional;

@Controller
@RequestMapping("/members/subjects/{subject-id}/hospitals/{hospital-id}/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final SubjectService subjectService;
    private final HospitalService hospitalService;
    private final ReservationService reservationService;
    private final Rq rq;
    private final AdminService adminService;
    private final MemberService memberService;

    private final SimpMessageSendingOperations sendingOperations;

    // 예약 신청페이지
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ModelAndView showReservation(@PathVariable("subject-id") Long subjectId, @PathVariable("hospital-id") Long hospitalId, Model model) {
        ModelAndView mv = new ModelAndView();

        mv.addObject("subject",subjectService.findById(subjectId).get());
        mv.addObject("hospital",hospitalService.findById(hospitalId).get());
        mv.setViewName("members/reservations");
        return mv;
    }

    // 예약하기 버튼
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public String createReservation(@PathVariable("subject-id") Long subjectId,
                                    @PathVariable("hospital-id") Long hospitalId,
                                    @RequestParam("selectedDate") String selectedDate,
                                    @RequestParam("selectedTime") String selectedTime
    ) {

        RsData<Reservation> reservationRs = reservationService.insert(hospitalId, subjectId, selectedDate, selectedTime);

        if (reservationRs.isSuccess()) {
            // 예약 생성 성공

            return "redirect:/members/subjects/" + subjectId + "/hospitals/" + hospitalId + "/reservations/";
        } else {
            // 예약 생성 실패
            return rq.historyBack(reservationRs);
        }
    }

    // 예약 대기 페이지
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/")
    public ModelAndView waitReservation(@PathVariable("subject-id") Long subjectId,
                                        @PathVariable("hospital-id") Long hospitalId,
                                        Model model) {
        ModelAndView mv = new ModelAndView();

        mv.addObject("subject",subjectService.findById(subjectId).get());
        mv.addObject("hospital",hospitalService.findById(hospitalId).get());
        mv.setViewName("members/reservations-wait");
        return mv;
    }

    @DeleteMapping("/")
    public String deleteReservation(@PathVariable("subject-id") Long subjectId,
                                    @PathVariable("hospital-id") Long hospitalId) {

        Optional<Admin> adminOptional = adminService.findByHospitalIdAndSubjectId(hospitalId, subjectId);

        if (adminOptional.isEmpty()) {
            return "redirect:/members";

        } else {
            Admin admin = adminOptional.get();

            RsData<Member> currentUserData = memberService.getCurrentUser();

            if (currentUserData.isSuccess()) {
                Member currentUser = currentUserData.getData();

                CompositePKEntity id = new CompositePKEntity();
                id.setAdminId(admin.getId());
                id.setMemberId(currentUser.getId());

                // 예약 정보 삭제
                RsData<String> deleteResult = reservationService.deleteReservation(id);

                if (deleteResult.isSuccess()) {
                    return "redirect:/members";
                } else {
                    return rq.historyBack(deleteResult);
                }
            } else {
                return rq.historyBack(currentUserData);
            }
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/confirm")
    public ModelAndView confirmReservation(@PathVariable("subject-id") Long subjectId,
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
        Reservation reservation = reservationService.findByIdAdminIdAndIdMemberId(admin.getId(), reservationDto.getMemberId()).get();
        if((reservationDto.getUserType() == ReservationDto.UserType.ADMIN)&&(reservation.getStatus() != reservationDto.getStatus())){
            reservationService.updateStatus(reservation, reservationDto.getStatus());
        }

        sendingOperations.convertAndSend("/topic/members/"+reservationDto.getMemberId()+"/subjects/"+reservationDto.getSubjectId()
                +"/hospitals/"+reservationDto.getHospitalId(), reservationDto);
    }

}
