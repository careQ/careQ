package com.reve.careQ.domain.Admin.controller;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.Admin.service.AdminService;
import com.reve.careQ.domain.Admin.dto.JoinFormDto;
import com.reve.careQ.domain.Member.entity.Member;
import com.reve.careQ.domain.Member.service.MemberService;
import com.reve.careQ.domain.RegisterChart.entity.RegisterChart;
import com.reve.careQ.domain.RegisterChart.entity.RegisterChartDto;
import com.reve.careQ.domain.RegisterChart.entity.RegisterChartStatus;
import com.reve.careQ.domain.RegisterChart.repository.RegisterChartRepository;
import com.reve.careQ.domain.RegisterChart.service.RegisterChartService;
import com.reve.careQ.domain.Reservation.controller.ReservationController;
import com.reve.careQ.domain.Reservation.service.ReservationService;
import com.reve.careQ.domain.Reservation.service.ReservationServiceImpl;
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

    private final ReservationServiceImpl reservationServiceImpl;
    private final ReservationService reservationService;
    private final RegisterChartRepository registerChartRepository;

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
        Optional<Reservation> reservationOptional = reservationRepository.findByAdminIdAndMemberId(adminId, memberId);

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

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value ="/queues")
    public String showAdminQueues(Model model){
        // 현재 로그인한 관리자 정보 가져오기
        RsData<Admin> currentAdminData = adminService.getCurrentAdmin();

        if (currentAdminData.isSuccess()) {
            Admin currentAdmin = currentAdminData.getData();

            // 관리자에 해당하는 예약 목록 가져오기
            List<Reservation> reservations = reservationServiceImpl.getTodayReservation(currentAdmin);

            model.addAttribute("reservations", reservations);

            return "admins/queues";
        } else {
            return "redirect:/";
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value ="/queues", params = {"name"})
    @ResponseBody
    public List<RegisterChartDto> showAdminQueues(@RequestParam(name="name",required=false,defaultValue="") String name, Model model) {
        // 현재 로그인한 관리자 정보 가져오기
        RsData<Admin> currentAdminData = adminService.getCurrentAdmin();

        Admin currentAdmin = currentAdminData.getData();

        // 관리자에 해당하는 줄서기 목록 가져오기
        List<RegisterChart> registerCharts = adminService.getRegisterChartByAdminAndMemberName(currentAdmin, name);

        List<RegisterChartDto> registerChartDto = registerCharts.stream()
                .map(registerChart -> new RegisterChartDto(registerChart.getAdmin().getId(), registerChart.getMember().getId(),
                        registerChart.getAdmin().getSubject().getId(), registerChart.getAdmin().getHospital().getId(),
                        registerChart.getMember().getUsername(), registerChart.getTime(), registerChart.getStatus()))
                .collect(Collectors.toList());
        return registerChartDto;
    }


    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/queues")
    public String deleteRegister(@RequestParam("memberId") Long memberId, @RequestParam("kind") String kind) {
        RsData<Admin> currentAdminData = adminService.getCurrentAdmin();

        if (currentAdminData.isSuccess()) {
            Admin admin = currentAdminData.getData();
            Optional<Member> memberOptional = memberService.findById(memberId);

            if(memberOptional.isEmpty()) {
                return adminRq.historyBack(RsData.of("F-5", "멤버 정보를 찾을 수 없습니다."));
            } else {
                Member member = memberOptional.get();

                if(kind.equals("queue")){
                    // 줄서기 정보 삭제
                    Optional<RegisterChart> registerChartOptional = registerChartRepository.findRegisterChartByAdminIdAndMemberId(admin.getId(), member.getId());

                    if (registerChartOptional.isPresent()) {
                        RsData<String> deleteRegisterRs = registerChartService.deleteRegister(registerChartOptional.get().getId());

                        if (deleteRegisterRs.isSuccess()) {
                            return "redirect:/admins/queues";
                        } else {
                            return adminRq.historyBack(deleteRegisterRs);
                        }
                    } else {
                        return adminRq.historyBack(RsData.of("F-5", "줄서기 정보를 찾을 수 없습니다."));
                    }
                }
                //당일 예약에서 진료 후 예약 삭제
                else {
                    Optional<Reservation> reservationOptional = reservationService.findByAdminIdAndMemberId(admin.getId(), member.getId());

                    if (reservationOptional.isPresent()) {
                        Reservation reservation = reservationOptional.get();
                        Long reservationId = reservation.getId();

                        RsData<String> deleteReservationRs = reservationService.deleteReservation(reservationId);

                        if (deleteReservationRs.isSuccess()) {
                            return "redirect:/admins/queues";
                        } else {
                            return adminRq.historyBack(deleteReservationRs);
                        }
                    } else {
                        return adminRq.historyBack(RsData.of("F-5", "예약 정보를 찾을 수 없습니다."));
                    }
                }

            }

        } else {
            return adminRq.historyBack(currentAdminData);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/queues")
    public String updateRegisterStatus(@RequestParam("memberId") Long memberId, @RequestParam("kind") String kind) {
        RsData<Admin> currentAdminData = adminService.getCurrentAdmin();

        if (currentAdminData.isSuccess()) {
            Admin admin = currentAdminData.getData();
            Optional<Member> memberOptional = memberService.findById(memberId);

            if(memberOptional.isEmpty()) {
                return adminRq.historyBack(RsData.of("F-5", "멤버 정보를 찾을 수 없습니다."));
            } else {
                Member member = memberOptional.get();

                //줄서기에서 진료 상태 변경
                if(kind.equals("queue")){
                    // 줄서기 정보 가져오기
                    Optional<RegisterChart> registerChartOptional = registerChartService.findByAdminIdAndMemberId(admin.getId(), member.getId());

                    if (registerChartOptional.isPresent()) {
                        RegisterChart registerChart = registerChartOptional.get();
                        RsData<RegisterChart> updateStatusRs = registerChartService.updateStatus(registerChart, RegisterChartStatus.ENTER);

                        if (updateStatusRs.isSuccess()) {
                            return "redirect:/admins/queues";
                        }else{
                            return adminRq.historyBack(updateStatusRs);
                        }
                    } else {
                        return adminRq.historyBack(RsData.of("F-5", "줄서기 정보를 찾을 수 없습니다."));
                    }
                }
                //당일 예약에서 진료 상태 변경
                else{
                    // 예약 정보 가져오기
                    Optional<Reservation> reservationOptional = reservationService.findByAdminIdAndMemberId(admin.getId(), memberId);

                    if (reservationOptional.isPresent()) {
                        Reservation reservation = reservationOptional.get();
                        RsData<Reservation> updateRegisterStatus = reservationService.updateRegisterStatus(reservation, RegisterChartStatus.ENTER);

                        if (updateRegisterStatus.isSuccess()) {
                            return "redirect:/admins/queues";
                        }else{
                            return adminRq.historyBack(updateRegisterStatus);
                        }
                    } else {
                        return adminRq.historyBack(RsData.of("F-5", "예약 정보를 찾을 수 없습니다."));
                    }
                }
            }

        } else {
            return adminRq.historyBack(currentAdminData);
        }
    }



}