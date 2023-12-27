package com.reve.careQ.domain.Admin.controller;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.Admin.service.AdminService;
import com.reve.careQ.domain.Admin.dto.JoinFormDto;
import com.reve.careQ.domain.Member.entity.Member;
import com.reve.careQ.domain.Member.service.MemberService;
import com.reve.careQ.domain.RegisterChart.entity.RegisterChart;
import com.reve.careQ.domain.RegisterChart.entity.RegisterChartDto;
import com.reve.careQ.domain.RegisterChart.entity.RegisterChartStatus;
import com.reve.careQ.domain.RegisterChart.service.RegisterChartService;
import com.reve.careQ.domain.Reservation.service.ReservationService;
import com.reve.careQ.global.compositePKEntity.CompositePKEntity;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    private final MemberService memberService;

    private final AdminRq adminRq;

    private final ReservationRepository reservationRepository;

    private final RegisterChartService registerChartService;

    private final ReservationService reservationService;

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

        RsData<Admin> joinRs = adminService.join(
                joinFormDto.getHospitalCode(),
                joinFormDto.getSubjectCode(),
                joinFormDto.getUsername(),
                joinFormDto.getPassword(),
                joinFormDto.getEmail()
        );

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

        if (!currentAdminData.isSuccess()) {
            return "redirect:/";
        }

        Admin currentAdmin = currentAdminData.getData();

        // 관리자에 해당하는 예약 목록 가져오기
        List<Reservation> reservations = adminService.getReservationsForAdmin(currentAdmin);

        model.addAttribute("reservations", reservations);

        return "admins/reservation";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/reservations")
    public String confirmReservation(@RequestParam("adminId") Long adminId,
                                     @RequestParam("memberId") Long memberId,
                                     HttpSession session) {
        // 예약 정보 가져오기
        Optional<Reservation> reservationOptional = reservationRepository.findByIdAdminIdAndIdMemberId(adminId, memberId);

        if (!reservationOptional.isPresent()) {
            return "redirect:/";
        }

        Reservation reservation = reservationOptional.get();
        reservation.setStatus(ReservationStatus.CONFIRMED);

        reservationRepository.save(reservation);

        // 승인 되면 세션에 승인 상태 저장
        session.setAttribute("reservationStatus", ReservationStatus.CONFIRMED.name());

        // 승인 되면 현재 페이지 다시 보여주고,
        return "redirect:/admins/reservations";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value ="/queues")
    public String showAdminQueues(Model model){
        // 현재 로그인한 관리자 정보 가져오기
        RsData<Admin> currentAdminData = adminService.getCurrentAdmin();

        if (!currentAdminData.isSuccess()) {
            return "redirect:/";
        }

        Admin currentAdmin = currentAdminData.getData();

        // 관리자에 해당하는 예약 목록 가져오기
        List<Reservation> reservations = reservationService.getTodayReservation(currentAdmin);

        model.addAttribute("reservations", reservations);

        return "admins/queues";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value ="/queues", params = {"name"})
    @ResponseBody
    public List<RegisterChartDto> showAdminQueues(@RequestParam(name="name",required=false,defaultValue="") String name) {
        // 현재 로그인한 관리자 정보 가져오기
        RsData<Admin> currentAdminData = adminService.getCurrentAdmin();

        if (!currentAdminData.isSuccess()) {
            // 로그인한 관리자 정보를 가져오지 못한 경우 빈 목록 반환
            return Collections.emptyList();
        }

        Admin currentAdmin = currentAdminData.getData();

        // 관리자에 해당하는 줄서기 목록 가져오기
        List<RegisterChart> registerCharts = adminService.getRegisterChartByAdminAndMemberName(currentAdmin, name);

        // RegisterChart를 RegisterChartDto로 변환
        List<RegisterChartDto> registerChartDto = registerCharts.stream()
                .map(registerChart -> new RegisterChartDto(
                        registerChart.getAdmin().getId(),
                        registerChart.getMember().getId(),
                        registerChart.getAdmin().getSubject().getId(),
                        registerChart.getAdmin().getHospital().getId(),
                        registerChart.getMember().getUsername(),
                        registerChart.getTime(),
                        registerChart.getStatus()))
                .collect(Collectors.toList());

        return registerChartDto;
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/queues")
    public String deleteRegister(@RequestParam("memberId") Long memberId, @RequestParam("kind") String kind) {
        RsData<Admin> currentAdminRs = adminService.getCurrentAdmin();

        if (currentAdminRs.isFail()) {
            return adminRq.historyBack(currentAdminRs);
        }

        Admin admin = currentAdminRs.getData();
        Member member = memberService.findById(memberId).get();

        CompositePKEntity id = new CompositePKEntity();
        id.setAdminId(admin.getId());
        id.setMemberId(member.getId());

        RsData<String> deleteRegisterRs;

        // 종류에 따라 다른 메서드 호출
        if ("queue".equals(kind)) {
            deleteRegisterRs = registerChartService.deleteRegister(id);
        } else {
            deleteRegisterRs = reservationService.deleteReservation(id);
        }

        if (deleteRegisterRs.isSuccess()) {
            return "redirect:/admins/queues";
        } else {
            return adminRq.historyBack(deleteRegisterRs);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/queues")
    public String updateRegisterStatus(@RequestParam("memberId") Long memberId, @RequestParam("kind") String kind) {
        RsData<Admin> currentAdminRs = adminService.getCurrentAdmin();

        if (currentAdminRs.isFail()) {
            return adminRq.historyBack(currentAdminRs);
        }

        Admin admin = currentAdminRs.getData();
        Member member = memberService.findById(memberId).get();

        RsData<?> updateStatusRs;

        if("queue".equals(kind)){
            // 줄서기 정보 가져오기
            Optional<RegisterChart> registerChartOptional = registerChartService.findByIdAdminIdAndIdMemberId(admin.getId(), member.getId());

            RegisterChart registerChart = registerChartOptional.get();

            updateStatusRs = registerChartService.updateStatus(registerChart, RegisterChartStatus.ENTER);
        } else {
            // 예약 정보 가져오기
            Optional<Reservation> reservationOptional = reservationRepository.findByIdAdminIdAndIdMemberId(admin.getId(), memberId);

            Reservation reservation = reservationOptional.get();

            updateStatusRs = reservationService.updateRegisterStatus(reservation, RegisterChartStatus.ENTER);

        }

        if (updateStatusRs.isSuccess()) {
            return "redirect:/admins/queues";
        } else {
            return adminRq.historyBack(updateStatusRs);
        }
    }
}