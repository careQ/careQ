package com.reve.careQ.domain.Admin.controller;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.Admin.service.AdminService;
import com.reve.careQ.domain.Admin.dto.JoinFormDto;
import com.reve.careQ.global.rq.AdminRq;
import com.reve.careQ.domain.Reservation.entity.Reservation;
import com.reve.careQ.domain.Reservation.entity.ReservationStatus;
import com.reve.careQ.domain.Reservation.repository.ReservationRepository;
import com.reve.careQ.global.rsData.RsData;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    private final AdminRq adminRq;

    private final ReservationRepository reservationRepository;

    @PreAuthorize("isAnonymous()")
    @GetMapping("/login")
    public String showLogin() {
        return "admins/login";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping()
    public String showAdminsHome() {
        return "admins/admins-home";
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/join")
    public String showJoin() {
        return "admins/join";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/join")
    public String join(@Valid JoinFormDto joinFormDto) {
        RsData<Admin> joinRs = adminService.join(joinFormDto.getHospitalCode(), joinFormDto.getSubjectCode(), joinFormDto.getUsername(), joinFormDto.getPassword());

        if (joinRs.isFail()) {

            return adminRq.historyBack(joinRs);
        }

        // 아래 링크로 리다이렉트(302, 이동) 하고 그 페이지에서 메세지 보여줌
        return adminRq.redirectWithMsg("/admins/login", joinRs);
    }


    @GetMapping("/reservations")
    public String showAdminReservations(Model model) {
        // 현재 로그인한 관리자 정보 가져오기
        RsData<Admin> currentAdminData = adminService.getCurrentAdmin();
        if (currentAdminData.isSuccess()) {
            Admin currentAdmin = currentAdminData.getData();

            // 관리자에 해당하는 예약 목록 가져오기
            List<Reservation> reservations = adminService.getReservationsForAdmin(currentAdmin);

            model.addAttribute("reservations", reservations);

            return "admins/reservation";
        } else {
            return "redirect:/";
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/reservations")
    public String confirmReservation(@RequestParam("adminId") Long adminId,
                                     @RequestParam("memberId") Long memberId,
                                     HttpSession session) {
        // 예약 정보 가져오기
        Optional<Reservation> reservationOptional = reservationRepository.findByIdAdminIdAndIdMemberId(adminId, memberId);

        if (reservationOptional.isPresent()) {
            Reservation reservation = reservationOptional.get();
            reservation.setStatus(ReservationStatus.CONFIRMED);

            reservationRepository.save(reservation);

            // 승인 되면 세션에 승인 상태 저장
            session.setAttribute("reservationStatus", "CONFIRMED");

            // 승인 되면 현재 페이지 다시 보여주고,
            return "redirect:/admins/reservations";
        } else {
            return "redirect:/";
        }
    }

}