package com.reve.careQ.domain.Admin.controller;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.Admin.dto.JoinFormDto;
import com.reve.careQ.domain.Admin.service.AdminService;
import com.reve.careQ.domain.Member.dto.OnsiteRegisterDto;
import com.reve.careQ.domain.RegisterChart.dto.RegisterChartDto;
import com.reve.careQ.domain.RegisterChart.entity.RegisterChartStatus;
import com.reve.careQ.domain.RegisterChart.service.RegisterChartService;
import com.reve.careQ.domain.Reservation.service.ReservationService;
import com.reve.careQ.global.rq.AdminRq;
import com.reve.careQ.domain.Reservation.entity.Reservation;
import com.reve.careQ.global.rsData.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Controller
@RequestMapping("/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final AdminRq adminRq;
    private final ReservationService reservationService;
    private final RegisterChartService registerChartService;

    @GetMapping("/login")
    public String showLogin(@RequestParam(value = "error", required = false)String error, @RequestParam(value = "exception", required = false)String exception, Model model) {
        model.addAttribute("error", error);
        model.addAttribute("exception", exception);
        return "admins/login";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping()
    public String showAdminsHome() {
        return "admins/admins-home";
    }

    @GetMapping("/join")
    public String showJoin() {
        return "admins/join";
    }

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

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/reservations")
    public String showAdminReservations(Model model) {
        try {
            List<Reservation> reservations = adminService.getReservationsForCurrentAdmin();
            model.addAttribute("reservations", reservations);
            return "admins/reservation";
        } catch (NoSuchElementException e) {
            return "redirect:/";
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/reservations")
    public String deleteReservation(@RequestParam("memberId") Long memberId) {
        Admin admin = adminService.getCurrentAdmin()
                .orElseThrow(() -> new RuntimeException ("관리자 정보를 찾을 수 없습니다."));

        Reservation reservation = reservationService.findReservationByAdminIdAndMemberIdAndIsDeletedFalse(admin.getId(), memberId)
                .orElseThrow(() -> new IllegalArgumentException("예약 정보를 찾을 수 없습니다."));

        reservationService.deleteReservation(reservation.getId());

        return "redirect:/admins/reservations";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value ="/queues")
    public String showAdminQueues(Model model){
        Optional<Admin> currentAdminOptional = adminService.getCurrentAdmin();
        if (!currentAdminOptional.isPresent()) {
            return "redirect:/";
        }

        Admin currentAdmin = currentAdminOptional.get();
        // 관리자에 해당하는 예약 목록 가져오기
        List<Reservation> reservations = reservationService.getTodayReservation(currentAdmin);
        model.addAttribute("reservations", reservations);

        return "admins/queues";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value ="/queues", params = {"name"})
    @ResponseBody
    public List<RegisterChartDto> showAdminQueues(@RequestParam(name="name",required=false,defaultValue="") String name) {
        return adminService.getRegisterChartDtoByMemberName(name);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/queues")
    public String updateRegisterStatus(@RequestParam("memberId") Long memberId, @RequestParam("status") RegisterChartStatus status) {
        Admin admin = adminService.getCurrentAdmin().get();

        reservationService.updateStatusByAdminAndMember(admin, memberId, status);

        return "redirect:/admins/queues";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/on-site")
    public String onSiteRegister(@Valid @ModelAttribute OnsiteRegisterDto onsiteRegisterDto,
                                 BindingResult bindingResult) {
        try {
            validateOnsiteRegister(onsiteRegisterDto, bindingResult);
            registerChartService.registerNewUser(onsiteRegisterDto);
            return "redirect:/admins/queues";
        } catch (RuntimeException e) {
            return handleRegisterError(e);
        }
    }

    private void validateOnsiteRegister(OnsiteRegisterDto onsiteRegisterDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new RuntimeException("회원 등록에 실패했습니다: " + bindingResult.getFieldError().getDefaultMessage());
        }
    }

    private String handleRegisterError(RuntimeException e) {
        String errorMsg = e.getMessage();
        return adminRq.historyBack(errorMsg);
    }
}
